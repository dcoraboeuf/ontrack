package net.ontrack.web.support.fm;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;
import net.ontrack.core.security.SecurityUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class FnSecAccountId implements TemplateMethodModel {

	private final SecurityUtils securityUtils;

	@Autowired
	public FnSecAccountId(SecurityUtils securityUtils) {
		this.securityUtils = securityUtils;
	}

	@Override
	public Object exec(@SuppressWarnings("rawtypes") List list)
			throws TemplateModelException {
		// Checks
		Validate.notNull(list, "List of arguments is required");
		Validate.isTrue(list.size() == 0, "No argument is needed");
		// Test
		return securityUtils.getCurrentAccountId();
	}

}
