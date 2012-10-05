package org.paritybits.pantheon.plutus.exchange;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.paritybits.pantheon.plutus.CurrencyExchange;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.Assert.assertEquals;


@RunWith(JMock.class)
public class CachingExchangeTest {

    final Mockery context = new JUnit4Mockery();

    @Test
    public void calculateExchangeRate() {
		final CurrencyExchange currencyExchange = context.mock(CurrencyExchange.class);
		CachingExchange cachingExchange =  new CachingExchange(currencyExchange);
		
		//Exchange rate from US Dollar -> British Pound, .66.  Should only be called once, 
		//All other times should go to the cache.
		final Currency dollar = Currency.getInstance("USD");
		final Currency pound = Currency.getInstance("GBP");
		final BigDecimal dollarToPoundRate = new BigDecimal(".66");
        context.checking(new Expectations(){{
            oneOf(currencyExchange).calculateExchangeRate(dollar, pound); will(returnValue(dollarToPoundRate));
        }});

        //First call.
		assertEquals(dollarToPoundRate, cachingExchange.calculateExchangeRate(dollar, pound)); 
		//Second call should use the cache.
		assertEquals(dollarToPoundRate, cachingExchange.calculateExchangeRate(dollar, pound));
		
		//Exchange rate from British Pound -> US Dollar, 1.5.  Should only be called once, 
		//All other times should go to the cache.
		final BigDecimal poundToDollarRate = new BigDecimal("1.5");
        context.checking(new Expectations(){{
            oneOf(currencyExchange).calculateExchangeRate(pound, dollar); will(returnValue(poundToDollarRate));
        }});
		//First Call.
		assertEquals(poundToDollarRate, cachingExchange.calculateExchangeRate(pound, dollar));
		//Second Call.  Should use cache.
		assertEquals(poundToDollarRate, cachingExchange.calculateExchangeRate(pound, dollar));
		
		//From dollar to yen.
		final Currency yen = Currency.getInstance("JPY");
		final BigDecimal dollarToYenRate = new BigDecimal("10");
        context.checking(new Expectations(){{
            oneOf(currencyExchange).calculateExchangeRate(dollar, yen); will(returnValue(dollarToYenRate));
        }});
		//First call.
		assertEquals(dollarToYenRate, cachingExchange.calculateExchangeRate(dollar, yen));
		//Second call.  Should use cache.
		assertEquals(dollarToYenRate, cachingExchange.calculateExchangeRate(dollar, yen));
        
        //From yen to dollar
		final BigDecimal yenToDollarRate = new BigDecimal("10");
        context.checking(new Expectations(){{
            oneOf(currencyExchange).calculateExchangeRate(yen, dollar); will(returnValue(yenToDollarRate));
        }});        
		//First call.
		assertEquals(dollarToYenRate, cachingExchange.calculateExchangeRate(yen, dollar));
		//Second call.  Should use cache.
		assertEquals(dollarToYenRate, cachingExchange.calculateExchangeRate(yen, dollar));
		
	}
}
