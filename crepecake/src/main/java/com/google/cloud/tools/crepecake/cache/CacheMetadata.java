package com.google.cloud.tools.crepecake.cache;

import com.google.cloud.tools.crepecake.json.templates.CacheMetadataTemplate;

import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

class CacheMetadata {

  private final List<TimestampedCachedLayer> baseImageLayers = new ArrayList<>();

  @Nullable
  private TimestampedCachedLayer dependenciesLayer;
  @Nullable
  private TimestampedCachedLayer resourcesLayer;
  @Nullable
  private TimestampedCachedLayer classesLayer;

  static File getLayerFilename(Path cacheDirectory, String layerName) {
    return cacheDirectory.resolve(layerName + ".tar.gz").toFile();
  }

  static CacheMetadata from(CacheMetadataTemplate template) {
    CacheMetadata cacheMetadata = new CacheMetadata();

    for (CacheMetadataTemplate.LayerObjectTemplate baseImageLayerTemplate : template.getBaseImageLayers()) {
      TimestampedCachedLayer timestampedCachedLayer = TimestampedCachedLayer.from(baseImageLayerTemplate);
      cacheMetadata.baseImageLayers.add(timestampedCachedLayer);
    }

    cacheMetadata.dependenciesLayer = TimestampedCachedLayer.from(template.getDependenciesLayer());
    cacheMetadata.resourcesLayer = TimestampedCachedLayer.from(template.getResourcesLayer());
    cacheMetadata.classesLayer = TimestampedCachedLayer.from(template.getClassesLayer());

    return cacheMetadata;
  }

  @Nullable
  TimestampedCachedLayer getDependenciesLayer() {
    return dependenciesLayer;
  }

  @Nullable
  TimestampedCachedLayer getResourcesLayer() {
    return resourcesLayer;
  }

  @Nullable
  TimestampedCachedLayer getClassesLayer() {
    return classesLayer;
  }
}
