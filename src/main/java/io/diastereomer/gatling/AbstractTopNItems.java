package io.diastereomer.gatling;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;

/**
 * An abstract class to store top N comparable items.
 *
 * @author Pei Wang
 */
public abstract class AbstractTopNItems<T extends Comparable<T>> {
  protected static final int MARGIN = 3;

  protected abstract PriorityCache<T> delegate();

  private Collection<T> sortedCollection;

  protected int maxSize;
  protected int topN;

  protected AbstractTopNItems(int topN) {
    maxSize = MARGIN + topN;
    this.topN = topN;
  }

  public void clear() {
    delegate().clear();
  }

  public boolean put(T t) {
    if (delegate().size() < topN || t.compareTo(delegate().peek()) > 0) {
      boolean isAdded = delegate().add(t);
      if (isAdded) {
        trim();
      }
      return isAdded;
    }
    return false;
  }

  public void trim() {
    if (delegate().size() >= maxSize) {
      synchronized (this) {
        while (delegate().size() > topN) {
          delegate().poll();
        }
      }
    }
  }

  public synchronized Collection<T> drainToDescSortedCollection() {
    while (delegate().size() > topN) {
      delegate().poll();
    }
    Deque<T> list = new LinkedList<>();
    while (!delegate().isEmpty()) {
      list.addFirst(delegate().poll());
    }
    return list;
  }

  public synchronized Collection<T> toDescSortedCollection() {
    if (sortedCollection != null) {
      return sortedCollection;
    }
    sortedCollection = drainToDescSortedCollection();
    return sortedCollection;
  }
}
