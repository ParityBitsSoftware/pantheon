package org.paritybits.pantheon.plutus;

import org.paritybits.pantheon.common.Immutable;
import org.paritybits.pantheon.common.Percentage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Currency;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an amount of money by wrapping a BigDecimal amount and a Currency.
 * The class has simple arithmetical operations that work on monies with the same currency
 * as well as a conversion method.  This class is immutable.
 * <p/>
 * <p>This class also has a custom serialized form.  It is the string representation of the Money
 * in the format of [amount] [currency code].</p>
 *
 * <p>This class does not have any methods for formatting.  The formatting of strings based
 * on Money is outside of the concern of this class.</p>
 *
 * @author Andrew Tillman
 * @version 0.9
 */
@Immutable
@SuppressWarnings("UnusedDeclaration")
public final class Money implements Serializable, Comparable<Money>, MonetaryValue {

    static final long serialVersionUID = 7918032368736296520L;

    //Pattern used to parse strings.
    private static final Pattern PARSE_PATTERN = Pattern.compile("(\\d+) (\\w\\w\\w)");

    /**
     * A comparator that will compare money objects via their currency code in alphatetical order
     */
    public static final Comparator<Money> BY_CURRENCY_ORDER = new Comparator<Money>() {
        public int compare(Money compareThis, Money toThat) {
            return compareThis.currency().getCurrencyCode().compareTo(toThat.currency().getCurrencyCode());
        }
    };

    //Fields
    private transient final BigDecimal amount;
    private transient final Currency currency;

    //Precalcuate the hashcode.  
    private transient final int hashCode;

    //This is here to handle having a custom serialized form
    //it is therefore not final.  This is also used by toString as 
    //toString is the same as the external form.
    private transient String externalForm;

    //Arithmetical operations
    private enum MoneyOperation  {
        ADD {
            BigDecimal eval(BigDecimal one, BigDecimal two) {
                return one.add(two);
            }
        },
        SUBTRACT {
            BigDecimal eval(BigDecimal one, BigDecimal two) {
                return one.subtract(two);
            }
        },
        MULTIPLY {
            BigDecimal eval(BigDecimal one, BigDecimal two) {
                return one.multiply(two);
            }
        },
        DIVIDE {
            BigDecimal eval(BigDecimal one, BigDecimal two) {
                return one.divide(two, BigDecimal.ROUND_HALF_EVEN);
            }
        };

        abstract BigDecimal eval(BigDecimal one, BigDecimal two);
    }
 

    /**
     * Required for Serializable.
     */
    Money() {
        amount = null;
        currency = null;
        externalForm = null;
        hashCode = 0;
    }

    private Money(final BigDecimal amount, final Currency currency) {
        if (amount == null || currency == null) throw new NullPointerException("Value and currency are required.");
        this.amount = amount;
        this.currency = currency;
        this.externalForm = amount.toString() + ' ' + currency.toString();
        this.hashCode = 29 * amount.stripTrailingZeros().hashCode();
    }

    /**
     * The raw amount of money
     *
     * @return The BigDecimal amount.
     */
    public BigDecimal amount() {
        return amount;
    }

    /**
     * The currecny of the money
     *
     * @return The Currency.
     */
    public Currency currency() {
        return currency;
    }

    /**
     * Add to the Money.
     *
     * @param amount The amount to add.  The currency must be the same.
     * @return A new Money representing the total.
     * @throws NullPointerException      If amount is null.
     * @throws CurrencyMismatchException If the currencies are not the same.
     */
    public Money add(final Money amount) throws CurrencyMismatchException {
        return doArithmatic(MoneyOperation.ADD, amount);
    }

    /**
     * Subtract from the Money.
     *
     * @param amount The amount to subtract.  The currency must be the same.
     * @return A new Money representing the difference.
     * @throws NullPointerException      If amount is null.
     * @throws CurrencyMismatchException If the currencies are not the same
     */
    public Money subtract(final Money amount) throws CurrencyMismatchException {
        return doArithmatic(MoneyOperation.SUBTRACT, amount);
    }

    /**
     * Multiplies the specified Money by the given amount.
     *
     * @param amount The amount to multiply the specified Money by.
     * @return A new Money object representing the result.  The scale of the result
     *         may be no greater than the sum of the scale of specified Money object's amount and
     *         the given amount.
     */
    public Money multiply(final BigDecimal amount) {
        return doArithmatic(MoneyOperation.MULTIPLY, amount);
    }


    /**
     * Divides the specified Money by the given amount.
     *
     * @param amount The amount to divide the specified Money by.
     * @return A new Money object representing the result.  The scale of the result
     *         is the same as the scale of the specified Money object.
     *         Rounding used is BigDecimal.ROUND_HALF_EVEN
     */
    public Money divide(final BigDecimal amount) {
        return doArithmatic(MoneyOperation.DIVIDE, amount);
    }

    /**
     * Negates the amount of the specified Money
     *
     * @return A new Money with the negated amount.
     */
    public Money negate() {
        return new Money(amount.negate(), currency);
    }

    /**
     * Gets the amount the given percentage of the specified Money.
     *
     * @param percentage The percentage used to calculate.
     * @return The new calculated amount.
     */
    public Money percentage(final Percentage percentage) {
        return doArithmatic(MoneyOperation.MULTIPLY, percentage.value());
    }

    /**
     * Compounds the specified Money based on given Percentage.
     *
     * @param percentage The Percentage to compound by.
     * @return The new calculated amount.
     */
    public Money compound(final Percentage percentage) {
        return add(percentage(percentage));
    }

    /**
     * Converts this Money to the given currency using the exchange rate provided
     * by the given currency exchange.
     *
     * @param toCurrency       The currency to convert to
     * @param currencyExchange The provides the exchange rate.
     * @return A new Money whose currency is toCurrency and whose amount is the
     *         amount of the conversion.
     * @see org.paritybits.pantheon.plutus.CurrencyExchange
     */
    public Money valueInCurrency(final Currency toCurrency, final CurrencyExchange currencyExchange) {
        if (currency.equals(toCurrency)) {
            return this;
        } else if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return new Money(BigDecimal.ZERO, toCurrency);
        } else {
            return new Money(amount.multiply(currencyExchange.calculateExchangeRate(
                    currency, toCurrency)), toCurrency);
        }
    }

    Money doArithmatic(final MoneyOperation moneyOperation, final Money amount) throws CurrencyMismatchException {
        if (amount == null) throw new NullPointerException("Amount cannot be null in operation");
        if (!currency.equals(amount.currency)) throw new CurrencyMismatchException(this, amount);
        return doArithmatic(moneyOperation, amount.amount);
    }

    Money doArithmatic(final MoneyOperation moneyOperation, final BigDecimal otherValue) {
        return new Money(moneyOperation.eval(amount, otherValue), currency);
    }

    /**
     * Compares this Money to another Money.  Both must have the same currency for this comparasion to work.
     *
     * @param other The Money to compare to.
     * @return The result of comparing the amounts.
     * @throws CurrencyMismatchException if different currencies are involved in this comparsion.
     */
    public int compareTo(final Money other) throws CurrencyMismatchException {
        if (!currency.equals(other.currency)) {
            throw new CurrencyMismatchException(this, other);
        }
        return amount.compareTo(other.amount);
    }

    /**
     * Test equality of this money to be equal to another object.  It will compare the values
     * of Money with BigDecimal.compareTo() == 0 to get around scale problems.  Money
     * objects must have the same currency as well as amount to be equal.  The exception is the case
     * of Money objects with a zero amount.  Those always are considered equal. So a Money object
     * that represents 10 US dollars would not be equal to a Money object that represents
     * 10 Japanese Yen.  But a Money object that represents 0 US dollars would be equal to
     * a Money object that represented 0 Japanese Yen.
     *
     * @param o The object to compare.
     * @return true if o is a Money object and the amount of the specified Money is equal
     *         to the amount of the given object and if the amount is greater than zero that
     *         the currency of the specified Money and the given Money is equal.
     */
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Money)) return false;

        final Money other = (Money) o;

        //If the amount is zero don't check currency
        return amount.compareTo(BigDecimal.ZERO) == 0 ||
                currency.equals(other.currency) &&
                        amount.compareTo(other.amount) == 0;
    }

    public int hashCode() {
        return hashCode;
    }

    /**
     * This will display the amount of the money followed by the three character currency code.
     * For example; 10 US dollars will be displayed as '10 USD' while 10 Canadian dolllars will be '10 CND'.
     * This string can be passed to valueOf to create a new Money that is equal to the specified Money.
     *
     * @return A String representation of the specified Money object in the format of [amount] [currency].
     */
    public String toString() {
        return externalForm;
    }

    //Methods used to implement the custom serialized form.
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.writeObject(externalForm);
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        externalForm = (String) in.readObject();
    }

    private Object readResolve() {
        if (externalForm != null) {
            return valueOf(externalForm);
        } else {
            throw new NullPointerException("The external form was null during readResolve");
        }
    }


    /**
     * Parses a string in in the format of [amount] [currency code] into a
     * new Money object.
     *
     * @param moneyString A money formatted string.
     * @return The Money result of the parse.
     * @throws IllegalArgumentException If the string is not formatted correctly.
     * @throws NullPointerException     If the string is null.
     */
    public static Money valueOf(final String moneyString) throws IllegalArgumentException {

        Matcher matcher = PARSE_PATTERN.matcher(moneyString);
        if (matcher.find()) {
            String valueString = matcher.group(1);
            String currencyCode = matcher.group(2);
            return new Money(new BigDecimal(valueString), Currency.getInstance(currencyCode));
        } else {
            throw new IllegalArgumentException("String not in the <amount> <currency code> format.");
        }
    }

    /**
     * Gets the money volue of the given amount and currency.
     *
     * @param amount   The amount of the money.
     * @param currency The currency of the money.
     * @return A Money instance.
     * @throws NullPointerException If amount or currency are null.
     */
    public static Money valueOf(final BigDecimal amount, final Currency currency) {
        return new Money(amount, currency);
    }

    /**
     * Create a zero amount in a specified currnecy.
     *
     * @param currency The currency for the zero amount
     * @return A Money with a zero amount and the given currency.
     */
    public static Money zero(Currency currency) {
        return valueOf(BigDecimal.ZERO, currency);
    }

    /**
     * Create a zero amount in a specified currnecy code.
     *
     * @param currencyCode The code for the currency for the zero amount
     * @return A Money with a zero amount and the given currency.
     */
    public static Money zero(String currencyCode) {
        return zero(Currency.getInstance(currencyCode));
    }
}