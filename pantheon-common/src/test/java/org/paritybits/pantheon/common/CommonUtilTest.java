package org.paritybits.pantheon.common;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.List;

import static java.awt.Color.WHITE;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


/**
 * Test the CommonUtil classes
 */
@SuppressWarnings({"unchecked"})
public class CommonUtilTest {

    private Integer keyValues = 100;
    private Set<Integer> keySet = new TreeSet<Integer>();
    private Map<Integer, String> expectedMap = new HashMap<Integer, String>();
    private List<String> expectedList = new ArrayList<String>();
    private Function<Integer, String> function;
    private Comparator<Integer> lastDigitComparator = new Comparator<Integer>() {
        @Override
        public int compare(Integer thisInt, Integer thatInt) {
            return (thisInt % 10) - (thatInt % 10);
        }
    };


    @Before
    public void setUp() throws Exception {
        for (Integer i = 0; i < keyValues; i++) {
            keySet.add(i);
            expectedMap.put(i, i.toString());
            expectedList.add(i.toString());
        }
        function = new Function<Integer, String>() {

            public String evaluate(Integer key) {
                return key.toString();
            }
        };
    }


    @Test
    public void getValue() {
        String key = "key";
        Map<String, Integer> map = new HashMap<String, Integer>();
        assertNull(CommonUtil.getValue(map, key, null));
        assertEquals(Integer.valueOf(3), CommonUtil.getValue(map, key, 3));
        map.put(key, 100);
        assertEquals(Integer.valueOf(100), CommonUtil.getValue(map, key, 3));

        try {
            CommonUtil.getValue(null, key, 3);
            fail("Expected NPE");
        } catch (NullPointerException e) {
            //Good
        } catch (Exception e) {
            fail("Expected NPE but got " + e.toString());
        }
        try {
            CommonUtil.getValue(map, null, 3);
            fail("Expected NPE");
        } catch (NullPointerException e) {
            //Good
        } catch (Exception e) {
            fail("Expected NPE but got " + e.toString());
        }
    }

    @Test
    public void getValueGenerics() {
        Map<String, Color> colors = new HashMap<String, Color>();
        Paint defaultPaint = new Paint() {

            public PaintContext createContext(ColorModel colorModel, Rectangle rectangle, Rectangle2D rectangle2D,
                                              AffineTransform affineTransform, RenderingHints renderingHints) {
                return null;
            }

            public int getTransparency() {
                return 0;
            }
        };
        String key = "test";
        colors.put(key, WHITE);
        Paint paint = CommonUtil.getValue(colors, "test2", defaultPaint);
        assertEquals(defaultPaint, paint);
        paint = CommonUtil.getValue(colors, key, Color.BLACK);
        assertEquals(WHITE, paint);
    }

    @Test
    public void map() {
        assertEquals(expectedMap, CommonUtil.map(keySet, function));
    }

    @Test
    public void mapInto() {
        Map<Integer, String> mappedInto = new HashMap<Integer, String>();
        CommonUtil.mapInto(keySet, mappedInto, function);
        assertEquals(expectedMap, mappedInto);
    }

    @Test
    public void transform() {
        assertEquals(expectedList, CommonUtil.collect(keySet, function));
    }

    @Test
    public void transformInto() {
        java.util.List<String> transformedInto = new ArrayList<String>();
        CommonUtil.collectInto(keySet, transformedInto, function);
        assertEquals(expectedList, transformedInto);
    }

    @Test
    public void reverse() {
        Map<String, Integer> base = new HashMap<String, Integer>();
        Map<Integer, String> expected = new HashMap<Integer, String>();
        for (Integer i = 0; i < 100; i++) {
            base.put(i.toString(), i);
            expected.put(i, i.toString());
        }
        assertEquals(expected, CommonUtil.reverse(base));
    }

    @Test
    public void isImmutable() {
        //The basic value classes found in java, these are in an internal list.
        assertTrue(CommonUtil.isImmutable(Class.class));
        assertTrue(CommonUtil.isImmutable(Boolean.TRUE));
        assertTrue(CommonUtil.isImmutable(Byte.MAX_VALUE));
        assertTrue(CommonUtil.isImmutable(Short.MAX_VALUE));
        assertTrue(CommonUtil.isImmutable(Integer.MAX_VALUE));
        assertTrue(CommonUtil.isImmutable(Long.MAX_VALUE));
        assertTrue(CommonUtil.isImmutable(Float.MAX_VALUE));
        assertTrue(CommonUtil.isImmutable(Double.MAX_VALUE));
        assertTrue(CommonUtil.isImmutable(BigInteger.TEN));
        assertTrue(CommonUtil.isImmutable(BigDecimal.TEN));
        assertTrue(CommonUtil.isImmutable(Currency.getInstance("USD")));

        //Assert that non immutable objects return false.
        assertFalse(CommonUtil.isImmutable(new Date()));

        //Assert objects that are in classes marked immutable or idempotent work.
        assertTrue(CommonUtil.isImmutable(new TestImmutable()));
        assertTrue(CommonUtil.isImmutable(new TestIdempotent()));

        //Subclasses should NOT test as true (Unless they also are tagged as Immutable, but that's bad practice).
        assertFalse(CommonUtil.isImmutable(new TestSubImmutable()));
        assertFalse(CommonUtil.isImmutable(new TestSubIdempotent()));
    }

    @Test
    public void between() {
        int low = 0;
        int high = 25;

        testBetweenOutsideBoundsIsFalse(low, high);
        testBetweenAtBounds(low, high);
        for(int i = low + 1; i < high; i++) {
            testItemsThatAreWithin(low, high, i);
        }

        //Test with other comparator
        assertFalse(CommonUtil.between(low, high, 16, lastDigitComparator));
        assertFalse(CommonUtil.between(low, high, 16, lastDigitComparator));
        assertTrue(CommonUtil.between(low, high, 34, lastDigitComparator));
        assertTrue(CommonUtil.between(high, low, 34, lastDigitComparator));
    }

    @Test
    public void betweenThrowsNPEWhenComparatorArgsOrComparatorIsNull() {
        int one = 1;
        int two = 2;
        int three = 3;
        testNPEOnBetween(null, two, three, lastDigitComparator);
        testNPEOnBetween(one, null, three, lastDigitComparator);
        testNPEOnBetween(one, two, null, lastDigitComparator);
        testNPEOnBetween(one, two, three, null);

    }

    private void testNPEOnBetween(Integer one, Integer two,
                                  Integer three, Comparator comp) {
        try {

            CommonUtil.between(one, two, three, comp);
            fail("Should have thrown exception");
        } catch (NullPointerException e) {
            //Good
        }catch (Exception e) {
            fail("Wrong exception thrown:" + e.getClass().toString());
        }
    }

    private void testBetweenAtBounds(int low, int high) {
        testIsBetween(low, high, low);
        testIsBetween(low, high, high);
        testNotBetween(low, high, low, true);
        testNotBetween(low, high, high, true);
    }

    private void testBetweenOutsideBoundsIsFalse(int low, int high) {
        testNotBetween(low, high, low - 1, false);
        testNotBetween(low, high, high + 1, false);
    }

    private void testItemsThatAreWithin(int low, int high, int i) {
        testIsBetween(low, high, i);
    }

    private void testIsBetween(int bound1, int bound2, int item) {
        //Default Compare
        assertTrue(CommonUtil.between(bound1, bound2, item));
        assertTrue(CommonUtil.between(bound2, bound1, item));
    }

    private void testNotBetween(int bound1, int bound2, int item, boolean strict) {
        assertFalse(CommonUtil.between(bound1, bound2, item, strict));
        assertFalse(CommonUtil.between(bound2, bound1, item, strict));
    }

    @Test
    public void containsUsesIterator() {
        Range<Integer> range = NumberRangeRule.createRange(2, 10, 2);
        assertTrue(CommonUtil.contains(range, 2));
        assertFalse(CommonUtil.contains(range, 3));
    }

    @Test
    public void containsOnCollectionsUsesContainsMethod() {
        //Given
        Collection collection = mock(Collection.class);
        Iterator iterator = mock(Iterator.class);
        when(collection.iterator()).thenReturn(iterator);
        Object shouldBeFound = new Object();
        when(collection.contains(shouldBeFound)).thenReturn(true);
        Object shouldNotBeFound = new Object();
        when(collection.contains(shouldNotBeFound)).thenReturn(false);

        //Then
        assertTrue(CommonUtil.contains(collection, shouldBeFound));
        assertFalse(CommonUtil.contains(collection, shouldNotBeFound));


    }

    @Test
    public void iterablePredicateTests() {
        //Given
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        int atMostOrAtLeast = 5;
        Predicate<Integer> allTrue = createIntegerPredicate(numbers.toArray(new Integer[numbers.size()]));
        Predicate sixTrue = createIntegerPredicate(1, 3, 4, 6, 7, 10);
        Predicate threeTrue = createIntegerPredicate(2, 7, 9);
        Predicate<Integer> noneTrue = createIntegerPredicate();

        //Test for all
        assertTrue(CommonUtil.all(numbers, allTrue));
        assertFalse(CommonUtil.all(numbers, sixTrue));
        assertFalse(CommonUtil.all(numbers, threeTrue));
        assertFalse(CommonUtil.all(numbers, noneTrue));

        //Test for any
        assertTrue(CommonUtil.any(numbers, allTrue));
        assertTrue(CommonUtil.any(numbers, sixTrue));
        assertTrue(CommonUtil.any(numbers, threeTrue));
        assertFalse(CommonUtil.any(numbers, noneTrue));

        //Test for none
        assertFalse(CommonUtil.none(numbers, allTrue));
        assertFalse(CommonUtil.none(numbers, sixTrue));
        assertFalse(CommonUtil.none(numbers, threeTrue));
        assertTrue(CommonUtil.none(numbers, noneTrue));


        //Test for at least
        assertTrue(CommonUtil.atLeast(numbers, allTrue, atMostOrAtLeast));
        assertTrue(CommonUtil.atLeast(numbers, sixTrue, atMostOrAtLeast));
        assertFalse(CommonUtil.atLeast(numbers, threeTrue, atMostOrAtLeast));
        assertFalse(CommonUtil.atLeast(numbers, noneTrue, atMostOrAtLeast));

        //Test for at most
        assertFalse(CommonUtil.atMost(numbers, allTrue, atMostOrAtLeast));
        assertFalse(CommonUtil.atMost(numbers, sixTrue, atMostOrAtLeast));
        assertTrue(CommonUtil.atMost(numbers, threeTrue, atMostOrAtLeast));
        assertTrue(CommonUtil.atMost(numbers, noneTrue, atMostOrAtLeast));

        //Test for within
        assertFalse(CommonUtil.inBetween(numbers, allTrue, atMostOrAtLeast, atMostOrAtLeast));
        assertFalse(CommonUtil.inBetween(numbers, sixTrue, atMostOrAtLeast, atMostOrAtLeast));
        assertFalse(CommonUtil.inBetween(numbers, threeTrue, atMostOrAtLeast, atMostOrAtLeast));
        assertFalse(CommonUtil.inBetween(numbers, noneTrue, atMostOrAtLeast, atMostOrAtLeast));
        assertTrue(CommonUtil.inBetween(numbers, createIntegerPredicate(1, 2, 4, 5, 8),
                atMostOrAtLeast, atMostOrAtLeast));

    }

    @Test
    public void invalidNumbersToPredicateTestsThrowsIAE() {
        List<Integer> items = mock(List.class);
        Predicate<Integer> predicate = mock(Predicate.class);

        //Negative numbers are not valid
        try {
            CommonUtil.atLeast(items, predicate, -5);
            fail("Should have thrown exception.");
        } catch (IllegalArgumentException e) {
            //Good!
        } catch (Exception e) {
            fail("Wrong exception thrown: " + e.toString());
        }

        try {
            CommonUtil.atMost(items, predicate, -2);
            fail("Should have thrown exception.");
        } catch (IllegalArgumentException e) {
            //Good!
        } catch (Exception e) {
            fail("Wrong exception thrown: " + e.toString());
        }

        try {
            CommonUtil.inBetween(items, predicate, -5, -4);
            fail("Should have thrown exception.");
        } catch (IllegalArgumentException e) {
            //Good!
        } catch (Exception e) {
            fail("Wrong exception thrown: " + e.toString());
        }


        //Within numbers cannot be such that atLeast > atMost
        try {
            CommonUtil.inBetween(items, predicate, 4, 3);
            fail("Should have thrown exception.");
        } catch (IllegalArgumentException e) {
            //Good!
        } catch (Exception e) {
            fail("Wrong exception thrown: " + e.toString());
        }

    }

    private static Predicate<Integer> createIntegerPredicate(Integer...trueIntegers) {
        Predicate<Integer> predicate = mock(Predicate.class);
        when(predicate.evaluate(anyInt())).thenReturn(false);
        for(Integer integer : trueIntegers) {
            when(predicate.evaluate(integer)).thenReturn(true);
        }
        return predicate;
    }

    @Immutable
    private static class TestImmutable {

    }
    private static class TestSubImmutable extends TestImmutable{

    }

    @Idempotent
    private static class TestIdempotent {

    }

    private static class TestSubIdempotent extends TestIdempotent {

    }
}
