package net.ontrack.core.model;

import lombok.Data;
import net.ontrack.core.validation.NameDescription;

import java.util.Collection;

@Data
public class BranchCloneForm implements NameDescription {

	private final String name;
	private final String description;
    private final Collection<PropertyCreationForm> branchProperties;
    private final Collection<PropertyReplacement> validationStampReplacements;
    private final Collection<PropertyReplacement> promotionLevelReplacements;

}
