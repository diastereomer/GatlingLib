package io.diastereomer.gatling;

import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;

public class UnmodifiableLongSet extends LongSets.UnmodifiableSet implements LongSet {
  private final int hashCode;

  public UnmodifiableLongSet(final LongSet s) {
    super(s);
    hashCode = s.hashCode();
  }

  @Override
  public int hashCode() {
    return hashCode;
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }
}