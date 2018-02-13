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

import com.google.common.collect.ImmutableList;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "stop")
class StopMojo extends AbstractMinikubeMojo {

  @Parameter private CommandConfiguration stop;

  @Override
  String getDescription() {
    return "Stopping minikube cluster";
  }

  @Override
  String getCommand() {
    return "stop";
  }

  @Override
  ImmutableList<String> getMoreFlags() {
    if (stop == null) {
      return ImmutableList.of();
    }
    return stop.getFlags();
  }
}
