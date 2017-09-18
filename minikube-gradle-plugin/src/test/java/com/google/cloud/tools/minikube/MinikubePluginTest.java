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

package com.google.cloud.tools.minikube;

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

    Assert.assertEquals(3, tc.size());

    AssertMinikubeTaskConfig(tc, "minikubeStart", "start");
    AssertMinikubeTaskConfig(tc, "minikubeStop", "stop");
    AssertMinikubeTaskConfig(tc, "minikubeDelete", "delete");
  }

  private void AssertMinikubeTaskConfig(
      TaskCollection<MinikubeTask> tc, String taskName, String taskCommand) {
    MinikubeTask minikubeTask = tc.getByName(taskName);
    Assert.assertEquals(minikubeTask.getMinikube(), "minikube");
    Assert.assertEquals(minikubeTask.getCommand(), taskCommand);
    Assert.assertArrayEquals(minikubeTask.getFlags(), new String[] {});
  }
}
