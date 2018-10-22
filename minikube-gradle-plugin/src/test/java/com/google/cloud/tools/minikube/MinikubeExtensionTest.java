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

import static org.mockito.Mockito.*;

import com.google.cloud.tools.minikube.util.CommandExecutor;
import com.google.cloud.tools.minikube.util.CommandExecutorFactory;
import java.io.IOException;
import java.util.*;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/** Tests for MinikubeExtension */
public class MinikubeExtensionTest {

  private CommandExecutor commandExecutorMock;
  private CommandExecutorFactory commandExecutorFactoryMock;
  private MinikubeExtension minikube;

  List<String> expectedCommand;
  private static final List<String> dockerEnvOutput =
      Arrays.asList("ENV_VAR1=VAL1", "ENV_VAR2=VAL2");
  private static final Map<String, String> expectedMap;

  static {
    Map<String, String> map = new HashMap<>(2);
    map.put("ENV_VAR1", "VAL1");
    map.put("ENV_VAR2", "VAL2");
    expectedMap = Collections.unmodifiableMap(map);
  }

  /*
   * Reinitialise variables before each test.
   */
  @Before
  public void setUp() {
    Project project = ProjectBuilder.builder().build();

    // Mocks the CommandExecutor.
    commandExecutorMock = mock(CommandExecutor.class);
    commandExecutorFactoryMock = mock(CommandExecutorFactory.class);
    when(commandExecutorFactoryMock.newCommandExecutor()).thenReturn(commandExecutorMock);

    // Creates an extension to test on.
    minikube = new MinikubeExtension(project, commandExecutorFactoryMock);
    minikube.setMinikube("/test/path/to/minikube");

    expectedCommand =
        new LinkedList<>(Arrays.asList("/test/path/to/minikube", "docker-env", "--shell=none"));
  }

  /*
   * Test with default minikube profile
   */
  @Test
  public void testGetDockerEnvWithDefaultProfile() throws IOException, InterruptedException {
    expectedCommand.add("--profile=");
    when(commandExecutorMock.run(expectedCommand)).thenReturn(dockerEnvOutput);
    Assert.assertEquals(expectedMap, minikube.getDockerEnv());
    verify(commandExecutorMock).run(expectedCommand);
  }

  /*
   * Test with 'testProfile'
   */
  @Test
  public void testGetDockerEnvWithTestProfile() throws IOException, InterruptedException {
    String profile = "testProfile";
    expectedCommand.add("--profile=".concat(profile));
    when(commandExecutorMock.run(expectedCommand)).thenReturn(dockerEnvOutput);
    Assert.assertEquals(expectedMap, minikube.getDockerEnv(profile));
    verify(commandExecutorMock).run(expectedCommand);
  }

  /*
   * Make sure both minikube.getDockerEnv() and minikube.getDockerEnv("") refer to the default minikube profile
   */
  @Test
  public void testGetSameDockerEnvWithTwoDefaultProfiles()
      throws IOException, InterruptedException {
    String profile = "";
    expectedCommand.add("--profile=".concat(profile));
    when(commandExecutorMock.run(expectedCommand)).thenReturn(dockerEnvOutput);
    Assert.assertEquals(minikube.getDockerEnv(), minikube.getDockerEnv(profile));
    verify(commandExecutorMock, times(2)).run(expectedCommand);
  }

  /*
   * getDockerEnv() should not permit null values
   */
  @Test(expected = NullPointerException.class)
  public void testGetDockerEnvWithNullProfile() throws IOException, InterruptedException {
    minikube.getDockerEnv(null);
  }
}
