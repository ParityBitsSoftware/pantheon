package org.paritybits.pantheon.plutus.exchange;

import org.paritybits.pantheon.plutus.CurrencyExchange;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

/**
 * A currency exchange that wraps around another currency exchange
 * and caches the results of it's calcuations.
 * 
 * @author Andrew Tillman
 * @version 0.9
 */
public final class CachingExchange implements CurrencyExchange {
	
	private final CurrencyExchange currencyExchange;
	private final Map<ExchangeKey, BigDecimal> cache = 
		new HashMap<ExchangeKey, BigDecimal>();

	/**
	 * Creates a new CachingExchange that wraps around the given CurrencyExchange. 
	 * 
	 * @param currencyExchange The underlying exchange for the new CachingExchange.
	 */
	public CachingExchange(final CurrencyExchange currencyExchange) {
		this.currencyExchange = currencyExchange;
	}

	/**
	 * Calculates the exchange rate using the cache if these two currencies have
	 * been encountered before, or calls to the underlying exchange if they have
	 * not.
	 * 
	 * @param from The currency converting from.
	 * @param to The currency converting to.
	 * @return The exchange rate for these 2 currencies.
	 */
	public BigDecimal calculateExchangeRate(final Currency from, final Currency to) {
		ExchangeKey exchangeKey = new ExchangeKey(from, to);
		BigDecimal rate = cache.get(exchangeKey);
		if(rate == null) {
			rate = currencyExchange.calculateExchangeRate(from, to);
			cache.put(exchangeKey, rate);
		}
		return rate;
	}


    public boolean equals(Object o) {
        if(o instanceof CachingExchange) {
            CachingExchange cExch = (CachingExchange) o;
            return currencyExchange.equals(cExch.currencyExchange);
        } else {
            return false;
        }
    }

    public String toString() {
		return MessageFormat.format("CachingExchange around {0}", currencyExchange);
	}
	
	private static class ExchangeKey {
		private final Currency from;
		private final Currency to;
		public ExchangeKey(Currency from, Currency to) {
			this.from = from;
			this.to = to;
		}
		
		public boolean equals(Object other) {
			return (other instanceof ExchangeKey)
				&& (from.equals(((ExchangeKey)other).from)) 
					&& (to.equals(((ExchangeKey)other).to));
		}
		
		public int hashCode() {			
			return from.hashCode() + to.hashCode();
		}
	}
}
