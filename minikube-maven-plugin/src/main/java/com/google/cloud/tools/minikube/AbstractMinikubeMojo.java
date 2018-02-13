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

package com.google.cloud.tools.minikube;

import com.google.cloud.tools.minikube.command.CommandExecutor;
import com.google.common.annotations.VisibleForTesting;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class AbstractMinikubeMojo extends AbstractMojo {

  /** Path to minikube executable. */
  @Parameter(defaultValue = "minikube", required = true)
  private String minikube;

  /** Flags to add when calling minikube. */
  @Parameter
  private List<String> flags = Collections.emptyList();

  private Supplier<CommandExecutor> commandExecutorSupplier = CommandExecutor::new;

  @Override
  public void execute() throws MojoExecutionException {
    List<String> minikubeCommand = buildMinikubeCommand();

    try {
      commandExecutorSupplier.get().setLogger(new MavenBuildLogger(getLog())).run(minikubeCommand);

    } catch (InterruptedException | IOException ex) {
      throw new MojoExecutionException(getDescription() + " failed", ex);
    }
  }

  @VisibleForTesting
  void setCommandExecutorSupplier(Supplier<CommandExecutor> commandExecutorSupplier) {
    this.commandExecutorSupplier = commandExecutorSupplier;
  }

  /** @return what this goal does */
  abstract String getDescription();

  /** @return the minikube command this goal runs */
  abstract String getCommand();

  @VisibleForTesting
  List<String> buildMinikubeCommand() {
    List<String> execString = new ArrayList<>();
    execString.add(minikube);
    execString.add(getCommand());
    execString.addAll(flags);

    return execString;
  }
}
