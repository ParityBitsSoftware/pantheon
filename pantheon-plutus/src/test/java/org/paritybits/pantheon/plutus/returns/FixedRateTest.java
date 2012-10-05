package org.paritybits.pantheon.plutus.returns;

import org.junit.Test;
import org.paritybits.pantheon.common.CommonUtil;
import org.paritybits.pantheon.common.Percentage;
import org.paritybits.pantheon.common.Range;
import org.paritybits.pantheon.janus.Period;
import org.paritybits.pantheon.janus.simple.Month;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;


public class FixedRateTest {

    @Test
    public void returnForPeriod() throws Exception {

        //Create the range to test over.
        DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        Date startDate = format.parse("01/01/2000");
        Date stopDate = format.parse("12/31/2006");
        Month start = Month.create(startDate);
        Month stop = Month.create(stopDate);
        Range<Month> range = Range.create(start, stop);

        //Create the rate
        Percentage expectedRate = Percentage.valueOf("14.567%");
        FixedRate fixedRate = FixedRate.create(expectedRate);
        for (Period period : range) {
            assertEquals(expectedRate, fixedRate.returnForPeriod(period));
        }
    }

    @Test
    public void compareTo() throws Exception {
        FixedRate five = FixedRate.create(Percentage.valueOf("5%"));
        FixedRate ten = FixedRate.create(Percentage.valueOf("10%"));
        FixedRate twenty = FixedRate.create(Percentage.valueOf("20%"));
        FixedRate forty = FixedRate.create(Percentage.valueOf("30%"));

        assertTrue(five.compareTo(five) == 0);
        assertTrue(five.compareTo(ten) < 0);
        assertTrue(five.compareTo(twenty) < 0);
        assertTrue(five.compareTo(forty) < 0);

        assertTrue(ten.compareTo(five) > 0);
        assertTrue(ten.compareTo(ten) == 0);
        assertTrue(ten.compareTo(twenty) < 0);
        assertTrue(ten.compareTo(forty) < 0);

        assertTrue(twenty.compareTo(five) > 0);
        assertTrue(twenty.compareTo(ten) > 0);
        assertTrue(twenty.compareTo(twenty) == 0);
        assertTrue(twenty.compareTo(forty) < 0);

        assertTrue(forty.compareTo(five) > 0);
        assertTrue(forty.compareTo(ten) > 0);
        assertTrue(forty.compareTo(twenty) > 0);
        assertTrue(forty.compareTo(forty) == 0);
    }

    @Test
    public void equalsAndHashCode() throws Exception {
        FixedRate five1 = FixedRate.create(Percentage.valueOf("5%"));
        FixedRate five2 = FixedRate.create(Percentage.valueOf("5%"));
        FixedRate ten = FixedRate.create(Percentage.valueOf("10%"));

        assertEquals(five1, five2);
        assertEquals(five2, five1);
        assertEquals(five1.hashCode(), five2.hashCode());
        assertFalse(five1.equals(ten));
        assertFalse(ten.equals(five1));
    }

    @Test
    public void serializable() throws Exception {
        FixedRate expected = FixedRate.create(Percentage.valueOf("14.343"));
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(data);
        out.writeObject(expected);
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data.toByteArray()));
        FixedRate fromExternal = (FixedRate) in.readObject();
        assertEquals(expected, fromExternal);
    }

    @Test
    public void isImmutable() {
        assertTrue(CommonUtil.isImmutable(FixedRate.create(Percentage.ZERO)));
    }
}
