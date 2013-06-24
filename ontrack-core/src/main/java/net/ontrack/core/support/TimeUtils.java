package net.ontrack.core.support;

import net.sf.jstring.Strings;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.PeriodFormat;

import java.util.Locale;

public final class TimeUtils {

    private TimeUtils() {
    }

    public static String format(Locale locale, DateTime dateTime) {
        return new DateTimeFormatterBuilder()
                .append(
                        DateTimeFormat.mediumDateTime()
                )
                .appendLiteral(" ")
                .appendTimeZoneId()
                .toFormatter()
                .withLocale(locale)
                .print(dateTime);
    }

    public static Period compress(Period period) {
        Period p;
        if (period.getYears() > 0) {
            p = period.withMonths(0).withWeeks(0).withDays(0).withHours(0).withMinutes(0).withSeconds(0).withMillis(0);
        } else if (period.getMonths() > 0) {
            p = period.withWeeks(0).withDays(0).withHours(0).withMinutes(0).withSeconds(0).withMillis(0);
        } else if (period.getWeeks() > 0) {
            p = period.withDays(0).withHours(0).withMinutes(0).withSeconds(0).withMillis(0);
        } else if (period.getDays() > 0) {
            p = period.withHours(0).withMinutes(0).withSeconds(0).withMillis(0);
        } else if (period.getHours() > 0) {
            p = period.withMinutes(0).withSeconds(0).withMillis(0);
        } else if (period.getMinutes() > 0) {
            p = period.withSeconds(0).withMillis(0);
        } else {
            p = period.withMillis(0);
        }
        return p;
    }

    public static DateTime now() {
        return new DateTime(DateTimeZone.UTC);
    }

    public static String elapsed(Strings strings, Locale locale, DateTime timestamp, DateTime now, String author) {
        Period period = new Period(timestamp, now);
        period = compress(period);
        String elapsed = PeriodFormat.wordBased(locale).print(period);
        return strings.get(locale, "event.ago", elapsed, author);
    }

    public static String elapsed(Strings strings, Locale locale, DateTime timestamp, DateTime now) {
        Period period = new Period(timestamp, now);
        period = compress(period);
        String elapsed = PeriodFormat.wordBased(locale).print(period);
        return strings.get(locale, "event.ago.simple", elapsed);
    }
}
