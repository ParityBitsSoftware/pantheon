package org.paritybits.pantheon.janus.simple;

import org.paritybits.pantheon.common.Immutable;
import org.paritybits.pantheon.common.Range;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A SimplePeriod representation of a quarter.  This class can be converted to and from a string using the ISO 8601 format;
 * <i>yyyy-Qq</i>.
 */
@Immutable
public final class Quarter extends SimplePeriod<Quarter> {

    private static final List<Integer> MANIPULATED_FIELDS = Arrays.asList(Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY,
            Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND);
    private static final int START_OFFSET = 3;
    private static final int STOP_OFFSET = 1;
    private static final Pattern QUARTER_PATTERN = Pattern.compile("^(\\d\\d\\d\\d)-Q([1-4])");

    Quarter() {
    }

    Quarter(Date dateInPeriod) {
        super(dateInPeriod);
    }

    Quarter createNewInstance(Date date) {
        return create(date);
    }

    Date calculateStart(Calendar calendar) {
        setMonth(calendar, START_OFFSET);
        return super.calculateStart(calendar);
    }

    Date calculateStop(Calendar calendar) {
        setMonth(calendar, STOP_OFFSET);
        return super.calculateStop(calendar);
    }

    private void setMonth(Calendar calendar, int offset) {
        int month = (getQuarter(calendar) * 3) - offset;
        calendar.set(Calendar.MONTH, month);
    }

    private int getQuarter(Calendar calendar) {
        return (calendar.get(Calendar.MONTH) / 3) + 1;
    }

    List<Integer> manipulatedFields() {
        return MANIPULATED_FIELDS;
    }

    String createExternalForm() {
        Calendar calendar = getCalibratedCalendar();
        calendar.setTime(stop());
        int quarter = getQuarter(calendar);
        int year = calendar.get(Calendar.YEAR);
        return new StringBuilder().append(year).append("-").append("Q").append(quarter).toString();
    }

    Quarter createFromExternalForm(String externalForm) {
        return valueOf(externalForm);
    }

    /**
     * @return A Range of Months that are contained in the Quarter
     */
    public Range<Month> asMonths() {
        return Range.create(Month.create(start()), Month.create(stop()));
    }

    /**
     * @return A Range of Days that are contained in the Quarter.
     */
    public Range<Day> asDays() {
        return Range.create(Day.create(start()), Day.create(stop()));
    }

    /**
     * @return The Year the Quarter is in.
     */
    public Year asYear() {
        return Year.create(start());
    }

    /**
     * @param rollAmount How many years to roll by.  A positive number will roll forward and a negative number
     *                   will roll backward.
     * @return The Quarter that is a x years away from the specified Quarter.
     */
    public Quarter rollYears(int rollAmount) {
        return roll(Calendar.YEAR, rollAmount);
    }

    /**
     * @param date A date of the Quarter to be created.
     * @return A new Quarter.
     */
    public static Quarter create(Date date) {
        return new Quarter(date);
    }

    /**
     * Gets the Quarter from an iso8601 formatted string.  This format is <i>yyyy-Qq</i>
     *
     * @param iso8601FormattedString A string representation of the Quarter.
     * @return The Quarter that the given string describes.
     * @throws IllegalArgumentException If the string is not formatted properly.
     */
    public static Quarter valueOf(String iso8601FormattedString) {
        Matcher matcher = QUARTER_PATTERN.matcher(iso8601FormattedString);
        if (matcher.matches()) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, Integer.decode(matcher.group(1)));
            int quarter = Integer.decode(matcher.group(2));
            calendar.set(Calendar.MONTH, quarter * START_OFFSET - 1);
            return create(calendar.getTime());
        } else {
            throw new IllegalArgumentException("String was not formatted correctly");
        }
    }

    /**
     * @return This quarter.
     */
    public static Quarter thisQuarter() {
        return create(new Date());
    }
}
