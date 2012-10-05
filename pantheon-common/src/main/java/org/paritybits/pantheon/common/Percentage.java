package org.paritybits.pantheon.common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents a percentage by wrapping a BigDecimal.  It is useful in that in abstracts away
 * the scale problems with regard to equals tests and preventing errors that result in the abiguity of
 * 1 being either 1% (.01) or 100% (1.00).  This class also provides various methods doing math.
 * This class is immutable.
 * <p/>
 * This class also has a custom serialized form, which is the same as it's toString method;
 * [value in US non grouped format]%
 *
 * @author Andrew Tillman
 * @version 0.9
 */
@Immutable
public final class Percentage implements Serializable, Comparable<Percentage> {

    static final long serialVersionUID = 5898030140330691835L;

    private static final Pattern PERCENTAGE_STRING_PATTERN = Pattern.compile("^(-?[\\d|,]*(\\.\\d+)?)\\%?");

    /**
     * 0%.
     */
    public static final Percentage ZERO = Percentage.valueOf("0%");

    /**
     * 100%.
     */
    public static final Percentage ONE_HUNDRED = Percentage.valueOf("100%");

    //The value of the percentage as a BD.
    private transient final BigDecimal value;


    //The external form of the Percentage, used for toString as well.  Can be parsed via valueOf.
    private transient String externalForm;

    //
    private transient int hashCode;  


    /**
     * Required for Serializable interface.
     */
    Percentage() {
        value = null;
        externalForm = null;
        hashCode = 0;
    }

    /**
     * Constructs a new Percentage with the given value.
     *
     * @param value BigDecimal value of the Percentage,
     *              .01 will be 1% and 1 will be 100%
     */
    private Percentage(final BigDecimal value) {
        this.value = value;
        externalForm = createExternalForm();
        hashCode = value.stripTrailingZeros().hashCode();
    }

    private String createExternalForm() {
        NumberFormat format = NumberFormat.getPercentInstance(Locale.US);
        format.setMaximumFractionDigits(Integer.MAX_VALUE);
        format.setGroupingUsed(false);
        return format.format(value);
    }

    /**
     * @return The BigDecimal value that drives this Percentage.
     */
    public BigDecimal value() {
        return value;
    }

    /**
     * @return The double value representation of this percentage.
     */
    public double doubleValue() {
        return value.doubleValue();
    }

    /**
     * Add the specified percentage to the given percentage.
     *
     * @param other The percentage to add.
     * @return The result of the addition.
     */
    public Percentage add(final Percentage other) {
        return valueOf(value.add(other.value));
    }

    /**
     * Subtract the given percentage from the specified percentage.
     *
     * @param other The percentage to subtract.
     * @return The result of the subtraction.
     */
    public Percentage subtract(final Percentage other) {
        return valueOf(value.subtract(other.value));
    }

    /**
     * Multiply the given percentage by the given amount.
     *
     * @param amount The amount to multiply by.
     * @return The result of the multiplication.
     */
    public Percentage multiply(final BigDecimal amount) {
        return valueOf(value.multiply(amount));
    }

    /**
     * Divide the given percentage by the given amount.
     *
     * @param amount The amount to divide by.
     * @return The result of the division.  The scale of the Percentage will be the
     *         largest of either the percentage or the amount.
     */
    public Percentage divide(final BigDecimal amount) {
        return valueOf(value.divide(amount, Math.max(value.scale(), amount.scale()), BigDecimal.ROUND_HALF_EVEN));
    }

    /**
     * Calculates the percentage of a given amount.  For example calling
     * this method on a 10% value with an amount of 100 would return 10.
     *
     * @param amount The amount to calculate on.
     * @return The value that is this percentage amount of the given amount.
     * @throws NullPointerException If amount is null.
     */
    public BigDecimal of(BigDecimal amount) {
        return amount.multiply(value()).setScale(amount.scale());
    }

    /**
     * Calculates the percent change on the given amount.  For example calling
     * this method on a 10% value with an amount of 100 would return 110.
     *
     * @param amount The amount to calculate on.
     * @return The value that is this percentage difference of the given amount.
     * @throws NullPointerException If amount is null.
     */
    public BigDecimal change(BigDecimal amount) {
        return amount.add(of(amount));
    }

    /**
     * Compares this percentage with another percentage.
     *
     * @param other The object to compare to
     * @return An integer with the results of the compare.
     */
    public int compareTo(final Percentage other) {
        return value.compareTo(other.value);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }


    /**
     * @param o The object to test equality against.
     * @return true if o is a Percentage with the same value as this Percentage, false otherwise.
     */
    @Override
    public boolean equals(final Object o) {
        if (o instanceof Percentage) {
            Percentage other = (Percentage) o;
            return compareTo(other) == 0;
        } else {
            return false;
        }
    }

    /**
     * A string representation of the Percentage.  This representation can be passed to Percentage.valueOf
     * to get the another Percentage with the same value.  It is also the custom serialized form of the Percentage.
     *
     * @return A string representation of this percentage in <value>% format.
     *         A value of .1 returns 10% and a value of 1 returns as 100%.  The string will
     *         a US formatted number with no grouping, no rounding and no trailing zeros.
     */
    @Override
    public String toString() {
        return externalForm;
    }

    //Methods used to implement the custom serialized form.
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.writeObject(externalForm);
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        externalForm = (String) in.readObject();
    }

    private Object readResolve() {
        if (externalForm != null) {
            return valueOf(externalForm);
        } else {
            throw new NullPointerException("The external form was null during readResolve");
        }
    }

    /**
     * Returns a Percentage based on the give BigDecimal value.
     *
     * @param value The BigDecimal value, 1 is 100% while .01 is 1%.
     * @return A new Percentage object.
     */
    public static Percentage valueOf(BigDecimal value) {
        return new Percentage(value);
    }

    /**
     * Returns a Percentage based on the give double value.
     *
     * @param value The double value, 1 is 100% while .01 is 1%.
     * @return A new Percentage object.
     */
    public static Percentage valueOf(double value) {
        return valueOf(new BigDecimal(value));
    }

    /**
     * This creates a new Percentage object from a percent formatted string, so that
     * 1% will create a Percentage with a value of .01.
     *
     * @param formattedString A US formatted percentage number that may use grouping and %.
     * @return A new Percentage object.  The scale of the Percentage will remain the same as that string passed.
     * @throws IllegalArgumentException If the string is not formatted correctly.
     */
    public static Percentage valueOf(String formattedString)
            throws IllegalArgumentException {
        Matcher matcher = PERCENTAGE_STRING_PATTERN.matcher(formattedString);
        if (matcher.find() && matcher.group(1).length() > 0) {
            String valueString = matcher.group(1);
            return new Percentage(new BigDecimal(valueString.replaceAll(",", "")).movePointLeft(2));
        } else {
            throw new IllegalArgumentException(MessageFormat.format("[{0}] not in <value>% format.",
                    formattedString));
        }
    }

    /**
     * Calculates what the percent change was from one number to another
     *
     * @param from The starting number of the calculation.
     * @param to   The ending number of the calculation.
     * @return The percent change between the numbers.
     */
    public static Percentage percentageChange(final BigDecimal from, final BigDecimal to) {
        return valueOf(to.subtract(from).divide(from, Math.max(2, Math.max(from.scale(), to.scale())),
                BigDecimal.ROUND_HALF_EVEN));
    }
}
