package net.ontrack.core.tree;

import java.util.*;

public class DefaultNode<D> extends AbstractCollection<Node<D>> implements Node<D> {

    private final NodeFactory<D> factory;
    private final D data;
    private final List<Node<D>> children;

    public DefaultNode(NodeFactory<D> factory, D data) {
        this(factory, data, Collections.<Node<D>>emptyList());
    }

    public DefaultNode(NodeFactory<D> factory, D data, Collection<Node<D>> children) {
        this.factory = factory;
        this.data = data;
        this.children = new ArrayList<>(children);
    }

    @Override
    public Iterable<Node<D>> getChildren() {
        return this;
    }

    @Override
    public Iterator<Node<D>> iterator() {
        return children.iterator();
    }

    @Override
    public int size() {
        return children.size();
    }

    @Override
    public D getData() {
        return data;
    }

    @Override
    public NodeFactory<D> getFactory() {
        return factory;
    }

    @Override
    public boolean isLeaf() {
        return children.isEmpty();
    }

    @Override
    public Node<D> transform(NodeTransformer<D> transformer) {
        return transform(transformer, factory);
    }

    @Override
    public Node<D> transform(NodeTransformer<D> transformer, NodeFactory<D> factory) {
        Node<D> t;
        if (isLeaf()) {
            t = factory.leaf(data);
        } else {
            List<Node<D>> newKids = new ArrayList<>();
            for (Node<D> child : children) {
                newKids.add(child.transform(transformer, factory));
            }
            t = factory.node(newKids);
        }
        return transformer.transform(t);
    }
}
