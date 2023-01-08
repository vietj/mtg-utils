package com.julienviet.manabase;

public enum Color {

  BLUE(),
  RED(),
  GREEN(),
  BLACK(),
  WHITE();

  public final int mask;

  Color() {
    this.mask = 2 ^ ordinal();
  }

}
