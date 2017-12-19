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

package com.google.cloud.tools.crepecake.cache;

import java.io.File;
import java.nio.file.Path;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

/** Tests for {@link CacheHelper}. */
@RunWith(MockitoJUnitRunner.class)
public class CacheHelperTest {

  @Mock
  private Path mockPath;
  @Mock
  private File mockFile;

  @Test
  public void testGetLayerFilename() {
    String testLayerName = "crepecake";

    ArgumentCaptor<String> fileNameCaptor = ArgumentCaptor.forClass(String.class);

    Mockito.when(mockPath.resolve(fileNameCaptor.capture())).thenReturn(mockPath);
    Mockito.when(mockPath.toFile()).thenReturn(mockFile);

    File layerFile = CacheHelper.getLayerFile(mockPath, testLayerName);

    Assert.assertEquals(
        testLayerName + ".tar.gz", fileNameCaptor.getValue());
    Assert.assertEquals(mockFile, layerFile);
  }

  @Test
  public void testGetNameForApplicationLayer() {
    Assert.assertEquals(
        "dependencies",
        CacheHelper.getNameForApplicationLayer(ApplicationLayerType.DEPENDENCIES));
    Assert.assertEquals(
        "resources",
        CacheHelper.getNameForApplicationLayer(ApplicationLayerType.RESOURCES));
    Assert.assertEquals(
        "classes",
        CacheHelper.getNameForApplicationLayer(ApplicationLayerType.CLASSES));
  }
}
