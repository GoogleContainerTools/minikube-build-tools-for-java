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

package com.google.cloud.tools.crepecake.cache;

import java.io.File;
import java.nio.file.Path;

/** Methods for getting static cache properties. */
class CacheHelper {

  private static final String LAYER_FILE_EXTENSION = ".tar.gz";
  private static final String DEPENDENCIES_LAYER_FILENAME = "dependencies";
  private static final String RESOURCES_LAYER_FILENAME = "resources";
  private static final String CLASSES_LAYER_FILENAME = "classes";

  static File getLayerFile(Path cacheDirectory, String layerName) {
    return cacheDirectory.resolve(layerName + LAYER_FILE_EXTENSION).toFile();
  }

  static String getNameForApplicationLayer(ApplicationLayerType layerType) {
    switch (layerType) {
      case DEPENDENCIES:
        return DEPENDENCIES_LAYER_FILENAME;
      case RESOURCES:
        return RESOURCES_LAYER_FILENAME;
      case CLASSES:
        return CLASSES_LAYER_FILENAME;
    }
    throw new IllegalStateException("Should never reach here - switch above is exhaustive");
  }

  private CacheHelper() {}
}
