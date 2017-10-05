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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/** Tests for PrepareDockerBuildTask */
public class PrepareDockerBuildTaskTest {

  @Rule public TemporaryFolder testProjectDir = new TemporaryFolder();

  @Test
  public void testSync() throws IOException {
    Project project = ProjectBuilder.builder().withProjectDir(testProjectDir.getRoot()).build();

    Path buildLibsDir = testProjectDir.getRoot().toPath().resolve("build").resolve("libs");
    Path srcMainDockerDir =
        testProjectDir.getRoot().toPath().resolve("src").resolve("main").resolve("docker");
    Files.createDirectories(buildLibsDir);
    Files.createFile(buildLibsDir.resolve("app.jar"));
    Files.createDirectories(srcMainDockerDir);
    Files.createFile(srcMainDockerDir.resolve("Dockerfile"));

    project.getTasks().create("prepareDockerBuildTestTask", PrepareDockerBuildTask.class).execute();

    Path destinationDir = project.getBuildDir().toPath().resolve("docker");
    Assert.assertTrue(Files.isRegularFile(destinationDir.resolve("app.jar")));
    Assert.assertTrue(Files.isRegularFile(destinationDir.resolve("Dockerfile")));
  }
}
