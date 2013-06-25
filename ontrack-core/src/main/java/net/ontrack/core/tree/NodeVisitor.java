package net.ontrack.core.tree;

public interface NodeVisitor<D> {

    void start(Node<D> node);

    void end(Node<D> node);

}
