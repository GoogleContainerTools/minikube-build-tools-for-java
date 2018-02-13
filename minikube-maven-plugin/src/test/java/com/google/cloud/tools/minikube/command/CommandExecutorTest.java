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

package com.google.cloud.tools.minikube.command;

import com.google.common.collect.ImmutableMap;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/** Tests for {@link CommandExecutor}. */
@RunWith(MockitoJUnitRunner.class)
public class CommandExecutorTest {

  private final List<String> command = Arrays.asList("someCommand", "someOption");
  private final List<String> expectedOutput =
      Arrays.asList("some output line 1", "some output line 2");

  @Mock private ProcessBuilder mockProcessBuilder;
  @Mock private Process mockProcess;
  @Mock private BuildLogger mockBuildLogger;

  private CommandExecutor testCommandExecutor;
  private InOrder loggerInOrder;

  @Before
  public void setUp() throws IOException {
    Mockito.when(mockProcessBuilder.start()).thenReturn(mockProcess);

    testCommandExecutor = new CommandExecutor().setProcessBuilderSupplier(() -> mockProcessBuilder);

    loggerInOrder = Mockito.inOrder(mockBuildLogger);
  }

  @Test
  public void testRun_success() throws IOException, InterruptedException {
    setMockProcessOutput(expectedOutput);

    // Executes the command.
    List<String> output = testCommandExecutor.run(command);

    verifyProcessBuilding(command);
    Assert.assertEquals(expectedOutput, output);

    Mockito.verifyZeroInteractions(mockBuildLogger);
  }

  @Test
  public void testRun_withEnvironmentVariables() throws IOException, InterruptedException {
    Map<String, String> expectedEnvironmentMap =
        ImmutableMap.of("SOME_VARIABLE_1", "SOME_VALUE_1", "SOME_VARIABLE_2", "SOME_VALUE_2");

    Map<String, String> processBuilderEnvironmentMap = new HashMap<>();
    Mockito.when(mockProcessBuilder.environment()).thenReturn(processBuilderEnvironmentMap);

    setMockProcessOutput(expectedOutput);

    List<String> output = testCommandExecutor.setEnvironment(expectedEnvironmentMap).run(command);

    verifyProcessBuilding(command);
    Assert.assertEquals(expectedEnvironmentMap, processBuilderEnvironmentMap);
    Assert.assertEquals(expectedOutput, output);

    Mockito.verifyZeroInteractions(mockBuildLogger);
  }

  @Test
  public void testRun_withLogging_success() throws IOException, InterruptedException {
    setMockProcessOutput(expectedOutput);

    List<String> output = testCommandExecutor.setLogger(mockBuildLogger).run(command);

    verifyProcessBuilding(command);
    Assert.assertEquals(expectedOutput, output);

    // Verifies the logger messages were logged.
    loggerInOrder.verify(mockBuildLogger).debug("Running command : someCommand someOption");
    loggerInOrder.verify(mockBuildLogger).lifecycle("some output line 1");
    loggerInOrder.verify(mockBuildLogger).lifecycle("some output line 2");
  }

  @Test
  public void testRun_withLogging_badProcessOutput() throws IOException, InterruptedException {
    InputStream errorInputStream =
        new InputStream() {

          @Override
          public int read() throws IOException {
            throw new IOException();
          }
        };
    Mockito.when(mockProcess.getInputStream()).thenReturn(errorInputStream);

    testCommandExecutor.setLogger(mockBuildLogger).run(command);

    loggerInOrder.verify(mockBuildLogger).debug("Running command : someCommand someOption");
    loggerInOrder.verify(mockBuildLogger).warn("IO Exception reading process output");
  }

  @Test
  public void testRun_withLogging_commandTimeout() throws InterruptedException, IOException {
    // Mocks the ExecutorService to be interrupted when awaiting termination.
    ExecutorService mockExecutorService = Mockito.mock(ExecutorService.class);
    Mockito.when(
            mockExecutorService.awaitTermination(CommandExecutor.TIMEOUT_SECONDS, TimeUnit.SECONDS))
        .thenThrow(new InterruptedException());

    testCommandExecutor
        .setExecutorServiceSupplier(() -> mockExecutorService)
        .setLogger(mockBuildLogger)
        .run(command);

    loggerInOrder.verify(mockBuildLogger).debug("Running command : someCommand someOption");
    loggerInOrder
        .verify(mockBuildLogger)
        .debug("Task Executor interrupted waiting for output consumer thread");
  }

  @Test
  public void testRun_commandError() throws InterruptedException, IOException {
    setMockProcessOutput(expectedOutput);

    Mockito.when(mockProcess.waitFor()).thenReturn(1);

    try {
      testCommandExecutor.run(command);
      Assert.fail("Expected an IOException to be thrown");

    } catch (IOException ex) {
      Assert.assertEquals("command exited with non-zero exit code : 1", ex.getMessage());

      verifyProcessBuilding(command);
      Mockito.verifyZeroInteractions(mockBuildLogger);
    }
  }

  /** Has the mocked process output the expected output. */
  private void setMockProcessOutput(List<String> expectedOutput) {
    Mockito.when(mockProcess.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(
                String.join("\n", expectedOutput).getBytes(StandardCharsets.UTF_8)));
  }

  /** Verifies that the process building and output reading is correct. */
  private void verifyProcessBuilding(List<String> command) throws IOException {
    Mockito.verify(mockProcessBuilder).command(command);
    Mockito.verify(mockProcessBuilder).redirectErrorStream(true);
    Mockito.verify(mockProcessBuilder).start();

    Mockito.verify(mockProcess).getInputStream();
  }
}
