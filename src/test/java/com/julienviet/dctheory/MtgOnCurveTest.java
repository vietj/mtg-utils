package com.julienviet.dctheory;

import com.julienviet.karsten.MtgOnCurve;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MtgOnCurveTest {

  @Test
  public void testMulligan() {
    assertFalse(MtgOnCurve.mullStrategy(7, 0));
    assertFalse(MtgOnCurve.mullStrategy(7, 1));
    assertTrue(MtgOnCurve.mullStrategy(7, 2));
    assertTrue(MtgOnCurve.mullStrategy(7, 3));
    assertTrue(MtgOnCurve.mullStrategy(7, 4));
    assertTrue(MtgOnCurve.mullStrategy(7, 5));
    assertFalse(MtgOnCurve.mullStrategy(7, 6));
    assertFalse(MtgOnCurve.mullStrategy(7, 7));
  }
}
