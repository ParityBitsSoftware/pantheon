package org.paritybits.pantheon.plutus.exchange;

import org.paritybits.pantheon.common.Immutable;

import java.util.Date;


/**
 * This factory wraps another ExchangeFactory.  It will return CachingExchange
 * objects that wrap exchanges created by the wrapped factory.
 *
 * @author Andrew Tillman
 * @version 0.9
 */
@Immutable
public final class CachingExchangeFactory implements CurrencyExchangeFactory {

    private final CurrencyExchangeFactory wrappedFactory;

    /**
     * Creates a new CachingExchangeFactory which wraps the given factory.
     *
     * @param wrappedFactory The wrapped factory.
     */
    public CachingExchangeFactory(final CurrencyExchangeFactory wrappedFactory) {
        this.wrappedFactory = wrappedFactory;
    }

    /**
     * Creates a new CachingExchange which wraps around the CurrencyExchange
     * created by the underlying factory.
     *
     * @param exchangeDate The date to valueOf the exchange for
     * @return A new CachingExchange object.
     */
    public CachingExchange createCurrencyExchange(final Date exchangeDate) {
        return new CachingExchange(
                wrappedFactory.createCurrencyExchange(exchangeDate));
	}
}
