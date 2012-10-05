package org.paritybits.pantheon.plutus;

import org.paritybits.pantheon.common.FunctionMap;
import org.paritybits.pantheon.common.FunctionMap.PutFunction;
import org.paritybits.pantheon.common.Immutable;
import org.paritybits.pantheon.common.Percentage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * A MoneyBag is used to perform cross currency additions and subtractions.  It will defer conversion until
 * the amount of the bag is calculated for a specific currency.  This class is immutable.
 * <p/>
 * This class has a custom serialized form that is the same as it's toString representation.
 *
 * @author Andrew Tillman
 * @version 0.9
 */
@Immutable
public final class MoneyBag implements MonetaryValue, Serializable {

    static final long serialVersionUID = 8812197307547270615L;

    //The totals in the moneybag
    private transient final Map<Currency, Money> totals;

    //The external form of the money bag, used for toString as well.
    private transient String externalForm;

    //Add function for operations
    private static final PutFunction<Money> ADD_MAP_FUNCTION =
            new PutFunction<Money>() {
                public Money determineValue(Money oldValue, Money newValue) {
                    return oldValue.add(newValue);
                }
            };

    //The empty bag
    private static final MoneyBag EMPTY_BAG = new MoneyBag(); 

    /**
     * Creates a new empty MoneyBag.
     */
    private MoneyBag() {
        totals = Collections.emptyMap();
    }

    /**
     * Private constructor used by operations to valueOf new results.
     *
     * @param totals The totals of the new MoneyBag.
     */
    private MoneyBag(final Map<Currency, Money> totals) {
        this.totals = Collections.unmodifiableMap(totals);
    }

    /**
     * Add Money to the MoneyBag.
     *
     * @param amount The amount to add to the MoneyBag.
     * @return A new MoneyBag representing the the specified MoneyBag plus the given Money.
     */
    public MoneyBag add(final Money amount) {
        FunctionMap<Currency, Money> newTotals =
                new FunctionMap<Currency, Money>(ADD_MAP_FUNCTION);
        newTotals.putAll(totals);
        newTotals.put(amount.currency(), amount);
        return new MoneyBag(newTotals);
    }

    /**
     * Adds a MoneyBag to this MoneyBag.
     *
     * @param otherMoneyBag The bag to add to this MoneyBag.
     * @return A new MoneyBag that is the result of adding these two MoneyBags.
     */
    public MoneyBag add(final MoneyBag otherMoneyBag) {
        //TODO.  This method may be too inefficient, profile to see if it needs tuning
        MoneyBag runningTotal = this;
        for (Money otherTotal : otherMoneyBag.totals.values()) {
            runningTotal = runningTotal.add(otherTotal);
        }
        return runningTotal;
    }

    /**
     * Subtract the specified Money to the MoneyBag.
     *
     * @param amount The amount to subtract from the MoneyBag.
     * @return A new MoneyBag representing the the specified MoneyBag less the amount of the give Money.
     */
    public MoneyBag subtract(final Money amount) {
        return add(amount.negate());
    }

    /**
     * Subtracts a MoneyBag from this MoneyBag
     *
     * @param otherMoneyBag The MoneyBag to subtract.
     * @return The result of the subtraction.
     */
    public MoneyBag subtract(final MoneyBag otherMoneyBag) {
        return add(otherMoneyBag.negate());
    }

    /**
     * Multiply the amount of this MoneyBag by the given amount.
     *
     * @param amount The BigDecimal amount to multiply by.
     * @return The result of the multiplication.
     */
    public MoneyBag multiply(final BigDecimal amount) {
        return operateOnTotals(new MoneyBagOperation() {
            public Money performOperation(final Money total) {
                return total.multiply(amount);
            }
        });
    }

    /**
     * Divide the amount of this MoneyBag by the given amount.
     *
     * @param amount The BigDecimal amount to divide by.
     * @return The result of the division.
     */
    public MoneyBag divide(final BigDecimal amount) {
        return operateOnTotals(new MoneyBagOperation() {
            public Money performOperation(final Money total) {
                return total.divide(amount);
            }
        });
    }

    /**
     * Negates the amount of this MoneyBag
     *
     * @return The negated amount.
     */
    public MoneyBag negate() {
        return operateOnTotals(new MoneyBagOperation() {
            public Money performOperation(Money amount) {
                return amount.negate();
            }
        });
    }

    /**
     * Get the given percentage amount of this MoneyBag.
     *
     * @param percentage The percentage for the calculation
     * @return The amount of the percentage.
     */
    public MoneyBag percentage(final Percentage percentage) {
        return operateOnTotals(new MoneyBagOperation() {
            public Money performOperation(Money amount) {
                return amount.percentage(percentage);
            }
        });
    }

    /**
     * Compounds the MoneyBag by the given amount.
     *
     * @param percentage The percentage to compound by.
     * @return The compounded amount.
     */
    public MoneyBag compound(final Percentage percentage) {
        return operateOnTotals(new MoneyBagOperation() {
            public Money performOperation(Money amount) {
                return amount.compound(percentage);
            }
        });
    }

    private MoneyBag operateOnTotals(final MoneyBagOperation operation) {
        Map<Currency, Money> newTotals = new HashMap<Currency, Money>();
        for (Money total : totals.values()) {
            newTotals.put(total.currency(),
                    operation.performOperation(total));
        }
        return new MoneyBag(newTotals);
    }

    /**
     * Finds the amount of this MoneyBag in the given currency.
     *
     * @param currency         What currency should the amount be in.
     * @param currencyExchange Used to convert the items in the bag.
     * @return A new Money object whose currency is the given currency and whose amount is the result
     *         of the calculation.
     * @see org.paritybits.pantheon.plutus.CurrencyExchange
     */
    public Money valueInCurrency(final Currency currency, final CurrencyExchange currencyExchange) {
        Money result = Money.zero(currency);
        for (Money total : totals.values()) {
            result = result.add(total.valueInCurrency(currency, currencyExchange));
        }
        return result;
    }

    /**
     * Returns this moneybag as a string of currency totals.  This is also the custom serialized form for a
     * MoneyBag.
     *
     * @return The string representation of this MoneyBag.
     */
    public String toString() {
        return externalForm();
    }

    private String externalForm() {
        if (externalForm == null) {
            List<Money> values = new ArrayList<Money>(totals.values());
            Collections.sort(values, Money.BY_CURRENCY_ORDER);
            externalForm = values.toString();
        }
        return externalForm;
    }

    /**
     * Compares for equality.
     *
     * @param other The object to compare to
     * @return true If other is a MoneyBag with the same amounts in it.
     */
    public boolean equals(final Object other) {
        if (other != null && other.getClass() == MoneyBag.class) {
            MoneyBag otherBag = (MoneyBag) other;
            return totals.equals(otherBag.totals);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return totals.hashCode();
    }

    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.writeObject(externalForm());
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        externalForm = (String) in.readObject();
    }

    private Object readResolve() {
        if (externalForm != null) {
            String[] valueStrings = externalForm.replace(
                    "[", "").replace("]", "").split(",");
            Money[] values = new Money[valueStrings.length];
            for (int i = 0; i < valueStrings.length; i++) {
                values[i] = Money.valueOf(valueStrings[i]);
            }
            return containing(values);
        } else {
            throw new NullPointerException("The external form was null during readResolve");
        }
    }

    /**
     * An interface to abstract out operations from the commom
     * algorithm to applying them to all the elements in a bag.
     */
    static interface MoneyBagOperation {
        Money performOperation(final Money amount);
    }

    /**
     * Creates a new money bag with the given monies as its contents.
     *
     * @param moneies The contents of the bag.  If more than one money
     *                share the same currency, they will be added.
     * @return A new MoneyBag.
     */
    public static MoneyBag containing(final Money... moneies) {
        Map<Currency, Money> totals = new FunctionMap<Currency, Money>(
                ADD_MAP_FUNCTION);
        for (final Money money : moneies) {
            totals.put(money.currency(), money);
        }
        return new MoneyBag(totals);
    }

    /**
     * @return An empty MoneyBag.
     */
    public static MoneyBag emptyBag() {
        return EMPTY_BAG;
    }
}
