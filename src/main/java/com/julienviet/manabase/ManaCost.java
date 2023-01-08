package com.julienviet.manabase;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManaCost {

  private static final Pattern RE = Pattern.compile("\\{([^}]+)}");

  public static ManaCost parse(String line) {
    ManaCost cost = new ManaCost();
    Matcher matcher = RE.matcher(line);
    while (matcher.find()) {
      String symbol = matcher.group(1);
      Bilto ms = parseSymbol(symbol);
      if (ms == null) {
        System.out.println("No mana symbol for " + symbol);
      } else {
        cost.add(ms.symbol, ms.amount);
      }
    }

    return cost;
  }

  private static class Bilto {
    private ManaSymbol symbol;
    private int amount;
    Bilto(ManaSymbol symbol, int amount) {
      this.symbol = symbol;
      this.amount = amount;
    }
  }

  private static Bilto parseSymbol(String symbol) {
    if (symbol.indexOf('/') != -1) {
      // TODO
      String[] array = symbol.split("/");
      if (array[array.length - 1].equals("P")) {
        if (array.length == 2) {
          return parseSimple(array[0]);
        } else {
          return new Bilto(ManaSymbol.hybrid(parseSimple(array[0]).symbol, parseSimple(array[1]).symbol), 1);
        }
      } else {
        return new Bilto(ManaSymbol.hybrid(parseSimple(array[0]).symbol, parseSimple(array[1]).symbol), 1);
      }
    } else {
      return parseSimple(symbol);
    }
  }

  private static Bilto parseSimple(String value) {
    try {
      int generic = Integer.parseInt(value);
      return new Bilto(ManaSymbol.GENERIC, generic);
    } catch (NumberFormatException e) {
      // Not generic
    }
    switch (value) {
      case "R":
        return new Bilto(ManaSymbol.Typed.RED, 1);
      case "G":
        return new Bilto(ManaSymbol.Typed.GREEN, 1);
      case "W":
        return new Bilto(ManaSymbol.Typed.WHITE, 1);
      case "B":
        return new Bilto(ManaSymbol.Typed.BLACK, 1);
      case "U":
        return new Bilto(ManaSymbol.Typed.BLUE, 1);
      case "S": // For now generic
      case "C":
        return new Bilto(ManaSymbol.GENERIC, 1);
      case "X":
        // Hardcoded for now
        return new Bilto(ManaSymbol.GENERIC, 2);
    }
    return null;
  }

  private final Map<ManaSymbol, Integer> types = new HashMap<>();
  private int cmc = 0;

  public ManaCost add(ManaSymbol symbol, int amount) {
    if (symbol == null) {
      throw new NullPointerException();
    }
    if (types.containsKey(symbol)) {
      types.put(symbol, types.get(symbol) + amount);
    } else {
      types.put(symbol, amount);
    }
    cmc = -1;
    return this;
  }

  public Map<ManaSymbol, Integer> map() {
    return new HashMap<>(types);
  }

  public ManaCost add(ManaSymbol symbol) {
    return add(symbol, 1);
  }

  public int cmc() {
    if (cmc == -1) {
      cmc = types.values().stream().mapToInt(i -> i).sum();
    }
    return cmc;
  }

  @Override
  public int hashCode() {
    return types.hashCode();
  }

  public boolean equals(Object o) {
    if (o instanceof ManaCost) {
      return ((ManaCost) o).types.equals(types);
    }
    return false;
  }
}
