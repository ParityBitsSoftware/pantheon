package org.paritybits.pantheon.janus.simple;

import org.junit.Test;
import org.paritybits.pantheon.common.Range;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class SimplePeriodTest {

    static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    static final DateFormat fullFormat = new SimpleDateFormat("MM/dd/yyyy KK:mm:ss.SSS");
    static final Date sometimeInAugust;

    static {
        try {
            sometimeInAugust = dateFormat.parse("08/15/2006");
        } catch (Exception e) {
            throw new RuntimeException("Problem getting sometime in august.");
        }
    }

    @Test
    public void equalsFalseWhenDifferentSubclass() {
        Date date = new Date();
        TestSimplePeriod period1 = new TestSimplePeriod(date);
        TestSimplePeriod period2 = new SubTestSimplePeriod(date);
        assertFalse("Equals must return false when the periods are of different subclasses", period1.equals(period2));
    }

    @Test
    public void safeStartAndStop() {
        Date date = new Date();
        SimplePeriod period = new TestSimplePeriod(date);
        Date expectedStart = new Date(period.start().getTime());
        Date expectedStop = new Date(period.stop().getTime());
        date.setTime(0);
        assertEquals(expectedStart, period.start());
        assertEquals(expectedStop, period.stop());
        date = period.start();
        date.setTime(0);
        assertEquals(expectedStart, period.start());
        date = period.stop();
        date.setTime(0);
        assertEquals(expectedStop, period.stop());
    }

    @Test
    public void serializable() throws Exception {
        testSerialization(new TestSimplePeriod(new Date()));
    }

    @Test
    public void roll() throws Exception {
        Date date = new Date();
        SimplePeriod period = new TestSimplePeriod(date);
        assertEquals(period.next().next().next(), period.roll(3));
        assertEquals(period.prior().prior(), period.roll(-2));
    }

    static void testSerialization(SimplePeriod period) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(data);
        out.writeObject(period);
        out.flush();
        out.close();
        ObjectInputStream in = new ObjectInputStream(
                new ByteArrayInputStream(data.toByteArray()));
        SimplePeriod period2 = (SimplePeriod) in.readObject();
        assertEquals(period, period2);
    }

    static void testThePeriod(SimplePeriod period, Date dateForTest, String startString,
                              String stopString, String expectedToString) throws Exception {
        assertNotNull(period);
        assertEquals(fullFormat.parse(startString), period.start());
        assertEquals(fullFormat.parse(stopString), period.stop());
        assertEquals(expectedToString, period.toString());
        SimplePeriod otherPeriod = period.createNewInstance(dateForTest);
        assertEquals(period, otherPeriod);
        assertEquals(period.hashCode(), otherPeriod.hashCode());
        assertTrue(period.compareTo(otherPeriod) == 0);
        assertTrue(otherPeriod.compareTo(otherPeriod) == 0);
    }

    static void testCompareTo(SimplePeriod low, SimplePeriod middle, SimplePeriod high) {
        assertTrue(low.compareTo(low) == 0);
        assertTrue(low.compareTo(middle) < 0);
        assertTrue(low.compareTo(high) < 0);
        assertTrue(middle.compareTo(low) > 0);
        assertTrue(middle.compareTo(middle) == 0);
        assertTrue(middle.compareTo(high) < 0);
        assertTrue(high.compareTo(low) > 0);
        assertTrue(high.compareTo(middle) > 0);
        assertTrue(high.compareTo(high) == 0);
    }

    static void testRange(Range<? extends SimplePeriod> range, SimplePeriod expectedFrom, SimplePeriod expectedTo) {
        assertNotNull(range);
        assertEquals(expectedFrom, range.from());
        assertEquals(expectedTo, range.to());
    }

    private static class TestSimplePeriod extends SimplePeriod<TestSimplePeriod> {
        String longString;

        public TestSimplePeriod(Date dateInPeriod) {
            super(dateInPeriod);
            longString = Long.toString(dateInPeriod.getTime());
        }

        TestSimplePeriod createNewInstance(Date date) {
            return new TestSimplePeriod(date);
        }

        String createExternalForm() {
            return longString;
        }

        TestSimplePeriod createFromExternalForm(String externalForm) {
            return new TestSimplePeriod(new Date(Long.decode(externalForm)));
        }

        List<Integer> manipulatedFields() {
            return Arrays.asList(Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND);
        }
    }

    private static class SubTestSimplePeriod extends TestSimplePeriod {
        public SubTestSimplePeriod(Date dateInPeriod) {
            super(dateInPeriod);
        }
    }
}
