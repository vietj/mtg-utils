package com.julienviet.manabase;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Card {

  private static final Pattern FETCH_LAND = Pattern.compile(
    "Search your library for an? (basic land|Plains|Island|Swamp|Mountain|Forest)(?: or )?" +
      "(Plains|Island|Swamp|Mountain|Forest)? card, put it onto the battlefield\\s?(tapped)?" +
      ", then shuffle( your library)?\\.(?: Then if you control )?(four or more)?(?: lands, untap that land.)?"
  );
  private static final Pattern SLOW_LAND = Pattern.compile("enters the battlefield tapped unless you control two or more other lands.");
  private static final Pattern VERY_FAST_LAND = Pattern.compile("If you control two or more other lands, [\\w ]+ enters the battlefield tapped.");
  private static final Pattern FAST_LAND = Pattern.compile("enters the battlefield tapped unless you control two or fewer other lands.");
  private static final Pattern ETB_TAPPED = Pattern.compile("(enters|onto) the battlefield tapped");
  private static final Pattern RAV_LAND = Pattern.compile("you may pay \\d life. If you don't, it enters the battlefield tapped.");
  private static final Pattern CHECK_LAND = Pattern.compile("enters the battlefield tapped unless you control an? (Plains|Island|Swamp|Mountain|Forest)( or an? (Plains|Island|Swamp|Mountain|Forest))?\\.");

  public static List<Card> from(JsonObject json) {
    if (json.containsKey("card_faces") && json.getJsonArray("card_faces").size() > 0) {
      JsonArray faces = json.getJsonArray(("card_faces"));
      return faces
        .stream()
        .map(o -> (JsonObject)o)
        .flatMap(card_face -> from(card_face).stream())
        .collect(Collectors.toList());
    } else {
      if (!json.containsKey("type_line") ) {
        // Not cards
        return Collections.emptyList();
      }
      String id = json.getString("id");
      String name = json.getString("name");
      String typeLine = json.getString("type_line");
      if (typeLine.equals("Card")) {
        return Collections.emptyList();
      }
      String l;
      Set<String> subTypes;
      int idx = typeLine.indexOf(" — ");
      if (idx != -1) {
        l = typeLine.substring(0, idx);
        subTypes = new HashSet<>(Arrays.asList(typeLine.substring(idx + 3).split(" ")));
      } else {
        l = typeLine;
        subTypes = Collections.emptySet();
      }
      String[] s = l.split(" ");
      String type = s[s.length - 1];
      Set<String> superTypes = new HashSet<>();
      for (int j = 0; j < s.length - 1;j++) {
        superTypes.add(s[j]);
      }

      String mana_cost = json.getString("mana_cost");

      Card card;
      if ("Land".equals(type)) {
        Set<ManaSymbol.Typed> manaTypes = new HashSet<>();
        JsonArray produced = json.getJsonArray("produced_mana");
        if (produced != null) {
          for (Object o : produced) {
            switch (o.toString()) {
              case "R":
                manaTypes.add(ManaSymbol.RED);
                break;
              case "B":
                manaTypes.add(ManaSymbol.BLACK);
                break;
              case "U":
                manaTypes.add(ManaSymbol.BLUE);
                break;
              case "W":
                manaTypes.add(ManaSymbol.WHITE);
                break;
              case "G":
                manaTypes.add(ManaSymbol.GREEN);
                break;
              case "C":
                // Not implemented
                break;
              default:
                throw new UnsupportedOperationException(o.toString());
            }
          }
        }

        // FETCH
        String oracle = json.getString("oracle_text");
        BiFunction<List<Card.Land>, Integer, Boolean> etbTapped;
        Set<String> fetchedTypes = null;
        Matcher matcher = FETCH_LAND.matcher(oracle);
        if (matcher.find()) {
          fetchedTypes = new HashSet<>();
          String first = matcher.group(1);
          if (first.equals("basic land")) {
            first = "Basic";
          }
          fetchedTypes.add(first);
          if (matcher.group(2) != null) {
            fetchedTypes.add(matcher.group(2));
          }
          if (matcher.group(5) != null) {
            etbTapped = (lands, cmc_) -> lands.size() < 4;
          } else if (matcher.group(3) != null) {
            etbTapped = (lands, cmc_) -> true;
          } else {
            etbTapped = (lands, cmc_) -> false;
          }
        } else if (ETB_TAPPED.matcher(oracle).find()) {
          if (FAST_LAND.matcher(oracle).find()) {
            etbTapped = (lands, cmc_) -> lands.size() > 3 && cmc_ > 3;
          } else if (VERY_FAST_LAND.matcher(oracle).find()) {
            etbTapped = (lands, cmc_) -> lands.size() > 2 && cmc_ > 2;
          } else if (SLOW_LAND.matcher(oracle).find()) {
            etbTapped = (lands, cmc_) -> lands.size() < 3;
          } else if (RAV_LAND.matcher(oracle).find()) {
            etbTapped = (lands, cmc_) -> false;
          } else if (CHECK_LAND.matcher(oracle).find()) {
            Matcher m = CHECK_LAND.matcher(oracle);
            m.find();
            Set<String> checkedLands = new HashSet<>();
            if (m.group(1) != null) {
              checkedLands.add(m.group(1));
            }
            if (m.group(3) != null) {
              checkedLands.add(m.group(3));
            }
            etbTapped = (lands, cmc_) -> {
              for (Card.Land land : lands) {
                for (String checkedLand : checkedLands) {
                  if (land.subTypes().contains(checkedLand)) {
                    return false;
                  }
                }
              }
              return true;
            };
          } else {
            etbTapped = (lands, cmc_) -> true;
          }
        } else {
          etbTapped = (lands, cmc_) -> false;
        }
        card = new Card.Land(id, name, Collections.unmodifiableSet(superTypes), type, subTypes, Collections.unmodifiableSet(manaTypes), etbTapped, fetchedTypes);
      } else {
        ManaCost manaCost = ManaCost.parse(mana_cost);
        card = new Card.Spell(id, name, type, manaCost);
      }
      return Collections.singletonList(card);
    }
  }

  public final String id; // Scryfall ID
  public final String name;
  public final String type;
  public final int cmc;

  public Card(String id, String name, String type, int cmc) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.cmc = cmc;
  }

  @Override
  public String toString() {
    return name;
  }

  public static class Land extends Card {

    Set<ManaSymbol.Typed> manaTypes; // colors
    private BiFunction<List<Land>, Integer, Boolean> etbTapped;
    Set<String> superTypes;
    Set<String> subTypes;
    public final Set<String> fetchedTypes;

    public Land(String id,
                String name,
                Set<String> superTypes,
                String type,
                Set<String> subTypes,
                Set<ManaSymbol.Typed> manaTypes,
                BiFunction<List<Land>, Integer, Boolean> etbTapped,
                Set<String> fetchedTypes) {
      super(id, name, type, 0);
      this.superTypes = superTypes;
      this.subTypes = subTypes;
      this.manaTypes = manaTypes;
      this.etbTapped = etbTapped;
      this.fetchedTypes = fetchedTypes;
    }

    public Land mutable() {
      if (fetchedTypes != null) {
        return new Land(id, name, new HashSet<>(superTypes), type, subTypes, new HashSet<>(manaTypes), etbTapped, fetchedTypes);
      } else {
        return this;
      }
    }

    public Set<String> subTypes() {
      return subTypes;
    }

    public BiFunction<List<Land>, Integer, Boolean> etbTapped() {
      return etbTapped;
    }
  }

  public static class Spell extends Card {

    public final ManaCost cost;

    public Spell(String id, String name, String type, ManaCost cost) {
      super(id, name, type, cost.cmc());
      this.cost = cost;
    }
  }
}
