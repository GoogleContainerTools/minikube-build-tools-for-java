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

  static Cache getInstance() {
    return instance;
  }

  /**
   * Initializes the cache with a directory. This also loads the cache metadata if it exists in the
   * directory. Can only be called once.
   */
  void init(File cacheDirectory) throws IOException {
    if (null != this.cacheMetadata) {
      throw new IllegalStateException("Cannot initialize cache more than once");
    }

    if (!cacheDirectory.isDirectory()) {
      throw new NotDirectoryException("The cache can only write to a directory");
    }
    this.cacheDirectory = cacheDirectory.toPath();

    // Loads the metadata.
    File cacheMetadataJsonFile =
        this.cacheDirectory.resolve(CacheMetadata.METADATA_FILENAME).toFile();

    if (!cacheMetadataJsonFile.exists()) {
      cacheMetadata = new CacheMetadata();
      return;
    }

    CacheMetadataTemplate cacheMetadataJson =
        JsonHelper.readJsonFromFile(cacheMetadataJsonFile, CacheMetadataTemplate.class);
    cacheMetadata = CacheMetadataTranslator.fromTemplate(cacheMetadataJson);
  }

  Path getDirectory() {
    if (null == cacheDirectory) {
      throw new IllegalStateException("Must initialize cache first");
    }
    return cacheDirectory;
  }
}
