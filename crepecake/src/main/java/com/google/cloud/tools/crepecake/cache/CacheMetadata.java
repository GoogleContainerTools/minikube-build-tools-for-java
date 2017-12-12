package com.google.cloud.tools.crepecake.cache;

import com.google.cloud.tools.crepecake.image.ImageException;
import com.google.cloud.tools.crepecake.image.ImageLayers;
import com.google.cloud.tools.crepecake.image.LayerPropertyNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

class CacheMetadata {

  static final String METADATA_FILENAME = "metadata.json";

  private final ImageLayers<TimestampedCachedLayer> baseImageLayers = new ImageLayers<>();

  private Map<ApplicationLayerType, TimestampedCachedLayer> applicationLayers;

  ImageLayers<TimestampedCachedLayer> getBaseImageLayers() {
    return baseImageLayers;
  }

  void addBaseImageLayer(TimestampedCachedLayer layer) throws LayerPropertyNotFoundException, ImageException {
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
