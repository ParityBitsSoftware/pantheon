package org.paritybits.pantheon.common;

public final class Functions {
    /**
     * Creates an identity function.  This is function that returns the object passed in
     * with no change
     *
     * @param <T> The type of object to be passed in
     * @return An identify function that will just return the object passed to evaluate.
     */
    public static <T> Function<T, T> identity() {
        return new Function<T, T>(){public T evaluate(T t) {return t;}};
    }

    /**
     * Creates a constant function that always evaluates to the given value
     *
     * @param c The constant value to be returned with every call.
     * @param <T> The type of parameter.
     * @param <C> The type of the constant value to be returned.
     * @return A function that always evaluates to c.
     */
    public static <T, C> Function<T, C> constant(final C c) {
        return new Function<T, C>(){public C evaluate(final T t) {return c;}};
    }
}
