package org.paritybits.pantheon.common;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.*;

public class NumberRangeRuleTest {

    @Test
    public void compare() {
        NumberRangeRule<Integer> rule = NumberRangeRule.create(0);
        assertTrue(rule.compare(1, 2) < 0);
        assertTrue(rule.compare(2, 2) == 0);
        assertTrue(rule.compare(3, 2) > 0);
    }

    @Test
    public void nextPrior() {

        Number increment = 7;
        Number[] testValues = new Integer[]{1, 5, 3, 4, 30, 339};
        Number[] expectedNextValues = new Integer[]{8, 12, 10, 11, 37, 346};
        Number[] expectedPriorValues = new Integer[]{-6, -2, -4, -3, 23, 332};
        testRuleNextAndPrevious(increment, testValues, expectedNextValues, expectedPriorValues);

        increment = .5;
        testValues = new Double[]{1.5, 4.0, 3.7};
        expectedNextValues = new Double[]{2.0, 4.5, 4.2};
        expectedPriorValues = new Double[]{1.0, 3.5, 3.2};
        testRuleNextAndPrevious(increment, testValues, expectedNextValues, expectedPriorValues);

        increment = new BigInteger("10");
        testValues = new BigInteger[]{BigInteger.valueOf(0),
                BigInteger.valueOf(15), BigInteger.valueOf(99)};
        expectedNextValues = new BigInteger[]{BigInteger.valueOf(10),
                BigInteger.valueOf(25), BigInteger.valueOf(109)};
        expectedPriorValues = new BigInteger[]{BigInteger.valueOf(-10),
                BigInteger.valueOf(5), BigInteger.valueOf(89)};
        testRuleNextAndPrevious(increment, testValues, expectedNextValues, expectedPriorValues);
    }

    private void testRuleNextAndPrevious(Number increment, Number[] testValues, Number[] expectedNextValues, Number[] expectedPriorValues) {
        NumberRangeRule<Number> rule = NumberRangeRule.create(increment);
        for (int i = 0; i < testValues.length; i++) {
            assertEquals(expectedNextValues[i], rule.next(testValues[i]));
            assertEquals(expectedPriorValues[i], rule.prior(testValues[i]));
        }
    }

    @Test
    public void toStringFormat() {       
        assertEquals("NumberRangeRule with increment of 10", NumberRangeRule.create(10).toString());
    }

    @Test
    public void equalsAndHashCode() {
        NumberRangeRule<Integer> rule1 = NumberRangeRule.create(1);
        NumberRangeRule<Integer> rule2 = NumberRangeRule.create(1);
        assertEquals(rule1.hashCode(), rule2.hashCode());
        assertTrue(rule1.equals(rule2));
        assertTrue(rule2.equals(rule1));

        NumberRangeRule<Integer> rule3 = NumberRangeRule.create(3);
        assertFalse(rule1.equals(rule3));
        assertFalse(rule3.equals(rule1));
        assertFalse(rule2.equals(rule3));
        assertFalse(rule3.equals(rule2));

        NumberRangeRule<BigDecimal> rule4 = NumberRangeRule.create(BigDecimal.ONE);        
        assertFalse(rule1.equals(rule4));
        assertFalse(rule4.equals(rule1));

        //Test that scale doesn't through off eqauls and hashcode
        NumberRangeRule<BigDecimal> bdRule1 = NumberRangeRule.create(new BigDecimal("10.0"));
        NumberRangeRule<BigDecimal> bdRule2 = NumberRangeRule.create(BigDecimal.TEN);        
        assertEquals(bdRule1.hashCode(), bdRule2.hashCode());
        assertTrue(bdRule1.equals(bdRule2));
        assertTrue(bdRule2.equals(bdRule1));
    }

    @Test
    public void create() {
        Long number = Math.round(Math.random());
        NumberRangeRule<Long> rule = NumberRangeRule.create(number);
        assertNotNull("Rule should not be null", rule);
        assertEquals("Rule should have increment of 1", number, rule.increment());
    }

    @Test
    public void createRange() {
        BigDecimal from = new BigDecimal("10");
        BigDecimal to = new BigDecimal("100");
        BigDecimal increment = new BigDecimal("5");
        Range<BigDecimal> range = NumberRangeRule.createRange(from, to, increment);
        assertNotNull(range);
        assertEquals(from, range.from());
        assertEquals(to, range.to());
        RangeRule rule = range.rangeRule();
        assertEquals(NumberRangeRule.class, rule.getClass());
        NumberRangeRule numberRangeRule = (NumberRangeRule) rule;
        assertEquals(increment, numberRangeRule.increment());
    }

    @Test
    public void isImmutable() {
        assertTrue(CommonUtil.isImmutable(NumberRangeRule.create(1)));
    }
}
