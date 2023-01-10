package com.julienviet.castability;

import io.vertx.core.json.JsonObject;

import java.util.HashMap;

public class KTableSet extends HashMap<String, KTable> {
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    forEach((color, ktable) -> {
      json.put(color, ktable.toJson());
    });
    return json;
  }
}
