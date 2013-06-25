package net.ontrack.core.tree;

import com.netbeetle.jackson.ObjectMapperFactory;
import net.ontrack.core.Helper;
import net.ontrack.core.tree.support.Markup;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static java.util.Arrays.asList;
import static net.ontrack.core.tree.support.Markup.of;

public class NodeTest {

    private final ObjectMapper mapper = ObjectMapperFactory.createObjectMapper();

    @Test
    public void json() throws IOException {
        NodeFactory<Markup> factory = new DefaultNodeFactory<>();
        Node<Markup> root = factory.node(
                null,
                asList(
                        factory.node(
                                of("link").attr("url", "http://test/id/177"),
                                asList(
                                        factory.leaf(Markup.text("#")),
                                        factory.node(
                                                of("em"),
                                                asList(
                                                        factory.leaf(Markup.text("177"))
                                                )
                                        )
                                )
                        ),
                        factory.leaf(Markup.text(" One match"))
                )
        );
        String actual = mapper.writeValueAsString(root);
        String expected = Helper.getResourceAsString("/node.json");
        Assert.assertEquals(expected, actual);
    }

}
