package net.ontrack.web.api.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.hateoas.Link;

import java.util.HashMap;
import java.util.Map;

public class AbstractResource<T extends AbstractResource<T>> implements Resource {

    public static final String REL_VIEW = "view";
    private final Map<String, String> links = new HashMap<>();

    public T withLink(Link link) {
        links.put(link.getRel(), link.getHref());
        return (T) this;
    }

    @JsonProperty("links")
    public Map<String, String> getLinks() {
        return links;
    }

    public T withView(String uri, Object... params) {
        return withLink(new Link(String.format(uri, params), REL_VIEW));
    }
}
