package org.paritybits.pantheon.common;

/**
 * This represents an object that can naturally be a part of a range.  An object of this
 * type knows how to get the next and prior versions of itself.  And object that is
 * Rangeable is expected to be immutable and serializable.
 *
 * @author Andrew Tillman
 * @version 0.9
 * @param <T> This is usually the same type as the implementing class
 * @see org.paritybits.pantheon.common.Range
 */
public interface Rangeable<T extends Rangeable> extends Comparable<T> {

    /**
     * @return The prior item in a natural range.  The result returned must return > 0
     *         when passed to this object's compareTo method.
     */
    T prior();

    /**
     * @return The next item in a natural range.  The result returned must return < 0
     *         when passed to this object's compareTo method.
     */
    T next();
}
