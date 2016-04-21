package ch.unisi.inf.datec.instrument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.StringTokenizer;

import ch.unisi.inf.datec.DatecProperties;
import ch.unisi.inf.datec.db.DBinterface;

public class CoverageCalculator {
	
	/**
	 * Execution context
	 */
	private static Stack<String> context = new Stack<String>();
	private static HashMap<Integer,ArrayList<Integer>> pairsCoverage = new HashMap<Integer, ArrayList<Integer>>();
	private static HashMap<Integer,ArrayList<Integer>> nonCtxPairsCoverage = new HashMap<Integer, ArrayList<Integer>>();
	private static boolean propertiesFileRead = false;

	/**
	 * When this method is called, it means that an interesting definition has been covered.
	 * Thus we start monitoring all the associations that contain that definition and we bring
	 * them to the state 'Definition covered'.
	 * 
	 * @param defid the id of the covered definition
	 * @param hashcodeObj the hashcode number of the object
	 */
	public static void checkDef(String defid, int hashcodeObj){
		readPropertiesFile();
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		StringTokenizer st = new StringTokenizer(defid, "-");
		st.nextToken();
		String field = st.nextToken();
		
		// contextual pairs
		ArrayList<Integer> killedPairs = DBinterface.getAllKilledPairs(field,true);
		for(Integer i:killedPairs){
			if(pairsCoverage.containsKey(i)){
				ArrayList<Integer> objects = pairsCoverage.get(i);
				int index = objects.indexOf(hashcodeObj);
				if(index!= -1){
					objects.remove(index);
					if(DatecProperties.getInstance().isVerbose())
						System.out.println("Def id ctxpair" + i + ", hc " + hashcodeObj + " killed!!");
				}
				pairsCoverage.put(i, objects);				
			}
		}
		
		// non contextual pairs
		killedPairs = DBinterface.getAllKilledPairs(field,false);
		for(Integer i:killedPairs){
			if(nonCtxPairsCoverage.containsKey(i)){
				ArrayList<Integer> objects = nonCtxPairsCoverage.get(i);
				int index = objects.indexOf(hashcodeObj);
				if(index!= -1){
					objects.remove(index);
					if(DatecProperties.getInstance().isVerbose())
						System.out.println("Def id nncontextualpair" + i + ", hc " + hashcodeObj + " killed!!");
				}
				nonCtxPairsCoverage.put(i, objects);				
			}
		}
		
		//contextual pairs
		ArrayList<Integer> pairs = DBinterface.getAllPairs(defid, getContextAsString(), true);
		for(Integer id:pairs){
			ArrayList<Integer> hcList = null;
			if(pairsCoverage.get(id) == null)
				hcList = new ArrayList<Integer>();
			else				
				hcList = pairsCoverage.get(id);			
			hcList.add(hashcodeObj); 
			pairsCoverage.put(id, hcList);
			if(DatecProperties.getInstance().isVerbose())
				System.out.println("Def id ctxpair" + id + ", hc " + hashcodeObj + " covered!!");
		}
		
		// non contextual pairs
		ArrayList<Integer> nonCtxPairs = DBinterface.getAllPairs(defid, getContextAsString(), false); 
		for(Integer id:nonCtxPairs){
			ArrayList<Integer> hcList = null;
			if(nonCtxPairsCoverage.get(id) == null)
				hcList = new ArrayList<Integer>();
			else				
				hcList = nonCtxPairsCoverage.get(id);			
			hcList.add(hashcodeObj); 
			nonCtxPairsCoverage.put(id, hcList);
			if(DatecProperties.getInstance().isVerbose())
				System.out.println("Def id nnctxpair" + id + ", hc " + hashcodeObj + " covered!!");
		}
		
		
	}
	
	/**
	 * When this method is called, it means that an interesting use has been covered.
	 * All the associations that contain this use and have the definition already covered are now
	 * completely covered. (The associations are brought to the state 'Covered')
	 * 
	 * @param useid the id of the covered use
	 * @param hashcodeObj the hashcode number of the object 
	 */
	public static void checkUse(String useid, int hashcodeObj){
		readPropertiesFile();
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		//contextual pairs
		ArrayList<Integer> pairs = DBinterface.getCovered(useid, pairsCoverage.keySet(),  getContextAsString(), true);
		for(Integer id:pairs){
			pairsCoverage.remove(id);
			if(DatecProperties.getInstance().isVerbose())
				System.out.println("Pair id " + id + ", hc " + hashcodeObj + " covered!!");
		}
		
		//non contextual pairs
		ArrayList<Integer> nonCtXPairs = DBinterface.getCovered(useid, nonCtxPairsCoverage.keySet(),  getContextAsString(), false);
		for(Integer id:nonCtXPairs){
			pairsCoverage.remove(id);
			if(DatecProperties.getInstance().isVerbose())
				System.out.println("Pair id " + id + ", hc " + hashcodeObj + " covered!!");
		}
	}
	
	/**
	 * When this method is called, it means that a new method has been called. Thus we have to 
	 * update the context and add the called method to the stack.
	 * @param call the called method
	 */
	public static void pushCall(String call){
		context.push(call);
		if(DatecProperties.getInstance().isVerbose())
			System.out.println("**  PUSH  ** "+getContextAsString());
	}
	
	/**
	 * When this method is called, it means that we just exited a method. Thus we have to update 
	 * the context and remove the last method from the stack.
	 * @param call the method
	 */
	public static void popCall(String call){
		String removedCall = context.pop();
		if(!call.equals(removedCall))
			System.err.println("Error in call stack!!");
		if(DatecProperties.getInstance().isVerbose())
			System.out.println("**  POP  ** "+getContextAsString());
	}
	
	public static String getContextAsString() {
		String toReturn = "";
		for (int i = 0; i<context.size();i++) {
			toReturn += context.get(i);
		}
		return toReturn;
	}	
	
	public static void reset(){
		context = new Stack<String>();
		pairsCoverage = new HashMap<Integer, ArrayList<Integer>>();
		nonCtxPairsCoverage = new HashMap<Integer, ArrayList<Integer>>();
	}
	
	public static void readPropertiesFile(){
		if(propertiesFileRead)
			return;
		/* parse the properties file */
		try{
			DatecProperties.getInstance().parsePropertiesFile("datec.properties");
			propertiesFileRead = true;
		}catch(IOException ioe){
			System.err.println("Errors in parsing the properties file. Default settings will be used");
		}
	}
}
