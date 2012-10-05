package org.paritybits.pantheon.janus.simple;

import org.junit.Test;
import org.paritybits.pantheon.common.CommonUtil;

import java.util.Date;

import static org.junit.Assert.*;
import static org.paritybits.pantheon.janus.simple.SimplePeriodTest.*;


public class MonthTest {


    @Test
    public void month() throws Exception {

        //Create and test the period.
        Date thisDate = dateFormat.parse("08/14/2006");
        Month august06 = Month.create(thisDate);
        testThePeriod(august06, thisDate, "08/01/2006 00:00:00.000",
                "08/31/2006 23:59:59.999", "2006-08");

        //Test the prior period
        Date priorDate = dateFormat.parse("07/22/2006");
        Month july06 = august06.prior();
        testThePeriod(july06, priorDate, "07/01/2006 00:00:00.000",
                "07/31/2006 23:59:59.999", "2006-07");

        //Test the next period.
        Date nextDate = dateFormat.parse("09/03/2006");
        Month september06 = august06.next();
        testThePeriod(september06, nextDate, "09/01/2006 00:00:00.000",
                "09/30/2006 23:59:59.999", "2006-09");

        //Test compareTo
        testCompareTo(july06, august06, september06);
    }

    @Test
    public void serializable() throws Exception {
        testSerialization(Month.create(new Date()));
    }

    @Test
    public void valueOf() throws Exception {
        assertEquals(Month.create(dateFormat.parse("07/10/2006")), Month.valueOf("2006-07"));
        try {
            Month.valueOf("01/2006");
            fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            //Good
        } catch (Exception e) {
            fail("Unexpected exception, expected IllegalArgument; " + e.toString());
        }
    }

    @Test
    public void asMethods() throws Exception {
        //Create the month
        Month month = Month.valueOf("2006-08");

        //Test asDays
        testRange(month.days(), Day.valueOf("2006-08-01"), Day.valueOf("2006-08-31"));

        assertEquals(Quarter.valueOf("2006-Q3"), month.quarter());
        assertEquals(Year.valueOf("2006"), month.year());
    }

    @Test
    public void rollMethods() {
        Month month = Month.valueOf("2006-08");
        assertEquals(Month.valueOf("2006-11"), month.rollQuarters(1));
        assertEquals(Month.valueOf("2006-5"), month.rollQuarters(-1));
        assertEquals(Month.valueOf("2007-08"), month.rollYears(1));
        assertEquals(Month.valueOf("2005-08"), month.rollYears(-1));
    }

    @Test
    public void thisMonth() {
        assertEquals(Month.create(new Date()), Month.thisMonth());
    }

    @Test
    public void isImmutable() {
        assertTrue(CommonUtil.isImmutable(Month.thisMonth()));
    }
}
