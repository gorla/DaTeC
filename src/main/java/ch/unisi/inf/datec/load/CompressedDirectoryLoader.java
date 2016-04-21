package ch.unisi.inf.datec.load;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import ch.unisi.inf.datec.DatecProperties;


/**
 * Compressed file loader. 
 * 
 * @author Alessandra Gorla
 * @author Renzo Russi
 *
 */
public class CompressedDirectoryLoader implements Loader {
	
	/**
	 * Directory where to store the uncompressed files.
	 * Writing permissions are required on this directory
	 */
	private String outFolder = DatecProperties.getInstance().getPathUnzip()+"DatecUnzip/";

	/* (non-Javadoc)
	 * @see ch.unisi.inf.datec.load.Loader#load(java.lang.String)
	 */
	public void load(String path) throws DatecLoaderException {
		checkUnzipPath();
		unzip(path);
		DirectoryLoader dl = new DirectoryLoader();
		dl.load(outFolder);
	}

	/**
	 * unzip files in outFolder directory
	 * @param zipFileName
	 */
	private void unzip(String zipFileName) {
		try {
			ZipFile zf = new ZipFile(zipFileName);

			// Enumerate each entry
			for (Enumeration entries = zf.entries(); entries.hasMoreElements();) {

				// Get the entry and its name
				ZipEntry zipEntry = (ZipEntry) entries.nextElement();
				
				//ignore meta-inf directory
				if(zipEntry.getName().contains("META-INF"))
					continue;

				if (zipEntry.isDirectory()) {
					(new File(outFolder + zipEntry.getName())).mkdir();
				} else{
					String zipEntryName = zipEntry.getName();

					OutputStream out = new FileOutputStream(outFolder + zipEntryName);
					InputStream in = zf.getInputStream(zipEntry);

					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}

					// Close streams
					out.close();
					in.close();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}
	
	/**
	 * Create the path to unzip the file if it does not exist yet
	 */
	private void checkUnzipPath(){
		File dirUnzip = new File(outFolder);
		if(!dirUnzip.exists()){
			dirUnzip.mkdirs();
		}
	}
}