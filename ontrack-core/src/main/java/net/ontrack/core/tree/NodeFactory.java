package net.ontrack.core.tree;

import java.util.Collection;

public interface NodeFactory<D> {

    Node<D> leaf(D data);

    Node<D> node(Collection<Node<D>> children);

}
