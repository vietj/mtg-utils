package com.julienviet.manabase;

import com.julienviet.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Main {


  //      "Baleful Strix": "96.57%",
//      "Imperial Recruiter": "0.00%",
//      "Midnight Reaper": "99.02%",
//      "Ophiomancer": "99.02%",
//      "Bone Shredder": "99.02%",
//      "Glen Elendra Archmage": "99.26%",
//      "Shriekmaw": "99.92%",
//      "The Scarab God": "99.68%",
//      "Grave Titan": "99.47%",
//      "Duplicant": "100.00%",
//      "Vampiric Tutor": "97.76%",
//      "Inquisition of Kozilek": "97.76%",
//      "Collective Brutality": "98.81%",
//      "Bitterblossom": "98.81%",
//      "Exhume": "98.81%",
//      "Chart a Course": "97.76%",
//      "Thought Erasure": "96.57%",
//      "Recurring Nightmare": "99.02%",
//      "Liliana of the Veil": "89.18%",
//      "Compulsive Research": "98.03%",
//      "Birthing Pod": "0.00%",
//      "Mystic Confluence": "96.11%",
//      "Living Death": "98.27%"

  public static final String DECK = "{\n" +
    "    \"deck\": [\n" +
    "        \"1 Baleful Strix\",\n" +
    "        \"1 Imperial Recruiter\",\n" +
    "        \"1 Midnight Reaper\",\n" +
    "        \"1 Ophiomancer\",\n" +
    "        \"1 Bone Shredder\",\n" +
    "        \"1 Glen Elendra Archmage\",\n" +
    "        \"1 Shriekmaw\",\n" +
    "        \"1 The Scarab God\",\n" +
    "        \"1 Grave Titan\",\n" +
    "        \"1 Duplicant\",\n" +
    "        \"1 Underground Sea\",\n" +
    "        \"1 Watery Grave\",\n" +
    "        \"5 Island\",\n" +
    "        \"8 Swamp\",\n" +
    "        \"1 Vampiric Tutor\",\n" +
    "        \"1 Inquisition of Kozilek\",\n" +
    "        \"1 Collective Brutality\",\n" +
    "        \"1 Bitterblossom\",\n" +
    "        \"1 Exhume\",\n" +
    "        \"1 Chart a Course\",\n" +
    "        \"1 Thought Erasure\",\n" +
    "        \"1 Recurring Nightmare\",\n" +
    "        \"1 Liliana of the Veil\",\n" +
    "        \"1 Compulsive Research\",\n" +
    "        \"1 Birthing Pod\",\n" +
    "        \"1 Mystic Confluence\",\n" +
    "        \"1 Urza's Bauble\",\n" +
    "        \"1 Living Death\"\n" +
    "    ]\n" +
    "}";

  public static void main(String[] args) throws Exception {
    CardDb db = new CardDb();

    // Deck deck = Deck.load(db, new JsonObject(DECK));
    DeckList deckList = DeckList.load(db, Main.class.getClassLoader().getResourceAsStream("deck2.txt"));

    Map<Card.Spell, Result> data = analyze(deckList);

    System.out.println("DONE");
    data.forEach((spell, res) -> {
      double ratio = res.ok / (double) (res.ok + res.nok);
      System.out.println(spell.name + " " + Math.floor( ratio * 10000) / 100);
    });
  }

  public static int getAverageLandCountInHand(int deckSize, int landCount) {
    double maxProba = 0D;
    int output = 0;
    for (int i = 2;i <= 7;i++) {
      double proba = Hypergeometric.pmf(i, deckSize, landCount, 7);
      if (proba > maxProba) {
        output = i;
        maxProba = proba;
      }
    }
    return output;
  }

  public static Map<Card.Spell, Result> analyze(DeckList deckList) {

    List<Card.Spell> spells = deckList.spells();
    List<Card.Land> lands = deckList.lands();

    Set<ManaCost> manaCosts = spells.stream().map(spell -> spell.cost).collect(Collectors.toSet());


    int averageLandCount = getAverageLandCountInHand(deckList.size(), lands.size());;

    int maxCmc = Math.max(spells.stream().mapToInt(s -> s.cmc).max().orElse(4), 4);
    int minCmc = Math.min(spells.stream().mapToInt(s -> s.cmc).max().orElse(2), 2);

    Map<ManaCost, Result> manaCostsResults = new HashMap<>();

    getAllCombinationsOfMinAndMaxLengthWithCallback2(comb -> {
      manaCosts.forEach(manaCost -> {
        int cmc = manaCost.cmc();
        if (comb.size() >= Math.max(cmc, 2) && comb.size() <= Math.max(2, Math.max(cmc, averageLandCount))) {

          // TODO
//          if (!comb.every(land => land.producesMana)) {
//            data[spell.name].nok ++;
//            return;
//          }
          Result res = manaCostsResults.get(manaCost);
          if (res == null) {
            res = new Result();
            manaCostsResults.put(manaCost, res);
          }
          if (canPlaySpellOnCurve(deckList, comb, manaCost)) {
            res.ok++;
          } else {
            if (manaCost.cmc() == 0) {
              System.out.println("Cannot play zero cmc spell with " + comb);
            }
            res.nok++;
          }
        }
      });
      return true;
    }, lands, minCmc, maxCmc);

    Map<Card.Spell, Result> spellResults = new HashMap<>();
    spells.forEach(spell -> {
      spellResults.put(spell, manaCostsResults.get(spell.cost));
    });

    return spellResults;
  }

  public static boolean canPlaySpellOnCurve(DeckList deckList, List<Card.Land> lands, Card.Spell spell) {
    return canPlaySpellOnCurve(deckList, lands, spell.cost);
  }

  public static boolean hasCorrectColors() {


    return false;
  }

  public static boolean canPlaySpellOnCurve(DeckList deckList, List<Card.Land> lands, ManaCost spell) {

//    if (!hasCorrectColors(lands, spell)) {
//      return false;
//    }

    int cmc = spell.cmc();
    if (cmc == 0) {
      return true;
    }

    if (!hasUntappedLand(lands, cmc)) {
      return false;
    }
    List<Card.Land> res = getAllCombinationsOfMinAndMaxLengthWithCallback2(comb -> !evaluateCost(deckList, comb, spell), lands, cmc, cmc);

    return res != null;

  }

  public static <T> List<List<T>> getAllCombinations(List<T> rest) {
    List<List<T>> a = new ArrayList<>();
    getAllCombinations(rest, new ArrayList<>(), a);
    return a;
  }

  private static <T> void getAllCombinations(List<T> rest, List<T> active, List<List<T>> res) {
    if (rest.isEmpty()) {
      res.add(active);
    } else {
      List<T> l = new ArrayList<>(active);
      l.add(rest.get(0));
      getAllCombinations(rest.subList(1, rest.size()), l, res);
      getAllCombinations(rest.subList(1, rest.size()), active, res);
    }
  }


  //  function canPlaySpellOnCurve(lands, spell) {
//    if (!hasCorrectColors(lands, spell)) {
//      return false;
//    }
//    if (!hasUntappedLand(lands, spell)) {
//      return false;
//    }
//
//    const comb = getAllCombinations(lands).filter(l => l.length === spell.cmc);
//    if (comb.length === 0) {
//      return false;
//    }
//    return comb.some(comb => evaluateCost(comb, spell.cost, spell.cmc));
//  }


//  function cachedCanPlaySpellOnCurve(lands, spell) {
//    const key = JSON.stringify([spell.mana_cost, lands.map(l => l.name).sort()]);
//    const value = cache.has(key) ?
//      cache.get(key) :
//      canPlaySpellOnCurve(lands, spell);
//
//    cache.set(key, value);
//    return value;
//  }

  public static <T> void getAllCombinationsOfMinAndMaxLengthWithCallback(Consumer<List<T>> callback, List<T> rest, int min, int max) {
    getAllCombinationsOfMinAndMaxLengthWithCallback(callback, rest, min, max, new ArrayList<>());
  }

  public static <T> void getAllCombinationsOfMinAndMaxLengthWithCallback(Consumer<List<T>> callback, List<T> rest, int min, int max, List<T> active) {
    if (rest.isEmpty() || active.size() == max) {
      if (active.size() >= min) {
        callback.accept(active);
      }
      return;
    }
    List<T> a = new ArrayList<>(active);
    a.add(rest.get(0));
    getAllCombinationsOfMinAndMaxLengthWithCallback(callback, rest.subList(1, rest.size()), min, max, a);
    getAllCombinationsOfMinAndMaxLengthWithCallback(callback, rest.subList(1, rest.size()), min, max, active);
  }

  public static <T> List<T> getAllCombinationsOfMinAndMaxLengthWithCallback2(Predicate<List<T>> callback, List<T> rest, int min, int max) {
    for (int i = min;i <= max;i++) {
      List<T> stopped = generate(callback, rest, i);
      if (stopped != null) {
        return stopped;
      }
    }
    return null;
  }

  public static <T> List<T> generate(Predicate<List<T>> callback, List<T> rest, int r) {
    int[] combination = new int[r];
    List<T> list = new ArrayList<>();

    for (int i = 0; i < r; i++) {
      combination[i] = i;
    }

    while (combination[r - 1] < rest.size()) {
      list.clear();
      for (int j : combination) {
        list.add(rest.get(j));
      }
      boolean accepted = callback.test(list);
      if (!accepted) {
        return list;
      }

      int t = r - 1;
      while (t != 0 && combination[t] == rest.size() - r + t) {
        t--;
      }
      combination[t]++;
      for (int i = t + 1; i < r; i++) {
        combination[i] = combination[i - 1] + 1;
      }
    }

    return null;
  }

  public static void generate2(int n, int r) {
    int[] combination = new int[r];

    for (int i = 0; i < r; i++) {
      combination[i] = i;
    }

    while (combination[r - 1] < n) {
//      List<Integer> list = new ArrayList<>();
//      for (int i = 0; i < combination.length;i++) {
//        list.add(combination[i]);
//      }
//      System.out.println(list);
      int t = r - 1;
      while (t != 0 && combination[t] == n - r + t) {
        t--;
      }
      combination[t]++;
      for (int i = t + 1; i < r; i++) {
        combination[i] = combination[i - 1] + 1;
      }
    }
  }

  public static boolean evaluateCost(DeckList deck, List<Card.Land> lands, ManaCost cost) {

    int cmc = cost.cmc();
    if (cmc == 0) {
      return true;
    }

    List<Card.Land> remainingLands = new ArrayList<>(lands);
    List<Card.Land> usedLands = new ArrayList<>();

    remainingLands.sort(Comparator
      .comparingInt((Card.Land o) -> deck.resolveManaTypes(o).size())
      .thenComparing(o -> o.name)
    );

    Map<ManaSymbol, Integer> map = cost.map();

    List<ManaSymbol> sortedLandsToFind = new ArrayList<>(map.keySet());
    sortedLandsToFind.sort(Comparator
      .comparingInt(ManaSymbol::sortPriority)
      .thenComparing(Object::toString));

    sortedLandsToFind.forEach(symbol -> {
      int amount = map.get(symbol);
      while (amount > 0) {
        Card.Land land = findCorrectLand(deck, remainingLands, symbol);
        if (land == null) {
          break;
        }
        usedLands.add(land);
        remainingLands.remove(land);
        amount--;
      }
      map.put(symbol, amount);
    });

    boolean res = map.entrySet().stream().allMatch(e -> e.getValue() == 0) && hasUntappedLand(usedLands, cmc);
    return res;
  }

  private static boolean hasUntappedLand(List<Card.Land> lands, int cmc) {
    for (Card.Land land : lands) {
      if (!land.etbTapped().apply(lands, cmc)) {
        return true;
      }
    }
    return false;
  }

  public static Card.Land findCorrectLand(DeckList deckList, List<Card.Land> lands, ManaSymbol symbol) {
    if (lands.isEmpty()) {
      return null;
    }
    if (symbol == ManaSymbol.GENERIC) {
      return lands.get(0);
    }
    if (symbol instanceof ManaSymbol.Hybrid) {
      ManaSymbol.Hybrid hybrid = (ManaSymbol.Hybrid) symbol;
      Card.Land land = findCorrectLand(deckList, lands, hybrid.first);
      if (land == null) {
        land = findCorrectLand(deckList, lands, hybrid.second);
      }
      return land;
    }
    ManaSymbol.Typed typed = (ManaSymbol.Typed) symbol;
    for (Card.Land land : lands) {
      Set<ManaSymbol.Typed> resolvedManaTypes = deckList.resolveManaTypes(land);
      if (resolvedManaTypes.size() == 1 && resolvedManaTypes.contains(typed)) {
        return land;
      }
    }
    for (Card.Land land : lands) {
      if (deckList.resolveManaTypes(land).contains(typed)) {
        return land;
      }
    }
    return null;
  }
}
