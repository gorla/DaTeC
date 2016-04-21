/**
 * 
 */
package ch.unisi.inf.datec.load.filter;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Filter for the directory loader
 * 
 * @author Alessandra Gorla
 * @author Renzo Russi
 *
 */
public class ClassNameFilter implements FilenameFilter {

	/* (non-Javadoc)
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	public boolean accept(File dir, String name) {
		return name.endsWith(".class");
	}

}
