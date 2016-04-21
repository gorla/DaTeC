package ch.unisi.inf.datec.data;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Class used to store all the information of a method during the analysis
 * 
 * @author Alessandra Gorla
 * 
 */
public class MethodData {
	
	/**
	 * List of reaching definitions (grouped by variable name)
	 */
	private HashMap <String, ArrayList<Definition>> reachingDefinitionsHM;
	/**
	 * List of reachable uses (grouped by variable name)
	 */
	private HashMap <String, ArrayList<Use>> reachableUsesHM;
	/**
	 * List of reaching definitions of the formal parameters (grouped by parameter name)
	 * TODO finish implementation
	 */
	private HashMap<String, ArrayList<Definition>> formalParamsReachingDefinitionsHM;
	/**
	 * List of reachable uses of the formal parameters (grouped by parameter name)
	 * TODO finish implementation
	 */
	private HashMap<String, ArrayList<Use>> formalParamsReachableUsesHM;
	/**
	 * List of dummy definitions still alive at the end of the analysis. 
	 * This information is used during the interprocedural analysis
	 */
	private ArrayList<String> aliveDummyDefs;
	
	/**
	 * Class constructor
	 * @param def the list of reching definitions
	 * @param use the list of reachable uses
	 */
	public MethodData(HashMap<String, ArrayList<Definition>> def, HashMap<String, ArrayList<Use>> use) {
		this.reachingDefinitionsHM = def;
		this.reachableUsesHM = use;
	}
	
	/**
	 * Class constructor
	 */
	public MethodData() {
		this.reachingDefinitionsHM = new HashMap<String, ArrayList<Definition>>();
		this.reachableUsesHM = new HashMap<String, ArrayList<Use>>();
	}
	
	/**
	 * Add definitions to the list of reaching definitions
	 * @param def the definitions to add
	 */
	public void addDefinitions(HashMap<String, ArrayList<Definition>> def) {
		this.reachingDefinitionsHM = def;
	}
	
	/**
	 * Add uses to the list of reachable uses
	 * @param uses the uses to add
	 */
	public void addUses(HashMap<String, ArrayList<Use>> uses) {
		this.reachableUsesHM = uses;
	}
	
	/**
	 * Add definitions to the list of formal parameters reaching definitions
	 * @param def the definitions to add
	 */
	public void addFormalDefinitions(HashMap<String, ArrayList<Definition>> def) {
		this.formalParamsReachingDefinitionsHM = def;
	}
	
	/**
	 * Add uses to the list of reachable uses of the formal parameters
	 * @param uses the uses to add 
	 */
	public void addFormalUses(HashMap<String, ArrayList<Use>> uses) {
		this.formalParamsReachableUsesHM = uses;
	}
	
	/**
	 * Get the list of reaching definitions
	 * @return the definitions
	 */
	public HashMap <String, ArrayList<Definition>> getReachingDefs() {
		return this.reachingDefinitionsHM;
	}
	
	/**
	 * Get the list of reachable uses
	 * @return the uses
	 */
	public HashMap <String, ArrayList<Use>> getReachableUses() {
		return this.reachableUsesHM;
	}

	/**
	 * Get the list of definitions of formal parameters
	 * @return the definitions
	 */
	public HashMap <String, ArrayList<Definition>> getFormalReachingDefs() {
		return this.formalParamsReachingDefinitionsHM;
	}
	
	/**
	 * Get the list of uses of the formal parameters
	 * @return the uses
	 */
	public HashMap <String, ArrayList<Use>> getFormalReachableUses() {
		return this.formalParamsReachableUsesHM;
	}
	
	/**
	 * @return the aliveDummyDefs
	 */
	public ArrayList<String> getAliveDummyDefs() {
		return aliveDummyDefs;
	}

	/**
	 * @param aliveDummyDefs the aliveDummyDefs to set
	 */
	public void setAliveDummyDefs(ArrayList<String> aliveDummyDefs) {
		this.aliveDummyDefs = aliveDummyDefs;
	}
}
