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

import com.google.cloud.tools.crepecake.image.Digest;
import com.google.cloud.tools.crepecake.image.DigestException;
import com.google.cloud.tools.crepecake.image.Image;
import com.google.cloud.tools.crepecake.image.ImageException;
import com.google.cloud.tools.crepecake.image.Layer;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/** Tests for {@link ImageTranslator}. */
public class ImageTranslatorTest {

  private Image testImage;
  private Layer fakeLayer;

  @Before
  public void setUp() throws ImageException, DigestException {
    testImage = new Image();

    testImage.setEnvironmentVariable("crepecake", "is good");

    testImage.setEntrypoint(Arrays.asList("some", "entrypoint", "command"));

    Digest fakeDigest =
        Digest.fromDigest(
            "sha256:8c662931926fa990b41da3c9f42663a537ccd498130030f9149173a0493832ad");
    fakeLayer = new Layer(fakeDigest, 1000, fakeDigest);
    testImage.addLayer(fakeLayer);
  }

  @Test
  public void testGetContainerConfiguration() {
    ImageTranslator imageTranslator = new ImageTranslator(testImage);
  }

  @Test
  public void testGetManifest() {
    Assert.fail("implement");
  }
}
