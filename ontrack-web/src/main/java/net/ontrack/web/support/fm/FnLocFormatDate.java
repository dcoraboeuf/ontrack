package net.ontrack.web.support.fm;

import java.util.Locale;

import net.sf.jstring.Strings;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;

public class FnLocFormatDate extends AbstractFnLocFormat<LocalDate> {
	
	@Autowired
	public FnLocFormatDate(Strings strings) {
		super(strings);
	}
	
	@Override
	protected String format(LocalDate o, Locale locale) {
		return o.toString(DateTimeFormat.fullDate().withLocale(locale));
	}
	
	@Override
	protected LocalDate parse(String value) {
		return LocalDate.parse(value);
	}

}
