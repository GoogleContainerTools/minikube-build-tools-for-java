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

package com.google.cloud.tools.crepecake.async;

import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.Nullable;

// TODO: Add ability to run code upon exception thrown.
/**
 * Executes code asynchronously and stores the returned value. {@link AsyncStep}s can depend on each
 * other such that {@link AsyncStep} runs after all of its dependencies have finished executing.
 *
 * <p>To ensure thread-safety, steps should not use any writable shared memory. Data should be
 * passed down only after execution.
 *
 * @param <R> the type of result to store
 */
abstract class AsyncStep<R> implements Runnable {

  // TODO: Change the number of threads.
  private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(4);

  /**
   * True if this step has finished executing; false otherwise. When this step has finished
   * executing, its result can be obtained with {@link #getResult()}.
   */
  private boolean finished = false;

  /** This task runs only after all dependency tasks are completed. */
  private DependencySet dependencies = new DependencySet();

  /** The future for running the asynchronous task. */
  private CompletableFuture<Void> future;

  /** Holds the result returned by {@link #execute()}. */
  @Nullable private R result;

  /** Executes the code for this step. */
  abstract R execute();

  /**
   * Runs the step to completion and ignores the result. This also runs all of the dependencies.
   * This would usually be called for the last step in the dependency tree such that the entire
   * graph of steps runs to completion.
   */
  @Override
  public final void run() {
    getFuture().join();
  }

  /** Add a dependency for this step. */
  final void dependsOn(AsyncStep<?>... dependency) {
    for (AsyncStep<?> asyncStep : dependency) {
      if (this == asyncStep) {
        throw new IllegalArgumentException("AsyncStep cannot depend on itself");
      }
    }
    dependencies.add(dependency);
  }

  /**
   * Gets the result stored after execution. This can only be called after the step has finished
   * executing (i.e. correct dependencies must be set).
   */
  final R getResult() {
    if (!finished) {
      throw new IllegalStateException("Cannot get result before execution finishes");
    }
    return result;
  }

  // TODO: Ensure that this is only called once per step.
  /**
   * Gets the future for this step. If the future has not been created, create the future based on
   * the dependencies.
   */
  @VisibleForTesting
  CompletableFuture<Void> getFuture() {
    if (null != future) {
      return future;
    }

    Runnable executionRunnable =
        () -> {
          result = execute();
          finished = true;
        };

    if (dependencies.isEmpty()) {
      // If there are no dependencies, create a new future that just executes this step.
      return future = CompletableFuture.runAsync(executionRunnable, THREAD_POOL);

    } else if (dependencies.hasOne()) {
      // If there is one dependency, execute this step after that dependency.
      return future =
          dependencies.getOnly().getFuture().thenRunAsync(executionRunnable, THREAD_POOL);

    } else {
      // If there are multiple dependencies, then execute this step after all dependencies have finished executing.
      CompletableFuture<Void> afterAllDependenciesFuture =
          CompletableFuture.allOf(
              dependencies.stream().map(AsyncStep::getFuture).toArray(CompletableFuture[]::new));

      return future = afterAllDependenciesFuture.thenRunAsync(executionRunnable, THREAD_POOL);
    }
  }
}
