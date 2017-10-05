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

import java.util.Arrays;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/** Tests for DockerBuildTask */
public class DockerBuildTaskTest {

  @Rule public TemporaryFolder tmp = new TemporaryFolder();

  @Test
  public void testBuildCommand() {
    Project project = ProjectBuilder.builder().withProjectDir(tmp.getRoot()).withName("").build();
    DockerBuildTask testTask =
        project
            .getTasks()
            .create(
                "dockerBuildTestTask",
                DockerBuildTask.class,
                dockerBuildTask -> {
                  dockerBuildTask.setMinikube("/test/path/to/minikube");
                  dockerBuildTask.setDocker("/test/path/to/docker");
                  dockerBuildTask.setFlags(new String[] {"testFlag1", "testFlag2"});
                  dockerBuildTask.setContext("some_build_context");
                });

    Assert.assertEquals(
        Arrays.asList(
            "/test/path/to/docker", "build", "testFlag1", "testFlag2", "some_build_context"),
        testTask.buildDockerBuildCommand());
  }

  @Test
  public void testBuildDefaultTag() {
    Project project = ProjectBuilder.builder().withName("some.project---123").build();

    DockerBuildTask testTask =
        project.getTasks().create("dockerBuildTestTask", DockerBuildTask.class);

    // Just project name
    Assert.assertEquals("some.project---123", testTask.buildDefaultTag());

    // Project group and name
    project.setGroup("some__group");
    Assert.assertEquals("some__group/some.project---123", testTask.buildDefaultTag());

    // Project name and version
    project.setGroup(null);
    project.setVersion("_someVersion_99.99.99---");
    Assert.assertEquals("some.project---123:_someVersion_99.99.99---", testTask.buildDefaultTag());

    // Everything
    project.setGroup("some_group");
    Assert.assertEquals("some_group/some.project---123:_someVersion_99.99.99---", testTask.buildDefaultTag());
  }

  @Test
  public void testBuildDefaultTag_invalidNameComponent() {
    Project project = ProjectBuilder.builder().withName("someProjectWithSeparatorAtEnd__").build();

    DockerBuildTask testTask =
        project.getTasks().create("dockerBuildTestTask", DockerBuildTask.class);
    Logger loggerMock = mock(Logger.class);
    testTask.setLogger(loggerMock);

    Assert.assertEquals("", testTask.buildDefaultTag());
    verify(loggerMock).warn("Default image tag could not be generated because project.name is not a valid name component");
  }

  @Test
  public void testBuildDefaultTag_invalidTagName() {
    Project project = ProjectBuilder.builder().withName("someproject").build();

    DockerBuildTask testTask =
        project.getTasks().create("dockerBuildTestTask", DockerBuildTask.class);
    Logger loggerMock = mock(Logger.class);
    testTask.setLogger(loggerMock);

    project.setVersion("someVersionWithIllegalCharacter:");
    Assert.assertEquals("", testTask.buildDefaultTag());
    verify(loggerMock).warn("Default image tag could not be generated because project.version is not a valid tag name");
  }
}
