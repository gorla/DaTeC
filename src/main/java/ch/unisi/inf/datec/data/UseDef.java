package ch.unisi.inf.datec.data;

import java.util.Stack;

/**
 * Abstract class to represent both definitions and uses
 * 
 * @author Alessandra Gorla
 * @author Renzo Russi
 */
public abstract class UseDef implements Cloneable{
	
	/**
	 * Complete name of the variable
	 */
	private String name;
	/**
	 * Variable name
	 */
	private String varName;
	/**
	 * Method name
	 */
	private String methodName;
	/**
	 * Class name
	 */
	private String className;
	/**
	 * Source line number
	 */
	private int lineNumber;
	/**
	 * ID to distinguish the definitions or uses 
	 * It is either the DefBox or the UseBox hash code. 
	 */
	private int id;
	/**
	 * Context of the definition/use
	 */
	private Stack<String> context = new Stack<String>();
	/**
	 * boolean to indicate whether the definition/use is a formal parameter
	 * TODO finish implementation of formal parameters
	 */
	private boolean isFormalParam;
	
	/**
	 * Definition Use constructor
	 * @param n complete name
	 * @param var variable name
	 * @param method method name
	 * @param cName className
	 * @param line source line number
	 */
	public UseDef(String n, String var, String method, String cName, int line) {
		this.name = n;
		this.varName = var;
		this.methodName = method;
		this.className = cName;
		this.lineNumber = line;
		this.context.push(method) ;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getVariableName(){
		return this.varName;
	}
	
	public String getMethodName(){
		return this.methodName;
	}
	
	public String getClassName(){
		return this.className;
	}
	
	public int getLineNumber(){
		return this.lineNumber;
	}
	
	public Stack<String> getContext(){
		return this.context;
	}
	
	public String getContextAsString() {
		String toReturn = "";
		for (int i = getContext().size(); i > 0 ; i--) {
			toReturn += getContext().get(i-1);
		}
		return toReturn;
	}
	
	public void addContext(String c) {
		context.push(c);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		return this.getId() == ((UseDef)obj).getId();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		UseDef cloned = (UseDef)super.clone();
		cloned.setContext((Stack<String>) cloned.context.clone());
		return cloned;
		}

	private void setContext(Stack<String> stk) {
		this.context = stk;
	} 
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	} 
	
}