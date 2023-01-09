package com.julienviet.dctheory;

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
      if (land.manaTypes().size() > 0) {
        landCount++;
      }
      for (ManaSymbol.Typed symbol : land.manaTypes()) {
        manaSources.compute(symbol.toString(), (s, v) -> v == null ? 1 : v + 1);
      }
    }
    System.out.println(manaSources);
    System.out.println(landCount);

    JsonObject result = new JsonObject();
    for (Map.Entry<String, Integer> abc : manaSources.entrySet()) {
      JsonObject input = new JsonObject()
        .put("land_count", landCount)
        .put("land_untapped", new JsonObject().put(abc.getKey(), abc.getValue()));
      Main main = new Main();
      result.mergeIn(main.run(input));
    }
    JsonArray report = new JsonArray();
    for (Card.Spell spell : deck.spells()) {
      for (Map.Entry<ManaSymbol, Integer> entry : spell.cost.map().entrySet()) {
        if (entry.getKey() instanceof ManaSymbol.Typed) {
          JsonObject ktable = result.getJsonObject(entry.getKey().toString());
          // Compute key in table
          StringBuilder sb = new StringBuilder();
          int rest = spell.cmc - entry.getValue();
          if (rest > 0) {
            sb.append(rest);
          }
          for (int i = 0;i < entry.getValue();i++) {
            sb.append('C');
          }
          JsonObject res = ktable.getJsonObject(sb.toString());
          if (res != null) {
            report.add(new JsonObject()
              .put("mana_source", entry.getKey().toString())
              .put("color_specificity", sb.toString())
              .put("card_name", spell.name)
              .put("n_land_success", res.getNumber("successRatio"))
              .put("on_curve", res.getDouble("onCurveRatio") * 100)
            );
          }
        }
      }
    }
    System.out.println(report.encodePrettily());
  }
}
