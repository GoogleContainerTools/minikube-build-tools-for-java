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

package com.google.cloud.tools.crepecake.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

// TODO: Add JsonFactory for HTTP response parsing.
/** Interface to a JSON parser. */
public class JsonParser {

  private static final ObjectMapper objectMapper;

  static {
    objectMapper = new ObjectMapper();
  }

  /**
   * Writes a JSON object to an {@link OutputStream}.
   *
   * @param outputStream the {@link OutputStream} to write to
   * @param jsonObject the
   * @throws IOException
   */
  public static void writeJson(OutputStream outputStream, Serializable jsonObject)
      throws IOException {
    objectMapper.writeValue(outputStream, jsonObject);
  }

  /**
   * Deserializes a JSON file via a JSON object template.
   *
   * @param jsonFile a file containing a JSON string
   * @param templateClass the template to deserialize the string to
   * @return the template filled with the values parsed from {@param jsonFile}
   * @throws IOException if an error occurred during reading the file or parsing the JSON
   */
  public static <T extends Deserializable> T readJsonFromFile(File jsonFile, Class<T> templateClass)
      throws IOException {
    return objectMapper.readValue(jsonFile, templateClass);
  }
}
