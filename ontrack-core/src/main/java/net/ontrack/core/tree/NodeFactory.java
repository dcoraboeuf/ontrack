package net.ontrack.core.tree;

import java.util.Collection;

public interface NodeFactory<D> {

    Node leaf(D data);

    Node node(D data, Collection<Node<D>> children);

}
