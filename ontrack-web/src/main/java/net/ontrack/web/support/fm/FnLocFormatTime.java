package net.ontrack.web.support.fm;

import java.util.Locale;

import net.sf.jstring.Strings;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;

public class FnLocFormatTime extends AbstractFnLocFormat<LocalTime> {
	
	@Autowired
	public FnLocFormatTime(Strings strings) {
		super(strings);
	}
	
	@Override
	protected LocalTime parse(String value) {
		return LocalTime.parse(value);
	}
	
	@Override
	protected String format(LocalTime o, Locale locale) {
		return o.toString(DateTimeFormat.shortTime().withLocale(locale));
	}

}
