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

import com.google.cloud.tools.minikube.command.BuildLogger;
import org.apache.maven.plugin.logging.Log;

class MavenBuildLogger implements BuildLogger {

  private final Log log;

  MavenBuildLogger(Log log) {
    this.log = log;
  }

  @Override
  public void lifecycle(CharSequence message) {
    log.info("[LIFECYCLE] " + message);
  }

  @Override
  public void info(CharSequence message) {
    log.info(message);
  }

  @Override
  public void debug(CharSequence message) {
    log.debug(message);
  }

  @Override
  public void warn(CharSequence message) {
    log.warn(message);
  }

  @Override
  public void error(CharSequence message) {
    log.error(message);
  }
}
