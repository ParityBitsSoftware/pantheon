package org.paritybits.pantheon.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This defines a class as <a href="http://en.wikipedia.org/wiki/Immutable_object">Immutable</a>.
 *
 * It is strongly recommended that classes that have this interface
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Immutable {}
