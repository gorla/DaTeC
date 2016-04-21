package ch.unisi.inf.datec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import soot.Scene;
import soot.SootClass;
import soot.jimple.toolkits.callgraph.CallGraph;
import ch.unisi.inf.datec.analyses.AssociationsBuilder;
import ch.unisi.inf.datec.analyses.CallGraphConstructor;
import ch.unisi.inf.datec.analyses.InterProceduralReachableUsesAnalysis;
import ch.unisi.inf.datec.analyses.InterProceduralReachingDefinitionAnalysis;
import ch.unisi.inf.datec.analyses.Utilities;
import ch.unisi.inf.datec.analyses.filter.DatecMethodFilter;
import ch.unisi.inf.datec.data.Association;
import ch.unisi.inf.datec.data.ClassRegistry;
import ch.unisi.inf.datec.db.DBinterface;
import ch.unisi.inf.datec.load.DatecLoaderException;
import ch.unisi.inf.datec.load.Loader;
import ch.unisi.inf.datec.load.LoaderBuilder;
import ch.unisi.inf.datec.report.CoverageReport;

/**
 * Main class.
 * Usage: Main <options> <classes>
 * where <options> can be -pf or --properties to specify the path to the properties file
 *  (if different form datec.properties)
 * and <classes> are the classes to e analyzed. They can be a single class, a directory or a zipped file.
 * 
 * @author Alessandra Gorla
 * @author Renzo Russi
 *
 */
public class Main {

	/**
	 * Properties file location
	 */
	private static String propertiesFile = "datec.properties";

	/**
	 * @param args
	 */
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
		
		/* set soot options */
		soot.options.Options.v().parse(new String[]{"--keep-line-number"});
		soot.options.Options.v().parse(new String[]{"-allow-phantom-refs"});
		soot.options.Options.v().parse(new String[]{"-w"});
		soot.PhaseOptions.v().setPhaseOption("cg","safe-forname");
		soot.PhaseOptions.v().setPhaseOption("cg","safe-newinstance");
        
		//set soot class path
		Scene.v().setSootClassPath(Scene.v().getSootClassPath()+":"+DatecProperties.getInstance().getJavaVMclasses());
		
		/* load the classes to analyze */
		Loader l = LoaderBuilder.getLoader(args[args.length-1]);
		try {
			l.load(args[args.length-1]);
		} catch (DatecLoaderException e) {
			e.printStackTrace();
		}
		
		if(DatecProperties.getInstance().isVerbose())
			System.out.println(ClassRegistry.getInstance().getClasses().keySet().size() + " classes are going to be analyzed");
        
		/* call graph construction */
		if(DatecProperties.getInstance().isVerbose())
			System.out.println("INIT call graph construction");
		CallGraphConstructor cgc = new CallGraphConstructor();
		CallGraph cg = cgc.getCallGraph();
		if(DatecProperties.getInstance().isVerbose())
			System.out.println("FINISH call graph construction");
		
		/* Reaching definitions */
		if(DatecProperties.getInstance().isVerbose())
			System.out.println("INIT interprocedural reaching definitions analysis");
		InterProceduralReachingDefinitionAnalysis ia = new InterProceduralReachingDefinitionAnalysis(cg,new DatecMethodFilter(),cg.sourceMethods(),DatecProperties.getInstance().isVerbose());
		if(DatecProperties.getInstance().isVerbose())
			System.out.println("FINISH interprocedural reaching definitions analysis");
		
		/* Reachable uses */
		if(DatecProperties.getInstance().isVerbose())
			System.out.println("INIT interprocedural reachable uses analysis");
		InterProceduralReachableUsesAnalysis ie = new InterProceduralReachableUsesAnalysis(cg,new DatecMethodFilter(),cg.sourceMethods(),DatecProperties.getInstance().isVerbose());
		if(DatecProperties.getInstance().isVerbose())
			System.out.println("FINISH interprocedural reachable uses analysis");
		
		/* For each class build the associations */
		if(DatecProperties.getInstance().isVerbose())
			System.out.println("INIT DU pairs construction");
		Iterator<SootClass> classIterator = ClassRegistry.getInstance().getClasses().keySet().iterator();
		while (classIterator.hasNext())
			AssociationsBuilder.build(classIterator.next());
		if(DatecProperties.getInstance().isVerbose())
			System.out.println("FINISH DU pairs construction");
		
		/* print associations*/
		int assocCounter = 0;
		Iterator<SootClass> printIterator = ClassRegistry.getInstance().getAssociations().keySet().iterator();
		while (printIterator.hasNext()) {
			int classAssociations = 0;
			SootClass sc = printIterator.next();
			ArrayList<Association> asList = ClassRegistry.getInstance().getAssociations().get(sc);

			for (Association as : asList) {			
				classAssociations++;
				assocCounter++;
			}
			if(DatecProperties.getInstance().isVerbose())
				System.out.println("Class "+sc.getName()+": "+ classAssociations);
		}
		if(DatecProperties.getInstance().isCleanAfterAnalysis())
			Utilities.cleanAfterAnalysis("");
		
		/* Store the registry */
		if(DatecProperties.getInstance().isVerbose())
			System.out.println("INIT storing the registry");	
		DBinterface.write();
		if(DatecProperties.getInstance().isVerbose())
			System.out.println("FINISH storing the registry");	
		
		/* Create reports if required */
		if(DatecProperties.getInstance().isCreateReport()){
			if(DatecProperties.getInstance().isVerbose())
				System.out.println("INIT report");
			CoverageReport.createReport();
			if(DatecProperties.getInstance().isVerbose())
				System.out.println("FINISH report");
		}
		
		System.out.println("Total number of contextual associations created: "+assocCounter);
	}
}