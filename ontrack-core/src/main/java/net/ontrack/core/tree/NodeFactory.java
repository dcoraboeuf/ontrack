package net.ontrack.core.tree;

import java.util.Collection;

public interface NodeFactory<D> {

    Node<D> leaf(D data);

    Node<D> node(D data, Collection<Node<D>> children);

}
