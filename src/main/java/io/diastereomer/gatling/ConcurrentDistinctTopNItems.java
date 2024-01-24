package io.diastereomer.gatling;

import java.util.concurrent.ConcurrentSkipListSet;

/**
 * An implementation to concurrently store top N comparable items.
 * This implementation doesn't allow dupes.
 *
 * @author Pei Wang
 */
public class ConcurrentDistinctTopNItems <T extends Comparable<T>> extends AbstractTopNItems<T>{
  private final PriorityCache<T> cache;

  public ConcurrentDistinctTopNItems(int topN) {
    super(topN);
    ConcurrentSkipListSet<T> sortedSetCache = new ConcurrentSkipListSet<>((t1, t2) -> {
      int compareResult = t1.compareTo(t2);
      if (compareResult == 0) {
        return t1.equals(t2) ? 0 : -1;
      }
      return compareResult;
    });
    cache = new PriorityCache<T>() {
      @Override
      public int size() {
        return sortedSetCache.size();
      }

      @Override
      public T peek() {
        return sortedSetCache.first();
      }

      @Override
      public boolean add(T t) {
        return sortedSetCache.add(t);
      }

      @Override
      public T poll() {
        return sortedSetCache.pollFirst();
      }

      @Override
      public boolean isEmpty() {
        return sortedSetCache.isEmpty();
      }

      @Override
      public void clear() {
        sortedSetCache.clear();
      }
    };
  }

  @Override
  public PriorityCache<T> delegate() {
    return cache;
  }
}
