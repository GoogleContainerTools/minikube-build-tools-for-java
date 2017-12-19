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

package com.google.cloud.tools.crepecake.registry;

import com.google.cloud.tools.crepecake.http.Authorization;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

/** Integration tests for {@link DockerCredentialRetriever}. */
public class DockerCredentialRetrieverIntegrationTest {

  /** Tests retrieval via {@code docker-credential-gcr} CLI. */
  @Test
  public void testRetrieveGCR() throws IOException {
    try {
      DockerCredentialRetriever dockerCredentialRetriever =
          new DockerCredentialRetriever("gcr.io", "gcr");

      Authorization authorization = dockerCredentialRetriever.retrieve();

      // Checks that some token was received.
      Assert.assertTrue(0 < authorization.getToken().length());

    } catch (NonexistentServerUrlDockerCredentialRetrievalException
        | NonexistentDockerCredentialHelperException ex) {

      Assume.assumeNoException("Skipping because docker-credential-gcr CLI not set up", ex);
    }
  }

  @Test
  public void testRetrieve_nonexistentCredentialHelper()
      throws IOException, NonexistentServerUrlDockerCredentialRetrievalException {
    try {
      DockerCredentialRetriever fakeDockerCredentialRetriever =
          new DockerCredentialRetriever("", "fake-cloud-provider");

      fakeDockerCredentialRetriever.retrieve();

      Assert.fail("Retrieve should have failed for nonexistent credential helper");

    } catch (NonexistentDockerCredentialHelperException ex) {
      Assert.assertEquals(
          "The system does not have docker-credential-fake-cloud-provider CLI", ex.getMessage());
    }
  }

  @Test
  public void testRetrieve_nonexistentServerUrl() throws IOException {
    try {
      DockerCredentialRetriever fakeDockerCredentialRetriever =
          new DockerCredentialRetriever("fake.server.url", "gcr");

      fakeDockerCredentialRetriever.retrieve();

      Assert.fail("Retrieve should have failed for nonexistent server URL");

    } catch (NonexistentServerUrlDockerCredentialRetrievalException ex) {
      Assert.assertEquals(
          "The credential store has nothing for server URL of fake.server.url", ex.getMessage());

    } catch (NonexistentDockerCredentialHelperException ex) {

      Assume.assumeNoException("Skipping because docker-credential-gcr CLI not set up", ex);
    }
  }
}
