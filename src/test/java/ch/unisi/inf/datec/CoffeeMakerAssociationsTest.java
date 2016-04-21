package ch.unisi.inf.datec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import ch.unisi.inf.datec.analyses.AssociationsBuilder;
import ch.unisi.inf.datec.analyses.CallGraphConstructor;
import ch.unisi.inf.datec.analyses.InterProceduralReachableUsesAnalysis;
import ch.unisi.inf.datec.analyses.InterProceduralReachingDefinitionAnalysis;
import ch.unisi.inf.datec.analyses.Utilities;
import ch.unisi.inf.datec.analyses.filter.DatecMethodFilter;
import ch.unisi.inf.datec.data.Association;
import ch.unisi.inf.datec.data.ClassRegistry;
import ch.unisi.inf.datec.data.Definition;
import ch.unisi.inf.datec.data.Use;
import ch.unisi.inf.datec.load.DatecLoaderException;
import ch.unisi.inf.datec.load.Loader;
import ch.unisi.inf.datec.load.LoaderBuilder;
import junit.framework.TestCase;


public class CoffeeMakerAssociationsTest extends TestCase {
	
	

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		/* parse the properties file */
		try{
			DatecProperties.getInstance().parsePropertiesFile("datec.properties");
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
	}

	public void testAssociations(){
		
		Loader l = LoaderBuilder.getLoader("src/test/resources/coffeemaker.zip");
		try {
			l.load("src/test/resources/coffeemaker.zip");
		} catch (DatecLoaderException e) {
			e.printStackTrace();
		}		
		assertEquals(3, ClassRegistry.getInstance().getClasses().keySet().size());
		assertEquals(35, ClassRegistry.getInstance().getAllMethodsToAnalyze().size());

		CallGraphConstructor cgc = new CallGraphConstructor();
		CallGraph cg = cgc.getCallGraph();

		InterProceduralReachingDefinitionAnalysis ia = new InterProceduralReachingDefinitionAnalysis(cg,new DatecMethodFilter(),cg.sourceMethods(),true);
		InterProceduralReachableUsesAnalysis ie = new InterProceduralReachableUsesAnalysis(cg , new DatecMethodFilter(), cg.sourceMethods() , true);
		
		//check reaching definitions
		for(SootMethod sm:ClassRegistry.getInstance().getAllMethodsToAnalyze()){
			HashMap<String,ArrayList<Definition>> rd = ClassRegistry.getInstance().getMethodData(sm).getReachingDefs();
			if(sm.getSignature().contains("Recipe: void <init>")){
				assertNull(rd.get("<coffeemaker.Recipe: java.lang.String name>"));
				assertNull(rd.get("<coffeemaker.Recipe: int price>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtCoffee>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtMilk>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtSugar>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtChocolate>"));
				continue;
			}
			if(sm.getSignature().contains("Recipe: int getAmtChocolate()")){
				assertNull(rd.get("<coffeemaker.Recipe: java.lang.String name>"));
				assertNull(rd.get("<coffeemaker.Recipe: int price>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtCoffee>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtMilk>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtSugar>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtChocolate>"));
				continue;
			}
			if(sm.getSignature().contains(".Recipe: void setAmtChocolate(int)")){
				assertNull(rd.get("<coffeemaker.Recipe: java.lang.String name>"));
				assertNull(rd.get("<coffeemaker.Recipe: int price>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtCoffee>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtMilk>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtSugar>"));
				assertEquals(2,rd.get("<coffeemaker.Recipe: int amtChocolate>").size());
				continue;
			}
			if(sm.getSignature().contains("Recipe: int getAmtCoffee()")){
				assertNull(rd.get("<coffeemaker.Recipe: java.lang.String name>"));
				assertNull(rd.get("<coffeemaker.Recipe: int price>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtCoffee>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtMilk>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtSugar>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtChocolate>"));
				continue;
			}
			if(sm.getSignature().contains("Recipe: void setAmtCoffee(int)")){
				assertNull(rd.get("<coffeemaker.Recipe: java.lang.String name>"));
				assertNull(rd.get("<coffeemaker.Recipe: int price>"));
				assertEquals(2,rd.get("<coffeemaker.Recipe: int amtCoffee>").size());
				assertNull(rd.get("<coffeemaker.Recipe: int amtMilk>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtSugar>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtChocolate>"));
				continue;
			}
			if(sm.getSignature().contains("Recipe: int getAmtMilk()")){
				assertNull(rd.get("<coffeemaker.Recipe: java.lang.String name>"));
				assertNull(rd.get("<coffeemaker.Recipe: int price>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtCoffee>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtMilk>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtSugar>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtChocolate>"));
				continue;
			}
			if(sm.getSignature().contains("Recipe: void setAmtMilk(int)")){
				assertNull(rd.get("<coffeemaker.Recipe: java.lang.String name>"));
				assertNull(rd.get("<coffeemaker.Recipe: int price>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtCoffee>"));
				assertEquals(2,rd.get("<coffeemaker.Recipe: int amtMilk>").size());
				assertNull(rd.get("<coffeemaker.Recipe: int amtSugar>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtChocolate>"));
				continue;
			}
			if(sm.getSignature().contains("Recipe: int getAmtSugar()")){
				assertNull(rd.get("<coffeemaker.Recipe: java.lang.String name>"));
				assertNull(rd.get("<coffeemaker.Recipe: int price>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtCoffee>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtMilk>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtSugar>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtChocolate>"));
				continue;
			}
			if(sm.getSignature().contains("Recipe: void setAmtSugar(int)")){
				assertNull(rd.get("<coffeemaker.Recipe: java.lang.String name>"));
				assertNull(rd.get("<coffeemaker.Recipe: int price>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtCoffee>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtMilk>"));
				assertEquals(2,rd.get("<coffeemaker.Recipe: int amtSugar>").size());
				assertNull(rd.get("<coffeemaker.Recipe: int amtChocolate>"));
				continue;
			}
			if(sm.getSignature().contains("Recipe: java.lang.String getName()")){
				assertNull(rd.get("<coffeemaker.Recipe: java.lang.String name>"));
				assertNull(rd.get("<coffeemaker.Recipe: int price>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtCoffee>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtMilk>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtSugar>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtChocolate>"));
				continue;
			}
			if(sm.getSignature().contains("Recipe: void setName(java.lang.String)")){
				assertEquals(1,rd.get("<coffeemaker.Recipe: java.lang.String name>").size());
				assertNull(rd.get("<coffeemaker.Recipe: int price>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtCoffee>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtMilk>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtSugar>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtChocolate>"));
				continue;
			}
			if(sm.getSignature().contains("Recipe: int getPrice()")){
				assertNull(rd.get("<coffeemaker.Recipe: java.lang.String name>"));
				assertNull(rd.get("<coffeemaker.Recipe: int price>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtCoffee>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtMilk>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtSugar>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtChocolate>"));
				continue;
			}
			if(sm.getSignature().contains("Recipe: void setPrice(int)")){
				assertNull(rd.get("<coffeemaker.Recipe: java.lang.String name>"));
				assertEquals(2,rd.get("<coffeemaker.Recipe: int price>").size());
				assertNull(rd.get("<coffeemaker.Recipe: int amtCoffee>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtMilk>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtSugar>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtChocolate>"));
				continue;
			}
			if(sm.getSignature().contains("Recipe: boolean equals(coffeemaker.Recipe)")){
				assertNull(rd.get("<coffeemaker.Recipe: java.lang.String name>"));
				assertNull(rd.get("<coffeemaker.Recipe: int price>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtCoffee>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtMilk>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtSugar>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtChocolate>"));
				continue;
			}
			if(sm.getSignature().contains("Recipe: java.lang.String toString()")){
				assertNull(rd.get("<coffeemaker.Recipe: java.lang.String name>"));
				assertNull(rd.get("<coffeemaker.Recipe: int price>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtCoffee>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtMilk>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtSugar>"));
				assertNull(rd.get("<coffeemaker.Recipe: int amtChocolate>"));
				continue;
			}
			
			//TODO add CoffeeMaker and Inventory methods
//			fail();
		}
		
		//check reachable uses
		for(SootMethod sm:ClassRegistry.getInstance().getAllMethodsToAnalyze()){
			HashMap<String,ArrayList<Use>> ru = ClassRegistry.getInstance().getMethodData(sm).getReachableUses();
			if(sm.getSignature().contains("Recipe: void <init>")){
				assertNull(ru.get("<coffeemaker.Recipe: java.lang.String name>"));
				assertNull(ru.get("<coffeemaker.Recipe: int price>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtCoffee>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtMilk>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtSugar>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtChocolate>"));
				continue;
			}
			if(sm.getSignature().contains("Recipe: int getAmtChocolate()")){
				assertNull(ru.get("<coffeemaker.Recipe: java.lang.String name>"));
				assertNull(ru.get("<coffeemaker.Recipe: int price>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtCoffee>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtMilk>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtSugar>"));
				assertEquals(1,ru.get("<coffeemaker.Recipe: int amtChocolate>").size());
				continue;
			}
			if(sm.getSignature().contains(".Recipe: void setAmtChocolate(int)")){
				assertNull(ru.get("<coffeemaker.Recipe: java.lang.String name>"));
				assertNull(ru.get("<coffeemaker.Recipe: int price>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtCoffee>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtMilk>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtSugar>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtChocolate>"));
				continue;
			}
			if(sm.getSignature().contains("Recipe: int getAmtCoffee()")){
				assertNull(ru.get("<coffeemaker.Recipe: java.lang.String name>"));
				assertNull(ru.get("<coffeemaker.Recipe: int price>"));
				assertEquals(1,ru.get("<coffeemaker.Recipe: int amtCoffee>").size());
				assertNull(ru.get("<coffeemaker.Recipe: int amtMilk>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtSugar>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtChocolate>"));
				continue;
			}
			if(sm.getSignature().contains("Recipe: void setAmtCoffee(int)")){
				assertNull(ru.get("<coffeemaker.Recipe: java.lang.String name>"));
				assertNull(ru.get("<coffeemaker.Recipe: int price>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtCoffee>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtMilk>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtSugar>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtChocolate>"));
				continue;
			}
			if(sm.getSignature().contains("Recipe: int getAmtMilk()")){
				assertNull(ru.get("<coffeemaker.Recipe: java.lang.String name>"));
				assertNull(ru.get("<coffeemaker.Recipe: int price>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtCoffee>"));
				assertEquals(1,ru.get("<coffeemaker.Recipe: int amtMilk>").size());
				assertNull(ru.get("<coffeemaker.Recipe: int amtSugar>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtChocolate>"));
				continue;
			}
			if(sm.getSignature().contains("Recipe: void setAmtMilk(int)")){
				assertNull(ru.get("<coffeemaker.Recipe: java.lang.String name>"));
				assertNull(ru.get("<coffeemaker.Recipe: int price>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtCoffee>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtMilk>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtSugar>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtChocolate>"));
				continue;
			}
			if(sm.getSignature().contains("Recipe: int getAmtSugar()")){
				assertNull(ru.get("<coffeemaker.Recipe: java.lang.String name>"));
				assertNull(ru.get("<coffeemaker.Recipe: int price>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtCoffee>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtMilk>"));
				assertEquals(1,ru.get("<coffeemaker.Recipe: int amtSugar>").size());
				assertNull(ru.get("<coffeemaker.Recipe: int amtChocolate>"));
				continue;
			}
			if(sm.getSignature().contains("Recipe: void setAmtSugar(int)")){
				assertNull(ru.get("<coffeemaker.Recipe: java.lang.String name>"));
				assertNull(ru.get("<coffeemaker.Recipe: int price>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtCoffee>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtMilk>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtSugar>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtChocolate>"));
				continue;
			}
			if(sm.getSignature().contains("Recipe: java.lang.String getName()")){
				assertEquals(1,ru.get("<coffeemaker.Recipe: java.lang.String name>").size());
				assertNull(ru.get("<coffeemaker.Recipe: int price>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtCoffee>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtMilk>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtSugar>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtChocolate>"));
				continue;
			}
			if(sm.getSignature().contains("Recipe: void setName(java.lang.String)")){
				assertNull(ru.get("<coffeemaker.Recipe: java.lang.String name>"));
				assertNull(ru.get("<coffeemaker.Recipe: int price>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtCoffee>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtMilk>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtSugar>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtChocolate>"));
				continue;
			}
			if(sm.getSignature().contains("Recipe: int getPrice()")){
				assertNull(ru.get("<coffeemaker.Recipe: java.lang.String name>"));
				assertEquals(1,ru.get("<coffeemaker.Recipe: int price>").size());
				assertNull(ru.get("<coffeemaker.Recipe: int amtCoffee>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtMilk>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtSugar>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtChocolate>"));
				continue;
			}
			if(sm.getSignature().contains("Recipe: void setPrice(int)")){
				assertNull(ru.get("<coffeemaker.Recipe: java.lang.String name>"));
				assertNull(ru.get("<coffeemaker.Recipe: int price>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtCoffee>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtMilk>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtSugar>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtChocolate>"));
				continue;
			}
			if(sm.getSignature().contains("Recipe: boolean equals(coffeemaker.Recipe)")){
				assertEquals(2,ru.get("<coffeemaker.Recipe: java.lang.String name>").size());
				assertNull(ru.get("<coffeemaker.Recipe: int price>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtCoffee>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtMilk>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtSugar>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtChocolate>"));
				continue;
			}
			if(sm.getSignature().contains("Recipe: java.lang.String toString()")){
				assertEquals(1,ru.get("<coffeemaker.Recipe: java.lang.String name>").size());
				assertNull(ru.get("<coffeemaker.Recipe: int price>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtCoffee>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtMilk>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtSugar>"));
				assertNull(ru.get("<coffeemaker.Recipe: int amtChocolate>"));
				continue;
			}
			//TODO check CoffeeMaker and Invontory methods
//			fail();
		}
//		
//		//check associations
//		for(SootClass sc:ClassRegistry.getInstance().getClasses().keySet()){
//			AssociationsBuilder.build(sc);
//			ArrayList<Association> as = ClassRegistry.getInstance().getAssociations().get(sc);
//			if(sc.getName().contains("Msg")){
//				assertEquals(2, as.size());
//				continue;
//			}
//			if(sc.getName().contains("Storage")){
//				assertEquals(7, as.size());
//				continue;
//			}
//			fail();
//		}
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		Utilities.cleanAfterAnalysis("");
	}
}
