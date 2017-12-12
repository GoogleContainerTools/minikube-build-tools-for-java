package com.google.cloud.tools.crepecake.cache;

import com.google.cloud.tools.crepecake.json.templates.CacheMetadataTemplate;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CacheMetadata {

  private static final String DEPENDENCIES_LAYER_NAME = "dependencies";
  private static final String RESOURCES_LAYER_NAME = "resources";
  private static final String CLASSES_LAYER_NAME = "classes";

  private final List<TimestampedCachedLayer> baseImageLayers = new ArrayList<>();

  private Map<ApplicationLayerType, TimestampedCachedLayer> applicationLayers;

  static File getLayerFilename(Cache cache, String layerName) {
    return cache.getDirectory().resolve(layerName + ".tar").toFile();
  }

  static String getNameForApplicationLayer(ApplicationLayerType layerType) {
    switch (layerType) {
      case DEPENDENCIES:
        return DEPENDENCIES_LAYER_NAME;
      break;
      case RESOURCES:
        return RESOURCES_LAYER_NAME;
      break;
      case CLASSES:
        return CLASSES_LAYER_NAME;
      break;
    }
    throw new IllegalStateException("Should never reach here - switch above is exhaustive");
  }

  static CacheMetadata fromTemplate(Cache cache, CacheMetadataTemplate template) {
    CacheMetadata cacheMetadata = new CacheMetadata();

    for (CacheMetadataTemplate.LayerObjectTemplate baseImageLayerTemplate : template.getBaseImageLayers()) {
      File cachedLayerFile = getLayerFilename(cache, baseImageLayerTemplate.getDigest().toString();
      TimestampedCachedLayer timestampedCachedLayer =
          TimestampedCachedLayer.fromTemplate(cachedLayerFile, baseImageLayerTemplate);
      cacheMetadata.baseImageLayers.add(timestampedCachedLayer);
    }

    for (ApplicationLayerType layerType : ApplicationLayerType.values()) {
      File layerFile = getLayerFilename(cache, getNameForApplicationLayer(layerType));
      TimestampedCachedLayer cachedLayer = TimestampedCachedLayer.fromTemplate(layerFile, template.getDependenciesLayer());
      cacheMetadata.setApplicationLayer(layerType, cachedLayer);
    }

    return cacheMetadata;
  }

  List<TimestampedCachedLayer> getBaseImageLayers() {
    return baseImageLayers;
  }

  @Nullable
  TimestampedCachedLayer  getApplicationLayer(ApplicationLayerType layerType) {
    return applicationLayers.get(layerType);
  }

  void setApplicationLayer(ApplicationLayerType layerType, TimestampedCachedLayer layer) {
    applicationLayers.put(layerType, layer);
  }
}
