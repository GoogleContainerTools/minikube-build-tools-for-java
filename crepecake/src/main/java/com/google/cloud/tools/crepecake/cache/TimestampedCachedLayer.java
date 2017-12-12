package com.google.cloud.tools.crepecake.cache;

class TimestampedCachedLayer extends CachedLayer {

  private final long lastModifiedTime;

  TimestampedCachedLayer(CachedLayer cachedLayer) {
    this(cachedLayer, System.currentTimeMillis());
  }

  TimestampedCachedLayer(CachedLayer cachedLayer, long lastModifiedTime) {
    super(
        cachedLayer.getContentTarFile(), cachedLayer.getBlobDescriptor(), cachedLayer.getDiffId());
    this.lastModifiedTime = lastModifiedTime;
  }

  long getLastModifiedTime() {
    return lastModifiedTime;
  }
}
