package com.google.cloud.tools.crepecake.cache;

import com.google.cloud.tools.crepecake.blob.BlobDescriptor;
import com.google.cloud.tools.crepecake.image.Image;
import com.google.cloud.tools.crepecake.image.Layer;
import com.google.cloud.tools.crepecake.image.LayerException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class CacheChecker extends CacheHelper {

  CacheChecker(Cache cache) {
    super(cache);
  }

  /**
   * @return true if the base image is different from the cached base image; false otherwise
   */
  public boolean isBaseImageModified(Image baseImage) throws IOException {
    CacheMetadata cacheMetadata = getMetadata();

    List<TimestampedCachedLayer> cachedLayers = cacheMetadata.getBaseImageLayers();
    List<Layer> baseImageLayers = baseImage.getLayers();

    if (cachedLayers.size() != baseImageLayers.size()) {
      return false;
    }

    for (int layerIndex = 0; layerIndex < baseImageLayers.size(); layerIndex ++) {
      BlobDescriptor cachedLayerBlobDescriptor = cachedLayers.get(layerIndex).getBlobDescriptor();
      BlobDescriptor baseImageLayer.getBlobDescriptor() = baseImageLayers.get(layerIndex);
      if (cachedLayer.getBlobDescriptor().equals(baseImageLayer.getBlobDescriptor())) {

      }
    }
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
