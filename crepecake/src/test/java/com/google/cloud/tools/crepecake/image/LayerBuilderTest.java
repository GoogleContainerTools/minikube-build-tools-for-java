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

package com.google.cloud.tools.crepecake.image;

import com.google.cloud.tools.crepecake.blob.Blob;
import com.google.cloud.tools.crepecake.tar.TarStreamBuilder;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/** Tests for {@link LayerBuilder}. */
public class LayerBuilderTest {

  @Mock private Blob mockBlob;
  @Mock private TarStreamBuilder mockTarStreamBuilder;

  @Before
  public void setUpMocks() throws IOException {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testBuild() throws LayerPropertyNotFoundException {
    Mockito.when(mockTarStreamBuilder.toBlob()).thenReturn(mockBlob);

    // Fake files to build into the layer.
    List<TarArchiveEntry> fileEntries =
        Arrays.asList(
            new TarArchiveEntry(new File("fileA"), "/path/to/fileA"),
            new TarArchiveEntry(new File("directory/fileB"), "/path/to/directory/fileB"),
            new TarArchiveEntry(new File("directory/fileC"), "/path/to/directory/fileC"),
            new TarArchiveEntry(new File("directory/"), "/path/to/directory/"));

    LayerBuilder layerBuilder = new LayerBuilder(() -> mockTarStreamBuilder);

    // Adds each file in the layer directory to the layer builder.
    for (TarArchiveEntry fileEntry : fileEntries) {
      layerBuilder.addFile(fileEntry.getFile(), fileEntry.getName());
    }

    Assert.assertEquals(mockBlob, layerBuilder.build().getBlob());

    // Verifies that all the files have been added to the tarball stream.
    for (TarArchiveEntry entry : fileEntries) {
      Mockito.verify(mockTarStreamBuilder).addEntry(entry);
    }
    Mockito.verify(mockTarStreamBuilder).toBlob();
  }
}
