package com.julienviet;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Definition of what contains a deck.
 */
public class DeckList implements Iterable<Card> {

  private static final Pattern LINE_RE = Pattern.compile("\\s*([0-9]+)\\s+(.+)");

  public static DeckList load(CardDb db, InputStream in) throws IOException {
    JsonArray array = new JsonArray();
    try (LineNumberReader reader = new LineNumberReader(new InputStreamReader(in))) {
      String line;
      while ((line = reader.readLine()) != null) {
        array.add(line);
      }
    }
    return load(db, new JsonObject().put("deck", array));
  }

  public static DeckList load(CardDb db, JsonObject json) {
    Builder builder = DeckList.builder();
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

    public DeckList build() {
      return new DeckList(new HashMap<>(list));
    }
  }

  private final Map<Card, Integer> list;
  private List<Card.Land> lands;
  private List<Card.Spell> spells;

  private DeckList(Map<Card, Integer> list) {
    this.list = list;
  }

  private <T extends Card> List<T> cards(Class<T> type) {
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

  private Map<Card.Land, Set<ManaType>> resolvedManaTypes = new LinkedHashMap<>();

  public Set<ManaType> resolveManaTypes(Card.Land card) {
    Set<ManaType> ret = resolvedManaTypes.get(card);
    if (ret == null) {
      Set<ManaType> resolved = new HashSet<>(card.manaTypes);
      if (card.fetchedTypes != null) {
        for (String s : card.fetchedTypes) {
          lands().forEach(land -> {
            if (land.subTypes.contains(s) || land.superTypes.contains(s)) {
              resolved.addAll(land.manaTypes);
            }
          });
        }
      }
      resolvedManaTypes.put(card, resolved);
      ret = resolved;
    }
    return ret;
  }


  public List<Card.Spell> spells() {
    if (spells == null) {
      spells = cards(Card.Spell.class);
    }
    return spells;
  }

  public int size() {
    return list.values().stream().mapToInt(amount -> amount).sum();
  }

  public List<Card.Land> lands() {
    if (lands == null) {
      lands = cards(Card.Land.class);
    }
    return lands;
  }

  @Override
  public Iterator<Card> iterator() {
    Iterator<Map.Entry<Card, Integer>> it = list.entrySet().iterator();
    return new Iterator<Card>() {
      Card card;
      int idx = 0;
      int size = 0;
      @Override
      public boolean hasNext() {
        while (idx >= size && it.hasNext()) {
          Map.Entry<Card, Integer> next = it.next();
          card = next.getKey();
          idx = 0;
          size = next.getValue();
        }
        return idx < size;
      }
      @Override
      public Card next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        idx++;
        return card;
      }
    };
  }

  public JsonObject toJson() {
    return new JsonObject(Collections.singletonMap("deck", list
      .entrySet()
      .stream()
      .map(e -> e.getValue() + " " + e.getKey())
      .collect(Collectors.toList())));
  }
}
