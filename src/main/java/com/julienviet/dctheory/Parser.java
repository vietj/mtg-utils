package com.julienviet.dctheory;

import com.julienviet.Card;
import com.julienviet.CardDb;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.julienviet.Deck;

public class Parser {

  public static void main(String[] args) {
    parseURL("https://www.moxfield.com/decks/klRrszQcWUGZBcstoVp9Rg");
  }

  private static final String BASE_API = "https://api2.moxfield.com/v2/decks/all/";
  private static final Pattern MOXFIELD_URL = Pattern.compile(".*/([^/]+)$");

  public static void parseURL(String url) {
    Matcher matcher = MOXFIELD_URL.matcher(url);
    if (!matcher.matches()) {
      throw new IllegalArgumentException();
    }
    String deckID = matcher.group(1);
    String targetURL = BASE_API + deckID;
  }

  public static Deck toStruct(JsonObject jsonDeck) {
    CardDb db = new CardDb();
    Deck.Builder deckBuilder = Deck.builder();
    for (JsonObject o : Arrays.asList(jsonDeck.getJsonObject("companions"), jsonDeck.getJsonObject("commanders"), jsonDeck.getJsonObject("mainboard"))) {
      o.forEach(entry -> {
        JsonObject cardJson = ((JsonObject) entry.getValue()).getJsonObject("card");
        String name = cardJson.getString("name");
        Card card = db.findByName(name);
        if (card == null) {
          if (cardJson.containsKey("card_faces")) {
            JsonArray faces = cardJson.getJsonArray("card_faces");
            for (int i = 0;i < faces.size();i++) {
              JsonObject face = faces.getJsonObject(i);
              card = db.findByName(face.getString("name"));
              if (card != null) {
                break;
              }
            }
          }
          if (card == null) {
            System.out.println("NOT FOUND " + cardJson);
          }
        } else {
          deckBuilder.add(card);
        }
      });
    }
    return deckBuilder.build();
  }
}
