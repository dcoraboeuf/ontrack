package net.ontrack.web.support.fm;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.Validate;
import org.springframework.context.i18n.LocaleContextHolder;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

public class FnLocSelected implements TemplateMethodModel {

	@Override
	public Object exec(@SuppressWarnings("rawtypes") List list) throws TemplateModelException {
		// Checks
		Validate.notNull(list, "List of arguments is required");
		Validate.isTrue(list.isEmpty(), "List of arguments must be empty");
		// Gets the locale from the context
		Locale locale = LocaleContextHolder.getLocale();
		// Gets the value
		return locale.getLanguage().toLowerCase();
	}

}
