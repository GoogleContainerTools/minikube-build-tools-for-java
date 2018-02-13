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
import java.util.List;
import javax.annotation.Nullable;

/** Additional Maven configuration for commands. */
public class CommandConfiguration {

  /** Additional flags to pass to the command. */
  @Nullable private List<String> flags;

  ImmutableList<String> getFlags() {
    if (flags == null) {
      return ImmutableList.of();
    }
    return ImmutableList.copyOf(flags);
  }
}
