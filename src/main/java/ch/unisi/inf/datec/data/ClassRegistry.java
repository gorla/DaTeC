/**
 * 
 */
package ch.unisi.inf.datec.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import soot.SootClass;
import soot.SootMethod;

/**
 * Singleton class used to store all the classes that have to be analyzed. 
 * 
 * @author Alessandra Gorla
 * @author Renzo Russi
 *
 */
public class ClassRegistry{
	
	/**
	 * Instance of the ClassRegistry
	 */
	private static ClassRegistry INSTANCE = new ClassRegistry();
	/**
	 * HashMap that contains all the classes that have to be analyzed. 
	 */
	private HashMap<SootClass, ArrayList<SootMethod>> classesHM = new HashMap<SootClass, ArrayList<SootMethod>>();
	/**
	 * HashMap that stores all the method data after the analysis. 
	 */
	private HashMap<SootMethod, MethodData> methodsHM = new HashMap<SootMethod, MethodData>();
	/**
	 * HashMap storing the associations (def, use pairs) in each class
	 */
	private HashMap<SootClass, ArrayList<Association>> associationsHM = new HashMap<SootClass, ArrayList<Association>>();

	/**
	 * Private constructor to prevent the creation of other ClassRegistry instances.
	 */
	private ClassRegistry() { }
	
	/**
	 * Load an existing Registry into memory
	 * @param c
	 */
	public static void loadRegistry(ClassRegistry c){
		INSTANCE = c;
	}
	
	/**
	 * @return The ClassRegistry instance
	 */
	public static ClassRegistry getInstance(){
		return INSTANCE;
	}
	
	/**
	 * Reset the analysis registry (necessary if you want to run analysis twice)
	 */
	public static void reset(){
		INSTANCE = new ClassRegistry();
	}
	
	public void addMethod(SootMethod sm, MethodData md) {
		this.methodsHM.put(sm, md);
	}
	
	public void removeMethod(SootMethod sm) {
		this.methodsHM.remove(sm);
	}
	
	public void clearAllMethods(){
		this.methodsHM.clear();
	}
	
	public void addClass(SootClass sc){
		ArrayList<SootMethod> sm = new ArrayList<SootMethod>(sc.getMethods());
		this.classesHM.put(sc, sm);
	}
	
	public void clearAllClasses(){
		this.classesHM.clear();
	}
	
	public HashMap<SootClass, ArrayList<SootMethod>> getClasses() {
		return this.classesHM;
	}
	
	public ArrayList<SootMethod> getClassMethods(SootClass sc){
		return this.classesHM.get(sc);
	}
	
	public HashMap<SootMethod, MethodData> getMethods() {
		return this.methodsHM;
	}
	
	public HashMap<SootClass, ArrayList<Association>> getAssociations() {
		return this.associationsHM;
	}
	
	public void addAssociation(SootClass sc, ArrayList<Association> a) {
		this.associationsHM.put(sc, a);
	}
	
	public boolean containsClass(SootClass sc){
		return this.classesHM.containsKey(sc);
	}
	
	/**
	 * Returns a SootMethod given the method name
	 * @param methodName The complete signature of the method, e.g.
	 * (<classes.IntraProceduralReachingDefs: void m(int)>)
	 * @return The SootMethod object
	 */
	public SootMethod getMethod(String methodName){
		for(SootClass sc:classesHM.keySet()){
			ArrayList<SootMethod> methods = classesHM.get(sc);
			for(SootMethod m : methods){
				if(m.toString().equals(methodName))
					return m;
			}
		}
		return null;
	}
	
	public MethodData getMethodData(SootMethod sm){
		return this.methodsHM.get(sm);
	}
	
	public boolean containsMethod(SootMethod sm){
		SootClass sc = sm.getDeclaringClass();
		if(!classesHM.containsKey(sc))
			return false;
		return classesHM.get(sc).contains(sm);
	}
	
	/**
	 * Returns all the methods to analyze
	 * @return list of methods
	 */
	public ArrayList<SootMethod> getAllMethodsToAnalyze(){
		ArrayList<SootMethod> m = new ArrayList<SootMethod>();
		for(ArrayList<SootMethod> a:this.classesHM.values()){
			m.addAll(a);
		}
		
		return m;
	}

}
