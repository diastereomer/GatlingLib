package io.diastereomer.gatling;

public interface PriorityCache<T extends Comparable<T>> {
  int size();

  T peek();

  boolean add(T t);

  T poll();

  boolean isEmpty();

  void clear();
}
