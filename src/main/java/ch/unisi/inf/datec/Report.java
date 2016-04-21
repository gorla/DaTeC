package ch.unisi.inf.datec;

import java.io.IOException;

import ch.unisi.inf.datec.report.CoverageReport;

public class Report {
	
	private static String propertiesFile = "datec.properties";
	
	public static void main(String[] args) {
		
		/* check if properties file is specified in a different location */
		for(int i=0;i<args.length;i++){
			String argument = args[i];
			if(argument.equals("-pf") | argument.equals("--properties"))
				propertiesFile = args[i+1]; 
		}
		
		/* parse the properties file */
		try{
			DatecProperties.getInstance().parsePropertiesFile(propertiesFile);
		}catch(IOException ioe){
			System.err.println("Errors in parsing the properties file. Default settings will be used");
		}
		
		CoverageReport.createReport();
	}
}
