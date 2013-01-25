package net.ontrack.service.model;

import org.joda.time.DateTime;

import lombok.Data;

@Data
public class Audit {
	
	private final int id;
	// TODO Author info
	private final DateTime timestamp;
	private final boolean creation;
	private final Audited audited;
	private final int auditedId;
	private final String auditedName;

}
