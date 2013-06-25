package net.ontrack.core.support;

import java.util.Collection;

public interface MessageAnnotator {

    Collection<MessageAnnotation> annotate(String text);

}
