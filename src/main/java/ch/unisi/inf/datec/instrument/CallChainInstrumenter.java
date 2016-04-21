package ch.unisi.inf.datec.instrument;

import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.IdentityStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.util.Chain;
import ch.unisi.inf.datec.DatecProperties;
import ch.unisi.inf.datec.db.DBinterface;

public class CallChainInstrumenter  extends BodyTransformer {
//	private SootClass coverageCalculator;
//	private SootMethod pushCall, popCall;
	
	static SootClass coverageCalculator;
	static SootMethod pushCall, popCall;

	static {
		coverageCalculator = Scene.v().loadClassAndSupport(
				"ch.unisi.inf.datec.instrument.CoverageCalculator");
		pushCall = coverageCalculator.getMethod("void pushCall(java.lang.String)");
		popCall  = coverageCalculator.getMethod("void popCall(java.lang.String)");
	}

	protected void internalTransform(Body body, String phase, Map options) {

		SootMethod method = body.getMethod();
		
		if(body.getMethod().getDeclaringClass().getName().contains("$"))
			return;
//		coverageCalculator = Scene.v().loadClassAndSupport("ch.unisi.inf.datec.instrument.CoverageCalculator");
//		pushCall = coverageCalculator.getMethod("void pushCall(java.lang.String)");
//		popCall  = coverageCalculator.getMethod("void popCall(java.lang.String)");

		if(!DBinterface.shouldBeInstrumented(method.getSignature())){
			if(DatecProperties.getInstance().isVerbose())
				System.out.println("skipping calls of method : " + method.getSignature());
			return;
		}
		
		// debugging
		if(DatecProperties.getInstance().isVerbose())
			System.out.println("instrumenting calls of method : " + method.getSignature());

		Chain units = body.getUnits();		
		
		InvokeExpr pushCExpr = Jimple.v().newStaticInvokeExpr(pushCall.makeRef(), StringConstant.v(method.getSignature()));
		Stmt pushStmt = Jimple.v().newInvokeStmt(pushCExpr);
		
		Stmt st = (Stmt) units.getFirst();
		while(st instanceof IdentityStmt)
			st = (Stmt) units.getSuccOf(st);
		if(method.getSignature().contains("<init>"))
			units.insertAfter(pushStmt, st);
		else
			units.insertBefore(pushStmt, st);
		
		InvokeExpr popExpr = Jimple.v().newStaticInvokeExpr(popCall.makeRef(), StringConstant.v(method.getSignature()));		
		
		Iterator<Unit> stmtIt = units.snapshotIterator();
		
		// typical while loop for iterating over each statement
		while (stmtIt.hasNext()) {
			// cast back to a statement.
			Stmt stmt = (Stmt) stmtIt.next();
			if(stmt instanceof ReturnStmt || stmt instanceof ReturnVoidStmt){
				Stmt popStmt = Jimple.v().newInvokeStmt(popExpr);
				units.insertBefore(popStmt, stmt);
			}

		}
	}
}
