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
import com.google.cloud.tools.crepecake.image.Digest;
import com.google.cloud.tools.crepecake.image.DigestException;
import com.google.cloud.tools.crepecake.image.Image;
import com.google.cloud.tools.crepecake.image.Layer;
import com.google.cloud.tools.crepecake.json.templates.ContainerConfigurationTemplate;
import com.google.cloud.tools.crepecake.json.templates.V22ManifestTemplate;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Translates an {@link Image} into a manifest or container configuration JSON BLOB. */
public class ImageTranslator {

  private final Image image;

  @Nullable
  private BlobStream containerConfigurationBlobStream;

  @Nullable
  private BlobStream manifestBlobStream;

  /** Instantiate with an {@link Image} that should not be modified afterwards. */
  public ImageTranslator(Image image) {
    this.image = image;
  }

  public BlobStream getContainerConfiguration() throws IOException {
    if (containerConfigurationBlobStream != null) {
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

    // Serialize into JSON.
    containerConfigurationBlobStream = JsonParser.toBlobStream(template);

    return containerConfigurationBlobStream;
  }

  public BlobStream getManifest() throws IOException, NoSuchAlgorithmException, DigestException {
    if (manifestBlobStream != null) {
      return manifestBlobStream;
    }

    // Set up the JSON template.
    V22ManifestTemplate template = new V22ManifestTemplate();

    Digest containerConfigurationDigest = containerConfigurationBlobStream.getDigest();
    int containerConfigurationSize = containerConfigurationBlobStream.getSize();
    template.setContainerConfiguration(containerConfigurationDigest, containerConfigurationSize);

    // Adds the layers.
    for (Layer layer : image.getLayers()) {
      template.addLayer(layer.getDigest(), layer.getSize());
    }

    // Serializes into JSON.
    manifestBlobStream = JsonParser.toBlobStream(template);

    return manifestBlobStream;
  }
}
