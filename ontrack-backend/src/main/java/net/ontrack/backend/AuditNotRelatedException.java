package net.ontrack.backend;

import net.sf.jstring.support.CoreException;

public class AuditNotRelatedException extends CoreException {

	public AuditNotRelatedException(int id) {
		super(id);
	}

}
