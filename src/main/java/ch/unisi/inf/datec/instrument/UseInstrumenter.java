package ch.unisi.inf.datec.instrument;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.FieldRef;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.util.Chain;
import ch.unisi.inf.datec.DatecProperties;
import ch.unisi.inf.datec.analyses.Utilities;
import ch.unisi.inf.datec.db.DBinterface;

public class UseInstrumenter extends BodyTransformer {

	static SootClass coverageCalculator;
	static SootMethod checkUse;

	static {
		coverageCalculator = Scene.v().loadClassAndSupport(
				"ch.unisi.inf.datec.instrument.CoverageCalculator");
		checkUse = coverageCalculator.getMethod("void checkUse(java.lang.String,int)");
	}

	protected void internalTransform(Body body, String phase, Map options) {

		SootMethod method = body.getMethod();
		if(body.getMethod().getDeclaringClass().getName().contains("$"))
			return;
		
		coverageCalculator = Scene.v().loadClassAndSupport("ch.unisi.inf.datec.instrument.CoverageCalculator");
		checkUse = coverageCalculator.getMethod("void checkUse(java.lang.String,int)");

		if(!DBinterface.shouldBeInstrumented(method.getSignature())){
			if(DatecProperties.getInstance().isVerbose())
				System.out.println("skipping uses of method : " + method.getSignature());
			return;
		} 
		// debugging
		if(DatecProperties.getInstance().isVerbose())
			System.out.println("instrumenting uses of method : " + method.getSignature());

		Chain units = body.getUnits();
		Iterator<Unit> stmtIt = units.snapshotIterator();
		
		Local hc = body.getLocals().getLast();
		// typical while loop for iterating over each statement
		while (stmtIt.hasNext()) {

			// cast back to a statement.
			Stmt stmt = (Stmt) stmtIt.next();
			
			//If the statement contains a field ref...
			if (stmt.containsFieldRef()) {
				ArrayList<ValueBox> useIt = new ArrayList<ValueBox>(stmt.getUseBoxes());
				for (ValueBox v : useIt) {
					// ignore non field defs
					if (!(v.getValue() instanceof FieldRef))
						continue;
					
					String id = "U-"+((FieldRef) v.getValue()).getField().getSignature()+"-"+method.getSignature()+"-"+Utilities.getLineNumber(stmt);
					
					if(!DBinterface.shouldBeInstrumented(id, 2)){
						if(DatecProperties.getInstance().isVerbose())
							System.out.println("skipping use: "+id);
						continue;
					}		

//					int id = v.getValue().hashCode();

					InvokeExpr checkUExpr = Jimple.v().newStaticInvokeExpr(
							checkUse.makeRef(), StringConstant.v(id), hc);

					Stmt checkUStmt = Jimple.v().newInvokeStmt(checkUExpr);
					units.insertBefore(checkUStmt, stmt);
				}
			}
		}
	}
}
