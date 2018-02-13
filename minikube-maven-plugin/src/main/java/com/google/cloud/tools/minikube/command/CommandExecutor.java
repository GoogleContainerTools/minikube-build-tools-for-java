/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.cloud.tools.minikube.command;

import com.google.common.annotations.VisibleForTesting;
import org.codehaus.plexus.util.cli.CommandLineException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

// TODO: Share this with minikube-gradle-plugin.
/** Executes a shell command. */
public class CommandExecutor {

  @VisibleForTesting
  static final int TIMEOUT_SECONDS = 5;

  private Supplier<ProcessBuilder> processBuilderSupplier = ProcessBuilder::new;
  private Supplier<ExecutorService> executorServiceSupplier = Executors::newSingleThreadExecutor;;
  private BuildLogger logger;
  private Map<String, String> environment;

  /** Sets the {@code BuildLogger} to use to log messages during the command execution. */
  public CommandExecutor setLogger(BuildLogger logger) {
    this.logger = logger;
    return this;
  }

  /** Sets the environment variables to run the command with. */
  public CommandExecutor setEnvironment(Map<String, String> environmentMap) {
    this.environment = environmentMap;
    return this;
  }

  @VisibleForTesting
  CommandExecutor setProcessBuilderSupplier(Supplier<ProcessBuilder> processBuilderSupplier) {
    this.processBuilderSupplier = processBuilderSupplier;
    return this;
  }

  @VisibleForTesting
  CommandExecutor setExecutorServiceSupplier(Supplier<ExecutorService> executorServiceSupplier) {
    this.executorServiceSupplier = executorServiceSupplier;
    return this;
  }

  /** Runs the command. Same as {@link #run(List)}. */
  public List<String> run(String... command) throws IOException, InterruptedException {
    return run(Arrays.asList(command));
  }

  /**
   * Runs the command.
   *
   * @param command the list of command line tokens
   * @return the output of the command as a list of lines
   * @throws IOException if the command failed to run or exited with non-zero exit code
   */
  public List<String> run(List<String> command) throws IOException, InterruptedException {
    if (logger != null) {
      logger.debug("Running command : " + String.join(" ", command));
    }

    ExecutorService executor = executorServiceSupplier.get();

    // Builds the command to execute.
    ProcessBuilder processBuilder = processBuilderSupplier.get();
    processBuilder.command(command);
    processBuilder.redirectErrorStream(true);
    if (environment != null) {
      processBuilder.environment().putAll(environment);
    }
    Process process = processBuilder.start();

    // Runs the command and streams the output.
    List<String> output = new ArrayList<>();
    executor.execute(makeOutputConsumerRunnable(process, output));
    int exitCode = process.waitFor();

    // Shuts down the executor.
    executor.shutdown();

    try {
      executor.awaitTermination(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    } catch (InterruptedException ex) {
      if (logger != null) {
        logger.debug("Task Executor interrupted waiting for output consumer thread");
      }
    }

    // Checks the command exit code.
    if (exitCode != 0) {
      throw new IOException("command exited with non-zero exit code : " + exitCode);
    }

    return output;
  }

  /**
   * Creates a {@link Runnable} to read the command output.
   *
   * @param process the process to read from
   * @param output a list to store the output lines to
   */
  private Runnable makeOutputConsumerRunnable(Process process, List<String> output) {
    return () -> {
      try (InputStream processInputStream = process.getInputStream();
           InputStreamReader inputStreamReader = new InputStreamReader(processInputStream, StandardCharsets.UTF_8);
           BufferedReader br = new BufferedReader(inputStreamReader)) {
        String line = br.readLine();
        while (line != null) {
          if (logger != null) {
            logger.lifecycle(line);
          }
          output.add(line);
          line = br.readLine();
        }

      } catch (IOException ex) {
        if (logger != null) {
          logger.warn("IO Exception reading process output");
        }
      }
    };
  }
}
