package net.ontrack.web.hateoas;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

public abstract class AbstractResource<T extends AbstractResource<T>> extends ResourceSupport {

    public T withLink(Link link) {
        add(link);
        return (T) this;
    }

}
