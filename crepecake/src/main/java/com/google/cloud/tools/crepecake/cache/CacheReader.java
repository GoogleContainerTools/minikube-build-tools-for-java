package com.google.cloud.tools.crepecake.cache;

import java.io.File;
import java.io.IOException;
import javax.annotation.Nullable;

public class CacheReader extends CacheHelper {

  CacheReader(Cache cache) {
    super(cache);
  }

  /** Gets the file that stores the content BLOB for the dependencies layer. */
  @Nullable
  public File getDependenciesLayerFile() throws IOException {
    return getLayerFile(ApplicationLayerType.DEPENDENCIES);
  }

  /** Gets the file that stores the content BLOB for the resources layer. */
  @Nullable
  public File getResourcesLayerFile() throws IOException {
    return getLayerFile(ApplicationLayerType.RESOURCES);
  }

  /** Gets the file that stores the content BLOB for the classes layer. */
  @Nullable
  public File getClassesLayerFile() throws IOException {
    return getLayerFile(ApplicationLayerType.CLASSES);
  }

  /** Gets the file that stores the content BLOB for an application layer. */
  private File getLayerFile(ApplicationLayerType layerType) throws IOException {
    CacheMetadata cacheMetadata = getMetadata();
    TimestampedCachedLayer dependenciesLayer = cacheMetadata.getApplicationLayer(layerType);

    if (null == dependenciesLayer) {
      return null;
    }

    return getLayerFilename(dependenciesLayer.getBlobDescriptor().getDigest().toString());
  }
}
