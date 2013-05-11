package net.ontrack.core.support;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class TimeUtilsTest {

    @Test
    public void format_english() {
        String value = TimeUtils.format(Locale.ENGLISH, new DateTime(
                2013,
                5,
                11,
                21,
                36,
                DateTimeZone.forID("Europe/Brussels")
        ));
        assertEquals("May 11, 2013 9:36:00 PM Europe/Brussels", value);
    }

    @Test
    public void format_french() {
        String value = TimeUtils.format(Locale.FRENCH, new DateTime(
                2013,
                5,
                11,
                21,
                36,
                DateTimeZone.forID("Europe/Brussels")
        ));
        assertEquals("11 mai 2013 21:36:00 Europe/Brussels", value);
    }

}
