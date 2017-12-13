package com.google.cloud.tools.crepecake.async;

import java.util.Arrays;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Stream;

/** Encapsulates an {@link AsyncStep}'s dependencies. */
class DependencySet {

  private final Set<AsyncStep<?>> dependencies = new HashSet<>();

  void add(AsyncStep<?>... dependency) {
    dependencies.addAll(Arrays.asList(dependency));
  }

  boolean isEmpty() {
    return dependencies.isEmpty();
  }

  boolean hasOne() {
    return 1 == dependencies.size();
  }

  AsyncStep<?> getOnly() {
    if (!hasOne()) {
      throw new NoSuchElementException("Dependency set does not have exactly one dependency");
    }
    return dependencies.iterator().next();
  }

  Stream<AsyncStep<?>> stream() {
    return dependencies.stream();
  }
}
