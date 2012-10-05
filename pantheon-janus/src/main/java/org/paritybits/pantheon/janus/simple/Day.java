package org.paritybits.pantheon.janus.simple;

import org.paritybits.pantheon.common.Immutable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A SimplePeriod representation of a day.  This class can be converted to and from a string using the ISO 8601 format;
 * <i>yyyy-MM-dd</i>.
 */
@Immutable
public final class Day extends SimplePeriod<Day> {

    private static final List<Integer> MANIPULATED_FIELDS = Arrays.asList(Calendar.MILLISECOND, Calendar.SECOND,
            Calendar.MINUTE, Calendar.HOUR, Calendar.HOUR_OF_DAY);

    Day() {
    }

    Day(Date dateInPeriod) {
        super(dateInPeriod);
    }

    Day createNewInstance(Date date) {
        return create(date);
    }

    String createExternalForm() {
        return getDateFormat().format(start());
    }

    Day createFromExternalForm(String externalForm) {
        return Day.valueOf(externalForm);
    }

    List<Integer> manipulatedFields() {
        return MANIPULATED_FIELDS;
    }

    /**
     * @return The Month this Day is a part of.
     */
    public Month month() {
        return Month.create(start());
    }

    /**
     * @return The Quarter this Day is a part of.
     */
    public Quarter quarter() {
        return Quarter.create(start());
    }

    /**
     * @return The Year this Day is a part of.
     */
    public Year year() {
        return Year.create(start());
    }


    /**
     * @param rollAmount How many weeks to roll by.  A positive number will roll forward and a negative number
     *                   will roll backward.
     * @return The Day that is x weeks from this Day.  The day of the week will be the same as the specified Day.
     */
    public Day rollWeeks(int rollAmount) {
        return roll(Calendar.WEEK_OF_YEAR, rollAmount);
    }

    /**
     * @param rollAmount How many months to roll by.  A positive number will roll forward and a negative number
     *                   will roll backward.
     * @return The Day is a x months away from this Day.  The day of the month with be
     *         the same as the specified Day, unless this Day is on the 31st of the month and we are rolling to
     *         a month with fewer than 31 days.  Then it will roll to the last day of the month.  So <i>2006-10-15</i>
     *         when rolled forward by 1 will return <i>2006-11-15</i>, but <i>2006-10-31</i> will return
     *         <i>2006-11-30</i>.
     */
    public Day rollMonths(int rollAmount) {
        return roll(Calendar.MONTH, rollAmount);
    }

    /**
     * @param rollAmount How many quarters to roll by.  A positive number will roll forward and a negative number
     *                   will roll backward.
     * @return The Day is a x quarters away from this Day.  The day of the month with be
     *         the same as this Day, unless this Day is on the 31st of the month and we are rolling to a quarter  month
     *         with fewer than 31 days.  Then it will roll to the last day of the month.  So <i>2006-08-15</i>
     *         when rolled forward by 1 will return <i>2006-11-15</i>, but <i>2006-08-31</i> will return
     *         <i>2006-11-30</i>.
     */
    public Day rollQuarters(int rollAmount) {
        return rollMonths(3 * rollAmount);
    }

    /**
     * @param rollAmount How many years to roll by.  A positive number will roll forward and a negative number
     *                   will roll backward.
     * @return The Day is a x years away from this Day.  The date of the Day will be the same as this Day, unless
     *         this Day is on leap year (Feb 29).  Then it will be the 28th of Feb. of the new year.  So <i>2004-02-15</i>
     *         when rolled forward by 1 will return <i>2005-02-15</i> but <i>2004-02-29</i> will return <i>2005-02-28</i>
     */
    public Day rollYears(int rollAmount) {
        return roll(Calendar.YEAR, rollAmount);
    }

    /**
     * @param date The date of the Day to be created.
     * @return A new Day that represents the day of the given date.
     */
    public static Day create(Date date) {
        return new Day(date);
    }

    /**
     * @return Today
     */
    public static Day today() {
        return create(new Date());
    }

    /**
     * Gets the Day value of an iso8601 formatted string for a day.  This format is
     * <i>yyyy-MM-dd</i>
     *
     * @param iso8601FormattedString A string representation of the Day.
     * @return The Day that the given string describes.
     * @throws IllegalArgumentException If the string is not formatted properly.
     */
    public static Day valueOf(String iso8601FormattedString) {
        try {
            return create(getDateFormat().parse(iso8601FormattedString));
        } catch (ParseException e) {
            throw new IllegalArgumentException("String was not iso8601 formatted", e);
        }
    }

    private static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }
}
