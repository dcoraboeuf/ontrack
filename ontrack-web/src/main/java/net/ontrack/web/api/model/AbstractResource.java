package net.ontrack.web.api.model;

import lombok.Data;
import org.springframework.hateoas.Link;

import java.util.HashMap;
import java.util.Map;

@Data
public class AbstractResource<T extends AbstractResource<T>> implements Resource {

    public static final String REL_VIEW = "view";
    private final Map<String, ResourceLink> links = new HashMap<>();

    public T withLink(ResourceLink link) {
        links.put(link.getRel(), link);
        return (T) this;
    }

    public T withLink(Link link) {
        return withLink(ResourceLink.of(link));
    }

    public T withView(String uri, Object... params) {
        return withLink(new Link(String.format(uri, params), REL_VIEW));
    }

    public T withLink(ResourceLink link, boolean granted) {
        if (granted) {
            return withLink(link);
        } else {
            return (T) this;
        }
    }
}
