package com.julienviet;

import java.util.concurrent.ThreadLocalRandom;

public class Deque<T> {

  Object[] values;
  private int size;
  private int head;

  public Deque(int maxSize) {
    values = new Object[maxSize];
    size = 0;
    head = 0;
  }

  public int size() {
    return size;
  }

  public T removeLast() {
    if (size < 1) {
      throw new IllegalStateException();
    }
    int idx = (head + size - 1) % values.length;
    T last = (T) values[idx];
    values[idx] = null;
    size--;
    return last;
  }

  public void addLast(T item) {
    if (size >= values.length) {
      throw new IllegalStateException();
    }
    int idx = (head + size) % values.length;
    values[idx] = item;
    size++;
  }

  public void addFirst(T item) {
    if (size >= values.length) {
      throw new IllegalStateException();
    }
    head--;
    int idx = head % values.length;
    if (idx < 0) {
      idx += values.length;
    }
    values[idx] = item;
    head = idx;
    size++;
  }

  public T removeFirst() {
    if (size < 1) {
      throw new IllegalStateException();
    }
    int idx = head % values.length;
    Object first = values[idx];
    values[idx] = null;
    head++;
    size--;
    return (T)first;
  }

  public void shuffle() {
    ThreadLocalRandom random = ThreadLocalRandom.current();
    for (int i = size; i > 1; i--) {
      swap(i - 1, random.nextInt(i));
    }
  }

  private void swap(int i, int j) {
    int idx1 = (head + i) % size;
    int idx2 = (head + j) % size;
    Object tmp = values[idx1];
    values[idx1] = values[idx2];
    values[idx2] = tmp;
  }
}
