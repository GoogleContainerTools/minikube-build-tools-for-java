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

package com.google.cloud.tools.crepecake.json;

import com.google.cloud.tools.crepecake.blob.BlobStream;
import com.google.cloud.tools.crepecake.image.DescriptorDigest;
import com.google.cloud.tools.crepecake.image.Image;
import com.google.cloud.tools.crepecake.image.Layer;
import com.google.cloud.tools.crepecake.image.LayerException;
import com.google.cloud.tools.crepecake.json.templates.ContainerConfigurationTemplate;
import com.google.cloud.tools.crepecake.json.templates.V22ManifestTemplate;
import java.io.IOException;
import java.security.DigestException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/** Translates an {@link Image} into a manifest or container configuration JSON BLOB. */
public class ImageToJsonTranslator {

  private final Image image;

  @Nullable private BlobStream containerConfigurationBlobStream;
  @Nullable private BlobStream manifestBlobStream;

  /** Instantiate with an {@link Image} that should not be modified afterwards. */
  public ImageToJsonTranslator(Image image) {
    this.image = image;
  }

  /** Gets the container configuration as a {@link BlobStream} */
  public BlobStream getContainerConfiguration() throws IOException, LayerException {
    if (null != containerConfigurationBlobStream) {
      return containerConfigurationBlobStream;
    }

    // Set up the JSON template.
    ContainerConfigurationTemplate template = new ContainerConfigurationTemplate();

    // Adds the layer diff IDs.
    for (Layer layer : image.getLayers()) {
      template.addLayerDiffId(layer.getDiffId());
    }

    // Adds the environment variables.
    Map<String, String> environmentMap = image.getEnvironmentMap();
    List<String> environment = new ArrayList<>(environmentMap.size());

    for (Map.Entry<String, String> environmentVariable : environmentMap.entrySet()) {
      String variableName = environmentVariable.getKey();
      String variableValue = environmentVariable.getValue();

      environment.add(variableName + "=" + variableValue);
    }

    template.setContainerEnvironment(environment);

    // Sets the entrypoint.
    template.setContainerEntrypoint(image.getEntrypoint());

    // Serializes into JSON.
    containerConfigurationBlobStream = JsonHelper.toBlobStream(template);

    return containerConfigurationBlobStream;
  }

  /**
   * Gets the manifest as a {@link BlobStream}. This is only valid <b>after</b> {@code
   * getContainerConfiguration} is called.
   *
   * @throws IllegalStateException if the container configuration BLOB does not exist or has not
   *     been written
   */
  public BlobStream getManifest()
      throws IOException, NoSuchAlgorithmException, DigestException, LayerException {
    if (null != manifestBlobStream) {
      return manifestBlobStream;
    }
    if (null == containerConfigurationBlobStream) {
      throw new IllegalStateException("Must call getContainerConfiguration before getManifest");
    }

    // Set up the JSON template.
    V22ManifestTemplate template = new V22ManifestTemplate();

    // Adds the container configuration reference.
    DescriptorDigest containerConfigurationDigest =
        containerConfigurationBlobStream.getWrittenBlobDescriptor().getDigest();
    long containerConfigurationSize =
        containerConfigurationBlobStream.getWrittenBlobDescriptor().getSize();
    template.setContainerConfiguration(containerConfigurationDigest, containerConfigurationSize);

    // Adds the layers.
    for (Layer layer : image.getLayers()) {
      template.addLayer(layer.getBlobDescriptor().getDigest(), layer.getBlobDescriptor().getSize());
    }

    // Serializes into JSON.
    manifestBlobStream = JsonHelper.toBlobStream(template);

    return manifestBlobStream;
  }
}
