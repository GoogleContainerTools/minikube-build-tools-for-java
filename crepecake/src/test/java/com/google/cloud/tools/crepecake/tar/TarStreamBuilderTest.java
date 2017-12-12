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

import com.google.cloud.tools.crepecake.blob.Blob;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestException;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.junit.Assert;
import org.junit.Test;

/** Tests for {@link TarStreamBuilder}. */
public class TarStreamBuilderTest {

  @Test
  public void testBuild_compressed()
      throws IOException, URISyntaxException, CompressorException, NoSuchAlgorithmException,
          DigestException {
    testBuild(true);
  }

  @Test
  public void testBuild_uncompressed()
      throws IOException, URISyntaxException, CompressorException, NoSuchAlgorithmException,
          DigestException {
    testBuild(false);
  }

  /**
   * Helper function to perform the archive build testing.
   *
   * @param compress true if to test {@link TarStreamBuilder} with compression; false otherwise
   */
  private void testBuild(boolean compress)
      throws URISyntaxException, IOException, CompressorException, DigestException,
          NoSuchAlgorithmException {
    // Gets the test resource files.
    Path fileA = Paths.get(Resources.getResource("fileA").toURI());
    Path fileB = Paths.get(Resources.getResource("fileB").toURI());

    String expectedFileAString = new String(Files.readAllBytes(fileA), Charsets.UTF_8);
    String expectedFileBString = new String(Files.readAllBytes(fileB), Charsets.UTF_8);

    // Prepares a test TarStreamBuilder.
    TarStreamBuilder tarStreamBuilder = new TarStreamBuilder();
    tarStreamBuilder.addFile(fileA.toFile(), "some/path/to/resourceFileA");
    tarStreamBuilder.addFile(fileB.toFile(), "crepecake");

    // Constructs the corresponding Blob (compressed vs. uncompressed).
    Blob blob =
        compress
            ? tarStreamBuilder.toBlobStreamCompressed()
            : tarStreamBuilder.toBlobStreamUncompressed();

    // Writes the BLOB and captures the output.
    ByteArrayOutputStream tarByteOutputStream = new ByteArrayOutputStream();
    blob.writeTo(tarByteOutputStream);

    // Rearrange the output into input for verification.
    ByteArrayInputStream byteArrayInputStream =
        new ByteArrayInputStream(tarByteOutputStream.toByteArray());
    InputStream tarByteInputStream =
        compress
            ? new CompressorStreamFactory().createCompressorInputStream(byteArrayInputStream)
            : byteArrayInputStream;
    TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(tarByteInputStream);

    // Verifies fileA was archived correctly.
    TarArchiveEntry headerA = tarArchiveInputStream.getNextTarEntry();
    Assert.assertEquals("some/path/to/resourceFileA", headerA.getName());
    String fileAString = CharStreams.toString(new InputStreamReader(tarArchiveInputStream));
    Assert.assertEquals(expectedFileAString, fileAString);

    // Verifies fileB was archived correctly.
    TarArchiveEntry headerB = tarArchiveInputStream.getNextTarEntry();
    Assert.assertEquals("crepecake", headerB.getName());
    String fileBString = CharStreams.toString(new InputStreamReader(tarArchiveInputStream));
    Assert.assertEquals(expectedFileBString, fileBString);
  }
}
