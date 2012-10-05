package org.paritybits.pantheon.plutus;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
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


@RunWith(JMock.class)
public class MoneyBagTest {

    //JMock context
    final Mockery context = new JUnit4Mockery(); 

    //Currencies.
    final private Currency dollars = Currency.getInstance("USD");
    final private Currency yen = Currency.getInstance("JPY");
    final private Currency euros = Currency.getInstance("EUR");
    final private Currency pounds = Currency.getInstance("GBP");

    //CurrencyExchange
    CurrencyExchange currencyExchange;

    @Before
    public void setupCurrencyExchange() {

        final CurrencyExchange currencyExchange = context.mock(CurrencyExchange.class);

        //Define the rates;
        final BigDecimal dollarsToYenRate = new BigDecimal("10");
        final BigDecimal dollarsToEurosRate = new BigDecimal(".75");
        final BigDecimal dollarsToPoundsRate = new BigDecimal(".5");
        final BigDecimal yenToDollarsRate = BigDecimal.ONE.divide(dollarsToYenRate);
        final BigDecimal yenToEurosRate = new BigDecimal(".075");
        final BigDecimal yenToPoundsRate = new BigDecimal(".05");
        final BigDecimal eurosToDollarsRate = BigDecimal.ONE.divide(dollarsToEurosRate, 2, BigDecimal.ROUND_HALF_EVEN);
        final BigDecimal eurosToYenRate = BigDecimal.ONE.divide(yenToEurosRate, 2, BigDecimal.ROUND_HALF_EVEN);
        final BigDecimal eurosToPoundsRate = new BigDecimal(".667");
        final BigDecimal poundsToDollarsRate = BigDecimal.ONE.divide(dollarsToPoundsRate, 2, BigDecimal.ROUND_HALF_EVEN);
        final BigDecimal poundsToYenRate = BigDecimal.ONE.divide(yenToPoundsRate, 2, BigDecimal.ROUND_HALF_EVEN);
        final BigDecimal poundsToEurosRate = BigDecimal.ONE.divide(eurosToPoundsRate, 2, BigDecimal.ROUND_HALF_EVEN);

        context.checking(new Expectations() {{
            allowing(currencyExchange).calculateExchangeRate(dollars, yen); will(returnValue(dollarsToYenRate));
            allowing(currencyExchange).calculateExchangeRate(dollars, euros); will(returnValue(dollarsToEurosRate));
            allowing(currencyExchange).calculateExchangeRate(dollars, pounds); will(returnValue(dollarsToPoundsRate));
            allowing(currencyExchange).calculateExchangeRate(yen, dollars); will(returnValue(yenToDollarsRate));
            allowing(currencyExchange).calculateExchangeRate(yen, euros); will(returnValue(yenToEurosRate));
            allowing(currencyExchange).calculateExchangeRate(yen, pounds); will(returnValue(yenToPoundsRate));
            allowing(currencyExchange).calculateExchangeRate(euros, dollars); will(returnValue(eurosToDollarsRate));
            allowing(currencyExchange).calculateExchangeRate(euros, yen); will(returnValue(eurosToYenRate));
            allowing(currencyExchange).calculateExchangeRate(euros, pounds); will(returnValue(eurosToPoundsRate));
            allowing(currencyExchange).calculateExchangeRate(pounds, dollars); will(returnValue(poundsToDollarsRate));
            allowing(currencyExchange).calculateExchangeRate(pounds, yen); will(returnValue(poundsToYenRate));
            allowing(currencyExchange).calculateExchangeRate(pounds, euros); will(returnValue(poundsToEurosRate));            
        }});        
        this.currencyExchange = currencyExchange;
    }      

    @Test
    public void moneyBag() {
        //Create the initial bag.
        MoneyBag baseMoneyBag = MoneyBag.emptyBag();

        //Should start at zero.
        testBaseIsZero(baseMoneyBag);

        //Add some money, check new amount.  Make sure
        //original is unchanged.
        Money tenDollars = Money.valueOf(BigDecimal.TEN, dollars);
        MoneyBag runningTotal = baseMoneyBag.add(tenDollars);
        assertNotNull(runningTotal);
        testCalculateValue(runningTotal, BigDecimal.TEN);
        testBaseIsZero(baseMoneyBag);
        Money tenPounds = Money.valueOf(BigDecimal.TEN, pounds);
        runningTotal = runningTotal.add(tenPounds);
        testCalculateValue(runningTotal, new BigDecimal("30"));
        testBaseIsZero(baseMoneyBag);
        Money fiftyYen = Money.valueOf(new BigDecimal("50"), yen);
        runningTotal = runningTotal.subtract(fiftyYen);
        testCalculateValue(runningTotal, new BigDecimal("25"));

        //Hold onto this MoneyBag for a later test.
        MoneyBag hasValueOf25Usd = runningTotal;

        testBaseIsZero(baseMoneyBag);
        runningTotal = runningTotal.add(tenDollars);
        testCalculateValue(runningTotal, new BigDecimal("35"));
        testBaseIsZero(baseMoneyBag);
        runningTotal = runningTotal.subtract(Money.valueOf(new BigDecimal("2.5"), pounds));
        testCalculateValue(runningTotal, new BigDecimal("30"));
        testBaseIsZero(baseMoneyBag);
        runningTotal = runningTotal.subtract(Money.valueOf(new BigDecimal("5"), dollars));
        testCalculateValue(runningTotal, new BigDecimal("25"));
        testBaseIsZero(baseMoneyBag);

        //Test multiplication and division.
        runningTotal = runningTotal.multiply(new BigDecimal("2"));
        testCalculateValue(runningTotal, new BigDecimal("50"));
        testBaseIsZero(baseMoneyBag);
        runningTotal = runningTotal.divide(new BigDecimal("10"));
        testCalculateValue(runningTotal, new BigDecimal("5"));
        testBaseIsZero(baseMoneyBag);

        //Hold onto this MoneyBag for a later test.
        MoneyBag hasValueOf5Usd = runningTotal;

        //Add and subtract money bags.
        runningTotal = runningTotal.add(hasValueOf5Usd);
        testCalculateValue(runningTotal, new BigDecimal("10"));
        testCalculateValue(hasValueOf5Usd, new BigDecimal("5"));
        testBaseIsZero(baseMoneyBag);
        runningTotal = runningTotal.subtract(hasValueOf25Usd);
        testCalculateValue(runningTotal, new BigDecimal("-15"));
        testCalculateValue(hasValueOf25Usd, new BigDecimal("25"));
        testBaseIsZero(baseMoneyBag);

        //Test negation
        runningTotal = runningTotal.negate();
        testCalculateValue(runningTotal, new BigDecimal("15"));
        testBaseIsZero(baseMoneyBag);

        //Get the percentage
        MoneyBag threeUsd = runningTotal.percentage(Percentage.valueOf("20%"));
        testCalculateValue(threeUsd, new BigDecimal("3"));

        //Compound the the amount
        runningTotal = runningTotal.compound(Percentage.valueOf("10%"));
        testCalculateValue(runningTotal, new BigDecimal("16.5"));
    }

    private void testCalculateValue(MoneyBag runningTotal, BigDecimal expectedDollarValue) {
        Money expectedDollars = Money.valueOf(expectedDollarValue, dollars);
        Money expectedYen = expectedDollars.valueInCurrency(yen, currencyExchange);
        Money expectedEuros = expectedDollars.valueInCurrency(euros, currencyExchange);
        Money expectedPounds = expectedDollars.valueInCurrency(pounds, currencyExchange);
        assertEquals(expectedDollars, runningTotal.valueInCurrency(dollars, currencyExchange));
        assertEquals(expectedYen, runningTotal.valueInCurrency(yen, currencyExchange));
        assertEquals(expectedEuros, runningTotal.valueInCurrency(euros, currencyExchange));
        assertEquals(expectedPounds, runningTotal.valueInCurrency(pounds, currencyExchange));
    }

    private void testBaseIsZero(MoneyBag moneyBag) {        
        Money result = moneyBag.valueInCurrency(dollars, currencyExchange);
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.amount());
        assertEquals(dollars, result.currency());
        result = moneyBag.valueInCurrency(yen, currencyExchange);
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.amount());
        assertEquals(yen, result.currency());
        result = moneyBag.valueInCurrency(euros, currencyExchange);
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.amount());
        assertEquals(euros, result.currency());
        result = moneyBag.valueInCurrency(pounds, currencyExchange);
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.amount());
        assertEquals(pounds, result.currency());
    }

    @Test
    public void equalsAndHashCode() {
        MoneyBag bag1 = MoneyBag.emptyBag();
        assertFalse(bag1.equals(null));
        assertFalse(bag1.equals(new Object()));
        MoneyBag bag2 = MoneyBag.emptyBag();
        assertTrue(bag1.equals(bag2));
        assertTrue(bag1.equals(bag2));
        assertEquals(bag1.hashCode(), bag2.hashCode());

        bag2 = bag2.add(Money.valueOf("100 USD"));
        assertFalse(bag1.equals(bag2));
        assertFalse(bag2.equals(bag1));
    }

    @Test
    public void serialization() throws Exception {
        MoneyBag bag1 = MoneyBag.emptyBag();
        bag1 = bag1.add(Money.valueOf("166 EUR"));
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(data);
        out.writeObject(bag1);
        out.close();
        ObjectInputStream in = new ObjectInputStream(
                new ByteArrayInputStream(data.toByteArray()));
        MoneyBag bag2 = (MoneyBag) in.readObject();
        assertEquals(bag1, bag2);
    }

    @Test
    public void containing() throws Exception {
        //Test using money params
        Money tenDollars = Money.valueOf("10 USD");
        Money fiveYen = Money.valueOf("5 JPY");
        MoneyBag expected = MoneyBag.emptyBag();
        expected = expected.add(tenDollars);
        expected = expected.add(fiveYen);
        MoneyBag createdBag = MoneyBag.containing(tenDollars, fiveYen);
        assertEquals(expected, createdBag);
        expected = expected.add(tenDollars);
        createdBag = MoneyBag.containing(tenDollars, tenDollars, fiveYen);
        assertEquals(expected, createdBag);
    }

    @Test
    public void checkToString() throws Exception {
        MoneyBag bag = MoneyBag.containing(Money.valueOf("10 USD"),
                Money.valueOf("20 JPY"), Money.valueOf("15 EUR"));
        assertEquals("[15 EUR, 20 JPY, 10 USD]", bag.toString());
        assertEquals("[]", MoneyBag.emptyBag().toString());
    }

    @Test
    public void isImmutable() {
        assertTrue(CommonUtil.isImmutable(MoneyBag.emptyBag()));
    }
}
