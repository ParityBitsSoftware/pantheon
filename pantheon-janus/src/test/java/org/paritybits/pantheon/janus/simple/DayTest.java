package org.paritybits.pantheon.janus.simple;

import org.junit.Test;
import org.paritybits.pantheon.common.CommonUtil;

import java.util.Date;

import static org.junit.Assert.*;
import static org.paritybits.pantheon.janus.simple.SimplePeriodTest.*;

public class DayTest {


    @Test
    public void day() throws Exception {


        //Create and test the period.
        Date thisDate = dateFormat.parse("08/15/2006");
        Day aug15th06 = Day.create(thisDate);
        testThePeriod(aug15th06, thisDate, "08/15/2006 00:00:00.000",
                "08/15/2006 23:59:59.999", "2006-08-15");

        //Test the prior period
        Date priorDate = dateFormat.parse("08/14/2006");
        Day aug14th06 = aug15th06.prior();
        testThePeriod(aug14th06, priorDate, "08/14/2006 00:00:00.000",
                "08/14/2006 23:59:59.999", "2006-08-14");

        //Test the next period.
        Date nextDate = dateFormat.parse("08/16/2006");
        Day aug16th06 = aug15th06.next();
        testThePeriod(aug16th06, nextDate, "08/16/2006 00:00:00.000",
                "08/16/2006 23:59:59.999", "2006-08-16");

        //Test compareTo
        testCompareTo(aug14th06, aug15th06, aug16th06);
    }

    @Test
    public void serializable() throws Exception {
        testSerialization(Day.create(new Date()));
    }

    @Test
    public void valueOf() throws Exception {
        assertEquals(Day.create(dateFormat.parse("07/26/2007")), Day.valueOf("2007-07-26"));

        try {
            Day.valueOf("01/01/2006");
            fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            //Good
        } catch (Exception e) {
            fail("Unexpected exception, expected IllegalArgument; " + e.toString());
        }
    }

    @Test
    public void asMethods() {
        Day day = Day.valueOf("2006-01-04");
        assertEquals(Month.valueOf("2006-01"), day.month());
        assertEquals(Quarter.valueOf("2006-Q1"), day.quarter());
        assertEquals(Year.valueOf("2006"), day.year());
    }

    @Test
    public void rollMethods() {
        Day day = Day.valueOf("2006-08-15");

        assertEquals(Day.valueOf("2006-07-25"), day.rollWeeks(-3));
        assertEquals(Day.valueOf("2006-01-15"), day.rollMonths(-7));
        assertEquals(Day.valueOf("2006-11-15"), day.rollQuarters(1));
        assertEquals(Day.valueOf("2005-08-15"), day.rollYears(-1));

        //Corner Cases, rolling the 31st of a month (or quarter) to a month (or quarter) with 30 days.  Leap year, etc        
        day = Day.valueOf("2006-10-31");
        assertEquals(Day.valueOf("2006-11-30"), day.rollMonths(1));
        assertEquals(Day.valueOf("2006-12-31"), day.rollMonths(2));
        day = Day.valueOf("2004-02-29");
        assertEquals(Day.valueOf("2004-03-29"), day.rollMonths(1));
        assertEquals(Day.valueOf("2005-02-28"), day.rollYears(1));
        day = Day.valueOf("2005-08-31");
        assertEquals(Day.valueOf("2005-11-30"), day.rollQuarters(1));
        assertEquals(Day.valueOf("2005-08-30"), day.rollQuarters(1).rollQuarters(-1));

        //Test when the rolling is large
        day = Day.valueOf("2006-01-01");
        assertEquals(Day.valueOf("2007-02-01"), day.rollMonths(13));
    }

    @Test
    public void today() {
        assertEquals(Day.create(new Date()), Day.today());
    }

    @Test
    public void isImmutable() {
        assertTrue(CommonUtil.isImmutable(Day.today()));
    }
}
