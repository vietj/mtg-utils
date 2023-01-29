package com.julienviet.castability;

import com.julienviet.Card;
import com.julienviet.Deck;
import com.julienviet.DeckList;
import com.julienviet.Hand;

public class Castability {

  private final DeckList deckList;

  public Castability(DeckList deckList) {
    this.deckList = deckList;
  }

  public Hand drawHand(Deck deck) {
    Hand hand;
    for (int handSize = 7;handSize >= 4;handSize--) {
      hand = new Hand();
      for (int i = 0;i < 7;i++) {
        Card type = deck.draw();
        hand.addCard(type);
      }
      if (Hand.isKeepable(hand, handSize)) {
        Hand handAfterMull = Hand.takeMulligan(hand, handSize);
        for (int i = 0;i < hand.coloredSources() - handAfterMull.coloredSources();i++) {
          deck.bottom(Main.ISLAND);
        }
        for (int i = 0;i < hand.colorlessSources() - handAfterMull.colorlessSources();i++) {
          deck.bottom(Main.WASTES);
        }
        for (int i = 0;i < hand.other() - handAfterMull.other();i++) {
          deck.bottom(Main.BALEFUL_STRIX);
        }
        return handAfterMull;
      } else {
        for (int i = 0;i < hand.coloredSources();i++) {
          deck.bottom(Main.ISLAND);
        }
        for (int i = 0;i < hand.colorlessSources();i++) {
          deck.bottom(Main.WASTES);
        }
        for (int i = 0;i < hand.other();i++) {
          deck.bottom(Main.BALEFUL_STRIX);
        }
        deck.shuffle();
      }
    }
    throw new IllegalStateException();
  }



  public Deck deck() {
    return new Deck(deckList).shuffle();
  }

  public static class Result {
    public final int totalSourcesSuccess;
    public final int colouredSourcesSuccess;
    public final double successRatio;
    public final double consistencyCutoffRatio;
    public final double onCurveRatio;
    public Result(int totalSourcesSuccess, int colouredSourcesSuccess, double successRatio, double consistencyCutoffRatio, double onCurveRatio) {
      this.totalSourcesSuccess = totalSourcesSuccess;
      this.colouredSourcesSuccess = colouredSourcesSuccess;
      this.successRatio = successRatio;
      this.consistencyCutoffRatio = consistencyCutoffRatio;
      this.onCurveRatio = onCurveRatio;
    }

    @Override
    public String toString() {
      return "totalSourcesSuccess: " + totalSourcesSuccess + "\n" +
        "coloredSourcesSuccess: " + colouredSourcesSuccess + "\n" +
        "successRatio: " + successRatio + "\n" +
        "consistencyCutoffRatio: " + consistencyCutoffRatio + "\n" +
        "onCurveRatio: " + onCurveRatio;
    }
  }

  public Result generate(final int iterations, final int coloredCastingCost, final int colorlessCastingCost) {
    int totalSourcesSuccess = 0;
    int coloredSourcesSuccess = 0;
    for (int iter = 0;iter < iterations;iter++) {
      Deck deck = deck();
      Hand hand = drawHand(deck);
      int turns = colorlessCastingCost + coloredCastingCost;
      for (int i = 1; i < turns; i++) {
        Card type = deck.draw();
        hand.addCard(type);
      }
      if (hand.totalSources() >= coloredCastingCost + colorlessCastingCost) {
        totalSourcesSuccess++;
        if (hand.coloredSources() >= coloredCastingCost) {
          coloredSourcesSuccess++;
        }
      }
    }
    int consistencyCutoffRatio = Math.min(95, 90 + colorlessCastingCost + coloredCastingCost);
    double onCurveRatio = ((double)coloredSourcesSuccess) / iterations;
    return new Result(totalSourcesSuccess, coloredSourcesSuccess, (100D * coloredSourcesSuccess / totalSourcesSuccess), consistencyCutoffRatio, onCurveRatio);
  }

}
