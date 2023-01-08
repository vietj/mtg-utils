package com.julienviet.dctheory;

import java.util.ArrayList;
import java.util.List;

public class Deck {

  private List<Card> cards = new ArrayList<>();

  public Deck addCard(Card card) {
    cards.add(card);
    return this;
  }

  public Deck addCard(List<Card> cards) {
    cards.addAll(cards);
    return this;
  }

}
