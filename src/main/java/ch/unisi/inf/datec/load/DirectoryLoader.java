/**
 * 
 */
package ch.unisi.inf.datec.load;

import java.io.File;

import soot.Scene;
import ch.unisi.inf.datec.load.filter.ClassNameFilter;

/**
 * Recursively load all the class from a directory
 * 
 * @author Alessandra Gorla
 * @author Renzo Russi
 */
public class DirectoryLoader implements Loader {
	
	/* (non-Javadoc)
	 * @see ch.unisi.inf.datec.load.Loader#load(java.lang.String)
	 */
	public void load(String path) throws DatecLoaderException {		
		File dir = new File(path);
		if(!dir.isDirectory() || !dir.exists())
			throw new DatecLoaderException("Cannot find "+dir+" directory.");
		Scene.v().setSootClassPath(Scene.v().getSootClassPath()+":"+path); 
		visitDirectory(dir,"");
	}	
	
	
	private void visitDirectory(File dir, String prefix) throws DatecLoaderException {
		File[] classFiles = dir.listFiles(new ClassNameFilter());
		for(int i=0; i<classFiles.length; i++){
			String className = classFiles[i].getName().substring(0, classFiles[i].getName().length() - 6);
			if (!prefix.equals(""))
				className = prefix + "." + className;
			
			//Load the class
			SingleClassLoader scl = new SingleClassLoader();
			scl.load(className);
		}

		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				String packageName;
				if (!prefix.equals(""))
					packageName = prefix + "." + files[i].getName();
				else
					packageName = files[i].getName();
				visitDirectory(files[i], packageName);
			}
		}

	}

}
