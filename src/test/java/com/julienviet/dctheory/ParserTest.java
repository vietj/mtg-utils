package com.julienviet.dctheory;

import com.julienviet.castability.Castability;
import com.julienviet.castability.KTable;
import com.julienviet.castability.KTableSet;
import com.julienviet.castability.Main;
import com.julienviet.manabase.Card;
import com.julienviet.manabase.ManaSymbol;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.Test;
import wiremock.com.github.jknack.handlebars.internal.Files;
import com.julienviet.manabase.Deck;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class ParserTest {

  @Test
  public void testFoo() throws Exception {
    File file = new File(ParserTest.class.getClassLoader().getResource("Graou_graou_X_initiative.json").toURI());
    JsonObject json = new JsonObject(Files.read(file));
    Deck deck = Parser.toStruct(json);
    Map<String, Integer> manaSources = new LinkedHashMap<>();
    int landCount = 0;
    for (Card.Land land : deck.lands()) {
      if (deck.resolveManaTypes(land).size() > 0) {
        landCount++;
      }
      for (ManaSymbol.Typed symbol : deck.resolveManaTypes(land)) {
        manaSources.compute(symbol.toString(), (s, v) -> v == null ? 1 : v + 1);
      }
    }
    JsonObject blah = new JsonObject();
    for (Map.Entry<String, Integer> abc : manaSources.entrySet()) {
      blah.put(abc.getKey(), abc.getValue());
    }
    Main main = new Main();
    KTableSet result = main.run(new JsonObject()
      .put("land_count", landCount)
      .put("land_untapped", blah));
    JsonArray report = new JsonArray();
    for (Card.Spell spell : deck.spells()) {
      for (Map.Entry<ManaSymbol, Integer> entry : spell.cost.map().entrySet()) {
        if (entry.getKey() instanceof ManaSymbol.Typed) {
          KTable ktable = result.get(entry.getKey().toString());
          // Compute key in table
          StringBuilder sb = new StringBuilder();
          int rest = spell.cmc - entry.getValue();
          if (rest > 0) {
            sb.append(rest);
          }
          for (int i = 0;i < entry.getValue();i++) {
            sb.append('C');
          }
          Castability.Result res = ktable.get(sb.toString());
          if (res != null) {
            report.add(new JsonObject()
              .put("mana_source", entry.getKey().toString())
              .put("color_specificity", sb.toString())
              .put("card_name", spell.name)
              .put("n_land_success", res.successRatio)
              .put("on_curve", res.onCurveRatio * 100)
            );
          }
        }
      }
    }
    System.out.println(report.encodePrettily());
  }
}
