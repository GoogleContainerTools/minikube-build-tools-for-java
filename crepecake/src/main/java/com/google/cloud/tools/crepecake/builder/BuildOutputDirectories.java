package com.google.cloud.tools.crepecake.builder;

import java.io.File;
import java.util.Set;

/**
 * Getters for the build output directories (dependencies, resources, and classes). Implementations
 * are specific to different build systems.
 */
public interface BuildOutputDirectories {

  /** @return the dependency JARs */
  Set<File> getDependencies();

  File getResourcesDirectory();

  File getClassesDirectory();
}
