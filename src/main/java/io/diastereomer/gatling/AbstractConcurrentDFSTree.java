package io.diastereomer.gatling;

import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;

/**
 * To concurrently depth first iterate a tree.
 * This approach can be applied to run all dependents of a tree node before run the parent node.
 *
 * @author Pei Wang
 */
public abstract class AbstractConcurrentDFSTree<T extends Runnable> {
  protected int timeout = 60;

  protected abstract Collection<T> getDependents(T node);

  private final T root;

  public AbstractConcurrentDFSTree(T root) {
    this.root = root;
  }

  public void depthFirstSearch() {
    runNodeParallel(root);
  }

  private void runNodeParallel(T node) {
    Collection<T> dependents = getDependents(node);
    if (CollectionUtils.isEmpty(dependents)) {
      return;
    }
    dependents.parallelStream().forEach(this::runNodeParallel);
    node.run();
  }
}
