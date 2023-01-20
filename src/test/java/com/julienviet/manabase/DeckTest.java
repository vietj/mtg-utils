package com.julienviet.manabase;

import org.junit.Test;

import java.util.Map;

import static com.julienviet.manabase.EvaluateCostTest.ARID_MESA;
import static com.julienviet.manabase.EvaluateCostTest.FOREST;
import static com.julienviet.manabase.EvaluateCostTest.ISLAND;
import static com.julienviet.manabase.EvaluateCostTest.KETRIA_TRIOME;
import static com.julienviet.manabase.EvaluateCostTest.MOUNTAIN;
import static com.julienviet.manabase.EvaluateCostTest.OMNATH_LOCUST_OF_CREATION;
import static com.julienviet.manabase.EvaluateCostTest.PLAINS;
import static com.julienviet.manabase.EvaluateCostTest.RAUGRIN_TRIOME;
import static com.julienviet.manabase.EvaluateCostTest.VOLCANIC_ISLAND;

public class DeckTest {

  @Test
  public void testResolveFetch() {

    Deck deck = Deck.builder().add(VOLCANIC_ISLAND).add(ARID_MESA).build();

    for (Card.Land land : deck.lands()) {
      System.out.println(land.name + " " + deck.resolveManaTypes(land));
    }

  }

  @Test
  public void testOmnath() {

    Deck deck = Deck.builder()
      .add(OMNATH_LOCUST_OF_CREATION, 4)
      .add(RAUGRIN_TRIOME, 2)
      .add(KETRIA_TRIOME, 2)
      .build();

    Map<Card.Spell, Result> analyze = Main.analyze(deck);

    Result res = analyze.get(OMNATH_LOCUST_OF_CREATION);

    System.out.println(res.ratio());

  }

}
