package net.ontrack.core.tree;

import java.util.Collection;

public class DefaultNodeFactory<D> implements NodeFactory<D> {
    @Override
    public Node<D> leaf(D data) {
        return new DefaultNode<>(this, data);
    }

    @Override
    public Node<D> node(Collection<Node<D>> children) {
        return new DefaultNode<>(this, null, children);
    }
}
