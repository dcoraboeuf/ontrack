package net.ontrack.core.support;

import net.ontrack.core.tree.DefaultNodeFactory;
import net.ontrack.core.tree.Node;
import net.ontrack.core.tree.NodeFactory;
import net.ontrack.core.tree.NodeTransformer;
import net.ontrack.core.tree.support.Markup;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class MessageAnnotationUtils {

    public static Node<Markup> annotate(String text, List<? extends MessageAnnotator> messageAnnotators) {
        final NodeFactory<Markup> factory = new DefaultNodeFactory<>();
        Node<Markup> root = factory.leaf(Markup.text(text));
        for (final MessageAnnotator messageAnnotator : messageAnnotators) {
            root = root.transform(new NodeTransformer<Markup>() {
                @Override
                public Node<Markup> transform(Node<Markup> node) {
                    if (node.isLeaf()) {
                        String text = node.getData().getText();
                        if (StringUtils.isNotBlank(text)) {
                            Collection<MessageAnnotation> annotations = messageAnnotator.annotate(text);
                            Collection<Node<Markup>> nodes = new ArrayList<>();
                            for (MessageAnnotation annotation : annotations) {
                                if (annotation.isText()) {
                                    nodes.add(factory.leaf(Markup.text(annotation.getText())));
                                } else {
                                    Node<Markup> child = factory.leaf(Markup.of(annotation.getType(), annotation.getAttributes()));
                                    if (annotation.hasText()) {
                                        child.append(
                                                factory.leaf(Markup.text(annotation.getText()))
                                        );
                                    }
                                    nodes.add(child);
                                }
                            }
                            return factory.node(null, nodes);
                        }
                    }
                    return node;
                }
            }, factory);
        }
        return root;
    }
}
