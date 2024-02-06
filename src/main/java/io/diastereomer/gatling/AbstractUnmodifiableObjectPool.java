package io.diastereomer.gatling;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public abstract class AbstractUnmodifiableObjectPool<T> {
  private final WeakHashMap<T, WeakReference<T>> cache = new WeakHashMap<>();

  protected abstract T unmodifiableClone(T object);

  public synchronized T putIfAbsent(T object) {
    if(object == null){
      return null;
    }
    WeakReference<T> value = cache.get(object);
    if (value != null) {
      try {
        return value.get();
      } catch (NullPointerException e){
         // GC unexpectedly removed the entry from UnmodifiableObjectPool - will re-insert ");
      }
    }
    T newObject = unmodifiableClone(object);
    cache.put(newObject, new WeakReference<>(newObject));
    return newObject;
  }

  public void clear() {
    cache.clear();
  }

  public void copy(AbstractUnmodifiableObjectPool<T> src) {
    clear();
    src.cache.forEach((k, v) -> {
      T object = unmodifiableClone(k);
      cache.put(object, new WeakReference<>(object));
    });
  }

  public int size() {
    return cache.size();
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof AbstractUnmodifiableObjectPool) {
      AbstractUnmodifiableObjectPool<T> otherCache = (AbstractUnmodifiableObjectPool<T>) o;
      if (otherCache.cache.size() != cache.size()) {
        return false;
      }
      return cache.keySet().equals(otherCache.cache.keySet());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return cache.keySet().hashCode();
  }
}
