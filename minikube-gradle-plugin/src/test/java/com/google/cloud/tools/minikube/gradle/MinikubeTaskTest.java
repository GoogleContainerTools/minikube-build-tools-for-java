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

package com.google.cloud.tools.minikube.gradle;

import java.io.File;
import java.util.Arrays;
import org.apache.commons.lang3.SystemUtils;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/** Tests for MinikubeTask */
public class MinikubeTaskTest {
  @Rule public TemporaryFolder tmp = new TemporaryFolder();

  private MinikubeTask createTestTask(Action<MinikubeTask> taskConfigPassthrough) {
    Project project = ProjectBuilder.builder().withProjectDir(tmp.getRoot()).build();
    return project.getTasks().create("minikubeTestTask", MinikubeTask.class, taskConfigPassthrough);
  }

  @Test
  public void testBuildCommand() {
    MinikubeTask testTask =
        createTestTask(
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
    MinikubeTask testTask =
        createTestTask(
            minikubeTask -> {
              minikubeTask.setMinikube(new File("/test/path/to/minikube"));
              minikubeTask.setCommand("testCommand");
              minikubeTask.setFlags(new String[] {"testFlag1", "testFlag2"});
            });

    Assert.assertEquals(
        Arrays.asList("/test/path/to/minikube", "testCommand", "testFlag1", "testFlag2"),
        testTask.buildMinikubeCommand());
  }

  @Test
  public void testMinikubeTask_defaultMinikubeWindows() {
    // windows only test
    Assume.assumeTrue(SystemUtils.IS_OS_WINDOWS);

    MinikubeTask testTask = createTestTask(noop -> {});
    Assert.assertEquals(testTask.getMinikube(), "minikube.exe");
  }

  @Test
  public void testMinikubeTask_defaultMinikubeNix() {
    // *nix only test
    Assume.assumeFalse(SystemUtils.IS_OS_WINDOWS);

    MinikubeTask testTask = createTestTask(noop -> {});
    Assert.assertEquals(testTask.getMinikube(), "minikube");
  }
}
