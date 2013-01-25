package net.ontrack.service;

import java.util.List;

import net.ontrack.service.model.Audit;
import net.ontrack.service.model.Audited;

public interface AuditService {

	void audit(boolean creation, Audited audited, int id);

	List<Audit> all(int offset, int count);

}
