/*
 * Copyright 2018 Google Inc.
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

package com.google.cloud.tools.minikube.maven;

import com.google.cloud.tools.minikube.command.CommandExecutor;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

/** Tests for {@link AbstractMinikubeMojo}. */
@RunWith(MockitoJUnitRunner.class)
public class AbstractMinikubeMojoTest {

  @Spy private AbstractMinikubeMojo spyAbstractMinikubeMojo;

  @Mock private CommandExecutor mockCommandExecutor;
  @Mock private MavenBuildLogger mockMavenBuildLogger;

  @Before
  public void setUp() {
    Mockito.when(mockCommandExecutor.setLogger(Mockito.any(MavenBuildLogger.class)))
        .thenReturn(mockCommandExecutor);
  }

  @Test
  public void testBuildMinikubeCommand() {
    spyAbstractMinikubeMojo.setMinikube("path/to/minikube");
    spyAbstractMinikubeMojo.setFlags(ImmutableList.of("someFlag1", "someFlag2"));

    Mockito.when(spyAbstractMinikubeMojo.getCommand()).thenReturn("somecommand");
    Mockito.when(spyAbstractMinikubeMojo.getMoreFlags())
        .thenReturn(ImmutableList.of("moreFlag1", "moreFlag2"));

    Assert.assertEquals(
        Arrays.asList(
            "path/to/minikube", "somecommand", "someFlag1", "someFlag2", "moreFlag1", "moreFlag2"),
        spyAbstractMinikubeMojo.buildMinikubeCommand());
  }

  @Test
  public void testExecute() throws IOException, MojoExecutionException, InterruptedException {
    List<String> minikubeCommand = Arrays.asList("some", "command");
    Mockito.doReturn(minikubeCommand).when(spyAbstractMinikubeMojo).buildMinikubeCommand();

    spyAbstractMinikubeMojo.setCommandExecutorSupplier(() -> mockCommandExecutor);
    spyAbstractMinikubeMojo.setMavenBuildLogger(mockMavenBuildLogger);

    spyAbstractMinikubeMojo.execute();

    Mockito.verify(mockCommandExecutor).setLogger(mockMavenBuildLogger);
    Mockito.verify(mockCommandExecutor).run(minikubeCommand);
  }

  @Test
  public void testExecute_fail() throws IOException, InterruptedException {
    List<String> minikubeCommand = Arrays.asList("some", "command");
    Mockito.doReturn(minikubeCommand).when(spyAbstractMinikubeMojo).buildMinikubeCommand();

    String description = "some description";
    Mockito.when(spyAbstractMinikubeMojo.getDescription()).thenReturn(description);

    spyAbstractMinikubeMojo.setCommandExecutorSupplier(() -> mockCommandExecutor);
    IOException expectedIOException = new IOException();
    Mockito.doThrow(expectedIOException).when(mockCommandExecutor).run(minikubeCommand);

    try {
      spyAbstractMinikubeMojo.execute();
      Assert.fail("execute() should have failed");

    } catch (MojoExecutionException ex) {
      Assert.assertEquals(description + " failed", ex.getMessage());
      Assert.assertEquals(expectedIOException, ex.getCause());
    }
  }
}
