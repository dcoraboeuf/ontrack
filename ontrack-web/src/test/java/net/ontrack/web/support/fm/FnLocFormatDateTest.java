package net.ontrack.web.support.fm;

import freemarker.template.TemplateModelException;
import net.sf.jstring.Strings;
import net.sf.jstring.SupportedLocales;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class FnLocFormatDateTest {

    private Strings strings;
    private SupportedLocales supportedLocales;

    @Before
    public void before() {
        supportedLocales = mock(SupportedLocales.class);
        strings = mock(Strings.class);
        when(strings.getSupportedLocales()).thenReturn(supportedLocales);
    }

    @Test
    public void date() throws TemplateModelException {
        when(supportedLocales.filterForLookup(any(Locale.class))).thenReturn(Locale.ENGLISH);
        FnLocFormatDate fn = new FnLocFormatDate(strings);
        DateTime d = new DateTime(2013, 7, 1, 13, 13, 25, DateTimeZone.UTC);
        String value = String.valueOf(d);
        assertEquals("2013-07-01T13:13:25.000Z", value);
        Object result = fn.exec(Arrays.asList(value));
        assertEquals("Monday, July 1, 2013", result);
    }

    @Test
    public void date_fr() throws TemplateModelException {
        when(supportedLocales.filterForLookup(any(Locale.class))).thenReturn(Locale.FRENCH);
        FnLocFormatDate fn = new FnLocFormatDate(strings);
        DateTime d = new DateTime(2013, 7, 1, 13, 13, 25, DateTimeZone.UTC);
        String value = String.valueOf(d);
        assertEquals("2013-07-01T13:13:25.000Z", value);
        Object result = fn.exec(Arrays.asList(value));
        assertEquals("lundi 1 juillet 2013", result);
    }

    @Test
    public void time() throws TemplateModelException {
        when(supportedLocales.filterForLookup(any(Locale.class))).thenReturn(Locale.ENGLISH);
        FnLocFormatTime fn = new FnLocFormatTime(strings);
        DateTime d = new DateTime(2013, 7, 1, 13, 13, 25, DateTimeZone.UTC);
        String value = String.valueOf(d);
        assertEquals("2013-07-01T13:13:25.000Z", value);
        Object result = fn.exec(Arrays.asList(value));
        assertEquals("1:13 PM", result);
    }

    @Test
    public void time_fr() throws TemplateModelException {
        when(supportedLocales.filterForLookup(any(Locale.class))).thenReturn(Locale.FRENCH);
        FnLocFormatTime fn = new FnLocFormatTime(strings);
        DateTime d = new DateTime(2013, 7, 1, 13, 13, 25, DateTimeZone.UTC);
        String value = String.valueOf(d);
        assertEquals("2013-07-01T13:13:25.000Z", value);
        Object result = fn.exec(Arrays.asList(value));
        assertEquals("13:13", result);
    }

}
