package org.paritybits.pantheon.plutus;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.paritybits.pantheon.common.CommonUtil;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.Assert.assertTrue;


@RunWith(JMock.class)
public class CrossCurrencyComparatorTest {

    final Mockery context = new JUnit4Mockery();

    @Test
    public void compare() throws Exception {
		final Currency dollar = Currency.getInstance("USD");
		final Currency yen = Currency.getInstance("JPY");
		final Currency pound = Currency.getInstance("GBP");
		BigDecimal oneHundred = new BigDecimal("100");
		Money oneHundredDollars = Money.valueOf(oneHundred, dollar);
		Money oneHundredYen = Money.valueOf(oneHundred, yen);
		Money oneHundredPounds = Money.valueOf(oneHundred, pound);
        final CurrencyExchange currencyExchange = context.mock(CurrencyExchange.class);
        context.checking(new Expectations(){{
            allowing(currencyExchange).calculateExchangeRate(yen, dollar); will(returnValue(new BigDecimal(".10")));
            allowing(currencyExchange).calculateExchangeRate(pound, dollar); will(returnValue(new BigDecimal("1.75")));
        }});		
		CrossCurrencyComparator comparator = new CrossCurrencyComparator(currencyExchange);
		assertTrue(comparator.compare(oneHundredDollars, oneHundredDollars) == 0);
		assertTrue(comparator.compare(oneHundredDollars, oneHundredYen) > 0);
		assertTrue(comparator.compare(oneHundredDollars, oneHundredPounds) < 0);
	}

    @Test
    public void isImmutable() {
        assertTrue(CommonUtil.isImmutable(new CrossCurrencyComparator(new CurrencyExchange() {
            public BigDecimal calculateExchangeRate(Currency from, Currency to) {
                return BigDecimal.ONE;
            }
        })));
    }

}
