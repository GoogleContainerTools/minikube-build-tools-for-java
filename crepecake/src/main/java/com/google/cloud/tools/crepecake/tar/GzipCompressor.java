package com.google.cloud.tools.crepecake.tar;

import java.io.OutputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

/** Wraps {@link OutputStream}s with GZIP compression. */
public abstract class GzipCompressor {

  private static final CompressorStreamFactory compressorStreamFactory =
      new CompressorStreamFactory();

  public static OutputStream wrap(OutputStream outputStream) throws CompressorException {
    return compressorStreamFactory.createCompressorOutputStream(
        CompressorStreamFactory.GZIP, outputStream);
  }
}
