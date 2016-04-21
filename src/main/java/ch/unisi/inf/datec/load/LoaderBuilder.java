/**
 * 
 */
package ch.unisi.inf.datec.load;

import java.io.File;

/**
 * Loader builder
 * 
 * @author Alessandra Gorla
 * @author Renzo Russi
 *
 */
public class LoaderBuilder {
	
	/**
	 * Creates and returns the right loader
	 * @param path The directory/file path
	 * @return the loader
	 * @throws DatecLoaderException
	 */
	public static Loader getLoader(String path){
		File f = new File(path);
		
		if(f.isDirectory())
			return new DirectoryLoader();
		
		if(f.getName().endsWith(".zip") || f.getName().endsWith(".jar"))
			return new CompressedDirectoryLoader();
		
		return new SingleClassLoader();
	}
}
