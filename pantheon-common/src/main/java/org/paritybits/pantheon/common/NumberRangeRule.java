package org.paritybits.pantheon.common;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;

/**
 * This is a range rule for Number types.  It takes a number that
 * represents the increments in the range.
 *
 * @author andrew
 * @param <T> The type of number this rule is for.
 */
@Immutable
public final class NumberRangeRule<T extends Number> implements RangeRule<T> {

    private final T increment;
    private final BigDecimal incrementBD;


    private final static String STRING_FORMAT = "NumberRangeRule with increment of {0}";

    private NumberRangeRule(T increment) {
        this.increment = increment;
        this.incrementBD = new BigDecimal(increment.toString()).stripTrailingZeros();
    }

    /**
     * @return The increment used by this rule.
     */
    public T increment() {
        return increment;
    }

    /**
     * Compares two numbers
     *
     * @param arg1 The first argument in the comparision
     * @param arg2 The seond argument in the comparision
     * @return An int that is <=> 0 iff arg1 is less than, equalivilent to
     *         or greater than arg2.
     */
    public int compare(T arg1, T arg2) {
        return new BigDecimal(arg1.toString()).compareTo(
                new BigDecimal(arg2.toString()));
    }

    /**
     * @param from the item to get the next value for.
     * @return The next item in the sequence.
     */
    public T next(T from) {
        return createTypedInstance(from, new BigDecimal(
                from.toString()).add(incrementBD));
    }

    /**
     * @param from the item to get the prior value for.
     * @return The prior item in the sequence.
     */
    public T prior(T from) {
        return createTypedInstance(from, new BigDecimal(
                from.toString()).subtract(incrementBD));
    }

    private T createTypedInstance(T from, BigDecimal amount) {
        try {
            Class fromClass = from.getClass();
            Constructor stringConstructor = fromClass.getConstructor(String.class);
            return (T) stringConstructor.newInstance(amount.toString());
        } catch (Exception e) {
            throw new IllegalStateException("NumberRangeRule trying to operate " +
                    "on a class that cannot be created from a string.", e);
        }
    }

    /**
     * @return A hashcode based on the increment.
     */
    @Override
    public int hashCode() {
        return incrementBD.hashCode();
    }

    /**
     * @return true if the object is a NumberRangeRule with the same increment.
     */
    @Override
    public boolean equals(Object obj) {
        if(getClass() == obj.getClass()) {
            NumberRangeRule otherRule = (NumberRangeRule) obj;
            return increment.getClass() == otherRule.increment().getClass() &&
                    incrementBD.equals(otherRule.incrementBD);
        } else {
            return false;
        }
    }


    @Override
    public String toString() {
        return MessageFormat.format(STRING_FORMAT, incrementBD);
    }

    public static <T extends Number> NumberRangeRule<T> create(T increment) {
        return new NumberRangeRule<T>(increment);
    }

    /**
     * Factory method for creating ranges with the NumberRangeRule.
     *
     * @param <T>       The type of Number that will be used.
     * @param from      Start of the range.
     * @param to        End of the range
     * @param increment How large of an increment between elements in the range.
     * @return A Range defined by the parameters.
     */
    public static <T extends Number> Range<T> createRange(T from, T to,
                                                          T increment) {
        return Range.create(from, to, create(increment));
		
	}
}
