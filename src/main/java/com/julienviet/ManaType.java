package com.julienviet;

public enum ManaType {

  BLUE("U"),
  RED("R"),
  GREEN("G"),
  BLACK("B"),
  WHITE("W"),
  COLORLESS("C");

  final String symbol;

  ManaType(String symbol) {
    this.symbol = symbol;
  }
}
