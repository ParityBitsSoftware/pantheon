package org.paritybits.pantheon.common;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;

import static org.junit.Assert.*;


public class PercentageTest {

    @Test
    public void arithmatic() {
        Percentage twentyPercent = Percentage.valueOf("20%");
        Percentage fivePercent = Percentage.valueOf("5%");
        BigDecimal five = new BigDecimal("5");
        assertEquals(Percentage.valueOf("25%"), twentyPercent.add(fivePercent));
        assertEquals(Percentage.valueOf("15%"), twentyPercent.subtract(fivePercent));
        assertEquals(Percentage.ONE_HUNDRED, twentyPercent.multiply(five));
        assertEquals(Percentage.valueOf("4%"), twentyPercent.divide(five));
        assertEquals(Percentage.valueOf("3%"), Percentage.valueOf("10%").divide(new BigDecimal("3")));
        assertEquals(Percentage.valueOf("3.33%"), Percentage.valueOf("10%").divide(new BigDecimal("3.0000")));
    }

    @Test
    public void ofCalculatesPercentOnGivenAmount() {
        BigDecimal number = new BigDecimal("100.0000");
        assertEquals(new BigDecimal("10.0000"), Percentage.valueOf("10%").of(number));
        number = number.setScale(0);
        assertEquals(new BigDecimal("-10"), Percentage.valueOf("-10%").of(number));
    }

    @Test
    public void changeCalculatesThePercentChangeOnGivenAmount() {
        BigDecimal number = new BigDecimal("100.0000");
        assertEquals(new BigDecimal("110.0000"), Percentage.valueOf("10%").change(number));
        number = number.setScale(0);
        assertEquals(new BigDecimal("90"), Percentage.valueOf("-10%").change(number));
    }

    @Test
    public void equalsAndHashCode() throws Exception {
        Percentage ten1 = Percentage.valueOf(new BigDecimal(".1"));
        Percentage ten2 = Percentage.valueOf(new BigDecimal(".1000"));
        Percentage eleven = Percentage.valueOf(.11);
        assertTrue(ten1.equals(ten2));
        assertTrue(ten2.equals(ten1));
        assertEquals(ten1.hashCode(), ten2.hashCode());
        assertFalse(ten1.equals(eleven));
    }

    @Test
    public void doubleValue() {
        BigDecimal value = new BigDecimal(".12344");
        Percentage percentage = Percentage.valueOf(value);
        assertEquals(value.doubleValue(), percentage.doubleValue(), 0);
    }

    @Test
    public void checkToString() throws Exception {
        String stringValue = "1,030,994.9090439954%";
        Percentage percentage = Percentage.valueOf(stringValue);
        assertEquals(stringValue.replaceAll(",", ""), percentage.toString());
        stringValue = "1030994";
        percentage = Percentage.valueOf(stringValue + ".0000000");
        assertEquals(stringValue + "%", percentage.toString());
    }

    @Test
    public void valueOf() throws Exception {
        String fivePercent = "5%";
        Percentage five = Percentage.valueOf(new BigDecimal(".05"));
        Percentage percentage = Percentage.valueOf(fivePercent);
        assertEquals(five, percentage);

        Percentage oneThousand = Percentage.valueOf(new BigDecimal(10));
        percentage = Percentage.valueOf("1,000%");
        assertEquals(oneThousand, percentage);
        percentage = Percentage.valueOf("1000%");
        assertEquals(oneThousand, percentage);
        percentage = Percentage.valueOf("1000");
        assertEquals(oneThousand, percentage);

        assertEquals(Percentage.ONE_HUNDRED, Percentage.valueOf(1));

        Percentage spotFiveTwoPercent = Percentage.valueOf(new BigDecimal(".0052"));
        percentage = Percentage.valueOf("0.52%");
        assertEquals(spotFiveTwoPercent, percentage);
        percentage = Percentage.valueOf(".52%");
        assertEquals(spotFiveTwoPercent, percentage);

        Percentage negTen = Percentage.valueOf(new BigDecimal("-.1"));
        percentage = Percentage.valueOf("-10%");
        assertEquals(negTen, percentage);

        Percentage someNeg = Percentage.valueOf(new BigDecimal("-.029"));
        percentage = Percentage.valueOf("-2.9%");
        assertEquals(someNeg, percentage);

        Percentage trailingZeros = Percentage.valueOf("10.00000%");
        assertEquals(new BigDecimal(".1000000"), trailingZeros.value());
        assertEquals("10%", trailingZeros.toString());

        String formattedString = "what the hell is this.";
        try {
            Percentage.valueOf(formattedString);
            fail("Expected an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("[" + formattedString + "] not in <value>% format.", e.getMessage());
        } catch (Exception e) {
            fail("Unexpected Exception, was expected IllegalArgumentException; " + e.toString());
        }
    }

    @Test
    public void serializable() throws Exception {
        Percentage percentage = Percentage.valueOf(.1);
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(percentage);
        out.flush();
        out.close();
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(byteOut.toByteArray()));
        Percentage fromStream = (Percentage) in.readObject();
        assertEquals(percentage, fromStream);
    }

    @Test
    public void compareTo() throws Exception {
        Percentage ten = Percentage.valueOf(new BigDecimal(".1"));
        assertTrue(ten.compareTo(Percentage.valueOf(.05)) > 0);
        assertTrue(ten.compareTo(Percentage.valueOf(new BigDecimal(".100"))) == 0);
        assertTrue(ten.compareTo(Percentage.valueOf(.11)) < 0);
    }

    @Test
    public void percentageChange() {
        BigDecimal number = new BigDecimal("100");
        assertEquals(Percentage.valueOf("10%"), Percentage.percentageChange(number,
                new BigDecimal("110")));
        assertEquals(Percentage.valueOf("-10%"), Percentage.percentageChange(number,
                new BigDecimal("90")));

        //Make sure that rouding happens when needed.
        number = new BigDecimal("90");
        assertEquals(Percentage.valueOf("33%"), Percentage.percentageChange(number, new BigDecimal("120")));
        assertEquals(Percentage.valueOf("33.33%"), Percentage.percentageChange(number, new BigDecimal("120.0000")));
    }

    @Test
    public void isImmutable() {
        assertTrue(CommonUtil.isImmutable(Percentage.valueOf(20)));
    }
}
