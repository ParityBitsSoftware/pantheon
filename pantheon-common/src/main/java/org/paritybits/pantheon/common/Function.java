package org.paritybits.pantheon.common;

/**
 * Defines an object that takes an object and converts it to another object.  It is highly, highly
 * recommended that implementations of this interface have no side effects and that evaluate be idempotent.
 *
 * @param <P> The type of parameter
 * @param <R> The type of result.
 */
public interface Function<P, R> {

    /**
     * * Execute the function on the value.  It is highly recommended that
     * implementations are idempotent.
     *
     * @param p The parameter to the function
     * @return The result of the function on the parameter.
     */
    R evaluate(P p);
}
