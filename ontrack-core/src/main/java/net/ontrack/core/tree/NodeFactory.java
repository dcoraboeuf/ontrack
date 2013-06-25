package net.ontrack.core.tree;

import java.util.Collection;

public interface NodeFactory<D> {

    Node leaf(D data);

    Node node(Collection<Node<D>> children);

}
