package com.google.cloud.tools.crepecake.cache;

import com.google.cloud.tools.crepecake.blob.BlobDescriptor;
import com.google.cloud.tools.crepecake.image.DescriptorDigest;

import java.io.File;
import java.io.IOException;

abstract class CacheHelper {

  private final Cache cache;

  CacheHelper(Cache cache) {
    this.cache = cache;
  }

  final CacheMetadata getMetadata() throws IOException {
    return cache.loadMetadata();
  }

  final File getLayerFilename(String layerName) {
    return CacheMetadata.getLayerFilename(cache, layerName);
  }
}
