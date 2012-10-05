package org.paritybits.pantheon.plutus.exchange;


import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.paritybits.pantheon.common.CommonUtil;
import org.paritybits.pantheon.plutus.CurrencyExchange;

import java.util.Date;

import static org.junit.Assert.*;


@RunWith(JMock.class)
public class CachingExchangeFactoryTest {

    final Mockery context = new JUnit4Mockery();


    @Test
    public void createCurrencyExchange() {
		
		//Create the currency exchange that will be returned
		//from the wrapped factory.
		final CurrencyExchange wrappedExchange = context.mock(CurrencyExchange.class);
		
		//Create the wrapped factory for the test.
        final CurrencyExchangeFactory wrappedFactory = context.mock(CurrencyExchangeFactory.class);
        context.checking(new Expectations(){{
            allowing(wrappedFactory).createCurrencyExchange(with(any(Date.class))); will(returnValue(wrappedExchange));
        }});
		
		//Create the factory and run the test.
		CachingExchangeFactory factory = new CachingExchangeFactory(wrappedFactory);
		CurrencyExchange exchange = factory.createCurrencyExchange(new Date());
		assertNotNull(exchange);
		assertEquals(CachingExchange.class, exchange.getClass());
		assertEquals(new CachingExchange(wrappedExchange), exchange);
	}

    @Test
    public void isImmutable() {
        assertTrue(CommonUtil.isImmutable(new CachingExchangeFactory(context.mock(CurrencyExchangeFactory.class))));
    }

}
