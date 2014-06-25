package org.paritybits.pantheon.common;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@SuppressWarnings({"unchecked"})
public class PredicatesTest {

    @Test
    public void tautologyAndContradiction() {
        Predicate<Integer> tautology = Predicates.tautology();
        Predicate<Integer> contradiction = Predicates.contradiction();
        for(Integer i = 0; i < 100; i++) {
            assertTrue("Tautology should always evaluate to true", tautology.test(i));
            assertFalse("Contradiction should always evaluate to false", contradiction.test(i));
        }
    }

    @Test
    public void groupPredicates() {

        //Given
        boolean result = false;
        TestPredicate one = new TestPredicate();
        TestPredicate two = new TestPredicate();
        TestPredicate three = new TestPredicate();
        Predicate<Integer> and = Predicates.and(one, two, three);
        Predicate<Integer> or = Predicates.or(one, two, three);

        assertNotNull(and);
        assertNotNull(or);

        int value = RandomUtils.nextInt();

        //All starts false
        assertThat(and.test(value), is(false));
        assertThat(or.test(value), is(false));

        one.setReturnValue(true);
        assertThat(and.test(value), is(false));
        assertThat(or.test(value), is(true));

        two.setReturnValue(true);
        assertThat(and.test(value), is(false));
        assertThat(or.test(value), is(true));

        three.setReturnValue(true);
        assertThat(and.test(value), is(true));
        assertThat(or.test(value), is(true));

        three.setReturnValue(false);
        assertThat(and.test(value), is(false));
        assertThat(or.test(value), is(true));

        two.setReturnValue(false);
        assertThat(and.test(value), is(false));
        assertThat(or.test(value), is(true));

        three.setReturnValue(true);
        assertThat(and.test(value), is(false));
        assertThat(or.test(value), is(true));


        List<Predicate<Integer>> empty = Collections.emptyList();
        assertThat(Predicates.and(empty).test(value), is(true));
        assertThat(Predicates.or(empty).test(value), is(true));

    }

    private static class TestPredicate implements Predicate<Integer> {

        private boolean returnValue;

        private TestPredicate() {
            returnValue = false;
        }

        @Override
        public boolean test(Integer integer) {
            return returnValue;
        }

        public void setReturnValue(boolean returnValue) {
            this.returnValue = returnValue;
        }
    }

}
