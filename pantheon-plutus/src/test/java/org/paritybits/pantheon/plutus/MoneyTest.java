package org.paritybits.pantheon.plutus;


import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.paritybits.pantheon.common.CommonUtil;
import org.paritybits.pantheon.common.Percentage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.Assert.*;
import static org.paritybits.pantheon.plutus.Money.BY_CURRENCY_ORDER;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Sep 7, 2005
 * Time: 9:20:54 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(JMock.class)
public class MoneyTest {


    final Mockery context = new JUnit4Mockery();

    private BigDecimal five = new BigDecimal("5");;
    private BigDecimal ten = new BigDecimal("10");
    private BigDecimal fifteen = new BigDecimal("15");
    private BigDecimal oneHundred = new BigDecimal("100");
    private Currency dollars = Currency.getInstance("USD");
    private Currency pounds = Currency.getInstance("GBP");
    private Currency yen = Currency.getInstance("JPY");

    @Test
    public void valueOf() throws Exception {
        BigDecimal value = new BigDecimal(13.5012);
        Currency currency = Currency.getInstance("EUR");
        Money money = Money.valueOf(value, currency);
        assertEquals(value, money.amount());
        assertEquals(currency, money.currency());
        try {
            Money.valueOf(null, currency);
            fail("Null amount did not except");
        } catch (NullPointerException e) {
            //Good
        }
        try {
            Money.valueOf(value, null);
            fail("Null currency did not except");
        } catch (NullPointerException e) {
            //Good
        }

        //Check that the String version works as well.
        money = Money.valueOf(ten, dollars);
        assertEquals(money, Money.valueOf("10 USD"));
        try {
            Money.valueOf("USD");
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
            //Good
        }
    }

    @Test
    public void add() throws Exception {
        Money tenDollars = Money.valueOf(ten, dollars);
        Money fiveDollars = Money.valueOf(five, dollars);
        Money fifteenDollars = Money.valueOf(fifteen, dollars);
        Money total = tenDollars.add(fiveDollars);
        assertEquals(fifteenDollars, total);
        try {
            tenDollars.add((Money) null);
            fail("Null amount in add did not except.");
        } catch (NullPointerException e) {
            //Good
        }
        Money tenYen = Money.valueOf(ten, yen);
        try {
            tenDollars.add(tenYen);
            fail("Currency mismatch in add did not except");
        } catch (CurrencyMismatchException e) {
            assertSame(tenDollars, e.firstMoney());
            assertSame(tenYen, e.secondMoney());
        }
    }

    @Test
    public void subtract() throws Exception {
        Money tenDollars = Money.valueOf(ten, dollars);
        Money fiveDollars = Money.valueOf(five, dollars);
        Money fifteenDollars = Money.valueOf(fifteen, dollars);
        Money total = fifteenDollars.subtract(fiveDollars);
        assertEquals(tenDollars, total);
        try {
            tenDollars.add((Money) null);
            fail("Null amount in add did not except.");
        } catch (NullPointerException e) {
            //Good
        }
        Money tenYen = Money.valueOf(ten, yen);
        try {
            tenDollars.add(tenYen);
            fail("Currency mismatch in add did not except");
        } catch (CurrencyMismatchException e) {
            assertSame(tenDollars, e.firstMoney());
            assertSame(tenYen, e.secondMoney());
        }
    }

    @Test
    public void multiply() throws Exception {
        Money twoSpotSomeDollars = Money.valueOf(new BigDecimal("2.525"), dollars);
        BigDecimal multiplier = new BigDecimal("7.575");
        Money expected = Money.valueOf(new BigDecimal("19.126875"), twoSpotSomeDollars.currency());
        Money result = twoSpotSomeDollars.multiply(multiplier);
        assertEquals(expected, result);
        assertTrue(expected.amount().scale() < 7);
    }

    @Test
    public void divide() throws Exception {
        Money oneHundredSpot75 = Money.valueOf(new BigDecimal("100.75"), dollars);
        BigDecimal fivish = new BigDecimal("5.05000001");
        Money almostTwentyDollars = Money.valueOf(new BigDecimal("19.95"), oneHundredSpot75.currency());
        Money result = oneHundredSpot75.divide(fivish);
        assertEquals(almostTwentyDollars, result);
        assertEquals(oneHundredSpot75.amount().scale(), result.amount().scale());
    }

    @Test
    public void negate() throws Exception {
        Money tenDollars = Money.valueOf(BigDecimal.TEN, dollars);
        Money negTenDollars = Money.valueOf(BigDecimal.TEN.negate(), dollars);
        assertEquals(negTenDollars, tenDollars.negate());
        assertEquals(tenDollars, tenDollars.negate().negate());
    }

    @Test
    public void percentage() throws Exception {
        Money twoFifty = Money.valueOf(new BigDecimal("250"), dollars);
        Percentage twentyThreePercent = Percentage.valueOf(new BigDecimal(".23"));
        Money expected = Money.valueOf(new BigDecimal("57.5"), dollars);
        Money result = twoFifty.percentage(twentyThreePercent);
        assertEquals(expected, result);
    }

    @Test
    public void compound() throws Exception {
        Money oneHundredDollars = Money.valueOf(oneHundred, dollars);
        Percentage tenPercent = Percentage.valueOf(new BigDecimal(".1"));
        Money oneHundredTen = Money.valueOf(new BigDecimal("110"), dollars);
        assertEquals(oneHundredTen, oneHundredDollars.compound(tenPercent));
    }

    @Test
    public void valueInCurrency() throws Exception {
        final BigDecimal conversionRate = new BigDecimal("12994.322445");
        Money oneHundredPounds = Money.valueOf(oneHundred, pounds);
        assertEquals(Money.valueOf(oneHundred, pounds), oneHundredPounds.valueInCurrency(pounds, null));
        final CurrencyExchange currencyExchange = context.mock(CurrencyExchange.class);
        context.checking(new Expectations() {{
            allowing(currencyExchange).calculateExchangeRate(pounds, yen); will(returnValue(conversionRate));
        }});        
        Money convertedValue = oneHundredPounds.valueInCurrency(yen, currencyExchange);
        assertNotNull(convertedValue);
        assertEquals(yen, convertedValue.currency());
        assertEquals(oneHundred.multiply(conversionRate), convertedValue.amount());
        Money zeroPounds = Money.valueOf(BigDecimal.ZERO, pounds);
        assertEquals(Money.valueOf(BigDecimal.ZERO, yen), zeroPounds.valueInCurrency(yen, currencyExchange));
    }

    @Test
    public void checkToString() throws Exception {
        Money tenDollars = Money.valueOf(new BigDecimal("10.000"), dollars);
        Money tenYen = Money.valueOf(new BigDecimal(10), yen);
        String tenDollarsString = "10.000 USD";
        String tenYenString = "10 JPY";
        assertEquals(tenDollarsString, tenDollars.toString());
        assertEquals(tenYenString, tenYen.toString());
        assertEquals(tenYen, Money.valueOf(tenYen.toString()));
    }

    @Test
    public void compareTo() throws Exception {
        Money nineDollars = Money.valueOf(new BigDecimal("9"), dollars);
        Money tenDollars = Money.valueOf(new BigDecimal("10"), dollars);
        Money elevenDollars = Money.valueOf(new BigDecimal("11"), dollars);
        Money tenDollars2 = Money.valueOf(new BigDecimal("10.00"), dollars);
        assertTrue(tenDollars.compareTo(nineDollars) > 0);
        assertTrue(tenDollars.compareTo(elevenDollars) < 0);
        assertEquals(0, tenDollars.compareTo(tenDollars2));

        Money tenYen = Money.valueOf(ten, yen);
        try {
            tenDollars.compareTo(tenYen);
            fail("Currency mismatch did not throw Exception.");
        } catch (CurrencyMismatchException e) {
            assertSame(tenDollars, e.firstMoney());
            assertSame(tenYen, e.secondMoney());
        }
    }

    @Test
    public void equalsAndHashCode() throws Exception {
        Money ten1 = Money.valueOf(new BigDecimal("10"), dollars);
        Money ten2 = Money.valueOf(new BigDecimal("10.0"), dollars);
        Money eleven = Money.valueOf(new BigDecimal("11"), dollars);
        Money tenYen = Money.valueOf(new BigDecimal("10"), yen);
        Money zeroDollars = Money.valueOf(BigDecimal.ZERO, dollars);
        Money zeroYen = Money.valueOf(BigDecimal.ZERO, yen);
        assertEquals(ten1, ten2);
        assertEquals(ten1.hashCode(), ten2.hashCode());
        assertFalse(ten1.equals(eleven));
        assertFalse(ten1.equals(tenYen));
        assertEquals(zeroDollars, zeroYen);
        assertEquals(zeroDollars.hashCode(), zeroYen.hashCode());
    }

    @Test
    public void serializable() throws Exception {
        Money money = Money.valueOf(ten, dollars);
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(money);
        out.flush();
        out.close();
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(byteOut.toByteArray()));
        Money fromStream = (Money) in.readObject();
        assertEquals(money, fromStream);
    }    

    @Test
    public void byCurrencyOrder() throws Exception {
        Money zeroEuro = Money.valueOf(BigDecimal.ZERO, Currency.getInstance("EUR"));
        Money zeroYen = Money.valueOf(BigDecimal.ZERO, Currency.getInstance("JPY"));
        Money zeroDollars = Money.valueOf(BigDecimal.ZERO, Currency.getInstance("USD"));

        assertTrue(BY_CURRENCY_ORDER.compare(zeroEuro, zeroEuro) == 0);
        assertTrue(BY_CURRENCY_ORDER.compare(zeroEuro, zeroYen) < 0);
        assertTrue(BY_CURRENCY_ORDER.compare(zeroYen, zeroEuro) > 0);
        assertTrue(BY_CURRENCY_ORDER.compare(zeroEuro, zeroDollars) < 0);
        assertTrue(BY_CURRENCY_ORDER.compare(zeroDollars, zeroEuro) > 0);

        assertTrue(BY_CURRENCY_ORDER.compare(zeroYen, zeroYen) == 0);
        assertTrue(BY_CURRENCY_ORDER.compare(zeroYen, zeroDollars) < 0);
        assertTrue(BY_CURRENCY_ORDER.compare(zeroDollars, zeroYen) > 0);

        assertTrue(BY_CURRENCY_ORDER.compare(zeroDollars, zeroDollars) == 0);
    }

    @Test
    public void zero() {
        Money zero = Money.zero(dollars);
        assertNotNull(zero);
        assertEquals(BigDecimal.ZERO, zero.amount());
        assertEquals(dollars, zero.currency());
        zero = Money.zero(yen);
        assertNotNull(zero);
        assertEquals(BigDecimal.ZERO, zero.amount());
        assertEquals(yen, zero.currency());
        zero = Money.zero("USD");
        assertNotNull(zero);
        assertEquals(BigDecimal.ZERO, zero.amount());
        assertEquals(dollars, zero.currency());
        zero = Money.zero("JPY");
        assertNotNull(zero);
        assertEquals(BigDecimal.ZERO, zero.amount());
        assertEquals(yen, zero.currency());        
    }

    @Test
    public void isImmutable() {
        assertTrue(CommonUtil.isImmutable(Money.zero("USD")));
    }
}
