package com.julienviet.karsten;

import com.julienviet.manabase.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import java.util.function.Predicate;

public class HowManySourcesUpdate {

  //Manually set the type of card that we're interested to cast here
  final int nrGoodLandsNeeded = 3;
  final int turnAllowed = 6;
  //We will look for the probability of casting a spell with CMC TurnAllowed on turn TurnAllowed that requires NrGoodLandsNeeded (which is no larger than TurnAllowed) colored mana of a certain color in its cost.
  //For example, for a 2WW Wrath of God, we use TurnAllowed=4 and NrGoodLandsNeeded=2.

  //Initialize the contents of the deck
  final int nrCards = 40;
  final int nrLands = 17;
  //Note that the final element needed to describe a deck (NrGoodLands) is set later, as we iterate over the various possible values

  public List<Double> run() {
    return run(1000000);
  }

  private boolean mulligan(int landsInHand, int minLands, int maxLands) {
    return landsInHand < minLands || landsInHand > maxLands;
  }

  public List<Double> run(int nrIterations) {
    Deck deck = new Deck();

    //Declare other variables

    List<Double> results = new ArrayList<>();

    for (int nrGoodLands = 3; nrGoodLands <= 17; nrGoodLands++) {

      double countOK = 0.0; //This will be the number of relevant games where you draw enough lands and the right colored sources
      double countConditional = 0.0; //This will be the number of relevant games where you draw enough lands

      for (int i = 1; i <= nrIterations; i++) {

        //Draw opening 7
        deck.setDeck(nrLands, nrGoodLands, nrCards);
        deck.drawCards(7);

        //Mulligan to 6
        if (mulligan(deck.landsInHand, 2, 5)) {
          deck.setDeck(nrLands, nrGoodLands, nrCards);
          deck.drawCards(6);

          //Mulligan to 5
          if (mulligan(deck.landsInHand, 2, 4)) {
            deck.setDeck(nrLands, nrGoodLands, nrCards);
            deck.drawCards(5);

            //Mulligan to 4
            if (mulligan(deck.landsInHand, 1, 4)) {
              deck.setDeck(nrLands, nrGoodLands, nrCards);
              deck.drawCards(4);
            }
          }
          //Vancouver mulligan scry
          //Wwe leave any land that can produce the right color on top and push any other card (both off-color lands and spells) to the bottom
          deck.scryCard(ct -> ct == CardType.GOOD_LAND);
        }

        //Draw step for turn 2 (remember, we're always on the play)
        if (turnAllowed > 1) {
          deck.drawCard();
        }

        //For turns 3 on, draw cards for the number of turns available
        for (int turn = 3; turn <= turnAllowed; turn++) {
          deck.drawCard();
        }

        if (deck.goodLandsInHand >= nrGoodLandsNeeded && deck.landsInHand >= turnAllowed) {
          countOK++;
        }
        if (deck.landsInHand >= turnAllowed) {
          countConditional++;
        }

      } // end of 1,000,000 iterations

      //System.out.println("With "+NrGoodLands+" good lands: Prob="+CountOK/CountConditional);
      results.add(countOK / countConditional);
    }

    return results;
  }

  public static void main(String[] args) {
    List<Double> results = new HowManySourcesUpdate().run();
    System.out.println("results = " + results);
  }
}

enum CardType {
  GOOD_LAND,
  LAND,
  OTHER
}

class Deck {

  private final SplittableRandom generator = new SplittableRandom();
  int numberOfLands;
  int numberOfGoodLands;
  int numberOfCards;
  int landsInHand; //This will describe the total amount of lands in your hand
  int goodLandsInHand; //This will describe the number of lands that can produce the right color in your hand
  private CardType next;

  void setDeck(int nrLands, int nrGoodLands, int nrCards) {
    numberOfLands = nrLands;
    numberOfGoodLands = nrGoodLands;
    numberOfCards = nrCards;
    landsInHand = 0;
    goodLandsInHand = 0;
  }

  private CardType chooseCard() {
    int RandomIntegerBetweenOneAndDeckSize = generator.nextInt(this.numberOfCards) + 1;
    int GoodLandCutoff = numberOfGoodLands;
    int LandCutoff = numberOfLands;
    if (RandomIntegerBetweenOneAndDeckSize <= GoodLandCutoff) {
      return CardType.GOOD_LAND;
    } else if (RandomIntegerBetweenOneAndDeckSize <= LandCutoff) {
      return CardType.LAND;
    } else {
      return CardType.OTHER;
    }
  }

  void scryCard(Predicate<CardType> pred) {
    if (next == null) {
      next = chooseCard();
    }
    if (!pred.test(next)) {
      // Keep
      next = null;
    }
  }

  void drawCards(int num) {
    for (int i = 0;i < num;i++) {
      drawCard();
    }
  }

  void drawCard() {
    CardType cardType = next;
    if (cardType == null) {
      cardType = chooseCard();
    } else {
      next = null;
    }
    switch (cardType) {
      case GOOD_LAND:
        this.numberOfGoodLands--;
        this.numberOfLands--;
        this.numberOfCards--;
        this.landsInHand++;
        this.goodLandsInHand++;
        break;
      case LAND:
        this.numberOfLands--;
        this.numberOfCards--;
        this.landsInHand++;
        break;
      case OTHER:
        this.numberOfCards--;
        break;
    }
  }
}
