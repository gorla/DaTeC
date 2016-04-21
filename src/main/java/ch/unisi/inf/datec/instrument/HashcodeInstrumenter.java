package ch.unisi.inf.datec.instrument;

import java.util.Map;

import ch.unisi.inf.datec.DatecProperties;
import ch.unisi.inf.datec.db.DBinterface;

import soot.Body;
import soot.BodyTransformer;
import soot.IntType;
import soot.Local;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.IdentityStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.util.Chain;

public class HashcodeInstrumenter extends BodyTransformer {

	static SootClass thisClass;
	static SootMethod thisHashcode;
	
	@Override
	protected void internalTransform(Body body, String phase, Map options) {
		
		if(body.getMethod().getDeclaringClass().getName().contains("$"))
			return;
		
		thisClass = body.getMethod().getDeclaringClass();
		SootClass superClass = thisClass.getSuperclass();

		if (superClass.declaresMethodByName("int hashCode()"))
			thisClass = superClass;
		else
			thisClass = Scene.v().loadClassAndSupport("java.lang.Object");
		thisHashcode = thisClass.getMethod("int hashCode()");
	
		SootMethod method = body.getMethod();
		
		if(!DBinterface.shouldBeInstrumented(method.getSignature())){
			if(DatecProperties.getInstance().isVerbose())
				System.out.println("skipping hshcode of method : " + method.getSignature());
			return;
		}
		
		// debugging
		if(DatecProperties.getInstance().isVerbose())
			System.out.println("instrumenting hashcode of method : " + method.getSignature());

		// Create local var hc to save hashcode value;
		Local hc = Jimple.v().newLocal("hc-datec", IntType.v());
		
		body.getLocals().add(hc);

		// Create invoke expression: this.hashCode()
		InvokeExpr hashcode = Jimple.v().newSpecialInvokeExpr(
				(Local) body.getLocals().getFirst(), thisHashcode.makeRef());

		// Create statement: hc = this.hashCode();
		Stmt assignmentHC = null;
		if(method.isStatic())
			assignmentHC = Jimple.v().newAssignStmt(hc, IntConstant.v(1));
		else
			assignmentHC = Jimple.v().newAssignStmt(hc, hashcode);		

		Chain units = body.getUnits();

		Stmt st = (Stmt) units.getFirst();
		while(st instanceof IdentityStmt)
			st = (Stmt) units.getSuccOf(st);
		if(method.getSignature().contains("<init>"))
			units.insertAfter(assignmentHC, st);
		else
			units.insertBefore(assignmentHC, st);
	}
}
