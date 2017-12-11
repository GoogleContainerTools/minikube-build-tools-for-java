package com.google.cloud.tools.crepecake.cache;

import com.google.cloud.tools.crepecake.image.Image;
import com.google.cloud.tools.crepecake.image.ReferenceLayer;
import com.google.cloud.tools.crepecake.image.UnwrittenLayer;
import com.google.cloud.tools.crepecake.json.JsonHelper;
import com.google.cloud.tools.crepecake.json.templates.CacheMetadataTemplate;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Cache {

  private static final String METADATA_FILENAME = "metadata.json";

  private final Path cacheDirectory;

  private final CacheWriter cacheWriter;
  private final CacheChecker cacheChecker;
  private final CacheReader cacheReader;

  private CacheMetadata cacheMetadata;

  public Cache(File cacheDirectory) throws NotDirectoryException {
    if (!cacheDirectory.isDirectory()) {
      throw new NotDirectoryException("The cache can only write to a directory");
    }
    this.cacheDirectory = cacheDirectory.toPath();

    cacheWriter = new CacheWriter(this);
    cacheChecker = new CacheChecker(this);
    cacheReader = new CacheReader(this);
  }

  public CacheWriter getWriter() {
    return cacheWriter;
  }

  public CacheChecker getChecker() {
    return cacheChecker;
  }

  public CacheReader getReader() {
    return cacheReader;
  }

  Path getDirectory() {
    return cacheDirectory;
  }

  CacheMetadata loadMetadata() throws IOException {
    if (null != cacheMetadata) {
      return cacheMetadata;
    }

    File cacheMetadataJsonFile = cacheDirectory.resolve(METADATA_FILENAME).toFile();

    if (!cacheMetadataJsonFile.exists()) {
      return cacheMetadata = new CacheMetadata();
    }

    CacheMetadataTemplate cacheMetadataJson = JsonHelper.readJsonFromFile(cacheMetadataJsonFile, CacheMetadataTemplate.class);
    return cacheMetadata = CacheMetadata.from(cacheMetadataJson);
  }
}
