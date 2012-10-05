package org.paritybits.pantheon.plutus.returns;

import org.paritybits.pantheon.common.Immutable;
import org.paritybits.pantheon.common.Percentage;
import org.paritybits.pantheon.janus.Period;

import java.io.Serializable;

/**
 * Represents a fixed rate of return.
 *
 * @author Andrew Tillman
 * @version 0.9
 */
@Immutable
public final class FixedRate implements Returns, Serializable, Comparable<FixedRate> {

    static final long serialVersionUID = 8616619230809577110L;

    private final Percentage rate;

    /**
     * Required by Serializable
     */
    protected FixedRate() {
        this.rate = null;
    }

    /**
     * Creates a new FixedReturn
     *
     * @param percentage The amount of the fixed return.
     */
    private FixedRate(final Percentage percentage) {
        this.rate = percentage;
    }

    /**
     * @param period Ignored by this implementation.
     * @return The rate of this FixedReturn.  The same rate will be returned by
     *         every call to this method.
     */
    public Percentage returnForPeriod(final Period period) {
        return rate;
    }

    /**
     * Compares this FixedRate to another FixedRate.
     *
     * @param other
     * @return -1, 0, or 1 depending on the result of the comparision.
     */
    public int compareTo(FixedRate other) {
        return rate.compareTo(other.rate);
    }


    /**
     * @param o The object to test against.
     * @return true if o is an instance of FixedRate and the rate of the specified FixedRate
     *         and the given FixedRate are the same
     */
    public boolean equals(Object o) {
        return ((o instanceof FixedRate) &&
                rate.equals(((FixedRate) o).rate));
    }

    public int hashCode() {
        return rate.hashCode();
    }

    /**
     * @return A formatted string of this rate; "FixedRate of [rate]"
     */
    public String toString() {
        return new StringBuilder("FixedRate of ").append(rate).toString();
	}

    /**
     * Creates a a fixed rate with the given rate amount.
     *
     * @param rate The rate.
     * @return A FixedRate that represents the given rate.
     */
    public static FixedRate create(Percentage rate) {
        return new FixedRate(rate);
    }
	
}
