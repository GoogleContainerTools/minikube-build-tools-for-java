package com.google.cloud.tools.minikube.gradle;

import java.io.File;
import java.util.Arrays;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/** Tests for MinikubeTask */
public class MinikubeTaskTest {
  @Rule public TemporaryFolder tmp = new TemporaryFolder();

  @Test
  public void testBuildCommand() {
    Project project = ProjectBuilder.builder().withProjectDir(tmp.getRoot()).build();
    MinikubeTask testTask =
        project
            .getTasks()
            .create(
                "minikubeTestTask",
                MinikubeTask.class,
                minikubeTask -> {
                  minikubeTask.setMinikube("/test/path/to/minikube");
                  minikubeTask.setCommand("testCommand");
                  minikubeTask.setFlags(new String[] {"testFlag1", "testFlag2"});
                });

    Assert.assertEquals(
        Arrays.asList("/test/path/to/minikube", "testCommand", "testFlag1", "testFlag2"),
        testTask.buildMinikubeCommand());
  }

  @Test
  public void testBuildCommand_withFilepathToMinikube() {
    Project project = ProjectBuilder.builder().withProjectDir(tmp.getRoot()).build();
    MinikubeTask testTask =
        project
            .getTasks()
            .create(
                "minikubeTestTask",
                MinikubeTask.class,
                minikubeTask -> {
                  minikubeTask.setMinikube(new File("/test/path/to/minikube"));
                  minikubeTask.setCommand("testCommand");
                  minikubeTask.setFlags(new String[] {"testFlag1", "testFlag2"});
                });

    Assert.assertEquals(
        Arrays.asList("/test/path/to/minikube", "testCommand", "testFlag1", "testFlag2"),
        testTask.buildMinikubeCommand());
  }
}
