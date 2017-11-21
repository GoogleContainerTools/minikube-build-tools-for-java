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

import com.google.cloud.tools.crepecake.image.Digest;
import com.google.cloud.tools.crepecake.image.DigestException;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

/** Type adapter for deserializing a JSON element into a {@link Digest}. */
public class DigestDeserializer implements JsonDeserializer<Digest> {

  @Override
  public Digest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    try {
      return Digest.fromDigest(json.getAsJsonPrimitive().getAsString());
    } catch (DigestException ex) {
      throw new JsonParseException(ex);
    }
  }
}
