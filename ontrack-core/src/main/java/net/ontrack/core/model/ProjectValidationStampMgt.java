package net.ontrack.core.model;

import lombok.Data;

import java.util.Collection;

@Data
public class ProjectValidationStampMgt {

    private final String branch1;
    private final String branch2;
    private final Collection<String> stamps;
    private final Collection<PropertyReplacement> replacements;

}
