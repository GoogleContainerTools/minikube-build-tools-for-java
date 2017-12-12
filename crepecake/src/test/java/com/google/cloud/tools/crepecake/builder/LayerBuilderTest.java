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

package com.google.cloud.tools.crepecake.builder;

import com.google.cloud.tools.crepecake.hash.ByteHasher;
import com.google.cloud.tools.crepecake.image.Digest;
import com.google.cloud.tools.crepecake.image.DigestException;
import com.google.cloud.tools.crepecake.image.Layer;
import com.google.cloud.tools.crepecake.tar.TarStreamBuilder;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.compress.compressors.CompressorException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/** Tests for {@link LayerBuilder}. */
public class LayerBuilderTest {

  @Mock private TarStreamBuilder tarStreamBuilderMock;

  @Before
  public void setUpMocksAndFakes() throws IOException {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testBuild() {
    BlobStream expectedBlobStream = new BlobStream();
    Digest expectedLayerDigest = Digest.fromHash(ByteHasher.hash(expectedBlobStream.toByteArray()));

    Mockito.when(tarStreamBuilderMock.toBlobStreamCompressed()).thenReturn(expectedBlobStream);
    Mockito.when(tarStreamBuilderMock.toBlobStreamUncompressed()).thenReturn(expectedBlobStream);

    // Fake files to build into the layer.
    List<LayerFileEntry> fileEntries =
        Arrays.asList(
            new LayerFileEntry(new File("fileA"), "/path/to/fileA"),
            new LayerFileEntry(new File("directory/fileB"), "/path/to/directory/fileB"),
            new LayerFileEntry(new File("directory/fileC"), "/path/to/directory/fileC"),
            new LayerFileEntry(new File("directory/"), "/path/to/directory/"));

    LayerBuilder layerBuilder = new LayerBuilder(() -> tarStreamBuilderMock);

    // Adds each file in the layer directory to the layer builder.
    for (LayerFileEntry fileEntry : fileEntries) {
      layerBuilder.addFile(fileEntry.getFile(), fileEntry.getArchivePath());
    }

    Layer layer = layerBuilder.build();

    // Verifies that all the files have been added to the tarball stream.
    for (LayerFileEntry fileEntry : fileEntries) {
      File file = fileEntry.getFile();
      String archivePath = fileEntry.getArchivePath();

      Mockito.verify(tarStreamBuilderMock).addFile(file, archivePath);
    }
    Mockito.verify(tarStreamBuilderMock).toBlobStreamCompressed();
    Mockito.verify(tarStreamBuilderMock).toBlobStreamUncompressed();

    Assert.assertTrue(layer.hasContent());

    Assert.assertEquals(expectedBlobStream, layer.getContent());
    Assert.assertEquals(expectedLayerDigest, layer.getDigest());
    Assert.assertEquals(expectedLayerDigest, layer.getDiffId());
    Assert.assertEquals(0, layer.getSize());
  }
}
