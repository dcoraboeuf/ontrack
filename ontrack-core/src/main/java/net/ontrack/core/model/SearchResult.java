package net.ontrack.core.model;

import lombok.Data;
import net.sf.jstring.Localizable;

@Data
public class SearchResult {

    private final Localizable title;
    private final String url;

}
