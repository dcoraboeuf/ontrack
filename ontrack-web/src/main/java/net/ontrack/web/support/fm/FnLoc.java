package net.ontrack.web.support.fm;

import java.util.List;
import java.util.Locale;

import net.sf.jstring.Strings;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

public class FnLoc implements TemplateMethodModel {
	
	private final Strings strings;
	
	@Autowired
	public FnLoc(Strings strings) {
		this.strings = strings;
	}

	@Override
	public Object exec(@SuppressWarnings("rawtypes") List list) throws TemplateModelException {
		// Checks
		Validate.notNull(list, "List of arguments is required");
		Validate.notEmpty(list, "List of arguments must not be empty");
		// Gets the key
		String key = (String) list.get(0);
		// Gets the rest of the arguments
		Object[] params = list.subList(1, list.size()).toArray();
		// Gets the locale from the context
		Locale locale = LocaleContextHolder.getLocale();
		// Gets the value
		return strings.get(locale, key, params);
	}

}
