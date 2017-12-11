package com.google.cloud.tools.crepecake.cache;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;

public class CacheReader extends CacheHelper {

  CacheReader(Cache cache) {
    super(cache);
  }

  /** Gets the file that stores the content BLOB for the dependencies layer. */
  @Nullable
  public File getDependenciesLayerFile() throws IOException {
    return getLayerFile(CacheMetadata::getDependenciesLayer);
  }

  /** Gets the file that stores the content BLOB for the resources layer. */
  @Nullable
  public File getResourcesLayerFile() throws IOException {
    return getLayerFile(CacheMetadata::getResourcesLayer);
  }

  /** Gets the file that stores the content BLOB for the classes layer. */
  @Nullable
  public File getClassesLayerFile() throws IOException {
    return getLayerFile(CacheMetadata::getClassesLayer);
  }

  /**
   * Gets the file that stores the content BLOB for a layer in the {@link CacheMetadata}.
   *
   * @param getLayerFunction a function that retrieves a layer from a {@link CacheMetadata}
   */
  private File getLayerFile(Function<CacheMetadata, TimestampedCachedLayer> getLayerFunction) throws IOException {
    CacheMetadata cacheMetadata = getMetadata();
    TimestampedCachedLayer dependenciesLayer = getLayerFunction.apply(cacheMetadata);

    if (null == dependenciesLayer) {
      return null;
    }

    return getLayerFilename(dependenciesLayer.getBlobDescriptor().getDigest().toString());
  }
}
