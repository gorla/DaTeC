/**
 * 
 */
package ch.unisi.inf.datec.analyses;

import java.io.File;
import java.util.Iterator;

import ch.unisi.inf.datec.DatecProperties;
import ch.unisi.inf.datec.data.ClassRegistry;

import soot.G;
import soot.Unit;
import soot.Value;
import soot.jimple.internal.JimpleLocal;
import soot.tagkit.LineNumberTag;
import soot.tagkit.Tag;

/**
 * Analysis utilities
 * 
 * @author Alessandra Gorla
 * @author Renzo Russi
 *
 */
public class Utilities {

	/**
	 * Given a unit, it returns the source line number
	 * @param unit The control flow graph unit 
	 * @return The source line number
	 */
	public static int getLineNumber(Unit unit) {
		for (Iterator j = unit.getTags().iterator(); j.hasNext();) {
			Tag tag = (Tag) j.next();
			if (tag instanceof LineNumberTag) {
				byte[] value = tag.getValue();
				int lineNumber = ((value[0] & 0xff) << 8) | (value[1] & 0xff);
				return lineNumber;
			}
		}
		return 0;
	}
	
	/**
	 * Checks whether the value object contained in a method invocation is 
	 * a field reference.
	 * @param v the value
	 * @return true if v is a field reference, false otherwise.
	 */
	public static boolean isFieldRef(Value v){
		if(((JimpleLocal)v).getName().contains("$"))
			return true;
		return false;
	}
	
	/**
	 * Clean registry, unzip directory and Soot global variables
	 * @param path 
	 */
	public static void cleanAfterAnalysis(String path){
		// Declare variables variables
        File fDir = null;
        if(path.equals(""))
        	fDir = new File(DatecProperties.getInstance().getPathUnzip()+"DatecUnzip/");
        else
        	fDir = new File(path);
        String[] strChildren = null;
        boolean bRet = false;
        
        // Validate directory
        if (fDir.isDirectory()){
                // -- Get children
                strChildren = fDir.list();
                // -- Go through each
                for (int i = 0; i < strChildren.length; i++)
                        cleanAfterAnalysis(new File(fDir, strChildren[i]).getAbsolutePath());
        }
        // The directory is now empty so delete it
        fDir.delete();
        ClassRegistry.reset();
        G.reset();
	}
	
}
