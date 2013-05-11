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
        assertEquals("Sat, 2013 May 11, 19:36 UTC", value);
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
        assertEquals("sam., 2013 mai 11, 19:36 UTC", value);
    }

}
