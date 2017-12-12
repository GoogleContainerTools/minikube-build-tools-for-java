package com.google.cloud.tools.crepecake.cache;

import com.google.cloud.tools.crepecake.blob.BlobDescriptor;
import com.google.cloud.tools.crepecake.image.CachedLayer;
import com.google.cloud.tools.crepecake.image.DescriptorDigest;
import com.google.cloud.tools.crepecake.json.templates.CacheMetadataTemplate;

import java.io.File;

class TimestampedCachedLayer extends CachedLayer {

  private final long lastModifiedTime;

  static TimestampedCachedLayer fromTemplate(File contentTarFile, CacheMetadataTemplate.LayerObjectTemplate layerObjectTemplate) {
    CachedLayer cachedLayer = new CachedLayer(contentTarFile, new BlobDescriptor(layerObjectTemplate.getSize(), layerObjectTemplate.getDigest()), layerObjectTemplate.getDiffId());
    return new TimestampedCachedLayer(cachedLayer, layerObjectTemplate.getLastModifiedTime());
  }

  TimestampedCachedLayer(CachedLayer cachedLayer) {
    this(cachedLayer, System.nanoTime());
  }

  TimestampedCachedLayer(CachedLayer cachedLayer, long lastModifiedTime) {
    super(cachedLayer.getContentTarFile(), cachedLayer.getBlobDescriptor(), cachedLayer.getDiffId());
    this.lastModifiedTime = lastModifiedTime;
  }

  long getLastModifiedTime() {
    return lastModifiedTime;
  }
}
