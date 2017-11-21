package com.google.cloud.tools.crepecake.json;

import com.google.gson.*;

import java.lang.reflect.Type;

class DigestSerializer {
  public static class Serializer implements JsonSerializer<Digest> {
    @Override
    public JsonElement serialize(Digest src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.toString());
    }
  }

  public static class Deserializer implements JsonDeserializer<Digest> {
    @Override
    public Digest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      return new Digest(json.getAsJsonPrimitive().getAsString());
    }
  }
}
