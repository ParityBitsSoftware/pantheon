package org.paritybits.pantheon.common;

import java.util.Comparator;

/**
 * This can be used to create a Range objects that are not
 * Rangeable or to create different range rules for items that are defined
 * by a Rangeable object.  It is expected that this method override
 * equals if it would produce the same sequece on a rule with the same
 * start and stop in a range.  Any object implementing this interface is expected
 * to be immutable and serializable
 *
 * @author andrew
 * @version 0.9.
 * @param <T> The type of object being the rule is for.
 */
public interface RangeRule<T> extends Comparator<T> {

    /**
     * @param from Starting point for the prior object
     * @return The next object in the sequence such that the
     *         result will return 1 when passed to compare(from, T)
     */
    T prior(T from);

    /**
     * @param from Starting point for the next object
     * @return The next object in the sequence such that the
     *         result will return -1 when passed to compare(from, T)
     */
    T next(T from);
}
