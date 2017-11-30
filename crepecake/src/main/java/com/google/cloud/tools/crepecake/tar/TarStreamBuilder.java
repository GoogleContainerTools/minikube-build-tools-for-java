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
import java.util.function.Function;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.CompressorException;
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
  public BlobStream toBlobStreamCompressed() throws IOException {
    return toBlobStream(this::applyCompressor);
  }

  /** Writes the uncompressed archive to a {@link BlobStream}. */
  public BlobStream toBlobStreamUncompressed() throws IOException {
    return toBlobStream(Function.identity());
  }

  /**
   * Helper function to build the archive.
   *
   * @param wrapOutputStreamFunction A function that possibly wraps an {@link OutputStream} in
   *     another {@link OutputStream}. This wrap can be a compressor.
   * @return a {@link BlobStream} containing the built archive BLOB.
   */
  private BlobStream toBlobStream(Function<OutputStream, OutputStream> wrapOutputStreamFunction)
      throws IOException {
    // Creates an underlying byte stream.
    ByteArrayOutputStream tarByteStream = new ByteArrayOutputStream();

    // Wraps the underlying byte stream with the provided function.
    OutputStream wrappedOutputStream;
    try {
      wrappedOutputStream = wrapOutputStreamFunction.apply(tarByteStream);
    } catch (RuntimeException ex) {
      throw new IOException(ex);
    }

    // Writes each entry in the filesystem to the tarball archive stream.
    TarArchiveOutputStream tarArchiveOutputStream = new TarArchiveOutputStream(wrappedOutputStream);

    for (Entry entry : entries) {
      tarArchiveOutputStream.putArchiveEntry(entry.header);
      ByteStreams.copy(entry.content, tarArchiveOutputStream);
      tarArchiveOutputStream.closeArchiveEntry();
    }

    tarArchiveOutputStream.close();

    return new BlobStream(tarByteStream);
  }

  private OutputStream applyCompressor(OutputStream outputStream) {
    try {
      return new CompressorStreamFactory()
          .createCompressorOutputStream(CompressorStreamFactory.GZIP, outputStream);
    } catch (CompressorException ex) {
      throw new RuntimeException(ex);
    }
  }
}
