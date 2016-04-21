package ch.unisi.inf.datec.data;

/**
 * This class represents a definition
 * 
 * @author Alessandra Gorla
 * @author Renzo Russi
 * 
 */
public class Definition extends UseDef {
	
	private String idDef;
	
	/**
	 * Class constructor
	 * @param n complete name of the variable defined
	 * @param var name of the variable defined
	 * @param method method name 
	 * @param cName class name
	 * @param line source line number
	 */
	public Definition(String n, String var, String method, String cName, int line) {
		super(n, var, method, cName, line);
		idDef = "D-"+n+"-"+method+"-"+line; 		
	}

	/**
	 * @return the idDef
	 */
	public String getIdDef() {
		return idDef;
	}	
}
