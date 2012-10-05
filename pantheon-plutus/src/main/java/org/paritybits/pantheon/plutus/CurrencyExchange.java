package org.paritybits.pantheon.plutus;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * This interface defines an object that can calculate the exchange rates
 * between currencies.
 * 
 * @author Andrew Tillman
 * @version 0.9
 */
public interface CurrencyExchange {
	
	/**
	 * Calculates the exchange rate between the two given currencies.
	 * 
	 * @param from Currency converting from
	 * @param to Currency converting to
	 * @return The exchange rate.
	 */
	public BigDecimal calculateExchangeRate(Currency from, Currency to); 

}
