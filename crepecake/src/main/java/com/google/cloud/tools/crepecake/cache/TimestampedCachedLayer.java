package com.google.cloud.tools.crepecake.cache;

import com.google.cloud.tools.crepecake.image.CachedLayer;

class TimestampedCachedLayer extends CachedLayer {

  private final long lastModifiedTime;

  TimestampedCachedLayer(CachedLayer cachedLayer, long lastModifiedTime) {
    super(
        cachedLayer.getContentTarFile(), cachedLayer.getBlobDescriptor(), cachedLayer.getDiffId());
    this.lastModifiedTime = lastModifiedTime;
  }

  long getLastModifiedTime() {
    return lastModifiedTime;
  }
}
