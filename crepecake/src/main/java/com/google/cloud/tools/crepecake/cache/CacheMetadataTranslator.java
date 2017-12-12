package com.google.cloud.tools.crepecake.cache;

import com.google.cloud.tools.crepecake.blob.BlobDescriptor;
import com.google.cloud.tools.crepecake.image.CachedLayer;
import com.google.cloud.tools.crepecake.json.templates.CacheMetadataTemplate;
import java.io.File;

/** Translates {@link CacheMetadata} to and from {@link CacheMetadataTemplate}. */
public class CacheMetadataTranslator {

  static CacheMetadata fromTemplate(CacheMetadataTemplate template) {
    Cache cache = Cache.getInstance();
    CacheMetadata cacheMetadata = new CacheMetadata();

    // Adds the base image layers to the template.
    for (CacheMetadataTemplate.LayerObjectTemplate baseImageLayerTemplate :
        template.getBaseImageLayers()) {
      File cachedLayerFile =
          CacheHelper.getLayerFilename(
              cache.getDirectory(), baseImageLayerTemplate.getDigest().toString());
      TimestampedCachedLayer timestampedCachedLayer =
          fromTemplate(baseImageLayerTemplate, cachedLayerFile);
      cacheMetadata.addBaseImageLayer(timestampedCachedLayer);
    }

    // Adds the application layers to the template.
    for (ApplicationLayerType layerType : ApplicationLayerType.values()) {
      File layerFile =
          CacheHelper.getLayerFilename(
              cache.getDirectory(), CacheHelper.getNameForApplicationLayer(layerType));
      TimestampedCachedLayer cachedLayer = fromTemplate(template.getDependenciesLayer(), layerFile);
      cacheMetadata.setApplicationLayer(layerType, cachedLayer);
    }

    return cacheMetadata;
  }

  private static TimestampedCachedLayer fromTemplate(
      CacheMetadataTemplate.LayerObjectTemplate layerObjectTemplate, File contentTarFile) {
    CachedLayer cachedLayer =
        new CachedLayer(
            contentTarFile,
            new BlobDescriptor(layerObjectTemplate.getSize(), layerObjectTemplate.getDigest()),
            layerObjectTemplate.getDiffId());
    return new TimestampedCachedLayer(cachedLayer, layerObjectTemplate.getLastModifiedTime());
  }
}
