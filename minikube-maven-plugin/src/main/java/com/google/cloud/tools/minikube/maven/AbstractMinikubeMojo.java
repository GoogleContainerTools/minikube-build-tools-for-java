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

import com.google.cloud.tools.minikube.command.CommandExecutor;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

abstract class AbstractMinikubeMojo extends AbstractMojo {

  /** Path to minikube executable. */
  @Parameter(defaultValue = "minikube", required = true)
  private String minikube;

  /** Common flags to add when calling minikube. */
  @Parameter private ImmutableList<String> flags = ImmutableList.of();

  private Supplier<CommandExecutor> commandExecutorSupplier = CommandExecutor::new;
  private MavenBuildLogger mavenBuildLogger = new MavenBuildLogger(getLog());

  @Override
  public void execute() throws MojoExecutionException {
    List<String> minikubeCommand = buildMinikubeCommand();

    try {
      commandExecutorSupplier.get().setLogger(mavenBuildLogger).run(minikubeCommand);

    } catch (InterruptedException | IOException ex) {
      throw new MojoExecutionException(getDescription() + " failed", ex);
    }
  }

  @VisibleForTesting
  void setMinikube(String minikube) {
    this.minikube = minikube;
  }

  @VisibleForTesting
  void setFlags(ImmutableList<String> flags) {
    this.flags = flags;
  }

  @VisibleForTesting
  void setMavenBuildLogger(MavenBuildLogger mavenBuildLogger) {
    this.mavenBuildLogger = mavenBuildLogger;
  }

  @VisibleForTesting
  void setCommandExecutorSupplier(Supplier<CommandExecutor> commandExecutorSupplier) {
    this.commandExecutorSupplier = commandExecutorSupplier;
  }

  /** @return what this goal does */
  abstract String getDescription();

  /** @return the minikube command this goal runs */
  abstract String getCommand();

  /** @return command-specific flags */
  abstract ImmutableList<String> getMoreFlags();

  @VisibleForTesting
  List<String> buildMinikubeCommand() {
    List<String> execString = new ArrayList<>();
    execString.add(minikube);
    execString.add(getCommand());
    execString.addAll(flags);
    execString.addAll(getMoreFlags());

    return execString;
  }
}
