package com.google.cloud.tools.crepecake.cache;

import com.google.cloud.tools.crepecake.image.CachedLayer;
import com.google.cloud.tools.crepecake.image.Layer;
import com.google.cloud.tools.crepecake.image.ReferenceLayer;
import com.google.cloud.tools.crepecake.image.UnwrittenLayer;
import java.io.File;
import java.io.IOException;

/** Writes {@link Layer}s to the cache. */
public class CacheWriter extends CacheHelper {

  CacheWriter(Cache cache) {
    super(cache);
  }

  public void writeBaseImageLayer(UnwrittenLayer layer) {}

  public void writeBaseImageLayer(ReferenceLayer layer) {}

  public CachedLayer writeDependenciesLayer(UnwrittenLayer layer) throws IOException {
    return writeLayer(ApplicationLayerType.DEPENDENCIES, layer);
  }

  public CachedLayer writeResourcesLayer(UnwrittenLayer layer) throws IOException {
    return writeLayer(ApplicationLayerType.RESOURCES, layer);
  }

  public CachedLayer writeClassesLayer(UnwrittenLayer layer) throws IOException {
    return writeLayer(ApplicationLayerType.CLASSES, layer);
  }

  private CachedLayer writeLayer(ApplicationLayerType layerType, UnwrittenLayer layer)
      throws IOException {
    File layerFile = getLayerFilename(CacheMetadata.getNameForApplicationLayer(layerType));

    return new TimestampedCachedLayer(layer.writeTo(layerFile));
  }
}
