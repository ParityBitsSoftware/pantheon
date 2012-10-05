package org.paritybits.pantheon.plutus.returns;

import org.paritybits.pantheon.common.Percentage;
import org.paritybits.pantheon.janus.Period;

/**
 * This iterface represents an object that can calculate it's returns for a 
 * given period.  This could be an interest rate, a security investment, etc.
 * 
 * @author Andrew Tillman
 * @version 0.9
 */
public interface Returns {

	/**
	 * @param period The period of the returns
	 * @return The return for the given period.
	 */
	Percentage returnForPeriod(Period period);
}
