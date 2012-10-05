package org.paritybits.pantheon.janus.simple;

import org.paritybits.pantheon.common.Immutable;
import org.paritybits.pantheon.common.Range;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A SimplePeriod representation of a year.  A Uear can be convereted to and from a string using the ISO 8601 format;
 * <i>yyyy</i>
 */
@Immutable
public final class Year extends SimplePeriod<Year> {

    private static final List<Integer> MANIPULATED_FIELDS = Arrays.asList(Calendar.DAY_OF_YEAR, Calendar.HOUR_OF_DAY,
            Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND);

    Year() {
    }

    Year(Date date) {
        super(date);
    }

    Year createNewInstance(Date date) {
        return create(date);
    }

    String createExternalForm() {
        return getDateFormat().format(start()).replace("-", "");
    }

    Year createFromExternalForm(String externalForm) {
        return valueOf(externalForm);
    }

    List<Integer> manipulatedFields() {
        return MANIPULATED_FIELDS;
    }

    /**
     * @return The Range of all the Quarters that are contained in the Year
     */
    public Range<Quarter> quarters() {
        return Range.create(Quarter.create(start()), Quarter.create(stop()));
    }

    /**
     * @return The Range of all the Months that are contained in the year.
     */
    public Range<Month> months() {
        return Range.create(Month.create(start()), Month.create(stop()));
    }

    /**
     * @return The Range of all the Days that are contained in the Year.
     */
    public Range<Day> days() {
        return Range.create(Day.create(start()), Day.create(stop()));
    }

    /**
     * @param date A date of the Year to be created.
     * @return A new Year the represents the year of the given date.
     */
    public static Year create(Date date) {
        return new Year(date);
    }

    /**
     * Gets the year of an iso8601 formatted string.  This format is <i>yyyy</i>
     *
     * @param iso8601FormattedString A string representation of the Year.
     * @return The Year that the given string describes.
     * @throws IllegalArgumentException If the string is not formatted properly.
     */
    public static Year valueOf(String iso8601FormattedString) {
        try {
            return create(getDateFormat().parse(iso8601FormattedString + "-"));
        } catch (ParseException e) {
            throw new IllegalArgumentException("String not formatted properly");
        }
    }

    /**
     * @return This year
     */
    public static Year thisYear() {
        return create(new Date());
    }

    private static DateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-");
    }


}
