package org.paritybits.pantheon.common;


import org.junit.Test;
import org.paritybits.pantheon.common.Range.Direction;

import java.util.*;

import static org.junit.Assert.*;
import static org.paritybits.pantheon.common.Range.Direction.BACKWARDS;
import static org.paritybits.pantheon.common.Range.Direction.FORWARDS;

@SuppressWarnings({"unchecked"})
public class RangeTest {

    private RangeRule<Integer> intRangeRule = new RangeRule<Integer>() {

        public int compare(Integer arg0, Integer arg1) {
            return arg0.compareTo(arg1);
        }

        public Integer next(Integer from) {
            return from + 1;
        }

        public Integer prior(Integer from) {
            return from - 1;
        }

        public String toString() {
            return "Int Range Rule";
        }
    };
    private static final RangeRule<Integer> BY_TWO_RULE = new RangeRule<Integer>() {
        @Override
        public Integer prior(Integer from) {
            return from - 2;
        }

        @Override
        public Integer next(Integer from) {
            return from + 2;
        }

        @Override
        public int compare(Integer thisInt, Integer thatInt) {
            return thisInt - thatInt;
        }

        @Override
        public String toString() {
            return "By 2 range rule";
        }
    };

    @Test
    public void create() {
        Integer one = 1;
        Integer ten = 10;
        Range<Integer> intRange = Range.create(one, ten, intRangeRule);
        assertNotNull(intRange);
        testRange(one, ten, intRangeRule, intRange, FORWARDS);
        intRange = Range.create(ten, one, intRangeRule);
        assertNotNull(intRange);
        testRange(ten, one, intRangeRule, intRange, BACKWARDS);


        MyRangeable my1 = new MyRangeable(1);
        MyRangeable my10 = new MyRangeable(10);
        Range<MyRangeable> myRange = Range.create(my1, my10);
        assertNotNull(myRange);
        testRange(my1, my10, null, myRange, FORWARDS);
        myRange = Range.create(my10, my1);
        assertNotNull(myRange);
        testRange(my10, my1, null, myRange, BACKWARDS);
    }

    private void testRange(Object from, Object to,
                           RangeRule rangeRule, Range range, Direction direction) {
        assertEquals(from, range.from());
        assertEquals(to, range.to());
        assertEquals(direction, range.direction());
        assertEquals(rangeRule, range.rangeRule());
    }

    @Test
    public void iterationWithRangable() throws Exception {
        //Create the high and low.
        MyRangeable low = new MyRangeable(1);
        MyRangeable high = new MyRangeable(10);

        //Create the list of expected values
        List<MyRangeable> expected = new ArrayList<MyRangeable>();
        for (Integer i = low.number; i < high.number + 1; i++) {
            expected.add(new MyRangeable(i));
        }

        //Test forward iteration
        Range<MyRangeable> range = Range.create(low, high);
        int infinateChecker = low.number;
        int sanityMax = 11;
        testRange(expected, range, infinateChecker, sanityMax);

        //Test iteration in reverse order.
        Collections.reverse(expected);
        infinateChecker = low.number;
        range = Range.create(high, low);
        testRange(expected, range, infinateChecker, sanityMax);
    }

    @Test
    public void iterationWithRangeRule() {
        Integer low = 1;
        Integer high = 10;

        //Create the list of expected values
        List<Integer> expected = new ArrayList<Integer>();
        for (Integer i = low; i < high + 1; i++) {
            expected.add(i);
        }

        //Test forward iteration
        Range<Integer> range = Range.create(low, high, intRangeRule);
        int infinateChecker = low;
        int sanityMax = 11;
        testRange(expected, range, infinateChecker, sanityMax);

        //Test iteration in reverse order.
        Collections.reverse(expected);
        infinateChecker = low;
        range = Range.create(high, low, intRangeRule);
        testRange(expected, range, infinateChecker, sanityMax);
    }

    private void testRange(List<?> expected, Range range,
                           int infinateChecker, int sanityMax) {
        List<Object> results = new ArrayList<Object>();
        ListIterator iterator = range.iterator();
        assertFalse("Iterator should not have a prior yet.", iterator.hasPrevious());
        assertTrue("Iterator should have a next.", iterator.hasNext());
        int expectedIndex = 0;
        while (iterator.hasNext()) {
            assertEquals(expectedIndex, iterator.nextIndex());
            assertEquals(expectedIndex, iterator.nextIndex());
            Object item = iterator.next();
            if (infinateChecker > sanityMax) {
                fail("We appear to be in an infinate loop");
            }
            results.add(item);
            infinateChecker++;
            expectedIndex++;
        }
        assertEquals(results.size(), iterator.nextIndex());
        assertEquals(results.size(), iterator.nextIndex());
        assertEquals(expected, results);

        expectedIndex--;
        assertTrue(iterator.hasPrevious());
        assertFalse(iterator.hasNext());
        results.clear();
        expected = new ArrayList<Object>(expected);
        Collections.reverse(expected);
        while (iterator.hasPrevious()) {
            assertEquals(expectedIndex, iterator.previousIndex());
            assertEquals(expectedIndex, iterator.previousIndex());
            Object item = iterator.previous();
            results.add(item);
            expectedIndex--;
        }
        assertEquals(-1, iterator.previousIndex());
        assertEquals(expected, results);
        assertFalse(iterator.hasPrevious());
        assertTrue(iterator.hasNext());
        assertEquals(iterator.next(), iterator.previous());
    }

    @Test
    public void optionalIteratorOperationsThrowUnsupported() {
        MyRangeable rangeable = new MyRangeable(1);
        Range<MyRangeable> range = Range.create(rangeable, rangeable);
        ListIterator<MyRangeable> iterator = range.iterator();

        //Test remove
        try {
            iterator.remove();
            fail("Expected Exception");
        } catch (UnsupportedOperationException e) {
            //Good.
        } catch (Exception e) {
            fail("Unexpected exception; " + e.getClass().getName());
        }

        //Test add.
        try {
            iterator.add(new MyRangeable(3));
            fail("Expected Exception");
        } catch (UnsupportedOperationException e) {
            //Good.
        } catch (Exception e) {
            fail("Unexpected exception; " + e.getClass().getName());
        }

        //Test set
        try {
            iterator.set(new MyRangeable(2));
            fail("Expected Exception");
        } catch (UnsupportedOperationException e) {
            //Good.
        } catch (Exception e) {
            fail("Unexpected exception; " + e.getClass().getName());
        }
    }

    @Test
    public void reverse() {
        Integer one = 1;
        Integer ten = 10;
        Range<Integer> forwards = Range.create(one, ten, intRangeRule);
        Range<Integer> backwards = Range.create(ten, one, intRangeRule);

        assertEquals(backwards, forwards.reverse());
        assertEquals(forwards, backwards.reverse());
        assertEquals(forwards, forwards.reverse().reverse());
        assertEquals(backwards, backwards.reverse().reverse());
    }

    @Test
    public void asList() {
        Integer from = 1;
        Integer to = 10;
        List<Integer> expected = new ArrayList<Integer>();
        for (int i = from; i <= to; i++) {
            expected.add(i);
        }
        Range<Integer> range = Range.create(from, to, intRangeRule);
        assertEquals(expected, range.asList());
        Collections.reverse(expected);
        range = Range.create(to, from, intRangeRule);
        assertEquals(expected, range.asList());
    }

    @Test
    public void asSortedSet() {

        //Test for Rangeable with no RangeRule
        MyRangeable from = new MyRangeable(1);
        MyRangeable to = new MyRangeable(10);
        SortedSet<MyRangeable> expected = new TreeSet<MyRangeable>();
        for (int i = 1; i <= 10; i++) {
            expected.add(new MyRangeable(i));
        }
        Range<MyRangeable> range = Range.create(from, to);
        SortedSet<MyRangeable> myRangeSet = range.asSortedSet();
        assertEquals(expected, myRangeSet);
        assertNull(myRangeSet.comparator());

        //Reverse the range and test
        range = range.reverse();
        SortedSet<MyRangeable> reverseExpected = new TreeSet<MyRangeable>(Collections.reverseOrder());
        reverseExpected.addAll(expected);
        myRangeSet = range.asSortedSet();
        assertEquals(reverseExpected, myRangeSet);

        //Test with a RangeRule
        SortedSet<Integer> expectedIntSet = new TreeSet<Integer>(intRangeRule);
        for (int i = 1; i <= 10; i++) {
            expectedIntSet.add(i);
        }
        Range<Integer> intRange = Range.create(1, 10, intRangeRule);
        SortedSet<Integer> intSortedSet = intRange.asSortedSet();
        assertEquals(expectedIntSet, intSortedSet);
        assertEquals(intRangeRule, intSortedSet.comparator());
        intRange = intRange.reverse();
        SortedSet<Integer> reverseExpectedIntSet = new TreeSet<Integer>(Collections.reverseOrder());
        reverseExpectedIntSet.addAll(expectedIntSet);
        intSortedSet = intRange.asSortedSet();
        assertEquals(reverseExpectedIntSet, intSortedSet);
    }

    @Test
    public void checkToString() {
        MyRangeable from = new MyRangeable(1);
        MyRangeable to = new MyRangeable(10);
        Range<MyRangeable> range = Range.create(from, to);
        String expected = "1...10";
        assertEquals(expected.trim(), range.toString().trim());
        Integer one = 1;
        Integer ten = 10;
        Range<Integer> intRange = Range.create(one, ten, intRangeRule);
        expected = expected + " using Int Range Rule";
        assertEquals(expected.trim(), intRange.toString().trim());
    }

    @Test
    public void equalsHashCode() {
        Range<MyRangeable> range1 = Range.create(new MyRangeable(1), new MyRangeable(10));
        //noinspection ObjectEqualsNull
        assertFalse(range1.equals(null));
        Range<MyRangeable> range2 = Range.create(new MyRangeable(1), new MyRangeable(10));
        assertEquals(range1.hashCode(), range2.hashCode());
        assertEquals(range1, range2);
        assertEquals(range2, range1);


        Range<MyRangeable> range4 = Range.create(new MyRangeable(2), new MyRangeable(10));
        assertFalse(range1.equals(range4));
        assertFalse(range4.equals(range1));

        Range<MyRangeable> range5 = Range.create(new MyRangeable(1), new MyRangeable(9));
        assertFalse(range1.equals(range5));
        assertFalse(range5.equals(range1));

        //Test with comparators
        RangeRule<Integer> rule1 = new RangeRule<Integer>() {
            public int compare(Integer arg0, Integer arg1) {
                return arg0.compareTo(arg1);
            }

            public Integer next(Integer from) {
                return from + 1;
            }

            public Integer prior(Integer from) {
                return from - 1;
            }
        };
        RangeRule<Integer> rule2 = new RangeRule<Integer>() {
            public int compare(Integer arg0, Integer arg1) {
                return arg0.compareTo(arg1);
            }

            public Integer next(Integer from) {
                return from + 1;
            }

            public Integer prior(Integer from) {
                return from - 1;
            }
        };


        Range<Integer> range6 = Range.create(1, 10, rule1);
        Range<Integer> range7 = Range.create(1, 10, rule1);
        Range<Integer> range8 = Range.create(1, 10, rule2);
        assertEquals(range6, range7);
        assertEquals(range7, range6);
        assertEquals(range6.hashCode(), range7.hashCode());
        assertFalse(range6.equals(range8));
        assertFalse(range8.equals(range6));
        assertFalse(range7.equals(range8));
        assertFalse(range8.equals(range7));
    }

    @Test
    public void isImmutable() {
        assertTrue(CommonUtil.isImmutable(Range.create(new MyRangeable(1), new MyRangeable(2))));
    }

    @Test
    public void stepsFrom() {
        RangeTest.MyRangeable rangeable = new RangeTest.MyRangeable(0);
        int nextSteps = 5;
        int priorSteps = -10;

        assertEquals(rangeable, Range.stepsFrom(rangeable, 0));
        assertEquals(new MyRangeable(nextSteps), Range.stepsFrom(rangeable, nextSteps));
        assertEquals(new MyRangeable(priorSteps), Range.stepsFrom(rangeable, priorSteps));

        Integer zero = 0;
        NumberRangeRule<Integer> rule = NumberRangeRule.create(1);
        assertEquals(zero, Range.stepsFrom(zero, 0, rule));
        assertEquals((Integer) nextSteps, Range.stepsFrom(zero, nextSteps, rule));
        assertEquals((Integer) priorSteps, Range.stepsFrom(zero, priorSteps, rule));

        try {
            Range.stepsFrom(null, 0);
            fail("Expected exception");
        } catch (NullPointerException e) {
            //Goood
        } catch (Exception e) {
            fail("Wrong exception; " + e.toString());
        }

        try {
            Range.stepsFrom(0, 0, null);
            fail("Expected exception");
        } catch (NullPointerException e) {
            //Goood
        } catch (Exception e) {
            fail("Wrong exception; " + e.toString());
        }

        //Make sure that we don't get a stack overflow when the steps are very large.
        try {
            Range.stepsFrom(rangeable, 350000);
        } catch(StackOverflowError e) {
            fail("Should not have gotten a stackoverflow.");
        }
    }

    @Test
    public void hasWithinChecksThatItemBetweenFromAndToInclusive() {
        int from = 0;
        int to = 10;

        //Test using a rangeable object
        Range<MyRangeable> range = Range.create(new MyRangeable(from),
                new MyRangeable(to));
        testHasWithinOnRange(range);
        testHasWithinOnRange(range.reverse());

        //Test using a rule that step by 2.
        Range<Integer> numberRange = Range.create(from, to, BY_TWO_RULE);
        testHasWithinOnRange(numberRange);
        testHasWithinOnRange(numberRange.reverse());

        //Test that items that are not returned via the iterator still return true.
        List<Integer> allInRange = numberRange.asList();
        int one = 1;
        assertFalse(allInRange.contains(one));
        assertTrue(numberRange.hasWithin(one));
    }

    private void testHasWithinOnRange(Range range) {
        RangeRule rule = range.getRangeRule();
        Range.Direction direction =  range.direction();
        assertFalse(range.hasWithin(direction.priorInDirection(range.from(), rule)));
        assertFalse(range.hasWithin(direction.nextInDirection(range.to(), rule)));
        testIteratorItemsAreWithin(range);
    }

    private static void testIteratorItemsAreWithin(Range range) {
        for(Object item : range) {
            assertTrue(range.hasWithin(item));
        }
    }

    @Test
    public void containsOnlyReturnsTrueForItemsInIterator() {
        int from = 0;
        int to = 10;

        //Test using a rangeable object
        Range<MyRangeabeBy2> range = Range.create(new MyRangeabeBy2(from),
                new MyRangeabeBy2(to));
        assertFalse(range.contains(new MyRangeabeBy2(1)));
        for(MyRangeabeBy2 item : range) {
            assertTrue(range.contains(item));
        }

        //Test using a rule that step by 2.
        Range<Integer> numberRange = Range.create(from, to, BY_TWO_RULE);
        assertFalse(numberRange.contains(1));
        for(MyRangeabeBy2 item : range) {
            assertTrue(range.contains(item));
        }
    }

    public static class MyRangeable implements Rangeable<MyRangeable> {
        protected final Integer number;

        public MyRangeable(Integer number) {
            this.number = number;
        }

        public MyRangeable prior() {
            return new MyRangeable(number - 1);
        }

        public MyRangeable next() {
            return new MyRangeable(number + 1);
        }

        public int compareTo(MyRangeable other) {
            return number.compareTo(other.number);
        }

        public String toString() {
            return number.toString();
        }

        public boolean equals(Object o) {
            if (!(o instanceof MyRangeable)) {
                return false;
            }
            MyRangeable other = (MyRangeable) o;
            return number.equals(other.number);
        }

        public int hashCode() {
            return number.hashCode();
        }
    }


    private static class MyRangeabeBy2 extends MyRangeable {
        private MyRangeabeBy2(Integer number) {
            super(number);
        }

        @Override
        public MyRangeabeBy2 prior() {
            return new MyRangeabeBy2(number - 2);
        }

        @Override
        public MyRangeabeBy2 next() {
            return new MyRangeabeBy2(number + 2);
        }
    }

}
