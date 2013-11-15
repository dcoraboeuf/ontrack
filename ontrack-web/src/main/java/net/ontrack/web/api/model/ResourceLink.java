package net.ontrack.web.api.model;

import lombok.Data;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpMethod;

@Data
public class ResourceLink {

    public static ResourceLink of(Link link) {
        return of(HttpMethod.GET, link);
    }

    public static ResourceLink of(HttpMethod method, Link link) {
        return new ResourceLink(
                method,
                link.getHref(),
                link.getRel()
        );
    }

    public static ResourceLink post(Link link) {
        return of(HttpMethod.POST, link);
    }

    private final HttpMethod method;
    private final String href;
    private final String rel;
}
