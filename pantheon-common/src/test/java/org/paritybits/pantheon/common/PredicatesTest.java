package org.paritybits.pantheon.common;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings({"unchecked"})
public class PredicatesTest {

    @Test
    public void tautologyAndContradiction() {
        Predicate<Integer> tautology = Predicates.tautology();
        Predicate<Integer> contradiction = Predicates.contradiction();
        for(Integer i = 0; i < 100; i++) {
            assertTrue("Tautology should always evaluate to true", tautology.evaluate(i));
            assertFalse("Contradiction should always evaluate to false", contradiction.evaluate(i));
        }
    }

    @Test
    public void negateReturnsPredicateThatNegatesGivenPredicate() {

        Predicate<Integer> predicate = mock(Predicate.class);
        Predicate<Integer> negation = Predicates.negate(predicate);
        assertNotNull(negation);

        when(predicate.evaluate(any(Integer.class))).thenReturn(true);
        int count = RandomUtils.nextInt(100) + 100;
        for(Integer i = 0; i < count; i++)
            assertFalse("Negate should evaluate as opposite of given predicate.",
                    negation.evaluate(i));
        when(predicate.evaluate(any(Integer.class))).thenReturn(false);
        count = RandomUtils.nextInt(100) + 100;
        for(Integer i = 0; i < count; i++)
            assertTrue("Negate should evaluate as opposite of given predicate.",
                    negation.evaluate(i));

    }

    @Test
    public void groupPredicates() {

        //Given
        Predicate<Integer> one = mock(Predicate.class);
        when(one.evaluate(anyInt())).thenReturn(false);
        Predicate<Integer> two = mock(Predicate.class);
        when(one.evaluate(anyInt())).thenReturn(false);
        Predicate<Integer> three = mock(Predicate.class);
        when(three.evaluate(anyInt())).thenReturn(false);
        Predicate<Integer> and = Predicates.and(one, two, three);
        Predicate<Integer> or = Predicates.or(one, two, three);

        assertNotNull(and);
        assertNotNull(or);

        int value = RandomUtils.nextInt();

        //All starts false
        assertFalse("And should evaluate to false when any predicate evaluates to false.",
                and.evaluate(value));
        assertFalse("Or should evaluate to false when all predicates evaluate to false.",
                or.evaluate(value));

        when(one.evaluate(anyInt())).thenReturn(true);
        assertFalse("And should evaluate to false when any predicate evaluates to false.",
                and.evaluate(value));
        assertTrue("Or should evaluate to true when any predicate evaluates to true.",
                or.evaluate(value));

        when(two.evaluate(anyInt())).thenReturn(true);
        assertFalse("And should evaluate to false when any predicate evaluates to false.",
                and.evaluate(value));
        assertTrue("Or should evaluate to true when any predicate evaluates to true.",
                or.evaluate(value));

        when(three.evaluate(anyInt())).thenReturn(true);
        assertTrue("And should evaluate to true when all predicates evaluate to true.",
                and.evaluate(value));
        assertTrue("Or should evaluate to true when any predicate evaluates to true.",
                or.evaluate(value));

        when(one.evaluate(anyInt())).thenReturn(false);
        assertFalse("And should evaluate to false when any predicate evaluates to false.",
                and.evaluate(value));
        assertTrue("Or should evaluate to true when any predicate evaluates to true.",
                or.evaluate(value));

        when(two.evaluate(anyInt())).thenReturn(false);
        assertFalse("And should evaluate to false when any predicate evaluates to false.",
                and.evaluate(value));
        assertTrue("Or should evaluate to true when any predicate evaluates to true.",
                or.evaluate(value));

        when(one.evaluate(anyInt())).thenReturn(true);
        assertFalse("And should evaluate to false when any predicate evaluates to false.",
                and.evaluate(value));
        assertTrue("Or should evaluate to true when any predicate evaluates to true.",
                or.evaluate(value));


        List<Predicate<Integer>> empty = Collections.emptyList();
        assertTrue("And predicate on no predicates should always evaluate to true.",
                Predicates.and(empty).evaluate(value));
        assertTrue("Or predicate on no predicates should always evaluate to true.",
                Predicates.or(empty).evaluate(value));

    }

}
