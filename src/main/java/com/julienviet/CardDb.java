package com.julienviet;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CardDb {

  public CardDb() {
    try {
      JsonArray array;
      try (ZipInputStream in = new ZipInputStream(CardDb.class.getClassLoader().getResourceAsStream("oracle-cards-20230109100204.json.zip"))) {
        ZipEntry entry = in.getNextEntry();
        byte[] bytes = loadFile(in);
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
    Card card = byName.get(name);
    return card;
  }

  public Card findById(String id) {
    return byId.get(id);
  }

  public static byte[] loadFile(InputStream in) throws IOException {
    try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
      byte[] buffer = new byte[1024];
      while (true) {
        int amount = in.read(buffer, 0, buffer.length);
        if (amount == -1) {
          break;
        }
        bos.write(buffer, 0, amount);
      }
      return bos.toByteArray();
    }
  }
}
