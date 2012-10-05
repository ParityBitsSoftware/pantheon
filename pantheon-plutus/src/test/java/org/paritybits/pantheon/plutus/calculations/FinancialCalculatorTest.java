package org.paritybits.pantheon.plutus.calculations;

import org.junit.Test;
import org.paritybits.pantheon.common.Percentage;
import org.paritybits.pantheon.common.Range;
import org.paritybits.pantheon.janus.simple.Month;
import org.paritybits.pantheon.plutus.Money;
import org.paritybits.pantheon.plutus.returns.FixedRate;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class FinancialCalculatorTest {

    @Test
    public void compoundedReturns() {
        FixedRate rate = FixedRate.create(Percentage.valueOf("10%"));
        Month period = Month.create(new Date());
        Range<Month> range = Range.create(period, period);
        Money startingValue = Money.valueOf("100 USD");
        assertEquals(Money.valueOf("110 USD"), FinancialCalculator.compoundedReturns(rate, range, startingValue));
        range = Range.create(period, period.next());
        assertEquals(Money.valueOf("121 USD"), FinancialCalculator.compoundedReturns(rate, range, startingValue));
    }

}
