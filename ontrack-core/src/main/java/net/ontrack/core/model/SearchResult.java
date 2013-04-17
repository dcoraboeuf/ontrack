package net.ontrack.core.model;

import lombok.Data;
import net.sf.jstring.Localizable;

@Data
public class SearchResult {

    private final String title;
    private final Localizable description;
    private final String url;

}
