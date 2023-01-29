package com.julienviet.castability;

import com.julienviet.Hand;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CastabilityTest {

  @Test
  public void testKeepable7() {
    assertFalse(Hand.isKeepable(makeHand(7, 0, 0), 7));
    assertFalse(Hand.isKeepable(makeHand(6, 0, 1), 7));
    assertTrue(Hand.isKeepable(makeHand(5, 0, 2), 7));
    assertTrue(Hand.isKeepable(makeHand(4, 0, 3), 7));
    assertTrue(Hand.isKeepable(makeHand(3, 0, 4), 7));
    assertTrue(Hand.isKeepable(makeHand(2, 0, 5), 7));
    assertFalse(Hand.isKeepable(makeHand(1, 0, 6), 7));
    assertFalse(Hand.isKeepable(makeHand(0, 0, 7), 7));
  }

  @Test
  public void testKeepable6() {
    assertFalse(Hand.isKeepable(makeHand(7, 0, 0), 6));
    assertFalse(Hand.isKeepable(makeHand(6, 0, 1), 6));
    assertTrue(Hand.isKeepable(makeHand(5, 0, 2), 6));
    assertTrue(Hand.isKeepable(makeHand(4, 0, 3), 6));
    assertTrue(Hand.isKeepable(makeHand(3, 0, 4), 6));
    assertTrue(Hand.isKeepable(makeHand(2, 0, 5), 6));
    assertFalse(Hand.isKeepable(makeHand(1, 0, 6), 6));
    assertFalse(Hand.isKeepable(makeHand(0, 0, 7), 6));
  }

  @Test
  public void testTakeMulligan6() {
    assertTakeMulligan(makeHand(7, 0, 0), 6, 6, 0, 0);
    assertTakeMulligan(makeHand(0, 7, 0), 6, 0, 6, 0);
    assertTakeMulligan(makeHand(4, 3, 0), 6, 4, 2, 0);
    assertTakeMulligan(makeHand(3, 2, 2), 6, 3, 1, 2);
  }

  @Test
  public void testKeepable5() {
    assertFalse(Hand.isKeepable(makeHand(7, 0, 0), 5));
    assertTrue(Hand.isKeepable(makeHand(6, 0, 1), 5));
    assertTrue(Hand.isKeepable(makeHand(5, 0, 2), 5));
    assertTrue(Hand.isKeepable(makeHand(4, 0, 3), 5));
    assertTrue(Hand.isKeepable(makeHand(3, 0, 4), 5));
    assertTrue(Hand.isKeepable(makeHand(2, 0, 5), 5));
    assertTrue(Hand.isKeepable(makeHand(1, 0, 6), 5));
    assertFalse(Hand.isKeepable(makeHand(0, 0, 7), 5));
  }

  @Test
  public void testTakeMulligan5() {
    assertTakeMulligan(makeHand(7, 0, 0), 5, 5, 0, 0);
    assertTakeMulligan(makeHand(0, 7, 0), 5, 0, 5, 0);
    assertTakeMulligan(makeHand(4, 3, 0), 5, 4, 1, 0);
    assertTakeMulligan(makeHand(3, 2, 2), 5, 3, 0, 2);
    assertTakeMulligan(makeHand(2, 1, 4), 5, 2, 1, 2);
    assertTakeMulligan(makeHand(2, 2, 3), 5, 2, 1, 2);
  }

  @Test
  public void testKeepable4() {
    assertTrue(Hand.isKeepable(makeHand(7, 0, 0), 4));
    assertTrue(Hand.isKeepable(makeHand(6, 0, 1), 4));
    assertTrue(Hand.isKeepable(makeHand(5, 0, 2), 4));
    assertTrue(Hand.isKeepable(makeHand(4, 0, 3), 4));
    assertTrue(Hand.isKeepable(makeHand(3, 0, 4), 4));
    assertTrue(Hand.isKeepable(makeHand(2, 0, 5), 4));
    assertTrue(Hand.isKeepable(makeHand(1, 0, 6), 4));
    assertTrue(Hand.isKeepable(makeHand(0, 0, 7), 4));
  }

  @Test
  public void testTakeMulligan4() {
    assertTakeMulligan(makeHand(7, 0, 0), 4, 4, 0, 0);
    assertTakeMulligan(makeHand(0, 7, 0), 4, 0, 4, 0);
    assertTakeMulligan(makeHand(4, 3, 0), 4, 4, 0, 0);
    assertTakeMulligan(makeHand(3, 2, 2), 4, 2, 0, 2);
    assertTakeMulligan(makeHand(2, 1, 4), 4, 2, 0, 2);
    assertTakeMulligan(makeHand(2, 2, 3), 4, 2, 0, 2);
  }

  private static Hand makeHand(int coloredSources, int colorlessSources, int otherCards) {
    Hand hand = new Hand();
    for (int i = 0;i < coloredSources;i++) {
      hand.addCard(Main.ISLAND);
    }
    for (int i = 0;i < colorlessSources;i++) {
      hand.addCard(Main.WASTES);
    }
    for (int i = 0;i < otherCards;i++) {
      hand.addCard(Main.BALEFUL_STRIX);
    }
    return hand;
  }

  private static void assertTakeMulligan(Hand hand, int handSize, int coloredSources, int colorlessSources, int otherCards) {
    Hand result = Hand.takeMulligan(hand, handSize);
    assertEquals(coloredSources, result.coloredSources());
    assertEquals(colorlessSources, result.colorlessSources());
    assertEquals(otherCards, result.other());
  }
}
