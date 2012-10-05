package org.paritybits.pantheon.janus.simple;

import org.paritybits.pantheon.common.Immutable;
import org.paritybits.pantheon.common.Range;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A SimplePeriod representation of a month.  This class can be convereted to and from a string using the ISO 8601 format;
 * <i>yyyy-MM</i>
 */
@Immutable
public final class Month extends SimplePeriod<Month> {

    private static final List<Integer> MANIPULATED_FIELDS = Arrays.asList(Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY,
            Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND);

    Month() {
    }

    Month(Date dateInPeriod) {
        super(dateInPeriod);
    }

    Month createNewInstance(Date date) {
        return create(date);
    }

    String createExternalForm() {
        return getDateFormat().format(start());
    }

    Month createFromExternalForm(String externalForm) {
        return Month.valueOf(externalForm);
    }

    List<Integer> manipulatedFields() {
        return MANIPULATED_FIELDS;
    }

    /**
     * @return The range of all the Days that are in the Month.
     */
    public Range<Day> days() {
        return Range.create(Day.create(start()), Day.create(stop()));
    }

    /**
     * @return The Quarter that the Month is part of.
     */
    public Quarter quarter() {
        return Quarter.create(start());
    }

    /**
     * @return The Year that the Month is part of.
     */
    public Year year() {
        return Year.create(start());
    }

    /**
     * @param rollAmount How many quarters to roll by.  A positive number will roll forward and a negative number
     *                   will roll backward.
     * @return The Month is a x quarters away.
     */
    public Month rollQuarters(int rollAmount) {
        return roll(Calendar.MONTH, 3 * rollAmount);
    }

    /**
     * @param rollAmount How many years to roll by.  A positive number will roll forward and a negative number
     *                   will roll backward.
     * @return The Month is a x years away.
     */
    public Month rollYears(int rollAmount) {
        return roll(Calendar.YEAR, rollAmount);
    }


    /**
     * @param date A date of the Month to be created.
     * @return A new Month the represents the month of the given date.
     */
    public static Month create(Date date) {
        return new Month(date);
    }

    /**
     * Gets the Month value of an iso8601 formatted string for a Month.  This format is
     * <i>yyyy-MM</i>
     *
     * @param iso8601FormattedString A string representation of the Month.
     * @return The Month that the given string describes.
     * @throws IllegalArgumentException If the string is not formatted properly.
     */
    public static Month valueOf(String iso8601FormattedString) {
        try {
            return create(getDateFormat().parse(iso8601FormattedString));
        } catch (ParseException e) {
            throw new IllegalArgumentException("String was not iso8601 formatted", e);
        }
    }

    /**
     * @return This month.
     */
    public static Month thisMonth() {
        return create(new Date());
    }

    private static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM");
    }


}
