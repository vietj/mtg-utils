package com.julienviet.dctheory;

import io.vertx.core.json.JsonObject;

import java.util.Collections;
import java.util.List;

public class Card {

  public static List<Card> card(JsonObject json) {
    if (json.containsKey("card_faces")) {
      throw new UnsupportedOperationException();
    } else {
      return Collections.singletonList(new Card(json));
    }
  }

  private final JsonObject json;

  private Card(JsonObject json) {
    this.json = json;
  }
}
