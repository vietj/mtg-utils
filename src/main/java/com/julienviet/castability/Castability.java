package com.julienviet.castability;

public class Castability {

  private int coloredSources;
  private int colorlessSources;
  private int other;

  public Castability coloredSources(int val) {
    coloredSources = val;
    return this;
  }

  public Castability colorlessSources(int val) {
    colorlessSources = val;
    return this;
  }

  public Castability other(int val) {
    other = val;
    return this;
  }

  public Hand drawHand(Deck deck) {
    Hand hand = new Hand();
    for (int handSize = 7;handSize >= 4;handSize--) {
      hand = new Hand();
      for (int i = 0;i < 7;i++) {
        CardType type = deck.drawCard();
        hand.addCard(type);
      }
      if (isKeepable(hand, handSize)) {
        int coloredSources = hand.coloredSources;
        int colorlessSources = hand.colorlessSources;
        int other = hand.other;
        takeMulligan(hand, handSize);
        for (int i = 0;i < coloredSources - hand.coloredSources;i++) {
          deck.bottom(CardType.COLORED_SOURCE);
        }
        for (int i = 0;i < colorlessSources - hand.colorlessSources;i++) {
          deck.bottom(CardType.COLORLESS_SOURCE);
        }
        for (int i = 0;i < other - hand.other;i++) {
          deck.bottom(CardType.OTHER);
        }
        break;
      } else {
        for (int i = 0;i < hand.coloredSources;i++) {
          deck.bottom(CardType.COLORED_SOURCE);
        }
        for (int i = 0;i < hand.colorlessSources;i++) {
          deck.bottom(CardType.COLORLESS_SOURCE);
        }
        for (int i = 0;i < hand.other;i++) {
          deck.bottom(CardType.OTHER);
        }
        deck.shuffle();
        hand.coloredSources = 0;
        hand.colorlessSources = 0;
        hand.other = 0;
      }
    }
    return hand;
  }



  public Deck deck() {
    return new Deck(coloredSources, colorlessSources, other).shuffle();
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

  public Result generate(int iterations, int coloredCastingCost, int colorlessCastingCost) {
    int totalSourcesSuccess = 0;
    int coloredSourcesSuccess = 0;
    for (int iter = 0;iter < iterations;iter++) {
      Deck deck = deck();
      Hand hand = drawHand(deck);
      for (int i = 1; i < colorlessCastingCost + coloredCastingCost; i++) {
        CardType type = deck.drawCard();
        hand.addCard(type);
      }
      if (hand.totalSources() >= coloredCastingCost + colorlessCastingCost) {
        totalSourcesSuccess++;
        if (hand.coloredSources >= coloredCastingCost) {
          coloredSourcesSuccess++;
        }
      }
    }
    int consistencyCutoffRatio = Math.min(95, 90 + colorlessCastingCost + coloredCastingCost);
    double onCurveRatio = ((double)coloredSourcesSuccess) / iterations;
    return new Result(totalSourcesSuccess, coloredSourcesSuccess, (100D * coloredSourcesSuccess / totalSourcesSuccess), consistencyCutoffRatio, onCurveRatio);
  }

  // Return true if take mulligan action
  public static boolean isKeepable(Hand hand, int handSize) {
    int totalSources = hand.totalSources();
    switch (handSize) {
      case 7:
        return 1 < totalSources && totalSources < 6;
      case 6:
        if (totalSources > 3) {
          totalSources--;
        }
        return 1 < totalSources && totalSources < 5;
      case 5:
        if (totalSources > 3) {
          int min = Math.min(2, (totalSources - 3));
          totalSources -= min;
        }
        return 0 < totalSources && totalSources < 5;
      default:
        return true;
    }
  }

  public static void takeMulligan(Hand hand, int handSize) {
    switch (handSize) {
      case 7:
        break;
      case 6:
        for (int i = 0;i < 1;i++) {
          if (hand.totalSources() > 3) {
            if (hand.colorlessSources > 0) {
              hand.colorlessSources--;
            } else {
              hand.coloredSources--;
            }
          } else {
            hand.other--;
          }
        }
        break;
      case 5:
        for (int i = 0;i < 2;i++) {
          if (hand.totalSources() > 3) {
            if (hand.colorlessSources > 0) {
              hand.colorlessSources--;
            } else {
              hand.coloredSources--;
            }
          } else {
            hand.other--;
          }
        }
        break;
      case 4:
        for (int i = 0;i < 3;i++) {
          if (hand.totalSources() > 2) {
            if (hand.colorlessSources > 0) {
              hand.colorlessSources--;
            } else {
              hand.coloredSources--;
            }
          } else {
            hand.other--;
          }
        }
        break;
      default:
        throw new UnsupportedOperationException();
    }
  }

  public enum CardType {
    COLORED_SOURCE,
    COLORLESS_SOURCE,
    OTHER
  }

  public static class Hand {
    private int coloredSources;
    private int colorlessSources;
    private int other;
    public Hand addCard(CardType cardType) {
      switch (cardType) {
        case COLORED_SOURCE:
          coloredSources++;
          break;
        case COLORLESS_SOURCE:
          colorlessSources++;
          break;
        case OTHER:
          other++;
          break;
      }
      return this;
    }
    public int totalSources() {
      return coloredSources + colorlessSources;
    }
    public int coloredSources() {
      return coloredSources;
    }
    public int colorlessSources() {
      return colorlessSources;
    }
    public int other() {
      return other;
    }
  }

  static class Deck {

    private Deque<CardType> cards;

    Deck(int coloredSources, int colorlessSources, int other) {
      cards = new Deque<>(coloredSources + colorlessSources + other);
      for (int i = 0; i < coloredSources; i++) {
        cards.addLast(CardType.COLORED_SOURCE);
      }
      for (int i = 0; i < colorlessSources; i++) {
        cards.addLast(CardType.COLORLESS_SOURCE);
      }
      for (int i = 0; i < other; i++) {
        cards.addLast(CardType.OTHER);
      }
    }

    Deck shuffle() {
      cards.shuffle();
      return this;
    }

    Deck bottom(CardType card) {
      cards.addLast(card);
      return this;
    }

    CardType drawCard() {
      return cards.removeFirst();
    }
  }
}
