package com.google.cloud.tools.minikube.gradle;

import org.gradle.api.Project;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/** Tests for MinikubePlugin */
public class MinikubePluginTest {
  @Rule public TemporaryFolder tmp = new TemporaryFolder();

  @Test
  public void testDefaultMinikubeTasks() {
    Project project = ProjectBuilder.builder().withProjectDir(tmp.getRoot()).build();
    project.getPluginManager().apply(MinikubePlugin.class);
    ((ProjectInternal) project).evaluate();

    TaskContainer t = project.getTasks();
    TaskCollection<MinikubeTask> tc = t.withType(MinikubeTask.class);

    Assert.assertEquals(2, tc.size());

    MinikubeTask minikubeStart = tc.getByName("minikubeStart");
    Assert.assertEquals(minikubeStart.getMinikube(), "minikube");
    Assert.assertEquals(minikubeStart.getCommand(), "start");
    Assert.assertArrayEquals(minikubeStart.getFlags(), new String[] {});

    MinikubeTask minikubeStop = tc.getByName("minikubeStop");
    Assert.assertEquals(minikubeStop.getMinikube(), "minikube");
    Assert.assertEquals(minikubeStop.getCommand(), "stop");
    Assert.assertArrayEquals(minikubeStop.getFlags(), new String[] {});
  }
}
