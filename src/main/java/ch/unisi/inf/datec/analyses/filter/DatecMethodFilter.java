package ch.unisi.inf.datec.analyses.filter;

import soot.SootMethod;
import soot.jimple.toolkits.annotation.purity.SootMethodFilter;
import ch.unisi.inf.datec.data.ClassRegistry;

/**
 * Methods filter for the construction of the call graph
 * 
 * @author Alessandra Gorla
 * @author Renzo Russi
 */
public class DatecMethodFilter implements SootMethodFilter{
	/* (non-Javadoc)
	 * @see soot.jimple.toolkits.annotation.purity.SootMethodFilter#want(soot.SootMethod)
	 */
	public boolean want(SootMethod m) {
		return ClassRegistry.getInstance().containsMethod(m);
	}
}