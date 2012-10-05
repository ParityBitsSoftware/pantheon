package org.paritybits.pantheon.plutus;

import org.paritybits.pantheon.common.Percentage;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * This interface represents something that has monetary amount.  This amount can be turned into a
 * concrete Money instance of a given currency.  It also can have certain arithmatic operations performed
 * on it.  Implementations of this interface are responsible for dealing with the details of currencies
 * and as such this interface does not include operations for addition or subtraction.
 * It is highly suggested that classes which implement this interface be immutable.
 *
 * @author Andrew Tillman
 * @version 0.9
 * @see org.paritybits.pantheon.plutus.Money
 */
public interface MonetaryValue {

    /**
     * Multiply this MonetaryValue by the given amount.
     *
     * @param amount The amount to multiply by
     * @return The result of the multiplication.
     */
    MonetaryValue multiply(BigDecimal amount);

    /**
     * Divide this MonetaryValue bu
     *
     * @param amount
     * @return The result of the division.
     */
    MonetaryValue divide(BigDecimal amount);

    /**
     * Negate this MonetaryValue, equivilant to multiplying this MonetaryValue by -1.
     *
     * @return The result of the negation.
     */
    MonetaryValue negate();

    /**
     * Calculates a percentage of this MonetaryValue.  This is similar to multiplying this
     * MonetaryValue by the given BigDecimal amount of the given Percentage.
     *
     * @param percentage The percentage to calculate with.
     * @return The result of the percentage calculation
     */
    MonetaryValue percentage(Percentage percentage);

    /**
     * Compounds the amount of this MonetaryValue.  This is similar to adding this MonetaryValue
     * to the result of the percentage calculation.
     *
     * @param percentage How much to compound by.
     * @return The result of the compound calculation.
     */
    MonetaryValue compound(Percentage percentage);

    /**
     * Gives the concrete Money amount of this MonetaryValue in the given currency.
     *
     * @param currency         The currency of the resulting Money object.
     * @param currencyExchange A converter to deal with any currency conversions.
     * @return The amount of this MonetaryValue in the given currency.
     */
	Money valueInCurrency(Currency currency, CurrencyExchange currencyExchange);
}
