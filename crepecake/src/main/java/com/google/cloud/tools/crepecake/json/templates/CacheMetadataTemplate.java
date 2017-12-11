package com.google.cloud.tools.crepecake.json.templates;

import com.google.cloud.tools.crepecake.blob.BlobDescriptor;
import com.google.cloud.tools.crepecake.image.DescriptorDigest;
import com.google.cloud.tools.crepecake.json.JsonTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON template for storing metadata about the cache.
 *
 * Example:
 *
 * <pre>{@code
 * {
 *   "baseImageLayers": [
 *     {
 *       "digest": "sha256:5f70bf18a086007016e948b04aed3b82103a36bea41755b6cddfaf10ace3c6ef",
 *       "size": 631,
 *       "diffId": "sha256:b56ae66c29370df48e7377c8f9baa744a3958058a766793f821dadcb144a4647",
 *       lastModifiedTime: 255073580723571
 *     },
 *     ...
 *   ],
 *   "dependenciesLayer": { ... },
 *   "resourcesLayer": { ... },
 *   "classesLayer": { ... }
 * }
 * }</pre>
 */
public class CacheMetadataTemplate extends JsonTemplate {

  private final List<LayerObjectTemplate> baseImageLayers = new ArrayList<>();

  private LayerObjectTemplate dependenciesLayer;
  private LayerObjectTemplate resourcesLayer;
  private LayerObjectTemplate classesLayer;

  /**
   * Template for inner JSON object representing a layer as part of the list of layer references.
   */
  public static class LayerObjectTemplate extends JsonTemplate {

    private DescriptorDigest digest;
    private long size;
    private DescriptorDigest diffId;
    private long lastModifiedTime;

    public DescriptorDigest getDigest() {
      return digest;
    }

    public long getSize() {
      return size;
    }

    public DescriptorDigest getDiffId() {
      return diffId;
    }

    public long getLastModifiedTime() {
      return lastModifiedTime;
    }
  }

  public List<LayerObjectTemplate> getBaseImageLayers() {
    return baseImageLayers;
  }

  public LayerObjectTemplate getDependenciesLayer() {
    return dependenciesLayer;
  }

  public LayerObjectTemplate getResourcesLayer() {
    return resourcesLayer;
  }

  public LayerObjectTemplate getClassesLayer() {
    return classesLayer;
  }
}
