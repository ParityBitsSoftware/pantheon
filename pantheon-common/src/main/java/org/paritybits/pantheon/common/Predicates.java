package org.paritybits.pantheon.common;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Common utility functions for creating special type of predicates
 */
public final class Predicates {


    /**
     * Creates a predicate that represents a tautology,
     * it is always true.
     *
     * @param <T> Type of object being evaluated against the predicate.
     * @return A predicate that always returns true.
     */
    public static <T> Predicate<T> tautology() {
        return new Predicate<T>() {public Boolean evaluate(T t) {return true;}};
    }

    /**
     * Creates a predicate that represents a contradiction,
     * it is always false.
     *
     * @param <T> Type of object being evaluated against the predicate.
     * @return A predicate that always returns false.
     */
    public static <T> Predicate<T> contradiction() {
        return new Predicate<T>() {public Boolean evaluate(T t) {return false;}};
    }

    /**
     * Creates a negated version of the given predicate
     *
     * @param predicate The predicate to negate
     * @param <T> Type of object being evaluated against the predicate.
     * @return A new predicate that will return the negation of the given predicate when evaluating the same object.
     */
    public static <T> Predicate<T> negate(final Predicate<T> predicate) {
        return new Predicate<T>(){public Boolean evaluate(T t) {return !predicate.evaluate(t);}};
    }

    /**
     * Creates a predicate that evaluates if all of the given
     * predicates return true for a given parameter.  If there are
     * no predicates given the created predicate will always return true.
     *
     * @param predicates The predicates to be evaluated as a group
     * @param <T> Type of object being evaluated against the predicate.
     * @return A new predicate that will evaluate to true if all the given predicates return true
     * for a given evaluation.
     */
    public static <T> Predicate<T> and(final Predicate<T>... predicates) {
        return and(Arrays.asList(predicates));
    }

    /**
     * Creates a predicate that evaluates if all of the given
     * predicates return true for a given parameter.  If there are
     * no predicates given the created predicate will always return true.
     *
     * @param predicates The predicates to be evaluated as a group
     * @param <T> Type of object being evaluated against the predicate.
     * @return A new predicate that will evaluate to true if all the given predicates return true
     * for a given evaluation.
     */
    public static <T> Predicate<T> and(final Iterable<Predicate<T>> predicates) {
        return groupedPredicate(predicates, false);
    }

    /**
     * Creates a predicate that evaluates if any of the given
     * predicates return true for a given parameter.  If there are
     * no predicates given the created predicate will always return true.
     *
     * @param predicates The predicates to be evaluated as a group
     * @param <T> Type of object being evaluated against the predicate.
     * @return A new predicate that will evaluate to true if any the given predicates return true
     * for a given evaluation.
     */
    public static <T> Predicate<T> or(final Predicate<T>... predicates) {
        return or(Arrays.asList(predicates));
    }

    /**
     * Creates a predicate that evaluates if any of the given
     * predicates return true for a given parameter.  If there are
     * no predicates given the created predicate will always return true.
     *
     * @param predicates The predicates to be evaluated as a group
     * @param <T> Type of object being evaluated against the predicate.
     * @return A new predicate that will evaluate to true if any the given predicates return true
     * for a given evaluation.
     */
    public static <T> Predicate<T> or(final Iterable<Predicate<T>> predicates) {
        return groupedPredicate(predicates, true);
    }

    private static <T> Predicate<T> groupedPredicate(final Iterable<Predicate<T>> predicates,
                                                    final boolean returnWhen) {
        return new Predicate<T>() {
            @Override
            public Boolean evaluate(T t) {
                Iterator<Predicate<T>> itr = predicates.iterator();
                if(itr.hasNext()) {
                    while(itr.hasNext()) {
                        Predicate<T> predicate = itr.next();
                        if(returnWhen == predicate.evaluate(t)) return returnWhen;
                    }
                    return !returnWhen;
                } else {
                    return true;
                }
            }
        };
    }
}
