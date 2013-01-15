package net.ontrack.web.support.fm;

import java.util.List;
import java.util.Locale;

import net.sf.jstring.Strings;

import org.apache.commons.lang3.Validate;
import org.springframework.context.i18n.LocaleContextHolder;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

public abstract class AbstractFnLocFormat<T> implements TemplateMethodModel {
	
	private final Strings strings;
	
	public AbstractFnLocFormat(Strings strings) {
		this.strings = strings;
	}

	@Override
	public Object exec(@SuppressWarnings("rawtypes") List list) throws TemplateModelException {
		// Checks
		Validate.notNull(list, "List of arguments is required");
		Validate.isTrue(list.size() == 1, "List of arguments must contain 1 element");
		// Gets the object to format
		T o = parse((String) list.get(0));
		// Gets the locale from the context
		Locale locale = LocaleContextHolder.getLocale();
		// Filters the locale
		locale = strings.getSupportedLocales().filterForLookup(locale);
		// Formatting
		return format(o, locale);
	}

	protected abstract T parse(String value);

	protected abstract String format(T o, Locale locale);

}
