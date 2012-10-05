package org.paritybits.pantheon.plutus;

import java.text.MessageFormat;

/**
 * Thrown when a attempting to work with two instances of Money that do not have
 * the same Currency.
 *
 * @author Andrew Tillman
 * @version 0.9
 * @see org.paritybits.pantheon.plutus.Money
 */
public class CurrencyMismatchException extends IllegalArgumentException {

    static final long serialVersionUID = -606950293579347433L;

    private final Money firstMoney;
    private final Money secondMoney;

    private static final String MESSAGE_TEMPLATE =
            "Currency mismatch between '{0}' and '{1}'.";

    public CurrencyMismatchException(final Money firstMoney, final Money secondMoney) {
        super(MessageFormat.format(MESSAGE_TEMPLATE, firstMoney, secondMoney));
        this.firstMoney = firstMoney;
        this.secondMoney = secondMoney;
    }

    /**
     * @return The first money in the operation.
     */
    public Money firstMoney() {
        return firstMoney;
    }

    /**
     * @return The second money in the operation.
     */
    public Money secondMoney() {
        return secondMoney;
    }
}
