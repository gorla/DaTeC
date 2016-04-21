package ch.unisi.inf.datec.load;

/**
 * Interface for class loading
 * 
 * @author Alessandra Gorla
 * @author Renzo Russi
 */
public interface Loader {
	
	/**
	 * Load the content of the path provided
	 * @param path Path to load
	 * @throws DatecLoaderException
	 */
	public void load(String path) throws DatecLoaderException;
	
}
