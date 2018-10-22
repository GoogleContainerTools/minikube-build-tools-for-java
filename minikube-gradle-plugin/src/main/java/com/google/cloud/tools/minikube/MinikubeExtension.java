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

package com.google.cloud.tools.minikube;

import com.google.cloud.tools.minikube.util.CommandExecutorFactory;
import com.google.cloud.tools.minikube.util.MinikubeDockerEnvParser;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.gradle.api.Project;
import org.gradle.api.provider.PropertyState;

/** Minikube configuration extension. */
public class MinikubeExtension {

  private final PropertyState<String> minikube;

  private final CommandExecutorFactory commandExecutorFactory;

  public MinikubeExtension(Project project, CommandExecutorFactory commandExecutorFactory) {
    minikube = project.property(String.class);
    setMinikube("minikube");

    this.commandExecutorFactory = commandExecutorFactory;
  }

  public String getMinikube() {
    return minikube.get();
  }

  public void setMinikube(String minikube) {
    this.minikube.set(minikube);
  }

  public PropertyState<String> getMinikubeProvider() {
    return minikube;
  }

  /**
   * Gets the minikube docker environment variables by running the command 'minikube docker-env
   * --shell=none'.
   *
   * @return A map of docker environment variables and their values
   */
  public Map<String, String> getDockerEnv() throws IOException, InterruptedException {
    return getDockerEnv("");
  }

  /**
   * Gets the minikube docker environment variables by running the command 'minikube docker-env
   * --shell=none'.
   *
   * @param profile target minikube profile
   * @return A map of docker environment variables and their values
   */
  public Map<String, String> getDockerEnv(String profile) throws IOException, InterruptedException {
    Preconditions.checkNotNull(profile, "Minikube profile must not be null");

    List<String> minikubeDockerEnvCommand =
        Arrays.asList(minikube.get(), "docker-env", "--shell=none", "--profile=" + profile);

    List<String> dockerEnv =
        commandExecutorFactory.newCommandExecutor().run(minikubeDockerEnvCommand);

    return MinikubeDockerEnvParser.parse(dockerEnv);
  }
}
