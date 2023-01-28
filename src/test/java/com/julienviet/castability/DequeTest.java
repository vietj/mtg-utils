package com.julienviet.castability;

import org.junit.Test;

import static org.junit.Assert.*;

public class DequeTest {

  @Test
  public void testAddLast() {
    Deque<Integer> list = new Deque<>(3);
    assertEquals(0, list.size());
    list.addLast(3);
    assertEquals(1, list.size());
    assertArrayEquals(new Object[]{3,null,null}, list.values);
    list.addLast(1);
    assertEquals(2, list.size());
    assertArrayEquals(new Object[]{3,1,null}, list.values);
    list.addLast(2);
    assertEquals(3, list.size());
    assertArrayEquals(new Object[]{3,1,2}, list.values);
    try {
      list.addLast(4);
      fail();
    } catch (IllegalStateException expected) {
    }
  }

  @Test
  public void testAddLast2() {
    Deque<Integer> list = new Deque<>(3);
    assertEquals(0, list.size());
    list.addLast(3);
    list.addLast(1);
    list.addLast(2);
    list.removeFirst();
    list.addLast(4);
    assertEquals(3, list.size());
    assertArrayEquals(new Object[]{4,1,2}, list.values);
  }

  @Test
  public void testRemoveFirst() {
    Deque<Integer> list = new Deque<>(3);
    assertEquals(0, list.size());
    list.addLast(3);
    list.addLast(1);
    list.addLast(2);
    assertEquals(3, (int)list.removeFirst());
    assertEquals(2, list.size());
    assertArrayEquals(new Object[]{null,1,2}, list.values);
    assertEquals(1, (int)list.removeFirst());
    assertEquals(1, list.size());
    assertArrayEquals(new Object[]{null,null,2}, list.values);
    assertEquals(2, (int)list.removeFirst());
    assertEquals(0, list.size());
    assertArrayEquals(new Object[]{null,null,null}, list.values);
  }

  @Test
  public void testRemoveFirst2() {
    Deque<Integer> list = new Deque<>(3);
    assertEquals(0, list.size());
    for (int i = 0;i < 10;i++) {
      list.addLast(i);
      assertEquals(i , (int)list.removeFirst());
    }
  }

  @Test
  public void testRemoveLast() {
    Deque<Integer> list = new Deque<>(3);
    assertEquals(0, list.size());
    list.addLast(3);
    list.addLast(1);
    assertEquals(1, (int)list.removeLast());
    assertEquals(1, list.size());
    assertArrayEquals(new Object[]{3,null,null}, list.values);
    assertEquals(3, (int)list.removeLast());
    assertEquals(0, list.size());
    assertArrayEquals(new Object[]{null,null,null}, list.values);
  }

  @Test
  public void testAddFirst() {
    Deque<Integer> list = new Deque<>(3);
    list.addFirst(3);
    assertEquals(1, list.size());
    assertArrayEquals(new Object[]{null,null,3}, list.values);
    list.addFirst(1);
    assertEquals(2, list.size());
    assertArrayEquals(new Object[]{null,1,3}, list.values);
    list.addFirst(2);
    assertEquals(3, list.size());
    assertArrayEquals(new Object[]{2,1,3}, list.values);
  }

  @Test
  public void testMix() {
    Deque<Integer> list = new Deque<>(3);
    list.addFirst(3);
    assertEquals(1, list.size());
    assertArrayEquals(new Object[]{null,null,3}, list.values);
    list.addLast(1);
    assertEquals(2, list.size());
    assertArrayEquals(new Object[]{1,null,3}, list.values);
  }

  @Test
  public void testShuffle() {
    Deque<Integer> list = new Deque<>(3);
    for (int i = 0;i < 3;i++) {
      list.addLast(i);
    }
    list.shuffle();
  }
}
