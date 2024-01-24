package io.diastereomer.gatling;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;


public class AbstractUnmodifiableObjectPoolTest {
  private UnmodifiableObjectPoolLongSetImplement testPool = new UnmodifiableObjectPoolLongSetImplement();

  @Test
  public void shouldGetProfileSet() {
    LongSet expectSet1 = new LongOpenHashSet(Arrays.asList(2L, 3L, 5L));
    LongSet expectSet2 = new LongOpenHashSet(Arrays.asList(2L, 3L, 5L));
    LongSet expectSet3 = new LongOpenHashSet(Arrays.asList(2L, 3L, 5L));
    LongSet expectSet4 = new LongOpenHashSet(Arrays.asList(1L, 3L, 5L));
    LongSet actualSet1 = testPool.putIfAbsent(expectSet1);
    assertEquals(expectSet1, actualSet1);
    LongSet actualSet2 = testPool.putIfAbsent(expectSet2);
    assertEquals(expectSet1, actualSet2);
    LongSet actualSet3 = testPool.putIfAbsent(expectSet3);
    assertEquals(expectSet1, actualSet3);
    assertEquals(1, testPool.size());
    LongSet actualSet4 = testPool.putIfAbsent(expectSet4);
    assertEquals(expectSet4, actualSet4);
    assertEquals(2, testPool.size());
  }

  @Test
  public void shouldGCRemove() throws InterruptedException {
    LongSet expectSet1 = new LongOpenHashSet(Arrays.asList(2L, 3L, 5L));
    LongSet expectSet2 = new LongOpenHashSet(Arrays.asList(2L, 3L, 5L));
    LongSet expectSet3 = new LongOpenHashSet(Arrays.asList(2L, 3L, 5L));
    LongSet expectSet4 = new LongOpenHashSet(Arrays.asList(1L, 3L, 5L));
    LongSet actualSet1 = testPool.putIfAbsent(expectSet1);
    LongSet actualSet2 = testPool.putIfAbsent(expectSet2);
    LongSet actualSet3 = testPool.putIfAbsent(expectSet3);
    LongSet actualSet4 = testPool.putIfAbsent(expectSet4);
    actualSet1 = null;
    actualSet2 = null;
    System.gc();
    await().atMost(1, TimeUnit.SECONDS);
    assertEquals(expectSet1, actualSet3);
    assertEquals(expectSet4, actualSet4);
    assertEquals(2, testPool.size());
    actualSet3 = null;
    System.gc();
    await().atMost(10, TimeUnit.SECONDS).until(() -> testPool.size() == 1);
  }

  @Test
  public void shouldClear() {
    LongSet expectSet1 = new LongOpenHashSet(Arrays.asList(2L, 3L, 5L));
    LongSet expectSet2 = new LongOpenHashSet(Arrays.asList(2L, 3L, 5L));
    LongSet expectSet3 = new LongOpenHashSet(Arrays.asList(2L, 3L, 5L));
    LongSet expectSet4 = new LongOpenHashSet(Arrays.asList(1L, 3L, 5L));
    LongSet actualSet1 = testPool.putIfAbsent(expectSet1);
    LongSet actualSet2 = testPool.putIfAbsent(expectSet2);
    LongSet actualSet3 = testPool.putIfAbsent(expectSet3);
    LongSet actualSet4 = testPool.putIfAbsent(expectSet4);
    testPool.clear();
    assertEquals(0, testPool.size());
    assertEquals(expectSet1, actualSet3);
    assertEquals(expectSet4, actualSet4);
  }

  @Test
  public void shouldEqual() {
    LongSet expectSet1 = new LongOpenHashSet(Arrays.asList(2L, 3L, 5L));
    LongSet expectSet2 = new LongOpenHashSet(Arrays.asList(2L, 3L, 5L));
    LongSet expectSet3 = new LongOpenHashSet(Arrays.asList(2L, 3L, 5L));
    LongSet expectSet4 = new LongOpenHashSet(Arrays.asList(1L, 3L, 5L));
    LongSet actualSet1 = testPool.putIfAbsent(expectSet1);
    LongSet actualSet2 = testPool.putIfAbsent(expectSet2);
    LongSet actualSet3 = testPool.putIfAbsent(expectSet3);
    LongSet actualSet4 = testPool.putIfAbsent(expectSet4);
    UnmodifiableObjectPoolLongSetImplement newTestPool = new UnmodifiableObjectPoolLongSetImplement();
    assertNotEquals(testPool, newTestPool);
    LongSet actualSet5 = newTestPool.putIfAbsent(expectSet1);
    LongSet actualSet6 = newTestPool.putIfAbsent(expectSet2);
    LongSet actualSet7 = newTestPool.putIfAbsent(expectSet3);
    LongSet actualSet8 = newTestPool.putIfAbsent(expectSet4);
    assertEquals(testPool, newTestPool);
  }

  @Test
  public void shouldCopy() {
    LongSet expectSet1 = new LongOpenHashSet(Arrays.asList(2L, 3L, 5L));
    LongSet expectSet2 = new LongOpenHashSet(Arrays.asList(2L, 3L, 5L));
    LongSet expectSet3 = new LongOpenHashSet(Arrays.asList(2L, 3L, 5L));
    LongSet expectSet4 = new LongOpenHashSet(Arrays.asList(1L, 3L, 5L));
    LongSet actualSet1 = testPool.putIfAbsent(expectSet1);
    LongSet actualSet2 = testPool.putIfAbsent(expectSet2);
    LongSet actualSet3 = testPool.putIfAbsent(expectSet3);
    LongSet actualSet4 = testPool.putIfAbsent(expectSet4);

    UnmodifiableObjectPoolLongSetImplement newTestPool = new UnmodifiableObjectPoolLongSetImplement();
    newTestPool.copy(testPool);
    assertEquals(testPool, newTestPool);
  }

  private static class UnmodifiableObjectPoolLongSetImplement extends AbstractUnmodifiableObjectPool<LongSet> {
    @Override
    protected LongSet unmodifiableClone(LongSet longSet) {
      return new UnmodifiableLongSet(new LongOpenHashSet(longSet));
    }
  }
}
