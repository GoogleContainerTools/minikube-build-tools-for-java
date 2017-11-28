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

package com.google.cloud.tools.crepecake.io;

import com.google.cloud.tools.crepecake.blob.BlobStream;
import com.google.cloud.tools.crepecake.hash.ByteHasher;
import com.google.cloud.tools.crepecake.image.Digest;
import com.google.cloud.tools.crepecake.image.DigestException;
import com.google.cloud.tools.crepecake.image.Layer;
import com.google.cloud.tools.crepecake.tar.TarStreamBuilder;
import com.google.common.annotations.VisibleForTesting;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.apache.commons.compress.compressors.CompressorException;

/** Builds a {@link Layer} from files. */
public class LayerBuilder {

  /** The partial filesystem changeset to build the layer with. */
  private final List<LayerFileEntry> filesystemEntries = new ArrayList<>();

  private Supplier<TarStreamBuilder> tarStreamBuilderSupplier = TarStreamBuilder::new;

  public LayerBuilder() {}

  /**
   * Prepares a file to be built into the layer.
   *
   * @param file the file to add
   * @param path the path of the file in the partial filesystem changeset
   */
  public void addFile(File file, String path) {
    filesystemEntries.add(new LayerFileEntry(file, path));
  }

  /** Builds and returns the layer. */
  public Layer build()
      throws IOException, CompressorException, NoSuchAlgorithmException, DigestException {
    TarStreamBuilder tarStreamBuilder = tarStreamBuilderSupplier.get();

    // Adds all the files to a tar.gzip stream.
    for (LayerFileEntry entry : filesystemEntries) {
      final File file = entry.getFile();
      final String path = entry.getArchivePath();

      tarStreamBuilder.addFile(file, path);
    }

    BlobStream layerBlob = tarStreamBuilder.toBlobStreamCompressed();
    byte[] layerBlobBytes = layerBlob.toByteArray();

    // Hashes the layer blob to obtain the layer's digest.
    Digest layerDigest = Digest.fromHash(ByteHasher.hash(layerBlobBytes));

    // Hashes the uncompressed layer blob to obtain the layer's diff ID.
    BlobStream layerDiffBlob = tarStreamBuilder.toBlobStreamUncompressed();
    byte[] layerDiffBlobBytes = layerDiffBlob.toByteArray();
    Digest layerDiffId = Digest.fromHash(ByteHasher.hash(layerDiffBlobBytes));

    // The layer size is the size of the compressed blob.
    int layerSize = layerBlobBytes.length;

    return new Layer(layerDigest, layerSize, layerDiffId, layerBlob);
  }

  @VisibleForTesting
  LayerBuilder(Supplier<TarStreamBuilder> tarStreamBuilderSupplier) {
    this.tarStreamBuilderSupplier = tarStreamBuilderSupplier;
  }
}
