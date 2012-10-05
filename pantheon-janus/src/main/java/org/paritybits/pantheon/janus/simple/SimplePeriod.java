package org.paritybits.pantheon.janus.simple;

import org.paritybits.pantheon.common.Immutable;
import org.paritybits.pantheon.common.Range;
import org.paritybits.pantheon.janus.Period;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * The base class for a set of simple implementations of Period.  These implementations represent
 * common types of periods; years, quarters, months, days etc.  All SimplePeriods are immutable, idempotent and
 * thread-safe.
 * <p/>
 * <p/>
 * Implementations of this class must create an external form, which will be the same as the string representation
 * of the period.  The subclass must use the ISO 8601 format for this representation.
 *
 * @author Andrew Tillman
 * @version 0.9
 */
@Immutable
abstract class SimplePeriod<T extends SimplePeriod> implements Period<T>, Serializable {

    static final long serialVersionUID = 2394502930948593029L;

    private transient final Long start;
    private transient final Long stop;

    private transient String externalForm;

    /**
     * This constructor is required for serializable.
     */
    SimplePeriod() {
        start = null;
        stop = null;
    }

    /**
     * This constructor will create the period based on the date passed in.
     *
     * @param dateInPeriod A date that lies in the range of the period.
     */
    SimplePeriod(Date dateInPeriod) {
        Calendar calendar = getCalibratedCalendar();
        calendar.setTime(dateInPeriod);
        this.start = calculateStart(calendar).getTime();
        this.stop = calculateStop(calendar).getTime();
    }


    /**
     * @return The beginning of the period.
     */
    public Date start() {
        return new Date(start);
    }

    /**
     * @return The end of the period.
     */
    public Date stop() {
        return new Date(stop);
    }


    /**
     * @return The period that comes right before this period.
     */
    public T prior() {
        return createNewInstance(new Date(start - 1));
    }

    /**
     * @return The period that comes right after this period.
     */
    public T next() {
        return createNewInstance(new Date(stop + 1));
    }


    /**
     * ISO 8601 string representation.
     *
     * @return A string representation of the period in
     *         <a href="http://en.wikipedia.org/wiki/ISO_8601">ISO 8601</a> format.
     */
    public String toString() {
        if (externalForm == null) {
            externalForm = createExternalForm();
        }
        return externalForm;
    }

    /**
     * Find the start time for this period.  For many types of periods this
     * is simply normalizing the fields in a calendar.  Some subclasses (Quarter)
     * do this as well as some other more complex calculations.
     *
     * @param calendar The calendar being used for start/stop calculation.
     * @return The date, representing the start of the period.
     */
    Date calculateStart(Calendar calendar) {
        for (int field : manipulatedFields()) {
            int value = calendar.getActualMinimum(field);
            calendar.set(field, value);
        }
        return calendar.getTime();
    }

    /**
     * Find the stop time for this period.  For many types of periods this
     * is simply normalizing the fields in a calendar.  Some subclasses (Quarter)
     * do this as well as some other more complex calculations.
     *
     * @param calendar The calendar being used for start/stop calculation.
     * @return The date, representing the stop of the period.
     */
    Date calculateStop(Calendar calendar) {
        for (int field : manipulatedFields()) {
            int value = calendar.getActualMaximum(field);
            calendar.set(field, value);
        }
        return calendar.getTime();
    }

    /**
     * Rolls this period by the given number of increments.  Calling period.roll(amount) is effectively the same as
     * calling Range.stepsFrom(amount, period).  Here mostly because I figure it would be expected given all
     * the other roll methods we have.
     *
     * @param amount The amount to roll by.
     * @return A new period that is rolled amount.
     * @see org.paritybits.pantheon.common.Range#stepsFrom(org.paritybits.pantheon.common.Rangeable, int)
     */
    public T roll(int amount) {
        return (T) Range.stepsFrom(this, amount);
    }

    /**
     * Rolls to a period by a given period amount.  Used by the implementation
     * classes rollX() methods.
     *
     * @param rollField The type of period to roll by; Year, Quarter, Month etc.
     * @param amount    The amount of periods to roll by.
     * @return A new period that is a result of the roll or this period if amount is 0.
     */
    T roll(int rollField, int amount) {
        return roll(start(), rollField, amount);
    }

    /**
     * Rolls to a period by a given period amount, but allows the subclass to specifiy the date to use.  Used
     * by certain period types.
     *
     * @param theDate   The date to use in the roll.
     * @param rollField The type of period to roll by; Year, Quarter, Month etc.
     * @param amount    The amount of periods to roll by.
     * @return A new period that is a result of the roll or this period if amount is 0.
     */
    T roll(Date theDate, int rollField, int amount) {
        if (amount == 0) {
            return (T) this;
        }
        Calendar calendar = getCalibratedCalendar();
        calendar.setTime(theDate);
        calendar.add(rollField, amount);
        return createNewInstance(calendar.getTime());
    }

    /**
     * Implemented by subclasses to create a new properly subclassed instance based on a given date.  This is
     * here to keep from using refelection.
     *
     * @param date The date to use in creating the period.
     * @return A new SimplePeriod with the same subclass as this SimplePeriod
     */
    abstract T createNewInstance(Date date);

    /**
     * Implementated by subclasses to create the string reprentation of the simple period.  The string representation
     * must conform to the <a href="http://en.wikipedia.org/wiki/ISO_8601">ISO 8601</a> format.
     *
     * @return The string representation of the period based on the subclass.
     */
    abstract String createExternalForm();

    /**
     * Implemented by subclass to create a new instence from an external form string.  Used by readResolve to
     * create a custom serialized form for all subclasses.
     *
     * @param externalForm The external form to create from.
     * @return A new SimplePeriod with the same subclass as this SimplePeriod.
     */
    abstract T createFromExternalForm(String externalForm);

    /**
     * @return A list of integers that represent the fields in a Calendar to manipulated to find start and stop
     *         dates.
     */
    abstract List<Integer> manipulatedFields();

    /**
     * Compares this period to another period of the same type.  You cannot cross compare different types
     * of SimplePeriod.
     *
     * @param other T The other period to compare to.
     * @return -1, 0 or 1 based on the reaults of comparing the starts of the the periods.
     */
    public int compareTo(T other) {
        return start.compareTo(other.start);
    }

    /**
     * Check if the specified period is equal to the given object
     *
     * @param o The object to test against.
     * @return true if o is a SimpleTimePeriod of the same class with the same start and stop.
     */
    public boolean equals(Object o) {
        if (!(o instanceof SimplePeriod)) {
            return false;
        }
        SimplePeriod other = (SimplePeriod) o;
        if (getClass().equals(other.getClass())) {
            return start.equals(other.start)
                    && stop.equals(other.stop);
        } else {
            return false;
        }
    }

    /**
     * @return A hashcode calculated from the start and stop.
     */
    public int hashCode() {
        int result = start.hashCode();
        result = 29 * result + stop.hashCode();
        return result;
    }

    //Methods used to implement the custom serialized form.
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.writeObject(toString());
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        externalForm = (String) in.readObject();
    }

    final Object readResolve() {
        if (externalForm != null) {
            return createFromExternalForm(externalForm);
        } else {
            throw new NullPointerException("The external form was null during readResolve");
        }
    }

    /**
     * @return A Calendar that has been calibrated to be ISO-8601 compatable.  Mostly deals with when the first
     *         week of the year is and how many days can be in a week.
     */
    static Calendar getCalibratedCalendar() {
        Calendar calendar = new GregorianCalendar(); //All SimplePeriods are based on the Gregorian Calendar.
        calendar.setFirstDayOfWeek(2);
        calendar.setMinimalDaysInFirstWeek(4);
        return calendar;
    }
}