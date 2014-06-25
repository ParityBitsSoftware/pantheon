package org.paritybits.pantheon.common;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Predicate;


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
        return new Predicate<T>() {public boolean test(T t) {return true;}};
    }

    /**
     * Creates a predicate that represents a contradiction,
     * it is always false.
     *
     * @param <T> Type of object being evaluated against the predicate.
     * @return A predicate that always returns false.
     */
    public static <T> Predicate<T> contradiction() {
        return new Predicate<T>() {public boolean test(T t) {return false;}};
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
        return groupedPredicate(predicates, GroupFunction.and);

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
        return groupedPredicate(predicates, GroupFunction.or);
    }

    private static <T> Predicate<T> groupedPredicate(final Iterable<Predicate<T>> predicates,
                                                    final GroupFunction groupFunction) {
        Predicate<T> grouped = defaultPredicate();
        for(Predicate<T> predicate : predicates) {
            grouped = groupFunction.group(grouped, predicate);
        }
        return grouped;
    }

    private static enum GroupFunction {
        and {
            @Override
            <T> Predicate<T> group(Predicate<T> predicate, Predicate<T> otherPredicate) {
                return predicate.and(otherPredicate);
            }
        },
        or {
            @Override
            <T> Predicate<T> group(Predicate<T> predicate, Predicate<T> otherPredicate) {
                return predicate.or(otherPredicate);
            }
        };
        abstract <T> Predicate<T> group(Predicate<T> predicate, Predicate<T> otherPredicate);
    }

    @SuppressWarnings("unchecked")
    private static <T> Predicate<T> defaultPredicate() {
        return new Predicate<T>() {
            @Override
            public boolean test(T t) {
                return true;
            }

            @Override
            public Predicate<T> and(Predicate<? super T> other) {
                return (Predicate<T>) other;
            }

            @Override
            public Predicate<T> or(Predicate<? super T> other) {
                return (Predicate<T>) other;
            }
        };
    }
}
