/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.cloud.tools.crepecake.tar;

import com.google.cloud.tools.crepecake.blob.BlobStream;
import com.google.common.io.ByteStreams;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

/** Builds a tarball archive. */
public class TarStreamBuilder {

  /** An entry in the archive. */
  private static class Entry {

    private final TarArchiveEntry header;
    private final InputStream content;

    private Entry(TarArchiveEntry header, InputStream content) {
      this.header = header;
      this.content = content;
    }
  }

  private final List<Entry> entries = new ArrayList<>();

  /**
   * Adds a file to the archive.
   *
   * @param file the file to add
   * @param path the relative archive extraction path
   */
  public void addFile(File file, String path) throws IOException {
    TarArchiveEntry header = new TarArchiveEntry(file, path);
    InputStream content = new BufferedInputStream(new FileInputStream(file));

    entries.add(new Entry(header, content));
  }

  /** Writes the compressed archive to a {@link BlobStream}. */
  public BlobStream toBlobStreamCompressed() throws IOException, CompressorException {
    return toBlobStream(true);
  }

  /** Writes the uncompressed archive to a {@link BlobStream}. */
  public BlobStream toBlobStreamUncompressed() throws IOException, CompressorException {
    return toBlobStream(false);
  }

  /**
   * Helper function to build the archive.
   *
   * @param compress compresses the archive if true
   * @return a {@link BlobStream} containing the built archive BLOB.
   */
  private BlobStream toBlobStream(boolean compress) throws IOException, CompressorException {
    // Creates an underlying byte stream.
    ByteArrayOutputStream underlyingByteStream = new ByteArrayOutputStream();

    // Possibly wraps the underlying byte stream with a compressor.
    if (compress) {
      try (CompressorOutputStream compressorStream =
          new CompressorStreamFactory()
              .createCompressorOutputStream(CompressorStreamFactory.GZIP, underlyingByteStream)) {
        writeEntriesAsTarArchive(compressorStream);
      }
    } else {
      writeEntriesAsTarArchive(underlyingByteStream);
    }

    return new BlobStream(underlyingByteStream);
  }

  /** Writes each entry in the filesystem to the tarball archive stream. */
  private void writeEntriesAsTarArchive(OutputStream tarByteStream) throws IOException {
    try (TarArchiveOutputStream tarArchiveOutputStream =
        new TarArchiveOutputStream(tarByteStream)) {
      for (Entry entry : entries) {
        tarArchiveOutputStream.putArchiveEntry(entry.header);
        ByteStreams.copy(entry.content, tarArchiveOutputStream);
        tarArchiveOutputStream.closeArchiveEntry();
      }
    }
  }
}
