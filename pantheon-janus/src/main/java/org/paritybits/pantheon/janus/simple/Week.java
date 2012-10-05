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
 * A SimplePeriod representation of a month.  This class can be convereted to and from a string using the ISO 8601 format;
 * <i>yyyy-Www</i>
 */
@Immutable
public class Week extends SimplePeriod<Week> {

    private static final List<Integer> MANIPULATED_FIELDS = Arrays.asList(Calendar.HOUR_OF_DAY,
            Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND);

    Week() {
    }

    Week(Date dateInPeriod) {
        super(dateInPeriod);
    }

    Week createNewInstance(Date date) {
        return create(date);
    }


    Date calculateStart(Calendar calendar) {
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return super.calculateStart(calendar);
    }

    Date calculateStop(Calendar calendar) {
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return super.calculateStop(calendar);
    }

    String createExternalForm() {
        return getDateFormat().format(middleOfWeekDay().start()).replaceAll("-", "-W");
    }

    Week createFromExternalForm(String externalForm) {
        return Week.valueOf(externalForm);
    }

    List<Integer> manipulatedFields() {
        return MANIPULATED_FIELDS;
    }

    /**
     * @return The middle day of the week.  This day will always match the year that the week is in.
     */
    private Day middleOfWeekDay() {
        return Day.create(start()).next().next();
    }

    /**
     * @return All the Days that are contained in the Week.
     */
    public Range<Day> days() {
        return Range.create(Day.create(start()), Day.create(stop()));
    }

    /**
     * @return The Year this week is in.  If this week crosses  a year boundary, the year that is used is the
     *         one that determines the Weeks string format.  For instance; while the week <i>2008-W1</i>
     *         contains the day <i>2007-12-31</i>, the year returned is <i>2008</i>.
     */
    public Year year() {
        return Year.create(middleOfWeekDay().stop());
    }


    /**
     * @param rollAmount The number of years to roll
     * @return A new week with the same week number in the new rolled to year.  If called on <i>2008-W3</i>
     * with a rollAmount of 2 this method will return <i>2010-W3</i>
     */
    public Week rollYears(int rollAmount) {
        return roll(middleOfWeekDay().start(), Calendar.YEAR, rollAmount);
    }

    /**
     * @param date The date of the Day to be created.
     * @return A new Day that represents the day of the given date.
     */
    public static Week create(Date date) {
        return new Week(date);
    }

    /**
     * Gets the Week value of an iso8601 formatted string for a day.  This format is
     * <i>yyyy-MM-dd</i>
     *
     * @param iso8601FormattedString A string representation of the Day.
     * @return The Day that the given string describes.
     * @throws IllegalArgumentException If the string is not formatted properly.
     */
    public static Week valueOf(String iso8601FormattedString) {
        try {
            return create(getDateFormat().parse(iso8601FormattedString.replace("W", "")));
        } catch (ParseException e) {
            throw new IllegalArgumentException("The string was not formatted correctly");
        }
    }

    /**
     * @return This week.
     */
    public static Week thisWeek() {
        return create(new Date());
    }

    private static DateFormat getDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-ww");
        dateFormat.setCalendar(getCalibratedCalendar());
        return dateFormat;
    }
}
