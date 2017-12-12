package com.google.cloud.tools.crepecake.cache;

import com.google.cloud.tools.crepecake.blob.BlobDescriptor;
import com.google.cloud.tools.crepecake.image.CachedLayer;
import com.google.cloud.tools.crepecake.image.Layer;
import com.google.cloud.tools.crepecake.image.LayerException;
import com.google.cloud.tools.crepecake.image.ReferenceLayer;
import com.google.cloud.tools.crepecake.image.UnwrittenLayer;

import java.io.File;
import java.io.IOException;
import java.security.DigestException;
import java.security.NoSuchAlgorithmException;

/** Writes {@link Layer}s to the cache. */
public class CacheWriter extends CacheHelper {

  CacheWriter(Cache cache) {
    super(cache);
  }

  public void writeBaseImageLayer(UnwrittenLayer layer)  {

  }

  public void writeBaseImageLayer(ReferenceLayer layer) {

  }

  public CachedLayer writeDependenciesLayer(UnwrittenLayer layer) throws IOException, DigestException {
    writeLayer(ApplicationLayerType.DEPENDENCIES, layer);
  }

  public void writeResourcesLayer(UnwrittenLayer layer) throws IOException {
    writeLayer(ApplicationLayerType.RESOURCES, layer);
  }

  public void writeClassesLayer(UnwrittenLayer layer) throws IOException {
    writeLayer(ApplicationLayerType.CLASSES, layer);
  }

  private CachedLayer writeLayer(ApplicationLayerType layerType, UnwrittenLayer layer) throws IOException {
    CacheMetadata cacheMetadata = getMetadata();

    File layerFile = getLayerFilename(CacheMetadata.getNameForApplicationLayer(layerType));
    TimestampedCachedLayer cachedLayer = new TimestampedCachedLayer(layer.writeTo(layerFile));

    cacheMetadata.setApplicationLayer(layerType, cachedLayer);
  }

  /** Flushes any unwritten updates to disk. */
  public void flush() {

  }
}
