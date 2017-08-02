package com.google.cloud.tools.minikube.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/** An extremely basic minikube plugin to manage the minikube lifecycle from gradle. */
public class MinikubePlugin implements Plugin<Project> {
  private Project project;

  @Override
  public void apply(Project project) {
    this.project = project;

    createStartTask();
    createStopTask();
  }

  private void createStartTask() {
    MinikubeTask task = project.getTasks().create("minikubeStart", MinikubeTask.class);
    task.setCommand("start");
  }

  private void createStopTask() {
    MinikubeTask task = project.getTasks().create("minikubeStop", MinikubeTask.class);
    task.setCommand("stop");
  }
}
