package net.ontrack.web.support.fm;

import java.util.Locale;

import net.sf.jstring.Strings;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;

public class FnLocFormatDate extends AbstractFnLocFormat<DateTime> {

    @Autowired
    public FnLocFormatDate(Strings strings) {
        super(strings);
    }

    @Override
    protected String format(DateTime o, Locale locale) {
        return o.toString(DateTimeFormat.fullDate().withLocale(locale));
    }

    @Override
    protected DateTime parse(String value) {
        return DateTime.parse(value);
    }

}
