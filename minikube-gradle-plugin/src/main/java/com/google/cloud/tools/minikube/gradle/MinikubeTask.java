package com.google.cloud.tools.minikube.gradle;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.impldep.com.google.common.annotations.VisibleForTesting;

/** Generic Minikube task. */
public class MinikubeTask extends DefaultTask {

  /**
   * The minikube executable, can include the full path to minikube, default assumes it is on path.
   * I'm not sure this works on windows, ask someone about this.
   */
  private String minikube = "minikube";

  /** The minikube command: start, stop, etc. */
  private String command;

  private String[] flags = {};

  @Input
  public String getMinikube() {
    return minikube;
  }

  public void setMinikube(String minikube) {
    this.minikube = minikube;
  }

  public void setMinikube(File minikube) {
    this.minikube = minikube.getAbsolutePath();
  }

  @Input
  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  @Input
  public String[] getFlags() {
    return flags;
  }

  public void setFlags(String[] flags) {
    this.flags = flags;
  }

  /** Task entry point. */
  @TaskAction
  public void execMinikube() throws IOException, InterruptedException {
    ExecutorService executor = Executors.newSingleThreadExecutor();

    List<String> execString = buildMinikubeCommand();
    getLogger().debug("Running command : " + String.join(" ", execString));

    ProcessBuilder pb = new ProcessBuilder();
    pb.command(execString);
    pb.redirectErrorStream(true);
    final Process process = pb.start();

    // stream consumer
    executor.execute(
        () -> {
          try (BufferedReader br =
              new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line = br.readLine();
            while (line != null) {
              getLogger().lifecycle(line);
              line = br.readLine();
            }
          } catch (IOException e) {
            getLogger().warn("IO Exception reading minikube process output");
          }
        });
    int exitCode = process.waitFor();
    executor.shutdown();
    try {
      executor.awaitTermination(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      getLogger().debug("Task Executor interrupted waiting for output consumer thread");
    }

    // stop the build if minikube fails to do something, we may want to make this configurable
    if (exitCode != 0) {
      throw new GradleException("minikube exited with non-zero exit code : " + exitCode);
    }
  }

  @VisibleForTesting
  List<String> buildMinikubeCommand() {
    List<String> execString = new ArrayList<>();
    execString.add(minikube);
    execString.add(command);
    execString.addAll(Arrays.asList(flags));

    return execString;
  }
}
