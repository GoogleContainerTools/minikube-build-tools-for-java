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
import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/** Tests for {@link StartMojo}. */
public class StartMojoTest {

  @ClassRule public static final TestPlugin testPlugin = new TestPlugin();

  @Rule public final TestProject testProject = new TestProject(testPlugin, "/projects/simple");

  @Test
  public void testExecute_withProject()
      throws VerificationException, IOException, URISyntaxException {
    testProject.replaceInPom("@@MinikubePath@@", FakeMinikube.getPath().toString());

    Verifier verifier = new Verifier(testProject.getProjectRoot().toString());
    verifier.setAutoclean(false);
    verifier.executeGoal("minikube:start");
    verifier.verifyErrorFreeLog();
  }
}
