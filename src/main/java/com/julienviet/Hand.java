package com.julienviet;

import com.julienviet.castability.Main;

public class Hand {

  private int coloredSources;
  private int colorlessSources;
  private int other;

  public Hand addCard(Card card) {
    if (card == Main.ISLAND) {
      coloredSources++;
    } else if (card == Main.WASTES) {
      colorlessSources++;
    } else if (card == Main.BALEFUL_STRIX) {
      other++;
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

  public static Hand takeMulligan(Hand hand, int handSize) {
    int coloredSources = hand.coloredSources();
    int colorlessSources = hand.colorlessSources();
    int other = hand.other();
    switch (handSize) {
      case 7:
        break;
      case 6:
        for (int i = 0;i < 1;i++) {
          if (coloredSources + colorlessSources > 3) {
            if (colorlessSources > 0) {
              colorlessSources--;
            } else {
              coloredSources--;
            }
          } else {
            other--;
          }
        }
        break;
      case 5:
        for (int i = 0;i < 2;i++) {
          if (coloredSources + colorlessSources > 3) {
            if (colorlessSources > 0) {
              colorlessSources--;
            } else {
              coloredSources--;
            }
          } else {
            other--;
          }
        }
        break;
      case 4:
        for (int i = 0;i < 3;i++) {
          if (coloredSources + colorlessSources > 2) {
            if (colorlessSources > 0) {
              colorlessSources--;
            } else {
              coloredSources--;
            }
          } else {
            other--;
          }
        }
        break;
      default:
        throw new UnsupportedOperationException();
    }
    Hand res = new Hand();
    for (int i = 0;i < coloredSources;i++) {
      res.addCard(Main.ISLAND);
    }
    for (int i = 0;i < colorlessSources;i++) {
      res.addCard(Main.WASTES);
    }
    for (int i = 0;i < other;i++) {
      res.addCard(Main.BALEFUL_STRIX);
    }
    return res;
  }
}
