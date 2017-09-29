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

package com.google.cloud.tools.minikube.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.tools.ant.filters.StringInputStream;
import org.junit.Assert;
import org.junit.Test;

/** Tests for CommandExecutor */
public class CommandExecutorTest {

  @Test
  public void testRunCommand_success() throws IOException, InterruptedException {
    List<String> command = Arrays.asList("someCommand", "someOption");
    List<String> expectedOutput = Arrays.asList("some output line 1", "some output line 2");

    ProcessBuilder processBuilderMock = mock(ProcessBuilder.class);
    Process processMock = mock(Process.class);

    when(processBuilderMock.start()).thenReturn(processMock);
    when(processMock.getInputStream()).thenReturn(new StringInputStream(String.join("\n", expectedOutput)));

    List<String> output = new CommandExecutor().setProcessBuilder(processBuilderMock).run(command);

    verify(processBuilderMock).command(command);
    verify(processBuilderMock).redirectErrorStream(true);
    verify(processBuilderMock).start();
    verify(processMock).getInputStream();

    Assert.assertEquals(expectedOutput, output);
  }
}
