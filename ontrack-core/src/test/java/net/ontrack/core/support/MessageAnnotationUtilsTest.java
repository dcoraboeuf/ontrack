package net.ontrack.core.support;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.ontrack.core.tree.Node;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class MessageAnnotationUtilsTest {

    @Test
    public void annotate_regex_with_one_match() {
        Node<MessageAnnotation> root = MessageAnnotationUtils.annotate(
                "#177 One match",
                Arrays.asList(
                        new RegexMessageAnnotator(
                                "(#\\d+)",
                                new Function<String, MessageAnnotation>() {
                                    @Override
                                    public MessageAnnotation apply(String match) {
                                        String id = match.substring(1);
                                        return MessageAnnotation.of("link").attr("url", "http://test/id/" + id).attr("text", match);
                                    }
                                }
                        )
                )
        );
        // Root
        assertNotNull(root);
        assertNull(root.getData());
        // Children
        List<Node<MessageAnnotation>> children = Lists.newArrayList(root.getChildren());
        assertEquals(2, children.size());
        {
            Node<MessageAnnotation> child = children.get(0);
            assertEquals(
                    MessageAnnotation.of("link").attr("url", "http://test/id/177").attr("text", "#177"),
                    child.getData()
            );
            assertTrue(child.isLeaf());
        }
        {
            Node<MessageAnnotation> child = children.get(1);
            assertEquals(
                    MessageAnnotation.text(" One match"),
                    child.getData()
            );
            assertTrue(child.isLeaf());
        }
    }

}
