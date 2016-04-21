/**
 * 
 */
package ch.unisi.inf.datec;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Class that stores all the options to be considered during the analysis
 * 
 * @author Alessandra Gorla
 */
public class DatecProperties {
	
	/*
	 * TODO temp directory (per unzippare e sootoutput)
	 * 		bin directory
	 * 		scr directory
	 */
	private static DatecProperties INSTANCE;
	
	/**
	 * Name of the analyzed project
	 */
	private String prjName = "";
	/**
	 * Path to the Java VM classes
	 */
	private String JavaVMclasses = "";
	/**
	 * Working directory. Used in the Eclipse plugin to represent the prj directory.
	 */
	private String projectDirectory = ".";
	/**
	 * Source folder. Used in the Eclipse plugin.
	 */
	private String srcFolder = "";
	/**
	 * Binary folder. Used in Eclipse plugin
	 */
	private String binaryFolder = "";
	/**
	 * Path to be used to uncompress and load the content of a compressed file
	 */
	private String pathUnzip = System.getProperty("java.io.tmpdir");	
	/**
	 * Output folder for reports
	 */
	private String pathReport = "report/";
	/**
	 * Enable verbose output (for debugging)
	 */
	private boolean verbose = false;
	/**
	 * Ignore static method calls
	 */
	private boolean ignoreStaticCalls = true;
	/**
	 * Ignore static instance variables
	 */
	private boolean ignoreStaticVariables = false; //TODO implement feature
	/**
	 * Creation of the reports
	 */
	private boolean createReport = true;
	/**
	 * Clean after analysis
	 */
	private boolean cleanAfterAnalysis = true;
	
	/**
	 * Directory containing all the binary files that have to be instrumented
	 */
	private String directoryInstrumenter = System.getProperty("java.io.tmpdir")+"/DatecUnzip/";	
	
	
	
	/**
	 * Private constructor
	 */
	private DatecProperties(){}
	
	/**
	 * Return the DatecProperties instance
	 * @return DatecProperties
	 */
	public static DatecProperties getInstance(){
		if(INSTANCE == null)
			INSTANCE = new DatecProperties();
		return INSTANCE;
	}
	
	/**
	 * Parse a properties file to set the datec properties 
	 * @param file the file to parse
	 * @throws IOException
	 */
	public void parsePropertiesFile(String file) throws IOException{
		InputStream in = null;
		Properties properties = null;
        try {
        	in = new FileInputStream(file);
            properties = new Properties();
            properties.load(in);
        } finally {
        	in.close();
        }
        this.prjName = properties.getProperty("prjName");
        this.JavaVMclasses = properties.getProperty("JavaVMclasses");
        this.projectDirectory = properties.getProperty("projectDirectory");
        this.srcFolder = properties.getProperty("srcFolder");
        this.binaryFolder = properties.getProperty("binaryFolder");
        this.projectDirectory = properties.getProperty("projectDirectory");
        this.pathUnzip = properties.getProperty("pathUnzip");
        this.pathReport = properties.getProperty("pathReport");
        this.verbose = properties.getProperty("verbose").equals("true");
        this.setIgnoreStaticCalls(properties.getProperty("ignoreStaticCalls").equals("true"));
        this.setIgnoreStaticVariables(properties.getProperty("ignoreStaticVariables").equals("true"));
        this.createReport = properties.getProperty("createReport").equals("true");
        this.cleanAfterAnalysis = properties.getProperty("cleanAfterAnalysis").equals("true");
        this.directoryInstrumenter = (properties.getProperty("directoryInstrumenter"));
	}

	/**
	 * @return the verbose
	 */
	public boolean isVerbose() {
		return verbose;
	}

	/**
	 * @param verbose the verbose to set
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * @return the pathUnzip
	 */
	public String getPathUnzip() {
		return pathUnzip;
	}

	/**
	 * @param pathUnzip the pathUnzip to set
	 */
	public void setPathUnzip(String pathUnzip) {
		this.pathUnzip = pathUnzip;
	}

	/**
	 * @return the pathReport
	 */
	public String getPathReport() {
		return pathReport;
	}

	/**
	 * @param pathReport the pathReport to set
	 */
	public void setPathReport(String pathReport) {
		this.pathReport = pathReport;
	}

	/**
	 * @return the createReport
	 */
	public boolean isCreateReport() {
		return createReport;
	}

	/**
	 * @param createReport the createReport to set
	 */
	public void setCreateReport(boolean createReport) {
		this.createReport = createReport;
	}

	/**
	 * @param ignoreStaticCalls the ignoreStaticCalls to set
	 */
	public void setIgnoreStaticCalls(boolean ignoreStaticCalls) {
		this.ignoreStaticCalls = ignoreStaticCalls;
	}

	/**
	 * @return the ignoreStaticCalls
	 */
	public boolean isIgnoreStaticCalls() {
		return ignoreStaticCalls;
	}

	/**
	 * @param ignoreStaticVariables the ignoreStaticVariables to set
	 */
	public void setIgnoreStaticVariables(boolean ignoreStaticVariables) {
		this.ignoreStaticVariables = ignoreStaticVariables;
	}

	/**
	 * @return the ignoreStaticVariables
	 */
	public boolean isIgnoreStaticVariables() {
		return ignoreStaticVariables;
	}

	/**
	 * @return the javaVMclasses
	 */
	public String getJavaVMclasses() {
		return JavaVMclasses;
	}

	/**
	 * @param javaVMclasses the javaVMclasses to set
	 */
	public void setJavaVMclasses(String javaVMclasses) {
		JavaVMclasses = javaVMclasses;
	}

	/**
	 * @return the projectDirectory
	 */
	public String getProjectDirectory() {
		return projectDirectory;
	}

	/**
	 * @param projectDirectory the projectDirectory to set
	 */
	public void setProjectDirectory(String projectDirectory) {
		this.projectDirectory = projectDirectory;
	}

	/**
	 * @return the srcFolder
	 */
	public String getSrcFolder() {
		return srcFolder;
	}

	/**
	 * @param srcFolder the srcFolder to set
	 */
	public void setSrcFolder(String srcFolder) {
		this.srcFolder = srcFolder;
	}

	/**
	 * @return the binaryFolder
	 */
	public String getBinaryFolder() {
		return binaryFolder;
	}

	/**
	 * @param binaryFolder the binaryFolder to set
	 */
	public void setBinaryFolder(String binaryFolder) {
		this.binaryFolder = binaryFolder;
	}

	/**
	 * @return the cleanAfterAnalysis
	 */
	public boolean isCleanAfterAnalysis() {
		return cleanAfterAnalysis;
	}

	/**
	 * @param cleanAfterAnalysis the cleanAfterAnalysis to set
	 */
	public void setCleanAfterAnalysis(boolean cleanAfterAnalysis) {
		this.cleanAfterAnalysis = cleanAfterAnalysis;
	}

	/**
	 * @param directoryInstrumenter
	 */
	public void setDirectoryInstrumenter(String directoryInstrumenter) {
		this.directoryInstrumenter = directoryInstrumenter;
	}

	/**
	 * @return
	 */
	public String getDirectoryInstrumenter() {
		return directoryInstrumenter;
	}

	/**
	 * @return the prjName
	 */
	public String getPrjName() {
		return prjName;
	}

	/**
	 * @param prjName the prjName to set
	 */
	public void setPrjName(String prjName) {
		this.prjName = prjName;
	}
	
	
	
}
