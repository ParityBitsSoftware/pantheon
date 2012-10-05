package org.paritybits.pantheon.janus.simple;

import org.junit.Test;
import org.paritybits.pantheon.common.CommonUtil;

import java.util.Date;

import static org.junit.Assert.*;
import static org.paritybits.pantheon.janus.simple.SimplePeriodTest.*;

public class QuarterTest {

    @Test
    public void quarter() throws Exception {
        //Create and test the period.
        Date thisDate = dateFormat.parse("08/15/2006");
        Quarter thirdQuarter06 = Quarter.create(thisDate);
        testThePeriod(thirdQuarter06, thisDate, "07/01/2006 00:00:00.000",
                "09/30/2006 23:59:59.999", "2006-Q3");

        //Test the prior period.
        Date priorDate = dateFormat.parse("5/14/2006");
        Quarter secondQuarter06 = thirdQuarter06.prior();
        testThePeriod(secondQuarter06, priorDate, "04/01/2006 00:00:00.000",
                "6/30/2006 23:59:59.999", "2006-Q2");

        //Test the next period
        Date nextDate = dateFormat.parse("10/11/2006");
        Quarter fourthQuarter06 = thirdQuarter06.next();
        testThePeriod(fourthQuarter06, nextDate, "10/01/2006 00:00:00.000",
                "12/31/2006 23:59:59.999", "2006-Q4");

        //Test compareTo
        testCompareTo(secondQuarter06, thirdQuarter06, fourthQuarter06);
    }

    @Test
    public void serializable() throws Exception {
        testSerialization(Quarter.create(new Date()));
    }

    @Test
    public void valueOf() throws Exception {
        assertEquals(Quarter.create(dateFormat.parse("01/01/2006")), Quarter.valueOf("2006-Q1"));
        assertEquals(Quarter.create(dateFormat.parse("04/01/2006")), Quarter.valueOf("2006-Q2"));
        assertEquals(Quarter.create(dateFormat.parse("07/01/2006")), Quarter.valueOf("2006-Q3"));
        assertEquals(Quarter.create(dateFormat.parse("10/01/2006")), Quarter.valueOf("2006-Q4"));

        try {
            Quarter.valueOf("Q1/2006");
            fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            //Good
        } catch (Exception e) {
            fail("Unexpected exception, expected IllegalArgument; " + e.toString());
        }

        try {
            Quarter.valueOf("2006-Q5");
            fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            //Good
        } catch (Exception e) {
            fail("Unexpected exception, expected IllegalArgument; " + e.toString());
        }

        //TEst that the other quarters work as well.

    }

    @Test
    public void asMethods() throws Exception {
        //Create the quarter
        Quarter quarter = Quarter.valueOf("2006-Q3");

        //Test asMonths
        testRange(quarter.asMonths(), Month.valueOf("2006-07"), Month.valueOf("2006-09"));

        //Test asDays
        testRange(quarter.asDays(), Day.valueOf("2006-07-01"), Day.valueOf("2006-09-30"));

        //Test asYear
        assertEquals(Year.valueOf("2006"), quarter.asYear());
    }

    @Test
    public void rollMethods() {
        Quarter quarter = Quarter.valueOf("2007-Q4");
        assertEquals(Quarter.valueOf("2005-Q4"), quarter.rollYears(-2));
        assertEquals(Quarter.valueOf("2008-Q4"), quarter.rollYears(1));
    }

    @Test
    public void thisQuarter() {
        assertEquals(Quarter.create(new Date()), Quarter.thisQuarter());
    }

    @Test
    public void isImmutable() {
        assertTrue(CommonUtil.isImmutable(Quarter.thisQuarter()));
    }
}
