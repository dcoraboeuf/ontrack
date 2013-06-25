package net.ontrack.core.support;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import net.ontrack.core.tree.DefaultNodeFactory;
import net.ontrack.core.tree.Node;
import net.ontrack.core.tree.NodeFactory;
import net.ontrack.core.tree.NodeTransformer;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;

public final class MessageAnnotationUtils {
    public static Node<MessageAnnotation> annotate(String text, List<MessageAnnotator> messageAnnotators) {
        final NodeFactory<MessageAnnotation> factory = new DefaultNodeFactory<>();
        Node<MessageAnnotation> root = factory.leaf(MessageAnnotation.text(text));
        for (final MessageAnnotator messageAnnotator : messageAnnotators) {
            root = root.transform(new NodeTransformer<MessageAnnotation>() {
                @Override
                public Node<MessageAnnotation> transform(Node<MessageAnnotation> node) {
                    if (node.isLeaf()) {
                        String text = node.getData().attr("text");
                        if (StringUtils.isNotBlank(text)) {
                            Collection<MessageAnnotation> annotations = messageAnnotator.annotate(text);
                            return factory.node(
                                    MessageAnnotation.empty(),
                                    Collections2.transform(
                                            annotations,
                                            new Function<MessageAnnotation, Node<MessageAnnotation>>() {
                                                @Override
                                                public Node<MessageAnnotation> apply(MessageAnnotation annotation) {
                                                    return factory.leaf(annotation);
                                                }
                                            }
                                    )
                            );
                        }
                    }
                    return node;
                }
            }, factory);
        }
        return root;
    }
}
