package com.julienviet.manabase;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CardDb {

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

  public CardDb() {
    try {
      JsonArray array;
      try (ZipInputStream in = new ZipInputStream(Main.class.getClassLoader().getResourceAsStream("oracle-cards-20220505210246.json.zip"))) {
        ZipEntry entry = in.getNextEntry();
        byte[] bytes = Main.loadFile(in);
        array = new JsonArray(Buffer.buffer(bytes));
      }
      for (int i = 0;i < array.size();i++) {
        JsonObject obj = array.getJsonObject(i);
        if ("card".equals(obj.getString("object"))) {
          String name = obj.getString("name");
          String typeLine = obj.getString("type_line");
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

          String mana_cost = obj.getString("mana_cost");
          if (mana_cost == null) {
            // ????
            continue;
          }

          int cmc = obj.getInteger("cmc");


          Card card;
          if ("Land".equals(type)) {
            Set<ManaSymbol.Typed> manaTypes = new HashSet<>();
            JsonArray produced = obj.getJsonArray("produced_mana");
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
            String oracle = obj.getString("oracle_text");
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
            card = new Card.Land(name, Collections.unmodifiableSet(superTypes), type, subTypes, cmc, Collections.unmodifiableSet(manaTypes), etbTapped, fetchedTypes);
          } else {
            ManaCost manaCost;
            try {
              manaCost = ManaCost.parse(mana_cost);
            } catch (Exception e) {
              e.printStackTrace();
              System.out.println("Cannot load " + name + " " + mana_cost);
              continue;
            }
            card = new Card.Spell(name, type, cmc, manaCost);
          }

          all.put(name, card);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Map<String, Card> all = new HashMap<>();

  public Card find(String name) {
    return all.get(name);
  }
}
