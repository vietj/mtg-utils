package com.julienviet.manabase;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Deck {

  private static final Pattern LINE_RE = Pattern.compile("\\s*([0-9]+)\\s+(.+)");

  public static Deck load(CardDb db, InputStream in) throws IOException {
    JsonArray array = new JsonArray();
    try (LineNumberReader reader = new LineNumberReader(new InputStreamReader(in))) {
      String line;
      while ((line = reader.readLine()) != null) {
        array.add(line);
      }
    }
    return load(db, new JsonObject().put("deck", array));
  }

  public static Deck load(CardDb db, JsonObject json) {
    Builder builder = Deck.builder();
    JsonArray list = json.getJsonArray("deck");
    for (int i = 0;i < list.size();i++) {
      String entry = list.getString(i);
      Matcher matcher = LINE_RE.matcher(entry);
      if (!matcher.matches()) {
        throw new IllegalArgumentException("Invalid deck entry " + entry);
      }
      int quantity = Integer.parseInt(matcher.group(1));
      String cardName = matcher.group(2);
      Card card = db.findByName(cardName);
      if (card == null) {
        throw new IllegalArgumentException("Card not found " + cardName);
      }
      builder.add(card, quantity);
    }
    return builder.build();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private final Map<Card, Integer> list = new HashMap<>();

    public Builder add(Card card) {
      return add(card, 1);
    }

    public Builder add(Card card, int quantity) {
      Integer amount = list.get(card);
      if (amount == null) {
        amount = 0;
      }
      amount += quantity;
      list.put(card, amount);
      return this;
    }

    public Deck build() {

      Map<Card, Integer> newList = new HashMap<>();

      List<Card.Land> lands = list.keySet().stream().filter(c -> c instanceof Card.Land).map(c -> (Card.Land) c).collect(Collectors.toList());

      list.entrySet().forEach(entry -> {
        Card card = entry.getKey();
        if (card instanceof Card.Land) {
          Card.Land land = (Card.Land) card;
          if (land.fetchedTypes != null) {
            land = land.mutable();
            land.resolveManaTypes(lands);
            card = land;
          }
        }
        newList.put(card, entry.getValue());
      });

      return new Deck(newList);
    }
  }

  private final Map<Card, Integer> list;

  private Deck(Map<Card, Integer> list) {
    this.list = list;
  }

  public <T extends Card> List<T> cards(Class<T> type) {
    List<T> cards = new ArrayList<>();
    for (Map.Entry<Card, Integer> entry : list.entrySet()) {
      Card card = entry.getKey();
      if (type.isInstance(card)) {
        for (int i = 0;i < entry.getValue();i++) {
          cards.add(type.cast(card));
        }
      }
    }
    return cards;
  }

  public List<Card.Spell> spells() {
    return cards(Card.Spell.class);
  }

  public int size() {
    return list.values().stream().mapToInt(amount -> amount).sum();
  }

  public List<Card.Land> lands() {
    return cards(Card.Land.class);
  }

  public JsonObject toJson() {
    return new JsonObject(Collections.singletonMap("deck", list
      .entrySet()
      .stream()
      .map(e -> e.getValue() + " " + e.getKey())
      .collect(Collectors.toList())));
  }
}
