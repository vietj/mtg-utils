package com.julienviet;

import java.util.Objects;

public interface ManaSymbol {

  ManaSymbol GENERIC = new ManaSymbol() {
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
  Typed COLORLESS = Typed.COLORLESS;

  enum Typed implements ManaSymbol {

    BLUE(ManaType.BLUE),
    RED(ManaType.RED),
    GREEN(ManaType.GREEN),
    BLACK(ManaType.BLACK),
    WHITE(ManaType.WHITE),
    COLORLESS(ManaType.COLORLESS);

    public final ManaType type;

    Typed(ManaType type) {
      this.type = type;
    }

    @Override
    public String toString() {
      return type.symbol;
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
    public String toString() {
      return first + "/" + second;
    }
  }
}
