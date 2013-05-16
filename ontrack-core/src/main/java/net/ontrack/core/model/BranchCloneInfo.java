package net.ontrack.core.model;

import lombok.Data;

import java.util.Collection;

/**
 * Information needed when cloning a branch.
 */
@Data
public class BranchCloneInfo {

    private final BranchSummary summary;
    private final Collection<DisplayablePropertyValue> properties;
    private final Collection<DisplayableProperty> validationStampProperties;
    private final Collection<DisplayableProperty> promotionLevelProperties;

}
