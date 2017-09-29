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

package com.google.cloud.tools.minikube.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;

/** Executes a shell command. */
public class CommandExecutor {
  private static final int TIMEOUT_SECONDS = 5;
  private Logger logger;

  public CommandExecutor() {}

  public CommandExecutor setLogger(Logger logger) {
    this.logger = logger;
    return this;
  }

  /**
   * Runs the command.
   *
   * @param command the list of command line tokens
   * @return the output of the command as a list of lines
   */
  public List<String> run(List<String> command) throws IOException, InterruptedException {
    if (logger != null) {
      logger.debug("Running command : " + String.join(" ", command));
    }

    ExecutorService executor = Executors.newSingleThreadExecutor();

    // Builds the command to execute with possible environment variables.
    final Process process = buildProcess(command);

    // Runs the command and streams the output.
    List<String> output = new ArrayList<>();
    executor.execute(outputConsumerRunnable(process, output));

    int exitCode = process.waitFor();
    executor.shutdown();
    try {
      executor.awaitTermination(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      if (logger != null) {
        logger.debug("Task Executor interrupted waiting for output consumer thread");
      }
    }

    // Stops the build if the command fails to do something, we may want to make this configurable.
    if (exitCode != 0) {
      throw new GradleException("command exited with non-zero exit code : " + exitCode);
    }

    return output;
  }

  private Process buildProcess(List<String> command) throws IOException {
    ProcessBuilder pb = new ProcessBuilder();
    pb.command(command);
    pb.redirectErrorStream(true);
    return pb.start();
  }

  /**
   * Creates a Runnable to for the single thread {@code ExecutorService} to read the command output.
   *
   * @param process the process to read from
   * @param output a list to store the output lines to
   */
  private Runnable outputConsumerRunnable(Process process, List<String> output) {
    return () -> {
      try (BufferedReader br =
          new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        String line = br.readLine();
        output.add(line);
        while (line != null) {
          if (logger != null) {
            logger.lifecycle(line);
          }
          line = br.readLine();
          output.add(line);
        }
      } catch (IOException e) {
        if (logger != null) {
          logger.warn("IO Exception reading process output");
        }
      }
    };
  }
}
