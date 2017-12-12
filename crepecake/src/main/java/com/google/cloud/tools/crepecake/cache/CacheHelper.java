package com.google.cloud.tools.crepecake.cache;

import java.io.File;
import java.nio.file.Path;

/** Methods for getting static cache properties. */
abstract class CacheHelper {

  private static final String DEPENDENCIES_LAYER_NAME = "dependencies";
  private static final String RESOURCES_LAYER_NAME = "resources";
  private static final String CLASSES_LAYER_NAME = "classes";

  static File getLayerFilename(Path cacheDirectory, String layerName) {
    return cacheDirectory.resolve(layerName + ".tar").toFile();
  }

  static String getNameForApplicationLayer(ApplicationLayerType layerType) {
    switch (layerType) {
      case DEPENDENCIES:
        return DEPENDENCIES_LAYER_NAME;
      case RESOURCES:
        return RESOURCES_LAYER_NAME;
      case CLASSES:
        return CLASSES_LAYER_NAME;
    }
    throw new IllegalStateException("Should never reach here - switch above is exhaustive");
  }
}
