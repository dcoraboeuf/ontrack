package net.ontrack.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.sf.jstring.Localizable;
import net.sf.jstring.NonLocalizable;

@Data
@AllArgsConstructor
public class SearchResult {

    private final Localizable title;
    private final Localizable description;
    private final String url;

    public SearchResult(String title, String description, String url) {
        this(
                title,
                new NonLocalizable(description),
                url
        );
    }

    public SearchResult(String title, Localizable description, String url) {
        this(
                new NonLocalizable(title),
                description,
                url
        );
    }
}
