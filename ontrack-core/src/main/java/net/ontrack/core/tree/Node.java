package net.ontrack.core.tree;

import java.util.Collection;

public interface Node<D> extends Collection<Node<D>> {

    D getData();

    NodeFactory<D> getFactory();

    Node<D> transform(NodeTransformer<D> transformer);

    Node<D> transform(NodeTransformer<D> transformer, NodeFactory<D> factory);

    boolean isLeaf();

    Iterable<Node<D>> getChildren();

}
