package net.ontrack.core.tree;

import net.ontrack.core.tree.support.Markup;

public interface Node<D> {

    D getData();

    NodeFactory<D> getFactory();

    Node<D> transform(NodeTransformer<D> transformer);

    Node<D> transform(NodeTransformer<D> transformer, NodeFactory<D> factory);

    boolean isLeaf();

    Iterable<Node<D>> getChildren();

    Node<D> append(Node<D> child);
}
