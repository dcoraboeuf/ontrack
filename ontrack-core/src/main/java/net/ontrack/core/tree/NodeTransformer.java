package net.ontrack.core.tree;

public interface NodeTransformer<D> {

    Node<D> transform(Node<D> node);

}
