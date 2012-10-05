package org.paritybits.pantheon.common;

/**
 * Defines an special type of function that transforms a given parameter to a boolean result.
 *
 * @param <P> The type of object this predicate can take.
 */
public interface Predicate<P> extends Function <P, Boolean> {

    /**
     * Determines the result of applying the predicate on the parameter
     *
     * @param p The parameter the predicate will use
     * @return The result of running the predicate on the parameter
     */
    Boolean evaluate(P p);
}
