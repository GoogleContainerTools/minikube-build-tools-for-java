package com.google.cloud.tools.crepecake.cache;

import com.google.cloud.tools.crepecake.image.Image;

import java.io.File;
import java.util.List;
import java.util.Set;

public class CacheChecker extends CacheHelper {

  CacheChecker(Cache cache) {
    super(cache);
  }

  /**
   * @return true if the base image is different from the cached base image; false otherwise
   */
  public boolean isBaseImageModified(Image baseImage) {

  }

  /** @return true if the dependencies have been modified since last cache; false otherwise */
  public boolean isDependenciesLayerModified(BuildOutputDirectories buildOutputDirectories) {

  }

  /** @return true if the resources have been modified since last cache; false otherwise */
  public boolean isResourcesLayerModified(BuildOutputDirectories buildOutputDirectories) {

  }

  /** @return true if the classes have been modified since last cache; false otherwise */
  public boolean isClassesLayerModified(BuildOutputDirectories buildOutputDirectories) {

  }
}
