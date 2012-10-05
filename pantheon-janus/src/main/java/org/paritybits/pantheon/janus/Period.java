package org.paritybits.pantheon.janus;

import org.paritybits.pantheon.common.Immutable;
import org.paritybits.pantheon.common.Rangeable;

import java.util.Date;

/**
 * This interface represents a discrete large grained period of time.  Periods are expected to
 * be Immutable and to be Serializable.
 * 
 * @author Andrew Tillman
 * @version 0.9
 */
public interface Period<T extends Period> extends Rangeable<T> {
	
	/**
	 * @return The beginning of the period.
	 */
	Date start();
	
	/**
	 * @return The end of the period.
	 */
	Date stop();
}
