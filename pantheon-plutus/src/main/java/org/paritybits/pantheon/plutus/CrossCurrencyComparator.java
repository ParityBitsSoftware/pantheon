package org.paritybits.pantheon.plutus;

import org.paritybits.pantheon.common.Immutable;

import java.util.Comparator;
import java.util.Currency;


/**
 * A comparator that can compare Money objects that may have different 
 * currencies.  To do this it uses a CurrencyExchange.
 * 
 * @author Andrew Tillman
 * @version 0.9
 * @see org.paritybits.pantheon.plutus.CurrencyExchange
 */
@Immutable
public final class CrossCurrencyComparator implements Comparator<Money>{

	private final CurrencyExchange currencyExchange;

	public CrossCurrencyComparator(final CurrencyExchange currencyConverter) {
		this.currencyExchange = currencyConverter;
	}
	
	/**
	 * Compares the two monies, normalizing the currencies if needed.
	 * 
	 * @param compareThis 
	 * @param toThat
	 * @return the result of the comparasion.
	 */
	public int compare(final Money compareThis, final Money toThat) {
		Currency compareThisCurrency = compareThis.currency();
		if (compareThisCurrency.equals(toThat.currency())) {
			return compareThis.compareTo(toThat);
		} else {
			return compareThis.compareTo(toThat.valueInCurrency(compareThisCurrency, 
					currencyExchange));
		}
	}
}
