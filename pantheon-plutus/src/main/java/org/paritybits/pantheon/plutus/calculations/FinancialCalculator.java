package org.paritybits.pantheon.plutus.calculations;

import org.paritybits.pantheon.common.Range;
import org.paritybits.pantheon.janus.Period;
import org.paritybits.pantheon.plutus.MonetaryValue;
import org.paritybits.pantheon.plutus.returns.Returns;


/**
 * DOCUMENTATION!!
 */
public class FinancialCalculator {

    public static <T extends MonetaryValue> T compoundedReturns(final Returns returns,
                                                                final Range<? extends Period> overPeriod,
                                                                final T startingValue) {
        MonetaryValue value = startingValue;
		for(Period period : overPeriod) {
			value = value.compound(returns.returnForPeriod(period));
		}
		return (T)value;
    }
}
