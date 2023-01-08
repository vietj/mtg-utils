package com.julienviet.manabase;

import org.apache.commons.math3.distribution.HypergeometricDistribution;

/**
 * https://github.com/stdlib-js/stats-base-dists-hypergeometric-pmf
 * https://github.com/stdlib-js/stats-base-dists-hypergeometric-cdf
 */
public class Hypergeometric {

  public static void main(String[] args) {
    double res1 = pmf(2, 8, 4, 2);
    double res2 = cdf( 1, 8, 4, 2);
    System.out.println(res1);
    System.out.println(res2);
  }

  public static double pmf(
    int x,
    int N, // population size
    int K, // sub population size
    int n  // number of draws
  ) {
    HypergeometricDistribution dis = new HypergeometricDistribution(N, n, K);
    return dis.probability(x);
  }

  public static double cdf(
    int x,
    int N, // population size
    int K, // sub population size
    int n  // number of draws
  ) {
    HypergeometricDistribution dis = new HypergeometricDistribution(N, n, K);
    return dis.cumulativeProbability(x);
  }


}
