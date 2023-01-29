package com.julienviet;

public class Deck {

  private Deque<Card> cards;

  public Deck(DeckList list) {
    cards = new Deque<>(list.size());
    for (Card card : list) {
      cards.addLast(card);
    }
  }

  public Deck shuffle() {
    cards.shuffle();
    return this;
  }

  public Deck bottom(Card card) {
    cards.addLast(card);
    return this;
  }

  public Card draw() {
    return cards.removeFirst();
  }
}
