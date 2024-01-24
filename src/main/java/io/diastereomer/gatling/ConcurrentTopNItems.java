package io.diastereomer.gatling;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * An implementation to concurrently store top N comparable items.
 * This implementation allows dupes.
 *
 * @author Pei Wang
 */
public class ConcurrentTopNItems<T extends Comparable<T>> extends AbstractTopNItems<T> {
  private final PriorityCache<T> cache;

  public ConcurrentTopNItems(int topN) {
    super(topN);
    PriorityBlockingQueue<T> priorityBlockingQueue = new PriorityBlockingQueue<>(topN + 2 * MARGIN);
    cache = new PriorityCache<T>() {
      @Override
      public int size() {
        return priorityBlockingQueue.size();
      }

      @Override
      public T peek() {
        return priorityBlockingQueue.peek();
      }

      @Override
      public boolean add(T t) {
        return priorityBlockingQueue.add(t);
      }

      @Override
      public T poll() {
        return priorityBlockingQueue.poll();
      }

      @Override
      public boolean isEmpty() {
        return priorityBlockingQueue.isEmpty();
      }

      @Override
      public void clear() {
        priorityBlockingQueue.clear();
      }
    };
  }

  @Override
  public PriorityCache<T> delegate() {
    return cache;
  }
}
