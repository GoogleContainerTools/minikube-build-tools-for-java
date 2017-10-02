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

import java.util.*;
import org.junit.Assert;
import org.junit.Test;

/** Tests for EnvironmentVariableMap */
public class EnvironmentVariableMapTest {

  @Test
  public void testCreateFromKeyValueStrings() {
    List<String> keyValueStrings =
        Arrays.asList("SOME_VARIABLE_1=SOME_VALUE_1", "SOME_VARIABLE_2=SOME_VALUE_2");
    EnvironmentVariableMap expectedEnvironment = new EnvironmentVariableMap();
    expectedEnvironment.put("SOME_VARIABLE_1", "SOME_VALUE_1");
    expectedEnvironment.put("SOME_VARIABLE_2", "SOME_VALUE_2");

    EnvironmentVariableMap environment =
        EnvironmentVariableMap.createFromKeyValueStrings(keyValueStrings);

    Assert.assertEquals(expectedEnvironment, environment);
  }

  @Test
  public void testPutKeyValueString_success() {
    EnvironmentVariableMap expectedEnvironment = new EnvironmentVariableMap();
    expectedEnvironment.put("SOME_VARIABLE_1", "SOME_VALUE_1");
    expectedEnvironment.put("SOME_VARIABLE_2", "SOME_VALUE_2");

    EnvironmentVariableMap environment = new EnvironmentVariableMap();
    environment.putKeyValueString("SOME_VARIABLE_1=SOME_VALUE_1");
    environment.putKeyValueString("SOME_VARIABLE_2=SOME_VALUE_2");

    Assert.assertEquals(expectedEnvironment, environment);
  }

  @Test
  public void testPutKeyValueString_variableNameEmpty() {
    try {
      EnvironmentVariableMap environment = new EnvironmentVariableMap();
      environment.putKeyValueString("=SOME_VALUE");
      Assert.fail("Expected a EnvironmentVariableMap.KeyValueStringNoKeyException to be thrown");
    } catch (RuntimeException ex) {
      Assert.assertEquals("Environment variable name cannot be empty", ex.getMessage());
    }
  }

  @Test
  public void testPutKeyValueString_invalidFormat() {
    try {
      EnvironmentVariableMap environment = new EnvironmentVariableMap();
      environment.putKeyValueString("SOME_VARIABLE_WITHOUT_EQUALS");
      Assert.fail("Expected a EnvironmentVariableMap.KeyValueStringInvalidFormatException to be thrown");
    } catch (RuntimeException ex) {
      Assert.assertEquals(
          "Environment variable string must be in KEY=VALUE format", ex.getMessage());
    }
  }
}
