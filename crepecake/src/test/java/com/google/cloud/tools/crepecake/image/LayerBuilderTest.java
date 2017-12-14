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
import com.google.cloud.tools.crepecake.blob.Blobs;
import com.google.cloud.tools.crepecake.tar.TarStreamBuilder;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/** Tests for {@link LayerBuilder}. */
@RunWith(MockitoJUnitRunner.class)
public class LayerBuilderTest {

  @Mock private TarStreamBuilder mockTarStreamBuilder;

  @InjectMocks private LayerBuilder layerBuilder;

  @Test
  public void testBuild() {
    Blob emptyBlob = Blobs.empty();

    Mockito.when(mockTarStreamBuilder.toBlob()).thenReturn(emptyBlob);

    // Fake files to build into the layer.
    List<TarArchiveEntry> fileEntries =
        Arrays.asList(
            new TarArchiveEntry(new File("fileA"), "/path/to/fileA"),
            new TarArchiveEntry(new File("directory/fileB"), "/path/to/directory/fileB"),
            new TarArchiveEntry(new File("directory/fileC"), "/path/to/directory/fileC"),
            new TarArchiveEntry(new File("directory/"), "/path/to/directory/"));

    // Adds each file in the layer directory to the layer builder.
    for (TarArchiveEntry fileEntry : fileEntries) {
      layerBuilder.addFile(fileEntry.getFile(), fileEntry.getName());
    }

    Assert.assertEquals(emptyBlob, layerBuilder.build().getBlob());

    // Verifies that all the files have been added to the tarball stream.
    for (TarArchiveEntry entry : fileEntries) {
      Mockito.verify(mockTarStreamBuilder).addEntry(entry);
    }
    Mockito.verify(mockTarStreamBuilder).toBlob();
  }
}
