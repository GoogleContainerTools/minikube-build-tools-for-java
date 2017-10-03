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

import java.util.HashMap;
import java.util.List;

/** Represents an environment variable. */
public class EnvironmentVariableMap extends HashMap<String, String> {

  /**
   * Exception indicating a KEY=VALUE string to represent an environment variable is not in the
   * correct format.
   */
  public static class KeyValueStringInvalidFormatException extends RuntimeException {
    private static final String MESSAGE = "Environment variable string must be in KEY=VALUE format";

    KeyValueStringInvalidFormatException() {
      super(MESSAGE);
    }
  }

  /**
   * Exception indicating a KEY=VALUE string to represent an environment variable does not have a
   * KEY.
   */
  public static class KeyValueStringNoKeyException extends RuntimeException {
    private static final String MESSAGE = "Environment variable name cannot be empty";

    KeyValueStringNoKeyException() {
      super(MESSAGE);
    }
  }

  /**
   * Creates a new {@code EnvironmentVariableMap} from a list of KEY=VALUE strings.
   *
   * @param keyValueStrings a list of KEY=VALUE string, where KEY is the environment variable name
   *     and VALUE is the value to set it to
   */
  public static EnvironmentVariableMap createFromKeyValueStrings(List<String> keyValueStrings)
      throws KeyValueStringInvalidFormatException, KeyValueStringNoKeyException {
    EnvironmentVariableMap environmentVariableMap = new EnvironmentVariableMap();

    for (String keyValueString : keyValueStrings) {
      environmentVariableMap.putKeyValueString(keyValueString);
    }

    return environmentVariableMap;
  }

  /**
   * Puts a KEY=VALUE string as an environment variable entry into the map.
   *
   * @param keyEqualsValue KEY=VALUE string, where KEY is the environment variable name and VALUE is
   *     the value to set it to
   * @throws KeyValueStringInvalidFormatException if the key-value string is not in the correct
   *     format
   * @throws KeyValueStringNoKeyException if the key-value string is missing a key
   */
  public String putKeyValueString(String keyEqualsValue)
      throws KeyValueStringInvalidFormatException, KeyValueStringNoKeyException {
    String[] keyValuePair = keyEqualsValue.split("=", 2);
    if (keyValuePair.length < 2) {
      throw new KeyValueStringInvalidFormatException();
    }
    if (keyValuePair[0].length() == 0) {
      throw new KeyValueStringNoKeyException();
    }
    return put(keyValuePair[0], keyValuePair[1]);
  }
}
