package ch.unisi.inf.datec.analyses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ch.unisi.inf.datec.DatecProperties;
import ch.unisi.inf.datec.data.ClassRegistry;
import ch.unisi.inf.datec.data.Definition;
import ch.unisi.inf.datec.data.MethodData;
import ch.unisi.inf.datec.data.Use;

import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.FieldRef;
import soot.jimple.ParameterRef;
import soot.jimple.Stmt;
import soot.jimple.ThisRef;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.BackwardFlowAnalysis;
import soot.toolkits.scalar.FlowSet;

/**
 * Performs the intraprocedural reachable uses analysis on a method.
 * 
 * @author Alessandra Gorla
 * @author Renzo Russi
 */
public class ReachableUses extends BackwardFlowAnalysis<Unit, FlowSet> implements Dataflow {
	
	/**
	 * The analyzed method
	 */
	private SootMethod method;
	/**
	 * The empty flowSet to clone
	 */
	private FlowSet emptySet;
	/**
	 * Hashmap that stores per each unit the list of uses (grouped by variable name) that reach the unit
	 */
	private HashMap<Unit, ArrayList<Use>> unitDataFlowAfter;
	/**
	 * Hashmap that stores per each unit the list of uses (grouped by variable name) that might reach out the unit
	 */
	private HashMap<Unit, ArrayList<Use>> unitDataFlowBefore;
	/**
	 * List of uses (grouped by variable name) that might be reachable from the entry node
	 */
	private ArrayList<Use> reachableUses;
	
	private String thisRef = "";
	private HashMap<String,SootField> localRefFieldsMap= new HashMap<String, SootField>();
	private HashMap<String,ParameterRef> localRefParametersMap= new HashMap<String, ParameterRef>();
	
	/**
	 * Class constructor. Perform the analysis
	 * @param graph
	 */
	public ReachableUses(DirectedGraph<Unit> graph) {
		// call the superclass constructor
		super(graph);		
		//initialize fields
		this.method = ((UnitGraph)graph).getBody().getMethod();
		this.emptySet = new ArraySparseSet();	
		this.unitDataFlowAfter = new HashMap<Unit, ArrayList<Use>> (graph.size() * 2 + 1, 0.7f);
		this.unitDataFlowBefore = new HashMap<Unit, ArrayList<Use>> (graph.size() * 2 + 1, 0.7f);
		
		// the first units of the method contain assignments of fields and parameters to local variables.
		// We need to keep track of the original names.
		for(Unit u:graph){
			if(u instanceof JIdentityStmt){
				Value left = ((JIdentityStmt)u).leftBox.getValue();
				String leftName = ((JimpleLocal)left).getName();
				Value right = ((JIdentityStmt)u).rightBox.getValue();
//				if(right instanceof FieldRef){ 
//					SootField f = ((FieldRef)right).getField();
//					this.localRefFieldsMap.put(leftName, f);
//					continue;
//				}
				if(right instanceof ParameterRef){ 
					this.localRefParametersMap.put(leftName, (ParameterRef)right);
					continue;
				}
				if(right instanceof ThisRef){ 
					this.thisRef = leftName;
				}
			}
		}
		
		//perform analysis
		doAnalysis();

		//for each unit, store data flow before and after unit
		List<Unit> unitIt = graph.getHeads();
		for(Unit u : unitIt) {
			FlowSet set = this.getFlowBefore(u);
			this.unitDataFlowBefore.put(u, new ArrayList<Use>(set.toList()));
			set = (FlowSet) this.getFlowAfter(u);
			this.unitDataFlowAfter.put(u, new ArrayList<Use>(set.toList()));			
		}
		
		//calculate the method reachable Uses	
		List<Unit> headIt = graph.getHeads();
		FlowSet reachableUsesSet = emptySet.clone();
		for(Unit u : headIt) {
			FlowSet set = (FlowSet) this.getFlowAfter(u);
			reachableUsesSet.union(set, reachableUsesSet);			
		}
		this.reachableUses = new ArrayList<Use>(reachableUsesSet.toList());		
		
		//store the information in the registry
		HashMap<String, ArrayList<Use>> reachableUsesHM = new HashMap<String, ArrayList<Use>>();

		MethodData md = ClassRegistry.getInstance().getMethodData(method);
		for (Use u : reachableUses) {
			ArrayList<Use> temp = new ArrayList<Use>();
			for(Use u1 : reachableUses) {
				if (u.getName().equals(u1.getName())) {
					if (!temp.contains(u1)) temp.add(u1);
				}
			}
			reachableUsesHM.put(u.getName(), temp);
			md.addUses(reachableUsesHM);
		}
		ClassRegistry.getInstance().addMethod(method, md);
	}
	
	/* (non-Javadoc)
	 * @see soot.toolkits.scalar.FlowAnalysis#flowThrough(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	protected void flowThrough(FlowSet in, Unit node, FlowSet out) {
		kill(in, node, out);
		gen(out, node);
	}

	/**
	 * Compute the uses generated in the unit u and stores them in the flowset outSet
	 * @param outSet The flowset to store the generated uses to
	 * @param u the unit to analyze
	 */
	private void gen(FlowSet outSet, Unit u) {
		// Take uses of called methods, take reachable uses of called method
		// clone them, update context, and add them to outset
		boolean ignoreStaticCalls = DatecProperties.getInstance().isIgnoreStaticCalls();
		if (((Stmt) u).containsInvokeExpr()) {
			SootMethod sm = ((Stmt) u).getInvokeExpr().getMethod();
			
			//Do not analyze if the method call is static and we want to ignore static calls
			if(!sm.isStatic() || !ignoreStaticCalls){

				// objInvoc is the invocation object (first use of the stmt)
				Value objInvoc = null; // will stay null if the called method is
				// static
				String localVarName = ""; // will stay empty if the called methos is
				// static
				if (!sm.isStatic()) {
					objInvoc = ((ValueBox) ((Stmt) u).getInvokeExprBox().getValue()
							.getUseBoxes().get(0)).getValue();
					localVarName = ((JimpleLocal) objInvoc).getName();
				}
				// do it only if the object is this or field
				if (sm.isStatic() || this.thisRef.equals(localVarName)
						|| Utilities.isFieldRef(objInvoc)) {// TODO or is a par
					HashMap<SootMethod, MethodData> hm = ClassRegistry
					.getInstance().getMethods();

					if (hm.containsKey(sm)) {
						MethodData md = hm.get(sm);
						ArrayList<Use> uses = new ArrayList<Use>();
						if (md.getReachableUses() != null) {
							Iterator iterator = md.getReachableUses().keySet()
							.iterator();
							while (iterator.hasNext()) {
								String key = (String) iterator.next();
								ArrayList<Use> value = md.getReachableUses().get(
										key);

								if (value != null) {
									for (Use us : value) {
										Use cloneU = null;
										try {
											cloneU = (Use) us.clone();
										} catch (CloneNotSupportedException e) {
											e.printStackTrace();
										}
										cloneU.addContext(this.method
												.getSignature());
										if (!this.thisRef.equals(localVarName)
												&& !sm.isStatic()) {
											cloneU.setName("*" + cloneU.getName());
											// FIXME name of the field
											// TODO check whether it is necessary to
											// update variable name too
										}
										outSet.add(cloneU);
									}
								}
							}
						}
					}
				}
			}
		}
		
		
		ArrayList<ValueBox> useIt = new ArrayList<ValueBox>(u.getUseBoxes());
		for (ValueBox v : useIt) {
			Value useValue = v.getValue();
			if (useValue instanceof FieldRef){
				String methodName = this.method.getSignature();
				int lineNumber = Utilities.getLineNumber(u);
				Use use = new Use(((FieldRef)useValue).getField().getSignature(),
						((FieldRef)useValue).getField().getName(),
						methodName,
						((FieldRef)useValue).getField().getDeclaringClass().getName(),
						lineNumber);
				use.setId(useValue.hashCode());
				outSet.add(use);
			}
		}
	}

	/**
	 * Compute the uses killed in the unit u and stores them in the flowset outSet
	 * @param outSet The flowset to store the generated uses to
	 * @param u the unit to analyze
	 */
	private void kill(FlowSet inSet, Unit u, FlowSet outSet) {
		FlowSet kills = (FlowSet)emptySet.clone();
		// check whether node contains a call and get the reachable uses and if for each 
		// def of the called method theres a use of same variable in the inset with same name, the use is killed

		boolean ignoreStaticCalls = DatecProperties.getInstance().isIgnoreStaticCalls();
		if(((Stmt)u).containsInvokeExpr()){
			SootMethod sm = ((Stmt)u).getInvokeExpr().getMethod();
			
			//Do not analyze if the method call is static and we want to ignore static calls
			if(!sm.isStatic() || !ignoreStaticCalls){
				// objInvoc is the invocation object (first use of the stmt)
				Value objInvoc = null; // will stay null if the called method is static
				String localVarName = ""; // will stay empty if the called method is static
				if(!sm.isStatic()){
					objInvoc = ((ValueBox)((Stmt)u).getInvokeExprBox().getValue().getUseBoxes().get(0)).getValue();
					localVarName = ((JimpleLocal)objInvoc).getName();
				}
				//do it only if the object is this or field
				if(sm.isStatic() || this.thisRef.equals(localVarName) || Utilities.isFieldRef(objInvoc)){// TODO or is a par

					HashMap<SootMethod, MethodData> hm = ClassRegistry.getInstance().getMethods();
					if(hm.get(sm)!=null){
						MethodData md = hm.get(sm);
						ArrayList<Definition> defs = new ArrayList<Definition>(inSet.toList());
						Iterator iterator = md.getReachableUses().keySet().iterator();			 
						while (iterator.hasNext()) {
							String key = (String) iterator.next();
							ArrayList<Use> value = md.getReachableUses().get(key);

							if (value != null) {
								for (Use us : value) {
									if (defs.contains((us.getName()))) {
										kills.add(us);
									}
								}
							}
						}
					}
				}
			}
		}

		
		ArrayList<ValueBox> defIt = new ArrayList<ValueBox>(u.getDefBoxes());//  unit defs
		for(ValueBox v : defIt) {
			Value defValue = v.getValue();
			if (defValue instanceof FieldRef) {
				
				String methodName = this.method.getSignature();
				int lineNumber = Utilities.getLineNumber(u);
				Definition def = new Definition(((FieldRef)defValue).getField().getSignature(),
						((FieldRef)defValue).getField().getName(),
						methodName,
						((FieldRef)defValue).getField().getDeclaringClass().getName(),
						lineNumber);
				def.addContext(methodName);
				
				ArrayList<Use> inIt = new ArrayList<Use>(inSet.toList());
				
				//for each reaching use
				for(Use inUse : inIt) {
					if(inUse.getVariableName().equals(def.getVariableName())) {
						kills.add(inUse);			
					}
				}
			}
		}
		inSet.difference(kills, outSet);
	}

	@Override
	protected void copy(FlowSet source, FlowSet dest) {
		source.copy(dest);
	}

	@Override
	protected FlowSet entryInitialFlow() {
		return emptySet.clone();
	}

	@Override
	protected void merge(FlowSet in1, FlowSet in2, FlowSet out) {
		in1.union(in2, out);
	}

	@Override
	protected FlowSet newInitialFlow() {
		return emptySet.clone();
	}

	public ArrayList<Use> getDataAfterUnit(Unit s) {
		return this.unitDataFlowAfter.get(s);
	}

	public ArrayList<Use> getDataBeforeUnit(Unit s) {
		return this.unitDataFlowBefore.get(s);
	}

	public ArrayList<Use> getFinalList() {
		return this.reachableUses;
	}

}
