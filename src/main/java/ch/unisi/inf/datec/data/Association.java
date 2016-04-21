package ch.unisi.inf.datec.data;


/**
 * This class represents a definition-use association
 * 
 * @author Alessandar Gorla
 * @author Renzo Russi
 */
public class Association {
	/**
	 * The use of the association
	 */
	Use use;
	/**
	 * The definition of the association
	 */
	Definition def;
	
	/**
	 * Class constructor
	 * @param d the definition of the association
	 * @param u the use of the association
	 */
	public Association(Definition d, Use u) {
		this.def = d;
		this.use = u;
	}
	
	/**
	 * Return the definition of the association
	 * @return definition
	 */
	public Definition getDef() {
		return this.def;
	}
	
	/**
	 * Return the use of the association
	 * @return use
	 */
	public Use getUse() {
		return this.use;
	}
	
	/**
	 * Set the definition
	 * @param d definition
	 */
	public void setDef(Definition d) {
		this.def = d;
	}
	
	/**
	 * Set the definition
	 * @param u use
	 */
	public void setUse(Use u) {
		this.use = u;
	}
}

