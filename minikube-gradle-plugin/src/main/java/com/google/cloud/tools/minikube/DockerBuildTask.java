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

import com.google.cloud.tools.minikube.util.CommandExecutor;
import com.google.cloud.tools.minikube.util.MinikubeDockerEnvParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.provider.PropertyState;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

/** Task to build Docker images. */
public class DockerBuildTask extends DefaultTask {
  /**
   * Matches a name component of a Docker image tag.
   *
   * <p>Name components may contain lowercase letters, digits and separators. A separator is defined
   * as a period, one or two underscores, or one or more dashes. A name component may not start or
   * end with a separator.
   *
   * @see <a href="https://docs.docker.com/engine/reference/commandline/tag/#description">docker
   *     tag</a>
   */
  private static final String NAME_COMPONENT_REGEX = "^[a-z0-9]+((_|__|\\.|-+)[a-z0-9]+)*$";

  /**
   * Matches a tag name portion of a Docker image tag.
   *
   * <p>A tag name may contain lowercase and uppercase letters, digits, underscores, periods and
   * dashes. A tag name may not start with a period or a dash and may contain a maximum of 128
   * characters.
   *
   * @see <a href="https://docs.docker.com/engine/reference/commandline/tag/#description">docker
   *     tag</a>
   */
  private static final String TAG_NAME_REGEX = "^[\\w_]+[\\w_\\.-]*$";

  /** minikube executable : lazily evaluated from extension input */
  private PropertyState<String> minikube;
  /** docker executable : lazily evaluated from extension input */
  private PropertyState<String> docker;
  /** The set of files to build (PATH | URL | -) */
  private String context;
  /** Flags passthrough */
  private String[] flags = {};
  /** The tag for the built image */
  private String tag;

  private Logger logger = getLogger();

  public DockerBuildTask() {
    minikube = getProject().property(String.class);
    docker = getProject().property(String.class);
    context = getProject().getBuildDir().toPath().resolve("libs").toString();
    tag = buildDefaultTag();
  }

  // @VisibleForTesting
  class CommandExecutorFactory {
    CommandExecutor createCommandExecutor() {
      return new CommandExecutor().setLogger(getLogger());
    }
  }

  // @VisibleForTesting
  DockerBuildTask setCommandExecutorFactory(CommandExecutorFactory commandExecutorFactory) {
    this.commandExecutorFactory = commandExecutorFactory;
    return this;
  }

  private CommandExecutorFactory commandExecutorFactory = new CommandExecutorFactory();

  // @VisibleForTesting
  void setLogger(Logger logger) {
    this.logger = logger;
  }

  @Input
  public String getMinikube() {
    return minikube.get();
  }

  public void setMinikube(String minikube) {
    this.minikube.set(minikube);
  }

  public void setMinikube(PropertyState<String> minikube) {
    this.minikube = minikube;
  }

  @Input
  public String getDocker() {
    return docker.get();
  }

  public void setDocker(String docker) {
    this.docker.set(docker);
  }

  public void setDocker(PropertyState<String> docker) {
    this.docker = docker;
  }

  @Input
  public String getContext() {
    return context;
  }

  public void setContext(String context) {
    this.context = context;
  }

  @Input
  public String[] getFlags() {
    return flags;
  }

  public void setFlags(String[] flags) {
    this.flags = flags;
  }

  @Input
  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  @TaskAction
  public void execDockerBuild() throws IOException, InterruptedException {
    // Gets the minikube docker environment variables by running the command 'minikube docker-env'.
    List<String> minikubeDockerEnvCommand =
        Arrays.asList(minikube.get(), "docker-env --shell none");
    List<String> dockerEnv =
        commandExecutorFactory.createCommandExecutor().run(minikubeDockerEnvCommand);

    Map<String, String> environment;
    environment = MinikubeDockerEnvParser.parse(dockerEnv);

    // Runs the docker build command with the minikube docker environment.
    List<String> dockerBuildCommand = buildDockerBuildCommand();
    commandExecutorFactory
        .createCommandExecutor()
        .setEnvironment(environment)
        .run(dockerBuildCommand);
  }

  // @VisibleForTesting
  List<String> buildDockerBuildCommand() {
    List<String> execString = new ArrayList<>();
    execString.add(docker.get());
    execString.add("build");

    if (!tag.isEmpty()) {
      execString.add("-t");
      execString.add(tag);
    }

    execString.addAll(Arrays.asList(flags));
    execString.add(context);

    return execString;
  }

  /**
   * Builds the default tag for the built image in the form: {@code
   * ${project.group}/${project.name}:${project.version}}.
   *
   * <p>If {@code ${project.group}} is empty, {@code ${project.group}/} will not be included.
   *
   * <p>If {@code ${project.version}} is empty, {@code :${project.version}} will not be included.
   *
   * @return the built tag, or null if any part does not satisfy Docker tag rules
   * @see <a href="https://docs.docker.com/engine/reference/commandline/tag/#description">docker
   *     tag</a>
   */
  // @VisibleForTesting
  String buildDefaultTag() {
    String group = getProject().getGroup().toString();
    String projectName = getProject().getName().toString();
    String projectVersion = getProject().getVersion().toString();

    StringBuilder tagBuilder = new StringBuilder();

    if (!group.isEmpty()) {
      // Checks if project.group can be used as part of the image name.
      if (!group.matches(NAME_COMPONENT_REGEX)) {
        logger.warn(
            "Default image tag could not be generated because project.group is not a valid name component");
        return null;
      }
      tagBuilder.append(group).append("/");
    }

    // Checks if project.name can be used as part of the image name.
    if (!projectName.matches(NAME_COMPONENT_REGEX)) {
      logger.warn(
          "Default image tag could not be generated because project.name is not a valid name component");
      return null;
    }
    tagBuilder.append(projectName);

    if (!"unspecified".equals(projectVersion)) {
      // Checks if project.version can be used as the image tag name.
      if (!projectVersion.matches(TAG_NAME_REGEX)) {
        logger.warn(
            "Default image tag could not be generated because project.version is not a valid tag name");
        return null;
      }
      tagBuilder.append(":").append(projectVersion);
    }

    return tagBuilder.toString();
  }
}
