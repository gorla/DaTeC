/**
 * 
 */
package ch.unisi.inf.datec.analyses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import soot.Local;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.ParameterRef;
import soot.jimple.Stmt;
import soot.jimple.ThisRef;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JimpleLocal;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;
import soot.util.Chain;
import ch.unisi.inf.datec.DatecProperties;
import ch.unisi.inf.datec.data.ClassRegistry;
import ch.unisi.inf.datec.data.Definition;
import ch.unisi.inf.datec.data.MethodData;

/**
 * Performs the intraprocedural reaching definitions analysis on a method.
 * 
 * @author Alessandra Gorla
 * @author Renzo Russi
 * 
 */
public class ReachingDefinitions extends ForwardFlowAnalysis<Unit, FlowSet>
		implements Dataflow {

	private SootMethod method;
	private FlowSet emptySet;
	private HashMap<Unit, ArrayList<Definition>> unitDataFlowAfter;
	private HashMap<Unit, ArrayList<Definition>> unitDataFlowBefore;
	private ArrayList<Definition> reachingOutDefinitions;
	private String thisRef = "";
	private HashMap<String,SootField> localRefFieldsMap= new HashMap<String, SootField>();
	private HashMap<String,ParameterRef> localRefParametersMap= new HashMap<String, ParameterRef>();

	public ReachingDefinitions(DirectedGraph<Unit> graph) {
		// call the superclass constructor
		super(graph);
		// initialize fields
		this.method = ((UnitGraph) graph).getBody().getMethod();
		this.emptySet = new ArraySparseSet();
		this.unitDataFlowAfter = new HashMap<Unit, ArrayList<Definition>>(graph
				.size() * 2 + 1, 0.7f);
		this.unitDataFlowBefore = new HashMap<Unit, ArrayList<Definition>>(
				graph.size() * 2 + 1, 0.7f);
		
		// the first units of the method contain assignments of fields and parameters to local variables.
		// We need to keep track of the original names.
//		for (Local ll:method.getActiveBody().getLocals()){
//			String namelocal = ((JimpleLocal)ll).getName();
//			for(SootField sf:method.getDeclaringClass().getFields()){
//				int hclocal = ll.equivHashCode();
//				int hcfield = sf.equivHashCode();
//				boolean jj = ((JimpleLocal)ll).equivTo(sf);
//			}
//		}
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
		
		// perform analysis
		doAnalysis();

		// for each unit, store data flow before and after unit
		ArrayList<Unit> unitIt = new ArrayList<Unit>(graph.getHeads());
		for (Unit u : unitIt) {
			FlowSet set = (FlowSet) this.getFlowBefore(u);
			this.unitDataFlowBefore.put(u, new ArrayList<Definition>(set
					.toList()));
			set = (FlowSet) this.getFlowAfter(u);
			this.unitDataFlowAfter.put(u, new ArrayList<Definition>(set
					.toList()));
		}

		// calculate the method reaching out definitions
		ArrayList<Unit> exitUnit = new ArrayList<Unit>(graph.getTails());
		FlowSet reachingOutDefs = emptySet.clone();
		for (Unit u : exitUnit) {
			FlowSet set = this.getFlowAfter(u);
			reachingOutDefs.union(set, reachingOutDefs);
		}
		this.reachingOutDefinitions = new ArrayList<Definition>(reachingOutDefs
				.toList());

		// remove dummy definitions
		ArrayList<Definition> dummyar = new ArrayList<Definition>();
		ArrayList<String> aliveDummyDefs = new ArrayList<String>();
		for (Definition dummy : this.reachingOutDefinitions) {
			if (dummy.getLineNumber() == -1) {
				dummyar.add(dummy);
				aliveDummyDefs.add(dummy.getName());
			}
		}
		this.reachingOutDefinitions.removeAll(dummyar);

		// store the information in the registry
		HashMap<String, ArrayList<Definition>> reachingDefs = new HashMap<String, ArrayList<Definition>>();

		MethodData md = new MethodData();
		for (Definition d : reachingOutDefinitions) {
			ArrayList<Definition> temp = new ArrayList<Definition>();
			for (Definition d1 : reachingOutDefinitions) {
				if (d.getName().equals(d1.getName())) {
					if (!temp.contains(d1))
						temp.add(d1);
				}
			}
			reachingDefs.put(d.getName(), temp);
			md.addDefinitions(reachingDefs);
		}
		md.setAliveDummyDefs(aliveDummyDefs);
		ClassRegistry.getInstance().addMethod(method, md);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see soot.toolkits.scalar.FlowAnalysis#flowThrough(java.lang.Object,
	 *      java.lang.Object, java.lang.Object)
	 */
	@Override
	protected void flowThrough(FlowSet in, Unit node, FlowSet out) {
		// out <- (in - expr containing locals defined in d) union out
		kill(in, node, out);
		// out <- out union expr used in d
		gen(out, node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see soot.toolkits.scalar.AbstractFlowAnalysis#copy(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	protected void copy(FlowSet source, FlowSet dest) {
		source.copy(dest);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see soot.toolkits.scalar.AbstractFlowAnalysis#entryInitialFlow()
	 */
	@Override
	protected FlowSet entryInitialFlow() {
		// for each field a dummy definition is created. If the dummy definition
		// reaches the end of the method
		// it means that there is a def free from the entry to the exit
		FlowSet entry = emptySet.clone();
		Chain fields = this.method.getDeclaringClass().getFields();
		Iterator<SootField> it = fields.iterator();
		int count = 0;
		while (it.hasNext()) {
			SootField sf = it.next();
			String fieldSign = sf.getSignature();
			String varName = sf.getName();
			Definition dummy = new Definition(fieldSign, varName, "", "", -1);
			dummy.setId(-1 + count);
			entry.add(dummy);
			count++;
		}
		return entry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see soot.toolkits.scalar.AbstractFlowAnalysis#merge(java.lang.Object,
	 *      java.lang.Object, java.lang.Object)
	 */
	@Override
	protected void merge(FlowSet in1, FlowSet in2, FlowSet out) {
		in1.union(in2, out);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see soot.toolkits.scalar.AbstractFlowAnalysis#newInitialFlow()
	 */
	@Override
	protected FlowSet newInitialFlow() {
		return emptySet.clone();
	}

	/**
	 * Performs kills by generating a killSet and then performing<br/> outSet <-
	 * inSet - killSet<br/> The kill set is generated by iterating over the
	 * def-boxes of the unit. For each local defined in the unit we iterate over
	 * the definitions in the inSet.
	 * 
	 * @param inSet
	 *            the set flowing into the unit
	 * @param u
	 *            the unit being flown through
	 * @param outSet
	 *            the set flowing out of the unit
	 */
	private void kill(FlowSet inSet, Unit u, FlowSet outSet) {
		FlowSet kills = emptySet.clone();
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
					if (hm.get(sm) != null) {
						MethodData md = hm.get(sm);
						ArrayList<Definition> defs = new ArrayList<Definition>(
								inSet.toList());
						for (Definition d : defs) {
							if (md.getReachingDefs().containsKey(d.getName())) {
								if (!md.getAliveDummyDefs().contains(d.getName()))
									kills.add(d);
							}
						}
					}
				}
			}
		}

		ArrayList<ValueBox> defIt = new ArrayList<ValueBox>(u.getDefBoxes());// unit
		// defs
		for (ValueBox v : defIt) {
			Value defValue = v.getValue();

			if (defValue instanceof FieldRef) {
				String methodName = this.method.getSignature();
				int lineNumber = Utilities.getLineNumber(u);
				Definition unitDef = new Definition(((FieldRef) defValue)
						.getField().getSignature(), ((FieldRef) defValue)
						.getField().getName(), methodName,
						((FieldRef) defValue).getField().getDeclaringClass()
								.getName(), lineNumber);
				unitDef.addContext(methodName);

				// for each reaching def
				ArrayList<Definition> inList = new ArrayList<Definition>(inSet.toList());
				for (Definition d : inList) {
					Definition inDef = d;
					if (inDef.getVariableName().equals(
							unitDef.getVariableName()))
						kills.add(inDef);
				}
			}
		}
		inSet.difference(kills, outSet);
	}

	/**
	 * Performs gens by iterating over the units use-boxes. If the value of a
	 * use-box is a binopExp then we add it to the outSet.
	 * 
	 * @param outSet
	 *            the set flowing out of the unit
	 * @param u
	 *            the unit being flown through
	 */
	private void gen(FlowSet outSet, Unit u) {
		// if there is a method call, get the reaching defs of the called method
		// CLONE THEM and . after cloning update the context(add method name to
		// context)
		// add to outSet.

		// TODO substitute formal parameters defs with actual parameters.
		// substitute ONLY if actual par is non primitive and is a field.
		boolean ignoreStaticCalls = DatecProperties.getInstance().isIgnoreStaticCalls();
		if (((Stmt) u).containsInvokeExpr()) {
			SootMethod sm = ((Stmt) u).getInvokeExpr().getMethod();
			
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
					HashMap<SootMethod, MethodData> hm = ClassRegistry
					.getInstance().getMethods();
					MethodData md = hm.get(sm);

					if (md != null && md.getReachingDefs() != null) {
						Iterator iterator = md.getReachingDefs().keySet()
						.iterator();
						while (iterator.hasNext()) {
							String key = (String) iterator.next();
							ArrayList<Definition> value = (ArrayList<Definition>) md
							.getReachingDefs().get(key);

							if (value != null) {
								for (Definition d : value) {
									Definition cloneD = null;
									try {
										cloneD = (Definition) d.clone();
									} catch (CloneNotSupportedException e) {
										e.printStackTrace();
									}
									cloneD.addContext(this.method.getSignature());
									if(!this.thisRef.equals(localVarName) && !sm.isStatic()){
										cloneD.setName("*"+cloneD.getName());
										//FIXME name of the field
										//TODO check whether it is necessary to update variable name too
									}
									outSet.add(cloneD);
								}
							}
						}
					}
				}
			}
		}

		ArrayList<ValueBox> defIt = new ArrayList<ValueBox>(u.getDefBoxes());
		for (ValueBox v : defIt) {
			Value defValue = v.getValue();
			// TODO consider also formal parameters defs (only if the par is non
			// primitive)
			// put these definitions in the same flowset, but tag them in a
			// different way
			// (add a field to the UseDef class to indicate whether the def is a
			// formal parameter or not)
			if (defValue instanceof FieldRef) {
				String methodName = this.method.getSignature();
				int lineNumber = Utilities.getLineNumber(u);
				Definition def = new Definition(((FieldRef) defValue)
						.getField().getSignature(), ((FieldRef) defValue)
						.getField().getName(), methodName,
						((FieldRef) defValue).getField().getDeclaringClass()
						.getName(), lineNumber);
				def.setId(defValue.hashCode()); 
				outSet.add(def);
			}
		}
	}

	public ArrayList<Definition> getDataAfterUnit(Unit s) {
		return this.unitDataFlowAfter.get(s);
	}

	public ArrayList<Definition> getDataBeforeUnit(Unit s) {
		return this.unitDataFlowBefore.get(s);
	}

	public ArrayList<Definition> getFinalList() {
		return this.reachingOutDefinitions;
	}

}
