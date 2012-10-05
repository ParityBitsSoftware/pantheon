package org.paritybits.pantheon.common;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.*;

/**
 * This represents a bounded range of objects.  It can be used to iterate over a large range of
 * objects without the need to hold onto a large collection.  The Range
 * can go in either a forwards or backwards direction.  This object is immutable.
 *
 * @author Andrew Tillman
 * @version 0.9
 * @param <T> The type of range this is.  T must be of type Rangeable if the Range has
 * no RangeComparator.
 * @see org.paritybits.pantheon.common.Rangeable
 * @see org.paritybits.pantheon.common.RangeRule
 */
@SuppressWarnings({"unchecked"})
@Immutable
public final class Range<T> implements Iterable<T>, Serializable {
    static final long serialVersionUID = 6119015706494334116L;

    private final T from;
    private final T to;
    private final Direction direction;
    private final RangeRule<T> rangeRule;

    private final String STRING_FORMAT = "{0}...{1}";
    private final String STRING_FORMAT_WITH_RULE = STRING_FORMAT + " using {2}" ;


    //The default range rule for Rangeable objects.
    private static final RangeRule<Rangeable> DEFAULT_RANGE_RULE = new RangeRule<Rangeable>() {
        public int compare(final Rangeable compareThis, final Rangeable toThat) {
            return compareThis.compareTo(toThat);
        }

        public Rangeable prior(final Rangeable from) {
            return from.prior();
        }

        public Rangeable next(final Rangeable from) {
            return from.next();
        }
    };

    /**
     * Defines the direction of a range.  This should never be used outside Range.  It is package protected mostly for
     * testing purposes.
     */
    static enum Direction {
        FORWARDS {
            boolean hasPrior(final Object from, final Object to, final RangeRule rangeRule) {
                return rangeRule.compare(from, to) > 0;
            }
            boolean hasNext(final Object from, final Object to, final RangeRule rangeRule) {
                return rangeRule.compare(from, to) <= 0;
            }
            Object priorInDirection(final Object item, final RangeRule rangeRule) {
                return rangeRule.prior(item);
            }
            Object nextInDirection(final Object item, final RangeRule rangeRule) {
                return rangeRule.next(item);
            }
            Comparator sortedSetComparator(final RangeRule rule) {
                return rule;
            }

        },
        BACKWARDS {
            boolean hasPrior(final Object from, final Object to, final RangeRule rangeRule) {
                return rangeRule.compare(from, to) < 0;
            }
            boolean hasNext(final Object from, final Object to, final RangeRule rangeRule) {
                return rangeRule.compare(from, to) >= 0;
            }
            Object priorInDirection(final Object item, final RangeRule rangeRule) {
                return rangeRule.next(item);
            }
            Object nextInDirection(final Object item, final RangeRule rangeRule) {
                return rangeRule.prior(item);
            }
            Comparator sortedSetComparator(final RangeRule rule) {
                return Collections.reverseOrder(rule);
            }
        };

        abstract boolean hasPrior(Object from, Object to, RangeRule rangeRule);

        abstract boolean hasNext(Object from, Object to, RangeRule rangeRule);

        abstract Object priorInDirection(Object item, RangeRule rangeRule);

        abstract Object nextInDirection(Object item, RangeRule rangeRule);

        abstract Comparator sortedSetComparator(RangeRule rule);
    }

    private Range(final T from, final T to, final RangeRule<T> rangeRule) {
        this.from = from;
        this.to = to;
        this.rangeRule = rangeRule;
        this.direction = getRangeRule().compare(from, to) > 0 ? Direction.BACKWARDS : Direction.FORWARDS;
    }

    protected Range() {
        this.from = null;
        this.to = null;
        this.direction = null;
        this.rangeRule = null;
    }

    /**
     * @return The start of the range.  This object will always be in the sequence at the beginning.
     */
    public T from() {
        return from;
    }

    /**
     * @return The end of the range.  This object may or may not be in the sequence, but the last item
     * in the sequence will have a value such that item.compareTo(to) (or rangeRule.compare(item, to)) < 0 
     */
    public T to() {
        return to;
    }

    Direction direction() {
        return direction;
    }

    /**
     * @return The rule that defines the range (what is first, second, how to get the
     *         next elements, etc).  If this is null then the Range is defined by the rules of
     *         the Rangeable object.
     */
    public RangeRule<T> rangeRule() {
        return rangeRule;
    }

    /**
     * Create an iterator that can go over the elements contained in the range.
     *
     * @return A list iterator that can go in both directions over the range.  This iterator will not be threadsafe.
     *         This iterator does not support the optional methods add, set and remove.
     */
    public ListIterator<T> iterator() {
        final RangeRule iteratorRule = getRangeRule();
        return new ListIterator<T>() {
            private T itemIndex = from;
            private int index;

            //Methods that define backwards iteration.
            public boolean hasPrevious() {
                return direction.hasPrior(itemIndex, from, iteratorRule);
            }

            public T previous() {
                itemIndex = (T) direction.priorInDirection(itemIndex, iteratorRule);
                index--;
                return itemIndex;
            }

            public int previousIndex() {
                return index - 1;
            }

            //Methods that define forward iteration.
            public boolean hasNext() {
                return direction.hasNext(itemIndex, to, iteratorRule);
            }

            public T next() {
                T returnValue = itemIndex;
                itemIndex = (T) direction.nextInDirection(itemIndex, iteratorRule);
                index++;
                return returnValue;
            }

            public int nextIndex() {
                return index;
            }

            //Methods that are optional and try and mutate the Iterable and therefore not supported.
            public void add(T arg0) {
                throw new UnsupportedOperationException("Iterators returned from Range do not support mutators");
            }

            public void set(T arg0) {
                throw new UnsupportedOperationException("Iterators returned from Range do not support mutators");
            }

            public void remove() {
                throw new UnsupportedOperationException("Iterators returned from Range do not support mutators");
            }
        };
    }

    /**
     * Check to see if the given item is within the range.  The basic check is:<br/>
     * <code>range.from <= item <= range.to.</code>
     * <br/>
     * This means that while all items with returned by the range iterator will return true,
     * there could be items that are not returned from the iterator that also return true.
     *
     * @param item The item to check.
     * @return True if this item is within the range, false otherwise.
     * @throws NullPointerException if item is null.
     */
    public boolean hasWithin(T item) {
        return CommonUtil.between(from, to, item, getRangeRule());
    }

    /**
     * Tests if the given item is contained in the range.  This mean that the item would
     * be returned by the iterator
     *
     * @param item The item to test for
     * @return True if item would be returned by an iterator created by this rule, false otherwise.
     * @throws NullPointerException if item is null.
     */
    public boolean contains(T item) {
        return CommonUtil.contains(this, item);
    }

    /**
     * Create the reverse of this range.
     *
     * @return A new Range going that starts at to and goes to from in the opposite direction.
     */
    public Range<T> reverse() {
        return create(to, from, rangeRule);
    }

    /**
     * Create a new list representation of the specified Range.
     *
     * @return A List of all objects in the range.  The order of the list
     *         is the same as the order the items in the range would appear
     *         in the iterator method of the range.  This List is alterable and doing
     *         so will not affect supsequent calls to asList.
     */
    public List<T> asList() {
        return (List<T>) fillCollection(new ArrayList<T>());
    }


    /**
     * Create a new SortedSet representation of the specified Range.
     *
     * @return A SortedSet of all the objects in the range.  The comparator
     *         of the SortedSet will be based on the RangeRule of the specified Range.
     *         This SortedSet is alterable and doing so will not affect supsequent calls to asSortedSet.
     */
    public SortedSet<T> asSortedSet() {
        return (SortedSet<T>) fillCollection(new TreeSet<T>(direction.sortedSetComparator(rangeRule)));
    }

    private Collection<T> fillCollection(final Collection<T> collection) {
        for (T item : this) {
            collection.add(item);
        }
        return collection;
    }

    //Gets a range rule, if null then it returns the default range rule.
    RangeRule<T> getRangeRule() {
        if (rangeRule != null) {
            return rangeRule;
        } else {
            return (RangeRule<T>) DEFAULT_RANGE_RULE;
        }
    }

    /**
     * Determines if the range is equal to another object.  It will be true that two
     * ranges that are equal will produce the same sequence when iterated over.  Also if the Rangable objects or
     * RangeRule have conformed to their contracts, then two Ranges that produce the same sequence will be equal. 
     *
     * @return true of the other object a Range with the same from, to, direction
     *         and rangeRule.  This means that the ranges will produce the same sequence.
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o instanceof Range) {
            Range other = (Range) o;
            return from.equals(other.from) && to.equals(other.to) &&
                    getRangeRule().equals(other.getRangeRule());
        } else {
            return false;
        }
    }

    /**
     * @return The hashcode for the range.
     */
    @Override
    public int hashCode() {
        int code = 29 * getRangeRule().hashCode();
        code = code + 29 * from.hashCode();
        code = code + 29 * to.hashCode();
        return code;
    }

    /**
     * @return The string representation of the range in the format of
     *         "[from]...[to]"
     */
    @Override
    public String toString() {
        if(rangeRule == null) {
            return MessageFormat.format(STRING_FORMAT, from, to);
        } else {
            return MessageFormat.format(STRING_FORMAT_WITH_RULE, from, to, rangeRule);
        }
    }

    /**
     * A static factory method that creates a range between the two parameters.  The
     * direction of the range is determined by the relationship between from and to.  If
     * from is less than two, then the direction will be forwards.  If from is greater
     * than to, then the direction will be backwards
     *
     * @param <T>  The type of Range this is
     * @param from The start of the range.
     * @param to   The end of the range
     * @return A new Rangee, defined by the parameters.
     */
    public static <T extends Rangeable> Range<T> create(final T from, final T to) {
        return create(from, to, null);
    }

    /**
     * A static factory method that creates a range between the two parameters.  The
     * direction of the range is determined by the relationship between from and to
     * with regards to the range rule.  If from is less than two, then the direction
     * will be forwards.  If from is greater than to, then the direction will be backwards
     *
     * @param <T>        The type of Range this is
     * @param from       The start of the range.
     * @param to         The end of the range
     * @param rangeRule, The rule that will be used for this range.
     * @return A new Rangee, defined by the parameters.
     */
    public static <T> Range<T> create(final T from, final T to, final RangeRule<T> rangeRule) {
        return new Range<T>(from, to, rangeRule);
    }

    /**
     * Finds the value that is the given number of steps from a given start value.
     *
     * @param start The starting point.
     * @param steps The number of steps to go.  Positive values will use the next method, while negative values will
     *              use the prior method.  If steps is 0, then the mehtod will return start.
     * @return The value that is n number of steps from the starting value
     * @throws NullPointerException If start is null.
     */
    public static <T extends Rangeable> T stepsFrom(final T start, final int steps) {
        return stepsFrom(start, steps, (RangeRule<T>) DEFAULT_RANGE_RULE);
    }

    /**
     * Finds the value that is the given number of steps from a given start value.
     *
     * @param start     The starting point.
     * @param steps     The number of steps to go.  Positive values will use the next method, while negative values will
     *                  use the prior method.  If steps is 0, then the mehtod will return start.
     * @param rangeRule The rule for getting the next and prior steps.
     * @return The value that is n number of steps from the starting value
     * @throws NullPointerException If either start or rangeRule are null.
     */
    public static <T> T stepsFrom(final T start, final int steps, final RangeRule<T> rangeRule) {
        if(start == null || rangeRule == null) throw new NullPointerException("start and rule cannot be null.");
        T value = start;
        boolean increment = steps > 0;
        int absoluteSteps = Math.abs(steps);
        for(int i = 0; i < absoluteSteps; i++)
            value = increment ? rangeRule.next(value) : rangeRule.prior(value);
        return value;
    }
}
