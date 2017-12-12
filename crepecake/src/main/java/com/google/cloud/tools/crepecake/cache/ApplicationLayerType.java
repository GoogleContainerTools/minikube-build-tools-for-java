package com.google.cloud.tools.crepecake.cache;

/** Types of application layers stored in cache. */
enum ApplicationLayerType {

  /** Layer of the application dependency JARs. */
  DEPENDENCIES,

  /** Layer of the application resources. */
  RESOURCES,

  /** Layer of the application classes. */
  CLASSES
}
