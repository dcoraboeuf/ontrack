package net.ontrack.core.model;

import lombok.Data;

@Data
public class SavedBuildFilter {

    private final String filterName;
    private final BuildFilter filter;

}
