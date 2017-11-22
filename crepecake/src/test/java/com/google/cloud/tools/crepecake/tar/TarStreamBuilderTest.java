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
import com.google.common.io.CharStreams;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.junit.Assert;
import org.junit.Test;

/** Tests for {@link TarStreamBuilder}. */
public class TarStreamBuilderTest {

  @Test
  public void testAddFile() throws IOException, CompressorException, URISyntaxException {
    File fileA = new File(getClass().getClassLoader().getResource("fileA").toURI());
    File fileB = new File(getClass().getClassLoader().getResource("fileB").toURI());

    String expectedFileAString =
        CharStreams.toString(new InputStreamReader(new FileInputStream(fileA)));
    String expectedFileBString =
        CharStreams.toString(new InputStreamReader(new FileInputStream(fileB)));

    TarStreamBuilder tarStreamBuilder = new TarStreamBuilder();
    tarStreamBuilder.addFile(fileA, "some/path/to/fileA");
    tarStreamBuilder.addFile(fileB, "crepecake");

    BlobStream blobStream = tarStreamBuilder.toBlobStream();

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    blobStream.writeTo(byteArrayOutputStream);

    ByteArrayInputStream byteArrayInputStream =
        new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    CompressorInputStream compressorInputStream =
        new CompressorStreamFactory().createCompressorInputStream(byteArrayInputStream);
    TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(compressorInputStream);

    TarArchiveEntry headerA = tarArchiveInputStream.getNextTarEntry();
    Assert.assertEquals("some/path/to/fileA", headerA.getName());
    String fileAString = CharStreams.toString(new InputStreamReader(tarArchiveInputStream));
    Assert.assertEquals(expectedFileAString, fileAString);

    TarArchiveEntry headerB = tarArchiveInputStream.getNextTarEntry();
    Assert.assertEquals("crepecake", headerB.getName());
    String fileBString = CharStreams.toString(new InputStreamReader(tarArchiveInputStream));
    Assert.assertEquals(expectedFileBString, fileBString);
  }
}
