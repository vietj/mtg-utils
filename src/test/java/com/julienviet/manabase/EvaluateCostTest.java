package com.julienviet.manabase;

import com.julienviet.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EvaluateCostTest {

  public static final CardDb DB = new CardDb();

  public static final Card.Spell COUNTERSPELL = (Card.Spell) DB.findByName("Counterspell");
  public static final Card.Spell BALEFUL_STRIX = (Card.Spell) DB.findByName("Baleful Strix");
  public static final Card.Spell VAMPIRIC_TUTOR = (Card.Spell) DB.findByName("Vampiric Tutor");
  public static final Card.Spell UNDERWORLD_DREAMD = (Card.Spell) DB.findByName("Underworld Dreams");
  public static final Card.Spell LICH = (Card.Spell) DB.findByName("Lich");
  public static final Card.Spell OMNATH_LOCUST_OF_CREATION = (Card.Spell) DB.findByName("Omnath, Locus of Creation");
  public static final Card.Spell URZA_S_BAUBLE = (Card.Spell) DB.findByName("Urza's Bauble");
  public static final Card.Spell TEZZERET_AGENT_OF_BOLAS = (Card.Spell) DB.findByName("Tezzeret, Agent of Bolas");

  public static final Card.Land ISLAND = (Card.Land) DB.findByName("Island");
  public static final Card.Land MOUNTAIN = (Card.Land) DB.findByName("Mountain");
  public static final Card.Land FOREST = (Card.Land) DB.findByName("Forest");
  public static final Card.Land PLAINS = (Card.Land) DB.findByName("Plains");
  public static final Card.Land SWAMP = (Card.Land) DB.findByName("Swamp");
  public static final Card.Land WASTES = (Card.Land) DB.findByName("Wastes");


  public static final Card.Land VOLCANIC_ISLAND = (Card.Land) DB.findByName("Volcanic Island");

  public static final Card.Land ARID_MESA = (Card.Land) DB.findByName("Arid Mesa");

  public static final Card.Land FABLED_PASSAGE = (Card.Land) DB.findByName("Fabled Passage");

  public static final Card.Land ABANDONED_OUTPOST = (Card.Land) DB.findByName("Abandoned Outpost");
  public static final Card.Land SEACHROME_COAST = (Card.Land) DB.findByName("Seachrome Coast");
  public static final Card.Land DEN_OF_THE_BUGBEAR = (Card.Land) DB.findByName("Den of the Bugbear");
  public static final Card.Land DESERTED_BEACH = (Card.Land) DB.findByName("Deserted Beach");
  public static final Card.Land BLOOD_CRYPT = (Card.Land) DB.findByName("Blood Crypt");
  public static final Card.Land GLACIAL_FORTRESS = (Card.Land) DB.findByName("Glacial Fortress");

  public static final Card.Land DROWNED_CATACOMB = (Card.Land) DB.findByName("Drowned Catacomb"); // Check
  public static final Card.Land SHIPWRECK_MARSH = (Card.Land) DB.findByName("Shipwreck Marsh"); // Slow
  public static final Card.Land DARKSLICK_SHORES = (Card.Land) DB.findByName("Darkslick Shores"); // Fast

  public static final Card.Land BLACKLEAVE_CLIFFS = (Card.Land) DB.findByName("Blackcleave Cliffs"); // Fast
  public static final Card.Land UNDERGROUND_SEA = (Card.Land) DB.findByName("Underground Sea"); // Dual
  public static final Card.Land BADLANDS = (Card.Land) DB.findByName("Badlands"); // Dual
  public static final Card.Land URZAS_SAGA = (Card.Land) DB.findByName("Urza's Saga");

  public static final Card.Land RAUGRIN_TRIOME = (Card.Land) DB.findByName("Raugrin Triome"); // Jeskai
  public static final Card.Land KETRIA_TRIOME = (Card.Land) DB.findByName("Ketria Triome"); // Temur
  public static final Card.Land FIELD_OF_RUIN = (Card.Land) DB.findByName("Field of Ruin"); // Temur

  @Test
  public void testEvaluate() {
    DeckList empty = new DeckList.Builder().build();
    assertTrue(Main.evaluateCost(empty, Arrays.asList(ISLAND), new ManaCost().add(ManaSymbol.BLUE)));
    assertFalse(Main.evaluateCost(empty, Arrays.asList(ISLAND), new ManaCost().add(ManaSymbol.RED)));
    assertFalse(Main.evaluateCost(empty, Arrays.asList(ISLAND), new ManaCost().add(ManaSymbol.BLUE, 2)));
    assertTrue(Main.evaluateCost(empty, Arrays.asList(ISLAND, ISLAND), new ManaCost().add(ManaSymbol.BLUE, 2)));

  }

  @Test
  public void testCmc() {
    assertEquals(1, new ManaCost().add(ManaSymbol.BLUE).cmc());
    assertEquals(2, new ManaCost().add(ManaSymbol.BLUE, 2).cmc());
    assertEquals(2, new ManaCost().add(ManaSymbol.BLUE).add(ManaSymbol.RED).cmc());
    assertEquals(4, new ManaCost().add(ManaSymbol.GENERIC, 4).cmc());
  }

  @Test
  public void testCanPlaySpell() {
    DeckList empty = new DeckList.Builder().build();
    assertFalse(Main.canPlaySpellOnCurve(empty, Arrays.asList(ISLAND), COUNTERSPELL));
    assertTrue(Main.canPlaySpellOnCurve(empty, Arrays.asList(ISLAND, ISLAND), COUNTERSPELL));
    assertFalse(Main.canPlaySpellOnCurve(empty, Arrays.asList(SWAMP, SWAMP), BALEFUL_STRIX));
    assertTrue(Main.canPlaySpellOnCurve(empty, Arrays.asList(SWAMP, ISLAND), BALEFUL_STRIX));
    assertFalse(Main.canPlaySpellOnCurve(empty, Arrays.asList(DROWNED_CATACOMB, DROWNED_CATACOMB, DROWNED_CATACOMB), UNDERWORLD_DREAMD));
    assertTrue(Main.canPlaySpellOnCurve(empty, Arrays.asList(DROWNED_CATACOMB, DROWNED_CATACOMB, SWAMP), UNDERWORLD_DREAMD));
    assertTrue(Main.canPlaySpellOnCurve(empty, Arrays.asList(DROWNED_CATACOMB, SHIPWRECK_MARSH, SWAMP), UNDERWORLD_DREAMD));
    assertTrue(Main.canPlaySpellOnCurve(empty, Arrays.asList(SHIPWRECK_MARSH, SHIPWRECK_MARSH, SWAMP), UNDERWORLD_DREAMD));
    assertTrue(Main.canPlaySpellOnCurve(empty, Arrays.asList(SHIPWRECK_MARSH, SHIPWRECK_MARSH, SHIPWRECK_MARSH), UNDERWORLD_DREAMD));
    assertTrue(Main.canPlaySpellOnCurve(empty, Arrays.asList(DARKSLICK_SHORES, DARKSLICK_SHORES, DARKSLICK_SHORES), UNDERWORLD_DREAMD));
    assertFalse(Main.canPlaySpellOnCurve(empty, Arrays.asList(DARKSLICK_SHORES, DARKSLICK_SHORES, DARKSLICK_SHORES, DARKSLICK_SHORES), LICH));
    assertFalse(Main.canPlaySpellOnCurve(empty, Arrays.asList(RAUGRIN_TRIOME, RAUGRIN_TRIOME, KETRIA_TRIOME, KETRIA_TRIOME, FIELD_OF_RUIN), OMNATH_LOCUST_OF_CREATION));
    assertTrue(Main.canPlaySpellOnCurve(empty, Arrays.asList(RAUGRIN_TRIOME, RAUGRIN_TRIOME, KETRIA_TRIOME, KETRIA_TRIOME, ISLAND), OMNATH_LOCUST_OF_CREATION));
    assertTrue(Main.canPlaySpellOnCurve(empty, Arrays.asList(SWAMP), URZA_S_BAUBLE));
  }

  @Test
  public void testAnalyzeDeck() {

    DeckList.Builder builder = DeckList.builder();
    builder.add(VAMPIRIC_TUTOR, 4);
    builder.add(SWAMP);
    builder.add(ISLAND, 20);
    DeckList deck = builder.build();

    Map<Card.Spell, Result> res = Main.analyze(deck);

    Result result = res.get(VAMPIRIC_TUTOR);
//    System.out.println(result.ok);
//    System.out.println(result.nok);
    System.out.println(result.ratio());


  }


}
