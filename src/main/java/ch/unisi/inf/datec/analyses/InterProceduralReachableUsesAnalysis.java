package ch.unisi.inf.datec.analyses;

import java.util.ArrayList;
import java.util.Iterator;

import soot.SootMethod;
import soot.jimple.Stmt;
import soot.jimple.toolkits.annotation.purity.AbstractInterproceduralAnalysis;
import soot.jimple.toolkits.annotation.purity.SootMethodFilter;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import ch.unisi.inf.datec.DatecProperties;
import ch.unisi.inf.datec.data.ClassRegistry;
import ch.unisi.inf.datec.data.Use;

/**
 * Interprocedural analysis that computes the reachable uses of the instance variables.
 * Calls intraprocedural analysis (ReachableUses) to compute the reachable uses within a method.
 * 
 * @author Alessandra Gorla
 * @author Renzo Russi
 * 
 */
public class InterProceduralReachableUsesAnalysis extends AbstractInterproceduralAnalysis {
	/**
	 * List of methods that have to be analyzed
	 */
	ArrayList<SootMethod> methodsToAnalyze = ClassRegistry.getInstance().getAllMethodsToAnalyze();
	
	FlowSet emptySet = new ArraySparseSet();
	
	public InterProceduralReachableUsesAnalysis(CallGraph cg, SootMethodFilter filter, Iterator heads, boolean verbose) {
		super(cg, filter, heads, verbose);
		doAnalysis(verbose);
		
		//methods that are still in the list are now analyzed
		for(SootMethod sm:this.methodsToAnalyze){
			if(sm.isAbstract())
				continue;
			UnitGraph graph = new BriefUnitGraph(sm.retrieveActiveBody());
			ReachableUses ru = new ReachableUses(graph);
		}
	}

	/* (non-Javadoc)
	 * @see soot.jimple.toolkits.annotation.purity.AbstractInterproceduralAnalysis#analyseMethod(soot.SootMethod, java.lang.Object)
	 */
	@Override
	protected void analyseMethod(SootMethod method, Object store) {
		FlowSet out = (FlowSet)store;
		UnitGraph graph = new BriefUnitGraph(method.retrieveActiveBody());
		ReachableUses ru = new ReachableUses(graph);
		
		String s = "** Reachable Uses ** "+ method.getName() +" ** \n";
		for(Use u : ru.getFinalList()){
			s = s+u.getVariableName()+"  ";
			s = s+u.getLineNumber()+"\n";
			out.add(u);
		}
		if(DatecProperties.getInstance().isVerbose())
			System.out.println(s+"\n **");
	}

	@Override
	protected void applySummary(Object arg0, Stmt arg1, Object arg2, Object arg3) {

	}

	@Override
	protected void copy(Object arg0, Object arg1) {
		//TODO manage recursion
	}

	@Override
	protected void merge(Object arg0, Object arg1, Object arg2) {

	}

	@Override
	protected Object newInitialSummary() {
		return emptySet.clone();
	}

	@Override
	protected Object summaryOfUnanalysedMethod(SootMethod arg0) {
		return emptySet.clone();
	}

	
	protected Object newInitialFlow() {
		return emptySet.clone();
	}

}
