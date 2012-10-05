package org.paritybits.pantheon.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method or class as idempotent.  If a class is marked as idempotent
 * all it's methods are considered idempotent as well.  It will also be considered immutable
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.TYPE})
public @interface Idempotent {
}
