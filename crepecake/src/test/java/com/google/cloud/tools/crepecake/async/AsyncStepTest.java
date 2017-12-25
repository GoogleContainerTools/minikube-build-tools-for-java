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

import java.util.concurrent.CompletionException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;

/** Tests for {@link AsyncStep}. */
public class AsyncStepTest {

  /** {@link AsyncStep} that only finishes execution when unlatched. */
  private static class LatchedAsyncStep extends AsyncStep<Void> {

    /**
     * A counter that is shared between instances of {@link LatchedAsyncStep}. Note that this is
     * only for testing purposes and in real subclasses of {@link AsyncStep}, no writeable memory
     * should be shared.
     */
    private static final AtomicInteger positionCounter = new AtomicInteger(1);

    /** A latch that pauses execution until released. */
    private final CountDownLatch latch = new CountDownLatch(1);

    private int position;

    /** Allows the task to finish running. */
    private void unlatch() {
      latch.countDown();
    }

    @Override
    Void execute() {
      try {
        latch.await();
        position = positionCounter.getAndIncrement();
        return null;
      } catch (InterruptedException ex) {
        throw new CompletionException(ex);
      }
    }
  }

  // TODO: Add test using #getResult().

  @Test
  public void testRun_correctOrder() {
    // Creates 5 steps.
    LatchedAsyncStep[] step =
        IntStream.range(0, 6)
            .mapToObj(index -> new LatchedAsyncStep())
            .toArray(LatchedAsyncStep[]::new);

    // Establishes dependencies.
    step[2].dependsOn(step[0]);
    step[3].dependsOn(step[1], step[2]);
    step[4].dependsOn(step[1]);
    step[5].dependsOn(step[0], step[1], step[2], step[3], step[4]);

    // Starts executing all the steps.
    step[5].getFuture();

    // Unlatches the steps in reverse order.
    IntStream.range(0, 6)
        .forEach(
            index -> {
              step[5 - index].unlatch();
            });

    // Waits until completion.
    step[5].run();

    Assert.assertTrue(step[2].position > step[0].position);
    Assert.assertTrue(step[3].position > step[1].position);
    Assert.assertTrue(step[3].position > step[2].position);
    Assert.assertTrue(step[4].position > step[1].position);
    Assert.assertTrue(step[5].position > step[0].position);
    Assert.assertTrue(step[5].position > step[1].position);
    Assert.assertTrue(step[5].position > step[2].position);
    Assert.assertTrue(step[5].position > step[3].position);
    Assert.assertTrue(step[5].position > step[4].position);
  }
}
