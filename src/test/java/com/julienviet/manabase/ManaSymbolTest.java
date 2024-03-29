package com.julienviet.manabase;

import com.julienviet.DeckList;
import com.julienviet.ManaCost;
import com.julienviet.ManaSymbol;
import org.junit.Test;

import java.util.Arrays;

import static com.julienviet.manabase.EvaluateCostTest.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ManaSymbolTest {

  @Test
  public void testParse() {
    assertEquals(new ManaCost().add(ManaSymbol.RED), ManaCost.parse("{R}"));
    assertEquals(new ManaCost().add(ManaSymbol.GENERIC, 4), ManaCost.parse("{4}"));
    assertEquals(new ManaCost().add(ManaSymbol.GENERIC, 4).add(ManaSymbol.RED, 2), ManaCost.parse("{4}{R}{R}"));
    assertEquals(new ManaCost().add(ManaSymbol.hybrid(ManaSymbol.RED, ManaSymbol.GREEN)), ManaCost.parse("{R/G}"));
    assertEquals(new ManaCost().add(ManaSymbol.GENERIC, 3).add(ManaSymbol.GREEN), ManaCost.parse("{3}{G/P}"));
    assertEquals(new ManaCost().add(ManaSymbol.GENERIC, 2), ManaCost.parse("{X}"));
  }

  @Test
  public void testFindCorrectLand() {

    DeckList empty = new DeckList.Builder().build();

    assertSame(ISLAND, Main.findCorrectLand(empty, Arrays.asList(ISLAND), ManaSymbol.BLUE));
    assertSame(ISLAND, Main.findCorrectLand(empty, Arrays.asList(ISLAND), ManaSymbol.hybrid(ManaSymbol.BLUE, ManaSymbol.GREEN)));
    assertSame(FOREST, Main.findCorrectLand(empty, Arrays.asList(FOREST), ManaSymbol.hybrid(ManaSymbol.BLUE, ManaSymbol.GREEN)));
    assertSame(null, Main.findCorrectLand(empty, Arrays.asList(MOUNTAIN), ManaSymbol.hybrid(ManaSymbol.BLUE, ManaSymbol.GREEN)));
  }
}
