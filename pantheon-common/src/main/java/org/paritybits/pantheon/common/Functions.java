package org.paritybits.pantheon.common;

import java.util.function.Function;

public final class Functions {
    /**
     * Creates a constant function that always evaluates to the given value
     *
     * @param c The constant value to be returned with every call.
     * @param <T> The type of parameter.
     * @param <C> The type of the constant value to be returned.
     * @return A function that always evaluates to c.
     */
    public static <T, C> Function<T, C> constant(final C c) {
        return new Function<T, C>(){public C apply(final T t) {return c;}};
    }
}
