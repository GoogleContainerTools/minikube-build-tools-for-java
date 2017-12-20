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

import com.google.api.client.http.HttpResponseException;
import com.google.cloud.tools.crepecake.blob.Blobs;
import com.google.cloud.tools.crepecake.http.Authorization;
import com.google.cloud.tools.crepecake.http.Connection;
import com.google.cloud.tools.crepecake.http.Request;
import com.google.cloud.tools.crepecake.http.Response;
import com.google.cloud.tools.crepecake.image.json.ManifestTemplate;
import com.google.cloud.tools.crepecake.image.json.UnknownManifestFormatException;
import com.google.cloud.tools.crepecake.image.json.V21ManifestTemplate;
import com.google.cloud.tools.crepecake.image.json.V22ManifestTemplate;
import com.google.cloud.tools.crepecake.json.JsonTemplateMapper;
import com.google.cloud.tools.crepecake.registry.json.ErrorEntryTemplate;
import com.google.cloud.tools.crepecake.registry.json.ErrorResponseTemplate;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.annotation.Nullable;

/** Registry interface for pulling an image's manifest. */
public class ManifestPuller {

  // TODO: This should be configurable.
  private static final String PROTOCOL = "http";

  @Nullable private final Authorization authorization;
  private final String serverUrl;
  private final String baseImage;

  /** Instantiates a manifest template from a JSON string. */
  public static ManifestTemplate getManifestTemplateFromJson(String jsonString)
      throws IOException, UnknownManifestFormatException {
    ManifestTemplate manifestTemplate =
        JsonTemplateMapper.readJson(jsonString, ManifestTemplate.class);

    switch (manifestTemplate.getSchemaVersion()) {
      case 1:
        return JsonTemplateMapper.readJson(jsonString, V21ManifestTemplate.class);

      case 2:
        return JsonTemplateMapper.readJson(jsonString, V22ManifestTemplate.class);

      default:
        throw new UnknownManifestFormatException(
            "Unknown schemaVersion: " + manifestTemplate.getSchemaVersion());
    }
  }

  public ManifestPuller(@Nullable Authorization authorization, String serverUrl, String baseImage) {
    this.authorization = authorization;
    this.serverUrl = serverUrl;
    this.baseImage = baseImage;
  }

  public ManifestTemplate pull(String imageTag)
      throws IOException, RegistryErrorException, RegistryUnauthorizedException,
          UnknownManifestFormatException {
    URL pullUrl = getApiRoute("/manifests/" + imageTag);

    try (Connection connection = new Connection(pullUrl)) {
      Request request = new Request();
      if (null != authorization) {
        request.setAuthorization(authorization);
      }
      Response response = connection.get(request);
      String responseString = Blobs.writeToString(response.getBody());

      return getManifestTemplateFromJson(responseString);

    } catch (HttpResponseException ex) {
      switch (ex.getStatusCode()) {
        case HttpURLConnection.HTTP_BAD_REQUEST:
        case HttpURLConnection.HTTP_NOT_FOUND:
          // The name or reference was invalid.
          ErrorResponseTemplate errorResponse;
          errorResponse = JsonTemplateMapper.readJson(ex.getContent(), ErrorResponseTemplate.class);
          String method = "pull image manifest for " + serverUrl + "/" + baseImage + ":" + imageTag;
          RegistryErrorExceptionBuilder registryErrorExceptionBuilder =
              new RegistryErrorExceptionBuilder(method, ex);
          for (ErrorEntryTemplate errorEntry : errorResponse.getErrors()) {
            registryErrorExceptionBuilder.addErrorEntry(errorEntry);
          }

          throw registryErrorExceptionBuilder.toRegistryHttpException();

        case HttpURLConnection.HTTP_UNAUTHORIZED:
        case HttpURLConnection.HTTP_FORBIDDEN:
          throw new RegistryUnauthorizedException(ex);

        default: // Unknown
          throw ex;
      }
    }
  }

  private URL getApiRoute(String route) throws MalformedURLException {
    String apiBase = "/v2/";
    return new URL(PROTOCOL + "://" + serverUrl + apiBase + baseImage + route);
  }
}
