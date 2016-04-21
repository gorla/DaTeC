/**
 * 
 */
package ch.unisi.inf.datec.analyses;

import java.util.ArrayList;
import java.util.HashMap;

import soot.SootClass;
import soot.SootMethod;
import ch.unisi.inf.datec.data.Association;
import ch.unisi.inf.datec.data.ClassRegistry;
import ch.unisi.inf.datec.data.Definition;
import ch.unisi.inf.datec.data.MethodData;
import ch.unisi.inf.datec.data.Use;

/**
 * Def-use associations builder
 * 
 * @author Renzo Russi
 * @author Alessandra Gorla
 *
 */
public class AssociationsBuilder {
	
	/**
	 * Build the def-use associations for a given class
	 * @param sc The class
	 */
	public static void build(SootClass sc){
		ArrayList<Association> as = new ArrayList<Association>();
		ArrayList<SootMethod> methodList = ClassRegistry.getInstance().getClasses().get(sc);
		ArrayList<SootMethod> methodList2 = ClassRegistry.getInstance().getClasses().get(sc);
		/* for each method */
		for (SootMethod s : methodList) {
			// if s is private or protected ignore it
			if (s.isPrivate() || s.isProtected())
				continue;
			for (SootMethod s2 : methodList2) {
				//if s2 is private or protected or is constructor
				if (s2.isPrivate() || s2.isProtected() || s2.getName().contains("<init>"))
					continue;
				MethodData data = ClassRegistry.getInstance().getMethods().get(s);
				MethodData data2 = ClassRegistry.getInstance().getMethods().get(s2);
				
				if(data == null || data2 == null)
					continue;
				HashMap<String, ArrayList<Definition>> defsHM = data.getReachingDefs();
				HashMap<String, ArrayList<Use>> usesHM = data2.getReachableUses();
				
				for(String defName:defsHM.keySet()){
	    			ArrayList<Definition> listDefs = defsHM.get(defName);
	    			ArrayList<Use> listUses = usesHM.get(defName);
	    			if(listDefs == null || listUses == null)
	    				continue;
	    			for(Definition d:listDefs){
	    				for(Use u:listUses){
	    					as.add(new Association(d, u));
	    				}
	    			}
	    		}
				
			}
		}
		// add association to registry
		ClassRegistry.getInstance().addAssociation(sc, as);
	}
}
