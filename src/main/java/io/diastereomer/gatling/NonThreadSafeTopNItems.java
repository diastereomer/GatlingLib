package io.diastereomer.gatling;

import java.util.PriorityQueue;

/**
 * An implementation to  store top N comparable items.
 * This implementation allows dupes and not thread safe.
 *
 * @author Pei Wang
 */
public class NonThreadSafeTopNItems<T extends Comparable<T>> extends AbstractTopNItems<T> {
  protected final PriorityCache<T> cache;

  @Override
  protected PriorityCache<T> delegate() {
    return cache;
  }

  public NonThreadSafeTopNItems(int topN) {
    super(topN);
    PriorityQueue<T> priorityQueue = new PriorityQueue<>(topN + 2 * MARGIN);
    cache = new PriorityCache<T>() {
      @Override
      public int size() {
        return priorityQueue.size();
      }

      @Override
      public T peek() {
        return priorityQueue.peek();
      }

      @Override
      public boolean add(T t) {
        return priorityQueue.add(t);
      }

      @Override
      public T poll() {
        return priorityQueue.poll();
      }

      @Override
      public boolean isEmpty() {
        return priorityQueue.isEmpty();
      }

      @Override
      public void clear() {
        priorityQueue.clear();
      }
    };
  }
}
