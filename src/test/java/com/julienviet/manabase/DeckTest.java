package com.julienviet.manabase;

import com.julienviet.Card;
import com.julienviet.DeckList;
import org.junit.Test;

import java.util.Map;

import static com.julienviet.manabase.EvaluateCostTest.ARID_MESA;
import static com.julienviet.manabase.EvaluateCostTest.KETRIA_TRIOME;
import static com.julienviet.manabase.EvaluateCostTest.OMNATH_LOCUST_OF_CREATION;
import static com.julienviet.manabase.EvaluateCostTest.RAUGRIN_TRIOME;
import static com.julienviet.manabase.EvaluateCostTest.VOLCANIC_ISLAND;

public class DeckTest {

  @Test
  public void testResolveFetch() {

    DeckList deck = DeckList.builder().add(VOLCANIC_ISLAND).add(ARID_MESA).build();

    for (Card.Land land : deck.lands()) {
      System.out.println(land.name + " " + deck.resolveManaTypes(land));
    }

  }

  @Test
  public void testOmnath() {

    DeckList deck = DeckList.builder()
      .add(OMNATH_LOCUST_OF_CREATION, 4)
      .add(RAUGRIN_TRIOME, 2)
      .add(KETRIA_TRIOME, 2)
      .build();

    Map<Card.Spell, Result> analyze = Main.analyze(deck);

    Result res = analyze.get(OMNATH_LOCUST_OF_CREATION);

    System.out.println(res.ratio());

  }

}
