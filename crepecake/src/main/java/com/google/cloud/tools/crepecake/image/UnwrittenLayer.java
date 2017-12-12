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
import com.google.cloud.tools.crepecake.blob.BlobDescriptor;
import java.io.IOException;
import java.io.OutputStream;

/** A layer that has not been written out and only has the unwritten content {@link Blob}. */
public class UnwrittenLayer implements Layer {

  private final Blob compressedBlob;
  private final Blob uncompressedBlob;

  /**
   * @param compressedBlob the compressed {@link Blob} of the layer content
   * @param uncompressedBlob the uncompressed {@link Blob} of the layer content
   */
  public UnwrittenLayer(Blob compressedBlob, Blob uncompressedBlob) {
    this.compressedBlob = compressedBlob;
    this.uncompressedBlob = uncompressedBlob;
  }

  /**
   * Writes the compressed layer BLOB to an {@link OutputStream} and returns the written BLOB
   * descriptor.
   */
  public BlobDescriptor writeCompressedBlobTo(OutputStream outputStream) throws IOException {
    return compressedBlob.writeTo(outputStream);
  }

  /**
   * Writes the uncompressed layer BLOB to an {@link OutputStream} and returns the associated diff
   * ID.
   */
  public DescriptorDigest writeUncompressedBlobTo(OutputStream outputStream) throws IOException {
    return uncompressedBlob.writeTo(outputStream).getDigest();
  }

  @Override
  public LayerType getType() {
    return LayerType.UNWRITTEN;
  }

  @Override
  public BlobDescriptor getBlobDescriptor() throws LayerPropertyNotFoundException {
    throw new LayerPropertyNotFoundException("Blob descriptor not available for unwritten layer");
  }

  @Override
  public DescriptorDigest getDiffId() throws LayerPropertyNotFoundException {
    throw new LayerPropertyNotFoundException("Diff ID not available for unwritten layer");
  }
}
