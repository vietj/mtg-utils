package com.julienviet.dctheory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    Deck deck = new Deck();

    JsonObject companions = jsonDeck.getJsonObject("companions");
    companions.forEach(entry -> {
      List<Card> cards = Card.card(((JsonObject) entry.getValue()).getJsonObject("card"));
      deck.addCard(cards);
    });

    //
    JsonObject commanders = jsonDeck.getJsonObject("commanders");
    commanders.forEach(entry -> {
      List<Card> cards = Card.card(((JsonObject) entry.getValue()).getJsonObject("card"));
      deck.addCard(cards);
    });

    JsonObject mainboard = jsonDeck.getJsonObject("mainboard");
    mainboard.forEach(entry -> {
      List<Card> cards = Card.card(((JsonObject) entry.getValue()).getJsonObject("card"));
      deck.addCard(cards);
    });

    return deck;
  }
}
