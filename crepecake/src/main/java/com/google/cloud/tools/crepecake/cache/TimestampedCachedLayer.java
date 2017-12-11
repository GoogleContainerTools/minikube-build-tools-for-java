package com.google.cloud.tools.crepecake.cache;

import com.google.cloud.tools.crepecake.blob.BlobDescriptor;
import com.google.cloud.tools.crepecake.image.CachedLayer;
import com.google.cloud.tools.crepecake.image.DescriptorDigest;
import com.google.cloud.tools.crepecake.json.templates.CacheMetadataTemplate;

import java.io.File;

class TimestampedCachedLayer extends CachedLayer {

  private final long lastModifiedTime;

  static TimestampedCachedLayer from(CacheMetadataTemplate.LayerObjectTemplate layerObjectTemplate) {
    return new TimestampedCachedLayer(new BlobDescriptor(layerObjectTemplate.getSize(), layerObjectTemplate.getDigest()), layerObjectTemplate.getDiffId(), layerObjectTemplate.getLastModifiedTime());
  }

  private TimestampedCachedLayer(File file, BlobDescriptor blobDescriptor, DescriptorDigest diffId, long lastModifiedTime) {
    super(file, blobDescriptor, diffId);
    this.lastModifiedTime = lastModifiedTime;
  }

  long getLastModifiedTime() {
    return lastModifiedTime;
  }
}
