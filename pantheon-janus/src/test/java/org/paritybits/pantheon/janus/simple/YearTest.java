package org.paritybits.pantheon.janus.simple;

import org.junit.Test;
import org.paritybits.pantheon.common.CommonUtil;

import java.util.Date;

import static org.junit.Assert.*;
import static org.paritybits.pantheon.janus.simple.SimplePeriodTest.*;

/**
 * Created by IntelliJ IDEA.
 * User: atillman
 * Date: Apr 24, 2007
 * Time: 5:27:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class YearTest {
    public void testYear() throws Exception {

        //Create and test the period.
        Date thisDate = dateFormat.parse("08/15/2006");
        Year year2006 = Year.create(thisDate);
        testThePeriod(year2006, thisDate, "01/01/2006 00:00:00.000",
                "12/31/2006 23:59:59.999", "2006");

        //Test the prior period
        Date priorDate = dateFormat.parse("08/15/2005");
        Year year2005 = year2006.prior();
        testThePeriod(year2005, priorDate, "01/01/2005 00:00:00.000",
                "12/31/2005 23:59:59.999", "2005");

        //Test the next period.
        Date nextDate = dateFormat.parse("08/15/2007");
        Year year2007 = year2006.next();
        testThePeriod(year2007, nextDate, "01/01/2007 00:00:00.000",
                "12/31/2007 23:59:59.999", "2007");

        //Test compareTo
        testCompareTo(year2005, year2006, year2007);
    }

    @Test
    public void serializable() throws Exception {
        testSerialization(Year.create(new Date()));
    }

    @Test
    public void valueOf() throws Exception {
        assertEquals(Year.create(dateFormat.parse("01/01/2007")), Year.valueOf("2007"));

        try {
            Year.valueOf("01/01/2006");
            fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            //Good
        } catch (Exception e) {
            fail("Unexpected exception, expected IllegalArgument; " + e.toString());
        }
    }

    @Test
    public void asMethods() throws Exception {
        //Create the year
        Year year = Year.valueOf("2006");

        //Dates used in this test.
        Date jan1st = dateFormat.parse("01/01/2006");
        Date dec31st = dateFormat.parse("12/31/2006");

        //Test asQuarters
        testRange(year.quarters(), Quarter.create(jan1st), Quarter.create(dec31st));

        //Test asMonths
        testRange(year.months(), Month.create(jan1st), Month.create(dec31st));

        //Test asDays
        testRange(year.days(), Day.create(jan1st), Day.create(dec31st));
    }

    @Test
    public void thisYear() {
        assertEquals(Year.create(new Date()), Year.thisYear());
    }

    @Test
    public void isImmutable() {
        assertTrue(CommonUtil.isImmutable(Year.thisYear()));
    }
}
