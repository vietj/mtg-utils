package com.julienviet.manabase;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class CombinatoryTest {

  @Test
  public void testAlgoSub() {
    Main.generate2(6, 3);
  }

  @Test
  public void testAlgo() {
    List<String> list = Arrays.asList("A", "B", "C", "D");
    Set<List<String>> expected = new HashSet<>();
    expected.add(Collections.singletonList("A"));
    expected.add(Collections.singletonList("B"));
    expected.add(Collections.singletonList("C"));
    expected.add(Collections.singletonList("D"));
    expected.add(Arrays.asList("A", "B"));
    expected.add(Arrays.asList("A", "C"));
    expected.add(Arrays.asList("A", "D"));
    expected.add(Arrays.asList("B", "C"));
    expected.add(Arrays.asList("B", "D"));
    expected.add(Arrays.asList("C", "D"));
    Set<List<String>> result1 = new HashSet<>();
    Main.getAllCombinationsOfMinAndMaxLengthWithCallback(result1::add, list, 1, 2);
    assertEquals(expected, result1);
    Set<List<String>> result2 = new HashSet<>();
    Main.getAllCombinationsOfMinAndMaxLengthWithCallback2(comb -> {
      result2.add(comb);
      return true;
    }, list, 1, 2);
    assertEquals(expected, result2);
  }

  @Test
  public void testAlgoStop() {
    List<String> list = Arrays.asList("A", "B", "C", "D");
    Set<List<String>> expected = new HashSet<>();
    expected.add(Collections.singletonList("A"));
    expected.add(Collections.singletonList("B"));
    expected.add(Collections.singletonList("C"));
    expected.add(Collections.singletonList("D"));
    Set<List<String>> result2 = new HashSet<>();
    AtomicReference<List<String>> stop = new AtomicReference<>();
    List<String> res = Main.getAllCombinationsOfMinAndMaxLengthWithCallback2(comb -> {
      if (comb.size() < 2) {
        result2.add(new ArrayList<>(comb));
        return true;
      } else {
        stop.set(comb);
        return false;
      }
    }, list, 1, 2);
    assertSame(stop.get(), res);
    assertEquals(expected, result2);
  }
}
