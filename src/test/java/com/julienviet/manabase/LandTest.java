package com.julienviet.manabase;

import com.julienviet.DeckList;
import com.julienviet.ManaSymbol;
import com.julienviet.ManaType;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static com.julienviet.manabase.EvaluateCostTest.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LandTest {

  @Test
  public void testColorless() {
    DeckList list = new DeckList.Builder().add(WASTES).build();
    Set<ManaType> resolved = list.resolveManaTypes(WASTES);
    assertEquals(Collections.singleton(ManaType.COLORLESS), resolved);
  }

  @Test
  public void testSubTypes() {
    assertTrue(EvaluateCostTest.VOLCANIC_ISLAND.subTypes().contains("Island"));
    assertTrue(EvaluateCostTest.VOLCANIC_ISLAND.subTypes().contains("Mountain"));
  }

  @Test
  public void testEtbTapped() {
    assertFalse(EvaluateCostTest.ARID_MESA.etbTapped().apply(Collections.emptyList(), 0));
    assertTrue(EvaluateCostTest.FABLED_PASSAGE.etbTapped().apply(Collections.emptyList(), 0));
    assertFalse(EvaluateCostTest.FABLED_PASSAGE.etbTapped().apply(Arrays.asList(ISLAND, ISLAND, ISLAND, ISLAND), 0));
    assertTrue(EvaluateCostTest.ABANDONED_OUTPOST.etbTapped().apply(Collections.emptyList(), 0));
    assertFalse(EvaluateCostTest.SEACHROME_COAST.etbTapped().apply(Collections.emptyList(), 0));
    assertFalse(EvaluateCostTest.DEN_OF_THE_BUGBEAR.etbTapped().apply(Collections.emptyList(), 0));
    assertTrue(EvaluateCostTest.DEN_OF_THE_BUGBEAR.etbTapped().apply(Arrays.asList(ISLAND, ISLAND, ISLAND), 3));
    assertTrue(EvaluateCostTest.DESERTED_BEACH.etbTapped().apply(Collections.emptyList(), 0));
    assertFalse(EvaluateCostTest.DESERTED_BEACH.etbTapped().apply(Arrays.asList(ISLAND, ISLAND, ISLAND), 0));
    assertFalse(EvaluateCostTest.BLOOD_CRYPT.etbTapped().apply(Collections.emptyList(), 0));
    assertTrue(EvaluateCostTest.GLACIAL_FORTRESS.etbTapped().apply(Collections.emptyList(), 0));
    assertFalse(EvaluateCostTest.GLACIAL_FORTRESS.etbTapped().apply(Collections.singletonList(ISLAND), 0));
    assertTrue(EvaluateCostTest.GLACIAL_FORTRESS.etbTapped().apply(Collections.singletonList(MOUNTAIN), 0));
  }
}
