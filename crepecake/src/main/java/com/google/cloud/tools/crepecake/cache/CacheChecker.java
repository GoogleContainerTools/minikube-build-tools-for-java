package com.google.cloud.tools.crepecake.cache;

import com.google.cloud.tools.crepecake.blob.BlobDescriptor;
import com.google.cloud.tools.crepecake.image.Image;
import com.google.cloud.tools.crepecake.image.ReferenceLayer;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class CacheChecker extends CacheHelper {

  CacheChecker(Cache cache) {
    super(cache);
  }

  /** @return true if the base image is different from the cached base image; false otherwise */
  public boolean isBaseImageModified(Image<ReferenceLayer> baseImage) throws IOException {
    CacheMetadata cacheMetadata = getMetadata();

    List<TimestampedCachedLayer> cachedLayers = cacheMetadata.getBaseImageLayers();
    List<ReferenceLayer> baseImageLayers = baseImage.getLayers();

    if (cachedLayers.size() != baseImageLayers.size()) {
      return false;
    }

    for (int layerIndex = 0; layerIndex < baseImageLayers.size(); layerIndex++) {
      BlobDescriptor cachedLayerBlobDescriptor = cachedLayers.get(layerIndex).getBlobDescriptor();
      BlobDescriptor baseImageBlobDescriptor = baseImageLayers.get(layerIndex).getBlobDescriptor();
      if (!cachedLayerBlobDescriptor.equals(baseImageBlobDescriptor)) {
        return false;
      }
    }
    return true;
  }

  /** @return true if the dependencies have been modified since last cache; false otherwise */
  public boolean isDependenciesLayerModified(BuildOutputDirectories buildOutputDirectories)
      throws IOException {
    return isLayerModified(ApplicationLayerType.DEPENDENCIES, buildOutputDirectories);
  }

  /** @return true if the resources have been modified since last cache; false otherwise */
  public boolean isResourcesLayerModified(BuildOutputDirectories buildOutputDirectories)
      throws IOException {
    return isLayerModified(ApplicationLayerType.RESOURCES, buildOutputDirectories);
  }

  /** @return true if the classes have been modified since last cache; false otherwise */
  public boolean isClassesLayerModified(BuildOutputDirectories buildOutputDirectories)
      throws IOException {
    return isLayerModified(ApplicationLayerType.CLASSES, buildOutputDirectories);
  }

  private boolean isLayerModified(
      ApplicationLayerType layerType, BuildOutputDirectories buildOutputDirectories)
      throws IOException {
    CacheMetadata cacheMetadata = getMetadata();
    TimestampedCachedLayer cachedLayer =
        cacheMetadata.getApplicationLayer(ApplicationLayerType.CLASSES);

    // Returns true if there is no cached layer.
    if (null == cachedLayer) {
      return true;
    }

    long lastModifiedTime = cachedLayer.getLastModifiedTime();

    switch (layerType) {
      case DEPENDENCIES:
        Set<File> filesToCheck = buildOutputDirectories.getDependencies();
        for (File file : filesToCheck) {
          if (isFileModifiedRecursive(file, lastModifiedTime)) {
            return true;
          }
        }
        return false;

      case RESOURCES:
        return isFileModifiedRecursive(
            buildOutputDirectories.getResourcesDirectory(), lastModifiedTime);

      case CLASSES:
        return isFileModifiedRecursive(
            buildOutputDirectories.getClassesDirectory(), lastModifiedTime);
    }
    throw new IllegalStateException("Should never reach here - switch above is exhaustive");
  }

  /**
   * Checks the file has been modified since the {@code lastModifiedTime}. Recursively checks all
   * subfiles if {@code file} is a directory.
   */
  private boolean isFileModifiedRecursive(File file, long lastModifiedTime) throws IOException {
    if (file.lastModified() > lastModifiedTime) {
      return true;
    }

    if (file.isDirectory()) {
      File[] subFiles = file.listFiles();
      if (null == subFiles) {
        throw new IOException("Failed to read directory: " + file.getAbsolutePath());
      }
      for (final File subFile : subFiles) {
        if (isFileModifiedRecursive(subFile, lastModifiedTime)) {
          return true;
        }
      }
    }

    return false;
  }
}
