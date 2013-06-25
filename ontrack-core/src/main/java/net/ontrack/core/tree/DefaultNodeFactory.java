package net.ontrack.core.tree;

import java.util.Collection;

public class DefaultNodeFactory<D> implements NodeFactory<D> {
    @Override
    public Node leaf(D data) {
        return new DefaultNode(this, data);
    }

    @Override
    public Node node(Collection<Node<D>> children) {
        return new DefaultNode(this, null, children);
    }
}
