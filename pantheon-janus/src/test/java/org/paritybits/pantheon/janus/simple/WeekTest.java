package org.paritybits.pantheon.janus.simple;

import org.junit.Test;
import org.paritybits.pantheon.common.CommonUtil;

import java.util.Date;

import static org.junit.Assert.*;
import static org.paritybits.pantheon.janus.simple.SimplePeriodTest.*;

public class WeekTest {

    @Test
    public void week() throws Exception {
        //Create and test the period.
        Date thisDate = dateFormat.parse("01/04/2006");
        Week firstWeek06 = Week.create(thisDate);
        testThePeriod(firstWeek06, thisDate, "01/02/2006 00:00:00.000",
                "01/08/2006 23:59:59.999", "2006-W01");

        //Test the prior period
        Date priorDate = dateFormat.parse("12/28/2005");
        Week lastWeek06 = firstWeek06.prior();
        testThePeriod(lastWeek06, priorDate, "12/26/2005 00:00:00.000",
                "01/01/2006 23:59:59.999", "2005-W52");

        //Test the next period.
        Date nextDate = dateFormat.parse("01/11/2006");
        Week secondWeek06 = firstWeek06.next();
        testThePeriod(secondWeek06, nextDate, "01/09/2006 00:00:00.000",
                "01/15/2006 23:59:59.999", "2006-W02");

        //Test compareTo
        testCompareTo(lastWeek06, firstWeek06, secondWeek06);

        //Test corner cases regarding week numbering and years when the first and last week crosses
        // the year boundry.
        Date firstOfJan06 = dateFormat.parse("01/01/2006");
        Date firstOfJan08 = dateFormat.parse("01/01/2008");
        testThePeriod(Week.create(firstOfJan06), firstOfJan06, "12/26/2005 00:00:00.000",
                "01/01/2006 23:59:59.999", "2005-W52");
        testThePeriod(Week.create(firstOfJan08), firstOfJan08, "12/31/2007 00:00:00.000",
                "01/06/2008 23:59:59.999", "2008-W01");
    }

    @Test
    public void serializalbe() throws Exception {
        testSerialization(Week.create(new Date()));
    }

    @Test
    public void valueOf() throws Exception {
        assertEquals(Week.create(dateFormat.parse("01/01/2006")), Week.valueOf("2005-W52"));
        assertEquals(Week.create(dateFormat.parse("01/01/2008")), Week.valueOf("2008-W01"));

        //Test invalid strings.
        try {
            Week.valueOf("01/2006");
            fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            //Good
        } catch (Exception e) {
            fail("Unexpected exception, expected IllegalArgument; " + e.toString());
        }
    }

    @Test
    public void asMethods() {
        Week week = Week.valueOf("2008-W01");
        testRange(week.days(), Day.valueOf("2007-12-31"), Day.valueOf("2008-01-06"));
        assertEquals(Year.valueOf("2008"), week.year());
        week = Week.valueOf("2005-W52");
        assertEquals(Year.valueOf("2005"), week.year());
    }

    @Test
    public void rollMethods() {
        Week week = Week.valueOf("2008-W01");
        assertEquals(Week.valueOf("2009-W01"), week.rollYears(1));
        assertEquals(Week.valueOf("2006-W01"), week.rollYears(-2));
    }

    @Test
    public void thisWeek() {
        assertEquals(Week.create(new Date()), Week.thisWeek());
    }

    @Test
    public void isImmutable() {
        assertTrue(CommonUtil.isImmutable(Week.thisWeek()));
    }
}
