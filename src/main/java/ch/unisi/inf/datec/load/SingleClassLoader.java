/**
 * 
 */
package ch.unisi.inf.datec.load;

import soot.Scene;
import soot.SootClass;
import ch.unisi.inf.datec.data.ClassRegistry;

/**
 * Single class loader
 * 
 * @author Alessandra Gorla
 * @author Renzo Russi
 *
 */
public class SingleClassLoader implements Loader {

	/* (non-Javadoc)
	 * @see ch.unisi.inf.datec.load.Loader#load(java.lang.String)
	 */
	public void load(String path) throws DatecLoaderException {
		SootClass sc;
		try{
			sc = Scene.v().loadClassAndSupport(path);		
		}catch(Exception e){
			throw new DatecLoaderException("File cannot be loaded");
		}
		sc.setApplicationClass();
		
		ClassRegistry.getInstance().addClass(sc);
	}

}
