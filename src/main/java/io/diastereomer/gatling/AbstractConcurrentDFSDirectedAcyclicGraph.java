package io.diastereomer.gatling;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;

/**
 * To concurrently depth first iterate a directed acyclic graph.
 * This approach can be applied to run all dependents of a collection of nodes before run the parent nodes.
 *
 * @author Pei Wang
 */
public abstract class AbstractConcurrentDFSDirectedAcyclicGraph<T extends Runnable> {
  protected int timeout = 60;
  protected TimeUnit timeUnit = TimeUnit.SECONDS;
  protected abstract Collection<T> getDependents(T node);
  private final Map<T, CompletableFuture<?>> node2CompletableFuture = new ConcurrentHashMap<>();
  private final Collection<T> roots;

  public AbstractConcurrentDFSDirectedAcyclicGraph(Collection<T> roots) {
    this.roots = roots;
  }

  public void depthFirstSearch() {
    node2CompletableFuture.clear();
    roots.forEach(this::runNodeParallel);
    var futures = node2CompletableFuture.values().toArray(size -> new CompletableFuture<?>[size]);
    CompletableFuture.allOf(futures).orTimeout(timeout, timeUnit).join();
  }

  private void runNodeParallel(T node) {
    node2CompletableFuture.computeIfAbsent(node, key -> CompletableFuture.runAsync(() -> {
      Collection<T> dependents = getDependents(node);
      if (CollectionUtils.isNotEmpty(dependents)) {
        dependents.forEach(this::runNodeParallel);
        var dependentFutures = dependents.stream().map(node2CompletableFuture::get).filter(Objects::nonNull).toArray(size -> new CompletableFuture<?>[size]);
        CompletableFuture.allOf(dependentFutures).orTimeout(timeout, timeUnit).join();
      }
      node.run();
    }).orTimeout(timeout, timeUnit));
  }
}
