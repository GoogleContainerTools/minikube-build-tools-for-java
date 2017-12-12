package com.google.cloud.tools.crepecake.cache;

import com.google.cloud.tools.crepecake.blob.BlobDescriptor;
import com.google.cloud.tools.crepecake.image.DescriptorDigest;
import com.google.cloud.tools.crepecake.image.Layer;
import com.google.cloud.tools.crepecake.image.UnwrittenLayer;
import com.google.common.io.ByteStreams;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/** Writes {@link Layer}s to the cache. */
public class CacheWriter {

  public CachedLayer writeBaseImageLayer(DescriptorDigest digest, UnwrittenLayer layer)
      throws IOException {
    File layerFile = CacheHelper.getLayerFilename(Cache.getDirectory(), digest.toString());

    return writeUnwrittenLayerToFile(layer, layerFile);
  }

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
    File layerFile =
        CacheHelper.getLayerFilename(
            Cache.getDirectory(), CacheHelper.getNameForApplicationLayer(layerType));

    return new TimestampedCachedLayer(writeUnwrittenLayerToFile(layer, layerFile));
  }

  /**
   * Writes the layer BLOB to a file and returns a {@link CachedLayer} that represents the new
   * cached layer.
   */
  private CachedLayer writeUnwrittenLayerToFile(UnwrittenLayer unwrittenLayer, File file)
      throws IOException {
    try (OutputStream fileOutputStream = new BufferedOutputStream(new FileOutputStream(file))) {
      BlobDescriptor blobDescriptor = unwrittenLayer.writeCompressedBlobStreamTo(fileOutputStream);
      DescriptorDigest diffId =
          unwrittenLayer.writeUncompressedBlobStreamTo(ByteStreams.nullOutputStream());

      return new CachedLayer(file, blobDescriptor, diffId);
    }
  }
}
