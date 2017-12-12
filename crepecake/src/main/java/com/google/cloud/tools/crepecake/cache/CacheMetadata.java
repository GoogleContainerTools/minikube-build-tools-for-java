package com.google.cloud.tools.crepecake.cache;

import com.google.cloud.tools.crepecake.image.DuplicateLayerException;
import com.google.cloud.tools.crepecake.image.ImageLayers;
import com.google.cloud.tools.crepecake.image.LayerPropertyNotFoundException;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

/** Hold information for what data the cache holds. */
class CacheMetadata {

  static final String METADATA_FILENAME = "metadata.json";

  private final ImageLayers<TimestampedCachedLayer> baseImageLayers = new ImageLayers<>();

  private Map<ApplicationLayerType, TimestampedCachedLayer> applicationLayers = new HashMap<>();

  ImageLayers<TimestampedCachedLayer> getBaseImageLayers() {
    return baseImageLayers;
  }

  void addBaseImageLayer(TimestampedCachedLayer layer)
      throws LayerPropertyNotFoundException, DuplicateLayerException {
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
