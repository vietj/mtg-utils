package com.julienviet.manabase;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CardDb {

  public CardDb() {
    try {
      JsonArray array;
      try (ZipInputStream in = new ZipInputStream(Main.class.getClassLoader().getResourceAsStream("oracle-cards-20230109100204.json.zip"))) {
        ZipEntry entry = in.getNextEntry();
        byte[] bytes = Main.loadFile(in);
        array = new JsonArray(Buffer.buffer(bytes));
      }
      for (int i = 0;i < array.size();i++) {
        JsonObject obj = array.getJsonObject(i);
        if ("card".equals(obj.getString("object"))) {
          try {
            Card.from(obj).forEach(card -> {
              byName.put(card.name, card);
              byId.put(card.id, card);
            });
          } catch (Exception e) {
            e.printStackTrace();
            // System.out.println("Cannot load " + name + " " + mana_cost);
            continue;
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // Name
  private Map<String, Card> byId = new HashMap<>();
  private Map<String, Card> byName = new HashMap<>();

  public Card findByName(String name) {
    return byName.get(name);
  }

  public Card findById(String id) {
    return byId.get(id);
  }
}
