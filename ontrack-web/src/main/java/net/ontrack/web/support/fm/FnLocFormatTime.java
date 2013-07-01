package net.ontrack.web.support.fm;

import net.sf.jstring.Strings;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

public class FnLocFormatTime extends AbstractFnLocFormat<DateTime> {

    @Autowired
    public FnLocFormatTime(Strings strings) {
        super(strings);
    }

    @Override
    protected DateTime parse(String value) {
        return DateTime.parse(value);
    }

    @Override
    protected String format(DateTime o, Locale locale) {
        return o.toString(DateTimeFormat.shortTime().withLocale(locale));
    }

}
