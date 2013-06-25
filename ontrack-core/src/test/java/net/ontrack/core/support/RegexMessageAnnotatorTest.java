package net.ontrack.core.support;

import com.google.common.base.Function;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RegexMessageAnnotatorTest {

    public static final String REGEX = "#(\\d+)";
    private final Function<String, MessageAnnotation> annotationFunction = new Function<String, MessageAnnotation>() {
        @Override
        public MessageAnnotation apply(String match) {
            return new MessageAnnotation("test").attr("match", match);
        }
    };

    @Test
    public void no_match() {
        Collection<MessageAnnotation> annotations = new RegexMessageAnnotator(REGEX, annotationFunction).annotate("No match here.");
        assertNotNull(annotations);
        assertEquals(
                Arrays.asList(
                        MessageAnnotation.text("No match here.")
                ),
                annotations);
    }

    @Test
    public void one_match() {
        Collection<MessageAnnotation> annotations = new RegexMessageAnnotator(REGEX, annotationFunction).annotate("#177 match here.");
        assertNotNull(annotations);
        assertEquals(
                Arrays.asList(
                        annotationFunction.apply("177"),
                        MessageAnnotation.text(" match here.")
                ),
                annotations);
    }

}
