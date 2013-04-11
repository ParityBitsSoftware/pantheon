package org.paritybits.pantheon.plutus.exchange;

import org.paritybits.pantheon.plutus.CurrencyExchange;

import java.util.Date;

/**
 * A factory for creating a CurrencyExchange.  This interface is based around the 
 * idea that a currency exchange rate is valid only for a certain period of time.
 * 
 * @author Andrew Tillman
 * @version 0.9
 */
public interface CurrencyExchangeFactory {

	/**
	 * Creates a new exchange for the given date.
	 * 
	 * @param exchangeDate The date that the exchange is valid for.
	 * @return A currency exchange.
	 */
	CurrencyExchange createCurrencyExchange(Date exchangeDate);

}
