package com.julienviet;

import java.util.Objects;

public interface ManaSymbol {

  ManaSymbol GENERIC = new ManaSymbol() {
    @Override
    public int sortPriority() {
      return 3;
    }

    @Override
    public String toString() {
      return "generic";
    }
  };

  // Short cuts
  Typed RED = Typed.RED;
  Typed GREEN = Typed.GREEN;
  Typed BLUE = Typed.BLUE;
  Typed BLACK = Typed.BLACK;
  Typed WHITE = Typed.WHITE;
  // Typed COLORLESS = Typed.COLORLESS;

  enum Typed implements ManaSymbol {

    BLUE("U"),
    RED("R"),
    GREEN("G"),
    BLACK("B"),
    WHITE("W");
    // COLORLESS();

    private final String string;
    public final int mask;

    Typed(String string) {
      this.string = string;
      this.mask = 2 ^ ordinal();
    }



    @Override
    public String toString() {
      return string;
    }

    @Override
    public int sortPriority() {
      return 1;
    }
  }

//  enum Phyrexian implements ManaSymbol {
//
//    BLUE(Color.BLUE),
//    RED(Color.RED),
//    GREEN(Color.GREEN),
//    BLACK(Color.BLACK),
//    WHITE(Color.WHITE);
//
//    ;
//
//    private final Color color;
//
//    Phyrexian(Color color) {
//      this.color = color;
//    }
//  }

  static Hybrid hybrid(ManaSymbol first, ManaSymbol second) {
    return new Hybrid(first, second);
  }

  class Hybrid implements ManaSymbol {

    public final ManaSymbol first;
    public final ManaSymbol second;

    public Hybrid(ManaSymbol first, ManaSymbol second) {
      Objects.requireNonNull(first);
      Objects.requireNonNull(second);
      this.first = first;
      this.second = second;
    }

    @Override
    public int hashCode() {
      return first.hashCode() ^ second.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof Hybrid) {
        Hybrid that = (Hybrid) obj;
        return that.first.equals(first) && that.second.equals(second);
      }
      return false;
    }

    @Override
    public int sortPriority() {
      return 2;
    }

    @Override
    public String toString() {
      return first + "/" + second;
    }
  }

  int sortPriority();

}
