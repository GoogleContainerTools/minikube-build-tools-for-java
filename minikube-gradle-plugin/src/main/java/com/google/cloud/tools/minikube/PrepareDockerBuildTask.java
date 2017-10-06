/*
 * Copyright (c) 2017 Google Inc. All Right Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.google.cloud.tools.minikube;

import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Sync;

/** Prepares a build context and Dockerfile for DockerBuildTask. */
public class PrepareDockerBuildTask extends Sync {

  /** The set of files to use for the Docker build */
  private String context;
  /** The path to the Dockerfile */
  private String dockerfile;

  public PrepareDockerBuildTask() {
    context = getProject().getBuildDir().toPath().resolve("libs").toString();
    dockerfile =
        getProject()
            .getProjectDir()
            .toPath()
            .resolve("src")
            .resolve("main")
            .resolve("docker")
            .resolve("Dockerfile")
            .toString();

    updateFrom();
    into(getProject().getBuildDir().toPath().resolve("docker").toString());
  }

  @Input
  public String getContext() {
    return context;
  }

  public void setContext(String context) {
    this.context = context;
    updateFrom();
  }

  @Input
  public String getDockerfile() {
    return dockerfile;
  }

  public void setDockerfile(String dockerfile) {
    this.dockerfile = dockerfile;
    updateFrom();
  }

  private void updateFrom() {
    from(context, dockerfile);
  }
}
