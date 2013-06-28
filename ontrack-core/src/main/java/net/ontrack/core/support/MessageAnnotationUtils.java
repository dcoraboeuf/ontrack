package net.ontrack.core.support;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.ontrack.core.tree.*;
import net.ontrack.core.tree.support.Markup;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

public final class MessageAnnotationUtils {

    public static List<String> annotate(List<String> texts, final List<? extends MessageAnnotator> messageAnnotators) {
        return Lists.transform(
                texts,
                new Function<String, String>() {
                    @Override
                    public String apply(String text) {
                        return annotate(text, messageAnnotators);
                    }
                }
        );
    }

    public static String annotate(String text, List<? extends MessageAnnotator> messageAnnotators) {
        Node<Markup> root = annotateAsNode(text, messageAnnotators);
        final StringBuilder html = new StringBuilder();
        root.visit(new NodeVisitor<Markup>() {

            @Override
            public void start(Node<Markup> node) {
                Markup m = node.getData();
                if (m != null) {
                    if (m.isOnlyText()) {
                        html.append(escapeHtml4(m.getText()));
                    } else {
                        html.append("<").append(m.getType());
                        Map<String, String> attributes = m.getAttributes();
                        for (Map.Entry<String, String> attr : attributes.entrySet()) {
                            html.append(" ").append(escapeHtml4(attr.getKey())).append("=\"").append(escapeHtml4(attr.getValue())).append("\"");
                        }
                        if (node.isLeaf()) {
                            html.append("/>");
                        } else {
                            html.append(">");
                        }
                    }
                }
            }

            @Override
            public void end(Node<Markup> node) {
                Markup m = node.getData();
                if (m != null && !m.isOnlyText() && !node.isLeaf()) {
                    html.append("</").append(m.getType()).append(">");
                }
            }
        });
        return html.toString();
    }

    public static Node<Markup> annotateAsNode(String text, List<? extends MessageAnnotator> messageAnnotators) {
        final NodeFactory<Markup> factory = new DefaultNodeFactory<>();
        Node<Markup> root = factory.leaf(Markup.text(text));
        if (messageAnnotators != null) {
            for (final MessageAnnotator messageAnnotator : messageAnnotators) {
                if (messageAnnotator != null) {
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
            }
        }
        return root;
    }
}
