package com.google.cloud.tools.crepecake.json.templates;

import com.google.cloud.tools.crepecake.image.Digest;
import com.google.cloud.tools.crepecake.image.DigestException;
import com.google.cloud.tools.crepecake.json.JsonParser;
import com.google.common.io.CharStreams;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

/** Tests for {@link ContainerConfigurationTemplate}. */
public class ContainerConfigurationTemplateTest {

  @Test
  public void testToJson() throws DigestException, IOException, URISyntaxException {
    // Loads the expected JSON string.
    File jsonFile =
        new File(getClass().getClassLoader().getResource("json/containerconfig.json").toURI());
    final String expectedJson =
        CharStreams.toString(new InputStreamReader(new FileInputStream(jsonFile)));

    // Creates the JSON object to serialize.
    ContainerConfigurationTemplate containerConfigJson = new ContainerConfigurationTemplate();

    containerConfigJson.setContainerEnvironment(Arrays.asList("VAR1=VAL1", "VAR2=VAL2"));
    containerConfigJson.setContainerEntrypoint(Arrays.asList("some", "entrypoint", "command"));

    containerConfigJson.addLayerDiffId(
        Digest.fromDigest(
            "sha256:8c662931926fa990b41da3c9f42663a537ccd498130030f9149173a0493832ad"));

    // Serializes the JSON object.
    ByteArrayOutputStream jsonStream = new ByteArrayOutputStream();
    JsonParser.writeJson(jsonStream, containerConfigJson);

    Assert.assertEquals(expectedJson, jsonStream.toString());
  }

  @Test
  public void testFromJson() throws IOException, URISyntaxException, DigestException {
    // Loads the JSON string.
    File jsonFile =
        new File(getClass().getClassLoader().getResource("json/containerconfig.json").toURI());

    // Deserializes into a manifest JSON object.
    ContainerConfigurationTemplate containerConfigJson =
        JsonParser.readJsonFromFile(jsonFile, ContainerConfigurationTemplate.class);

    Assert.assertEquals(
        Arrays.asList("VAR1=VAL1", "VAR2=VAL2"), containerConfigJson.getContainerEnvironment());

    Assert.assertEquals(
        Arrays.asList("some", "entrypoint", "command"),
        containerConfigJson.getContainerEntrypoint());

    Assert.assertEquals(
        Digest.fromDigest(
            "sha256:8c662931926fa990b41da3c9f42663a537ccd498130030f9149173a0493832ad"),
        containerConfigJson.getLayerDiffId(0));
  }
}
