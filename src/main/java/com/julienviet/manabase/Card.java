package com.julienviet.manabase;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

public class Card {

  public final String name;
  public final String type;
  public final int cmc;

  public Card(String name, String type, int cmc) {
    this.name = name;
    this.type = type;
    this.cmc = cmc;
  }

  @Override
  public String toString() {
    return name;
  }

  public static class Land extends Card {

    private Set<ManaSymbol.Typed> manaTypes; // colors
    private BiFunction<List<Land>, Integer, Boolean> etbTapped;
    private Set<String> superTypes;
    private Set<String> subTypes;
    public final Set<String> fetchedTypes;

    public Land(String name,
                Set<String> superTypes,
                String type,
                Set<String> subTypes,
                int cmc,
                Set<ManaSymbol.Typed> manaTypes,
                BiFunction<List<Land>, Integer, Boolean> etbTapped,
                Set<String> fetchedTypes) {
      super(name, type, cmc);
      this.superTypes = superTypes;
      this.subTypes = subTypes;
      this.manaTypes = manaTypes;
      this.etbTapped = etbTapped;
      this.fetchedTypes = fetchedTypes;
    }

    public Land mutable() {
      if (fetchedTypes != null) {
        return new Land(name, new HashSet<>(superTypes), type, subTypes, cmc, new HashSet<>(manaTypes), etbTapped, fetchedTypes);
      } else {
        return this;
      }
    }

    public void resolveManaTypes(List<Land> list) {
      for (String s : fetchedTypes) {
        for (Land l : list) {
          if (l.subTypes.contains(s) || l.superTypes.contains(s)) {
            manaTypes.addAll(l.manaTypes);
          }
        }
      }
    }

    public Set<ManaSymbol.Typed> manaTypes() {
      return manaTypes;
    }

    public Set<String> subTypes() {
      return subTypes;
    }

    public BiFunction<List<Land>, Integer, Boolean> etbTapped() {
      return etbTapped;
    }
  }

  public static class Spell extends Card {

    public final ManaCost cost;

    public Spell(String name, String type, int cmc, ManaCost cost) {
      super(name, type, cmc);
      this.cost = cost;
    }
  }
}
