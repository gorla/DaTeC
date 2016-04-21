package ch.unisi.inf.datec.analyses;

import java.util.ArrayList;
import java.util.Iterator;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.CallGraphBuilder;
import ch.unisi.inf.datec.data.ClassRegistry;

/**
 * Call graph constructor
 * 
 * @author Alessandra Gorla
 * @author Renzo Russi
 */
public class CallGraphConstructor {

	/**
	 * Application call graph
	 */
	private CallGraph cg;
	
	/**
	 * Create a call graph of all the application methods
	 */
	public CallGraphConstructor(){
		Iterator<SootClass> it = ClassRegistry.getInstance().getClasses().keySet().iterator();
		
		ArrayList<SootMethod> applMethods = new ArrayList<SootMethod>();
		
		while(it.hasNext()){
			ArrayList<SootMethod> csm = ClassRegistry.getInstance().getClassMethods(it.next());
			applMethods.addAll(csm);
		}
		
		Scene.v().setEntryPoints(applMethods);
		new CallGraphBuilder().build();
		this.cg = Scene.v().getCallGraph();
	}

	/**
	 * Create a call graph of the classes contained in the list
	 * @param scs
	 */
	public CallGraphConstructor(ArrayList<SootMethod> scs) {
		Scene.v().setEntryPoints(scs);
		new CallGraphBuilder().build();
		this.cg = Scene.v().getCallGraph();
	}

	/**
	 * Return the application call graph
	 * @return cg
	 */
	public CallGraph getCallGraph() {
		return this.cg;
	}

}
