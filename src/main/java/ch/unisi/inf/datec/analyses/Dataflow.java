/**
 * 
 */
package ch.unisi.inf.datec.analyses;

import java.util.ArrayList;

import soot.Unit;

/**
 * Provides an interface for querying the data flow before and after method
 * units and at the end of the method analysis.
 * 
 * @author Alessandra Gorla
 * @author Renzo Russi
 * 
 */
public interface Dataflow {

	/**
	 * Returns the data that might reach the unit u.
	 * 
	 * @param s
	 *            The unit
	 * @return List of reaching data
	 */
	public ArrayList getDataBeforeUnit(Unit s);

	/**
	 * Returns the data that might reach the end of the unit u.
	 * 
	 * @param s
	 *            The unit
	 * @return List of reaching out data
	 */
	public ArrayList getDataAfterUnit(Unit s);

	/**
	 * Returns the final sets at the end of the analysis. In forward analysis
	 * the union of exit points sets is returned, while in backward analysis the
	 * entry point set is returned.
	 * 
	 * @return List of data at the end of the analysis. Reaching out definitions
	 *         (when implemented by ReachingDefinitions) or reachable uses (when
	 *         implemented by ReachableUses)
	 */
	public ArrayList getFinalList();

}
