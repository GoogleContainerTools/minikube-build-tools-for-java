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

import com.google.cloud.tools.crepecake.image.UnwrittenLayer;
import com.google.cloud.tools.crepecake.tar.TarStreamBuilder;
import com.google.common.annotations.VisibleForTesting;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;

/** Builds an {@link UnwrittenLayer} from files. */
public class LayerBuilder {

  /** The partial filesystem changeset to build the layer with. */
  private final List<TarArchiveEntry> filesystemEntries = new ArrayList<>();

  private Supplier<TarStreamBuilder> tarStreamBuilderSupplier = TarStreamBuilder::new;

  public LayerBuilder() {}

  /**
   * Prepares a file to be built into the layer.
   *
   * @param file the file to add
   * @param path the path of the file in the partial filesystem changeset
   */
  public void addFile(File file, String path) {
    filesystemEntries.add(new TarArchiveEntry(file, path));
  }

  /** Builds and returns the layer. */
  public UnwrittenLayer build() {
    TarStreamBuilder tarStreamBuilder = tarStreamBuilderSupplier.get();

    // Adds all the files to a tar.gzip stream.
    for (TarArchiveEntry entry : filesystemEntries) {
      tarStreamBuilder.addEntry(entry);
    }

    return new UnwrittenLayer(tarStreamBuilder.toBlob());
  }

  @VisibleForTesting
  LayerBuilder(Supplier<TarStreamBuilder> tarStreamBuilderSupplier) {
    this.tarStreamBuilderSupplier = tarStreamBuilderSupplier;
  }
}
