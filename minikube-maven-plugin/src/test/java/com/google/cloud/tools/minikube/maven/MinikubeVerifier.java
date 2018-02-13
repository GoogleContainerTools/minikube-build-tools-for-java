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

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.junit.Assert;

/** Utilities for verifying minikube goals on a {@link TestProject}. */
class MinikubeVerifier {

  private final Verifier verifier;
  private String profile;

  /** Sets up the a verifier on the {@link TestProject}. */
  MinikubeVerifier(TestProject testProject)
      throws VerificationException, IOException, URISyntaxException {
    // Sets the minikube executable to fakeminikube.
    testProject.replaceInPom("@@MinikubePath@@", FakeMinikube.getPath().toString());

    verifier = new Verifier(testProject.getProjectRoot().toString());
    verifier.setAutoclean(false);
  }

  /** Sets a profile to use. */
  MinikubeVerifier setProfile(String profile) {
    this.profile = profile;
    return this;
  }

  /** Verifies execution of the goal produces the correct fakeminikube output. */
  void verify(String goal, String expectedOutput) throws IOException, VerificationException {
    if (profile != null) {
      verifier.setCliOptions(Collections.singletonList("-P" + profile));
    }
    verifier.executeGoal("minikube:" + goal);
    verifier.verifyErrorFreeLog();

    String fakeMinikubeOutput =
        new String(
            Files.readAllBytes(Paths.get(verifier.getBasedir()).resolve("fakeminikube.log")),
            StandardCharsets.UTF_8);
    Assert.assertEquals(expectedOutput, fakeMinikubeOutput);
  }
}
