package com.julienviet.manabase;

public class Result {

  int ok;
  int nok;

  double ratio() {
    double val = ok / (double) (ok + nok);
    return Math.floor(val * 10000) / 100;
  }
}
