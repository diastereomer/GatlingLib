package io.diastereomer.gatling;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.collections4.CollectionUtils;

/**
 * To concurrently depth first iterate a directed acyclic graph.
 * This approach can be applied to run all dependents of a collection of nodes before run the parent nodes.
 *
 * @author Pei Wang
 */
public abstract class AbstractConcurrentDFSDirectedAcyclicGraph<T extends Runnable> {
  protected int timeout = 60;
  protected abstract Collection<T> getDependents(T node);
  private final Map<T, CompletableFuture<?>> node2CompletableFuture = new ConcurrentHashMap<>();
  private final Collection<T> roots;

  public AbstractConcurrentDFSDirectedAcyclicGraph(Collection<T> roots) {
    this.roots = roots;
  }

  public void depthFirstSearch() {
    roots.forEach(this::runNodeParallel);
    node2CompletableFuture.forEach((node, future) -> {
      try {
        future.get(timeout, TimeUnit.SECONDS);
      } catch (InterruptedException | ExecutionException | TimeoutException e) {
        node2CompletableFuture.clear();
        Thread.currentThread().interrupt();
      }
    });
    node2CompletableFuture.clear();
  }

  private void runNodeParallel(T node) {
    node2CompletableFuture.computeIfAbsent(node, key -> CompletableFuture.runAsync(() -> {
      Collection<T> dependents = getDependents(node);
      if (CollectionUtils.isNotEmpty(dependents)) {
        dependents.forEach(this::runNodeParallel);
        dependents.forEach(dependent -> {
          try {
            var dependentFuture = node2CompletableFuture.get(dependent);
            if (dependentFuture == null) {
              return;
            }
            dependentFuture.get(timeout, TimeUnit.SECONDS);
          } catch (InterruptedException | ExecutionException | TimeoutException e) {
            Thread.currentThread().interrupt();
          }
        });
      }
      node.run();
    }));
  }
}
