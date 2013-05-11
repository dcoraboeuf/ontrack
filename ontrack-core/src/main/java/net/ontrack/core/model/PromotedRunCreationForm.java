package net.ontrack.core.model;

import lombok.Data;
import org.joda.time.DateTime;

@Data
public class PromotedRunCreationForm {

    private final DateTime creation;
	private final String description;

}
