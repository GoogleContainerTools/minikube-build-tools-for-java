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

import com.google.cloud.tools.crepecake.blob.BlobDescriptor;
import com.google.cloud.tools.crepecake.blob.BlobStream;
import java.io.IOException;
import java.io.OutputStream;

/** A layer that has not been written out and only has the unwritten content {@link BlobStream}. */
public class UnwrittenLayer implements Layer {

  private final BlobStream compressedBlobStream;
  private final BlobStream uncompressedBlobStream;

  /**
   * @param compressedBlobStream the compressed {@link BlobStream} of the layer content
   * @param uncompressedBlobStream the uncompressed {@link BlobStream} of the layer content
   */
  UnwrittenLayer(BlobStream compressedBlobStream, BlobStream uncompressedBlobStream) {
    this.compressedBlobStream = compressedBlobStream;
    this.uncompressedBlobStream = uncompressedBlobStream;
  }

  /**
   * Writes the compressed layer BLOB to an {@link OutputStream} and returns the written BLOB
   * descriptor.
   */
  public BlobDescriptor writeCompressedBlobStreamTo(OutputStream outputStream) throws IOException {
    return compressedBlobStream.writeTo(outputStream);
  }

  /**
   * Writes the uncompressed layer BLOB to an {@link OutputStream} and returns the associated diff
   * ID.
   */
  public DescriptorDigest writeUncompressedBlobStreamTo(OutputStream outputStream)
      throws IOException {
    return uncompressedBlobStream.writeTo(outputStream).getDigest();
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
