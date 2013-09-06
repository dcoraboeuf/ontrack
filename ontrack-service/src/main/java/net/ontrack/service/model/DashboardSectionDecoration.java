package net.ontrack.service.model;

import lombok.Data;

import java.util.Collection;

@Data
public class DashboardSectionDecoration {

    private final Collection<String> cssClasses;
    private final String link;

}
