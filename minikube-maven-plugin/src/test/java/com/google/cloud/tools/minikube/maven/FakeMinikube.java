/*
 * Copyright 2018 Google Inc.
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

package com.google.cloud.tools.minikube.maven;

import com.google.common.io.Resources;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;

/** Gets a fake minikube executable. */
class FakeMinikube {

  static Path getPath() throws IOException, URISyntaxException {
    // Makes the 'fakeminikube' executable.
    Path fakeMinikube = Paths.get(Resources.getResource("fakeminikube").toURI());
    // 755
    Files.setPosixFilePermissions(fakeMinikube, PosixFilePermissions.fromString("rwxr-xr-x"));

    return fakeMinikube.toAbsolutePath();
  }

  private FakeMinikube() {}
}
