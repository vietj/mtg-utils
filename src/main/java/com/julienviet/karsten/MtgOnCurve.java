package com.julienviet.karsten;

import java.util.SplittableRandom;

public class MtgOnCurve {

  public static void main(String[] args) {

    runSims(99, 8);

  }

  public static boolean mullStrategy(int handSize, int landsInHand) {
    switch (handSize) {
      case 7:
        return 1 < landsInHand && landsInHand < 6;
      case 6:
        return 1 < landsInHand && landsInHand < 5;
      case 5:
        return 0 < landsInHand && landsInHand < 5;
      case 4:
        return landsInHand < 5;
      default:
        throw new UnsupportedOperationException();
    }
  }

  public static void runSims(int deckSize, int maxTurnAllowed) {

    final int NUM_ITERATIONS = 250_000;

    for (int totalLands = deckSize / 4;totalLands <= deckSize / 2;totalLands++) {

      System.out.print("\n" + totalLands);

      // for calculating avg. starting hand size
      int sumOfStartingHandSizes = 0;
      int totalIterationsWithThisLandCount = 0;

      for (int turnAllowed = 1;turnAllowed <= maxTurnAllowed;turnAllowed++) {
        // we're looking for the probability of casting a spell with CMC turn_allowed
        // that requires num_good_lands_needed (which is no larger than turn_allowed)
        // of a certain color in its cost
        // e.g. for 2WW [[Wrath of God]], we use turn_allowed = 4 and num_good_lands_needed = 2
        int countOkTotal = 0; // for calculating P(on curve)
        int totalIterationsWithThisCmc = 0;
        for (int numGoodLandsNeeded = 1;numGoodLandsNeeded <= turnAllowed;numGoodLandsNeeded++) {
          // start with the maximum number of sources and go downwards until we go _below_ the
          // consistency cutoff, this way we try fewer configurations on average than by starting
          // low and going upwards
          for (int numGoodLands = totalLands;numGoodLands >= numGoodLandsNeeded;numGoodLands--) {
            int countOk = 0; // number of games with enough lands *and* good lands
            int countConditional = 0; // number of relevant games with enough lands
            totalIterationsWithThisLandCount += NUM_ITERATIONS;
            totalIterationsWithThisCmc += NUM_ITERATIONS;
            for (int i = 0;i < NUM_ITERATIONS;i++) {
              Deck deck = new Deck(deckSize, totalLands, numGoodLands);
              int startingHandSize = 7;
              int landsInHand;
              int goodLandsInHand;
              while (true) {
                landsInHand = 0;
                goodLandsInHand = 0;
                deck.totalCards = deckSize;
                deck.totalLands = totalLands;
                deck.goodLands = numGoodLands;
                // Draw opening hand:
                for (int j = 0;j < 7;j++) {
                  CardType cardType = deck.drawCard();
                  if (cardType != CardType.NON_LAND) {
                    landsInHand++;
                  }
                  if (cardType == CardType.GOOD_LAND) {
                    goodLandsInHand++;
                  }
                }
                // bottoms cards before deciding whether to mull again,
                // and will bottom lands if more than half the cards to be kept are land
                // a card, once bottomed, won't be shuffled back in, and is therefore effectively removed from the deck
                int bottomedLands = 0;
                int bottomedGoodLands = 0;
                for (int j = startingHandSize;j < 7;j++) {
                  if (landsInHand > (startingHandSize / 2) && landsInHand > 2) {
                    landsInHand--;
                    bottomedLands++;
                    if (goodLandsInHand > landsInHand) {
                      goodLandsInHand--;
                      bottomedGoodLands++;
                    }
                  }
                }
                if (mullStrategy(startingHandSize, landsInHand)) {
                  deck.totalCards += 7 - startingHandSize;
                  deck.totalLands += bottomedLands;
                  deck.goodLands += bottomedGoodLands;
                  break;
                }
                startingHandSize--;
              }
              sumOfStartingHandSizes += startingHandSize;
              for (int turn = 2;turn <= turnAllowed;turn++) {
                CardType cardType = deck.drawCard();
                if (cardType != CardType.NON_LAND) {
                  landsInHand += 1;
                }
                if (cardType == CardType.GOOD_LAND) {
                  goodLandsInHand += 1;
                }
              }
              if (landsInHand >= turnAllowed) {
                countConditional++;
                if (goodLandsInHand >= numGoodLandsNeeded) {
                  countOk++;
                }
              }
            }
            countOkTotal += countOk;
            int consistencyCutoff = Math.min(95, 90 + turnAllowed);
            double percentageOk = (countOk * 100D) / countConditional;
            if (percentageOk < consistencyCutoff) {
              System.out.print("\t" + (numGoodLands + 1));
              break;
            }
          }
        }
        double freqCurveOut = (countOkTotal * 100D) / totalIterationsWithThisCmc;
        System.out.print("\t" + String.format("%.2f", freqCurveOut) + "%");
      }

      double avgStartingHandSize = ((double)sumOfStartingHandSizes) / totalIterationsWithThisLandCount;
      System.out.print("\t" + String.format("%.2f", avgStartingHandSize) + "cards");
    }


  }

  enum CardType {
    NON_LAND,
    LAND,
    GOOD_LAND,
  }

  private static final SplittableRandom rng = new SplittableRandom();

  static class Deck {
    int totalCards;
    int totalLands;
    int goodLands;
    Deck(int totalCards, int totalLands, int goodLands) {
      this.totalCards = totalCards;
      this.totalLands = totalLands;
      this.goodLands = goodLands;
    }
    CardType drawCard() {
      int intBetweenOneAndDeckSize = rng.nextInt(0, totalCards) + 1;
      int goodLandCutoff = goodLands;
      int landCutoff = totalLands;
      if (intBetweenOneAndDeckSize <= goodLandCutoff) {
        totalCards -= 1;
        totalLands -= 1;
        goodLands -= 1;
        return CardType.GOOD_LAND;
      } else if (intBetweenOneAndDeckSize <= landCutoff) {
        totalCards -= 1;
        totalLands -= 1;
        return CardType.LAND;
      } else {
        totalCards -= 1;
        return CardType.NON_LAND;
      }
    }
  }
}
