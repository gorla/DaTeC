package ch.unisi.inf.datec.data;

/**
 * This class represents a use
 * 
 * @author Alessandra Gorla
 * @author Renzo Russi
 */
public class Use extends UseDef {


	private String idUse;
	
	/** 
	 * Use constructor
	 * @param n complete name of the variable used
	 * @param var name of the variable used
	 * @param method method name
	 * @param name class name
	 * @param line source line number
	 */	
	
	public Use(String n, String var, String method, String name, int line) {
		super(n, var, method, name, line);
		idUse = "U-"+n+"-"+method+"-"+line; 
	}
	
	/**
	 * @return the idUse
	 */
	public String getIdUse() {
		return idUse;
	}	
}
