package com.google.cloud.tools.crepecake.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

class CacheMetadata {

  static final String METADATA_FILENAME = "metadata.json";

  private final List<TimestampedCachedLayer> baseImageLayers = new ArrayList<>();

  private Map<ApplicationLayerType, TimestampedCachedLayer> applicationLayers;

  List<TimestampedCachedLayer> getBaseImageLayers() {
    return baseImageLayers;
  }

  void addBaseImageLayer(TimestampedCachedLayer layer) {
    baseImageLayers.add(layer);
  }

  @Nullable
  TimestampedCachedLayer getApplicationLayer(ApplicationLayerType layerType) {
    return applicationLayers.get(layerType);
  }

  void setApplicationLayer(ApplicationLayerType layerType, TimestampedCachedLayer layer) {
    applicationLayers.put(layerType, layer);
  }
}
