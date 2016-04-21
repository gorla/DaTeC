/**
 * 
 */
package ch.unisi.inf.datec.load;

/**
 * Exception that can be thrown while loading classes before the analysis
 * 
 * @author Alessandra Gorla
 * @author Renzo Russi
 *
 */
public class DatecLoaderException extends Exception {

	private static final long serialVersionUID = 1L;

	public DatecLoaderException(String string) {
		super(string);
	}

}
