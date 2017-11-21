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

package com.google.cloud.tools.crepecake.json;

import com.google.cloud.tools.crepecake.image.Digest;
import com.google.cloud.tools.crepecake.image.DigestException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/** Tests for {@link JsonParser}. */
public class JsonParserTest {
  private static class TestJson {
    private int number;
    private String text;
    private Digest digest;
    private transient String transientString;
    private InnerObject innerObject;
    private List<InnerObject> list;

    private static class InnerObject {
      private int number;
      private List<String> texts;
      private List<Digest> digests;
    }
  }

  @Test
  public void testFromJson() throws DigestException {
    StringBuffer jsonBuffer = new StringBuffer();
    jsonBuffer.append("{");
    jsonBuffer.append("number: 54,");
    jsonBuffer.append("text: \"crepecake\",");
    jsonBuffer.append(
        "digest: \"sha256:8c662931926fa990b41da3c9f42663a537ccd498130030f9149173a0493832ad\",");
    jsonBuffer.append("transientString: \"notpartofjson\",");
    jsonBuffer.append("innerObject: {");
    jsonBuffer.append("number: 23,");
    jsonBuffer.append("texts: [\"first text\", \"second text\"],");
    jsonBuffer.append("digests: [");
    jsonBuffer.append(
        "\"sha256:91e0cae00b86c289b33fee303a807ae72dd9f0315c16b74e6ab0cdbe9d996c10\",");
    jsonBuffer.append(
        "\"sha256:4945ba5011739b0b98c4a41afe224e417f47c7c99b2ce76830999c9a0861b236\"");
    jsonBuffer.append("]");
    jsonBuffer.append("},");
    jsonBuffer.append("list: [");
    jsonBuffer.append("{");
    jsonBuffer.append("number: 42,");
    jsonBuffer.append("texts: [],");
    jsonBuffer.append("digests: []");
    jsonBuffer.append("},");
    jsonBuffer.append("{");
    jsonBuffer.append("number: 99,");
    jsonBuffer.append("texts: [\"some text\"],");
    jsonBuffer.append(
        "digests: [\"sha256:d38f571aa1c11e3d516e0ef7e513e7308ccbeb869770cb8c4319d63b10a0075e\"]");
    jsonBuffer.append("}");
    jsonBuffer.append("]");
    jsonBuffer.append("}");

    final String json = jsonBuffer.toString();

    TestJson testJson = JsonParser.fromJson(json, TestJson.class);

    Assert.assertEquals(54, testJson.number);
    Assert.assertEquals("crepecake", testJson.text);
    Assert.assertEquals(
        Digest.fromDigest(
            "sha256:8c662931926fa990b41da3c9f42663a537ccd498130030f9149173a0493832ad"),
        testJson.digest);
    Assert.assertNull(testJson.transientString);
    Assert.assertEquals(23, testJson.innerObject.number);
    Assert.assertEquals(2, testJson.innerObject.texts.size());
    Assert.assertEquals("first text", testJson.innerObject.texts.get(0));
    Assert.assertEquals("second text", testJson.innerObject.texts.get(1));
    Assert.assertEquals(2, testJson.innerObject.digests.size());
    Assert.assertEquals(
        Digest.fromDigest(
            "sha256:91e0cae00b86c289b33fee303a807ae72dd9f0315c16b74e6ab0cdbe9d996c10"),
        testJson.innerObject.digests.get(0));
    Assert.assertEquals(
        Digest.fromHash("4945ba5011739b0b98c4a41afe224e417f47c7c99b2ce76830999c9a0861b236"),
        testJson.innerObject.digests.get(1));
    Assert.assertEquals(42, testJson.list.get(0).number);
    Assert.assertEquals(0, testJson.list.get(0).texts.size());
    Assert.assertEquals(0, testJson.list.get(0).digests.size());
    Assert.assertEquals(99, testJson.list.get(1).number);
    Assert.assertEquals(1, testJson.list.get(1).texts.size());
    Assert.assertEquals("some text", testJson.list.get(1).texts.get(0));
    Assert.assertEquals(1, testJson.list.get(1).digests.size());
    Assert.assertEquals(
        Digest.fromDigest(
            "sha256:d38f571aa1c11e3d516e0ef7e513e7308ccbeb869770cb8c4319d63b10a0075e"),
        testJson.list.get(1).digests.get(0));
  }

  @Test
  public void testToJson() throws DigestException {
    final String expectedJson =
        "{\"number\":54,\"text\":\"crepecake\",\"digest\":"
            + "\"sha256:8c662931926fa990b41da3c9f42663a537ccd498130030f9149173a0493832ad\",\"innerObject\":"
            + "{\"number\":23,\"texts\":[\"first text\",\"second text\"],\"digests\":["
            + "\"sha256:91e0cae00b86c289b33fee303a807ae72dd9f0315c16b74e6ab0cdbe9d996c10\","
            + ""
            + "\"sha256:4945ba5011739b0b98c4a41afe224e417f47c7c99b2ce76830999c9a0861b236\"]},\"list\":["
            + "{\"number\":42,\"texts\":[]},{\"number\":99,\"texts\":[\"some text\"],\"digests\":["
            + "\"sha256:d38f571aa1c11e3d516e0ef7e513e7308ccbeb869770cb8c4319d63b10a0075e\"]}]}";

    TestJson testJson = new TestJson();
    testJson.number = 54;
    testJson.text = "crepecake";
    testJson.digest =
        Digest.fromDigest(
            "sha256:8c662931926fa990b41da3c9f42663a537ccd498130030f9149173a0493832ad");
    testJson.transientString = "notpartofjson";
    testJson.innerObject = new TestJson.InnerObject();
    testJson.innerObject.number = 23;
    testJson.innerObject.texts = Arrays.asList("first text", "second text");
    testJson.innerObject.digests =
        Arrays.asList(
            Digest.fromDigest(
                "sha256:91e0cae00b86c289b33fee303a807ae72dd9f0315c16b74e6ab0cdbe9d996c10"),
            Digest.fromHash("4945ba5011739b0b98c4a41afe224e417f47c7c99b2ce76830999c9a0861b236"));

    TestJson.InnerObject innerObject1 = new TestJson.InnerObject();
    innerObject1.number = 42;
    innerObject1.texts = Collections.emptyList();
    TestJson.InnerObject innerObject2 = new TestJson.InnerObject();
    innerObject2.number = 99;
    innerObject2.texts = Collections.singletonList("some text");
    innerObject2.digests =
        Collections.singletonList(
            Digest.fromDigest(
                "sha256:d38f571aa1c11e3d516e0ef7e513e7308ccbeb869770cb8c4319d63b10a0075e"));
    testJson.list = Arrays.asList(innerObject1, innerObject2);

    String json = JsonParser.toJson(testJson);

    Assert.assertEquals(expectedJson, json);
  }
}
