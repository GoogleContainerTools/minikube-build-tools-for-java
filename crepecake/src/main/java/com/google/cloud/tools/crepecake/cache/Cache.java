package com.google.cloud.tools.crepecake.cache;

import com.google.cloud.tools.crepecake.json.JsonHelper;
import com.google.cloud.tools.crepecake.json.templates.CacheMetadataTemplate;
import java.io.File;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import javax.annotation.Nullable;

/** Holds various properties of the cache. */
public class Cache {

  private static final Cache instance = new Cache();

  @Nullable private Path cacheDirectory;

  @Nullable private CacheMetadata cacheMetadata;

  static Path getDirectory() {
    if (null == instance.cacheDirectory) {
      throw new IllegalStateException("Must initialize cache first");
    }
    return instance.cacheDirectory;
  }

  static CacheMetadata getMetadata() {
    if (null == instance.cacheMetadata) {
      throw new IllegalStateException("Must initialize cache first");
    }
    return instance.cacheMetadata;
  }

  /**
   * Initializes the cache with a directory. This also loads the cache metadata if it exists in the
   * directory. Can only be called once.
   */
  static void init(File cacheDirectory) throws IOException {
    if (null != instance.cacheMetadata) {
      throw new IllegalStateException("Cannot initialize cache more than once");
    }

    if (!cacheDirectory.isDirectory()) {
      throw new NotDirectoryException("The cache can only write to a directory");
    }
    instance.cacheDirectory = cacheDirectory.toPath();

    // Loads the metadata.
    File cacheMetadataJsonFile =
        instance.cacheDirectory.resolve(CacheMetadata.METADATA_FILENAME).toFile();

    if (!cacheMetadataJsonFile.exists()) {
      instance.cacheMetadata = new CacheMetadata();
      return;
    }

    CacheMetadataTemplate cacheMetadataJson =
        JsonHelper.readJsonFromFile(cacheMetadataJsonFile, CacheMetadataTemplate.class);
    instance.cacheMetadata = CacheMetadataTranslator.fromTemplate(cacheMetadataJson);
  }

  private static Cache getInstance() {
    return instance;
  }

  private Cache() {}
}
