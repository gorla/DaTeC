package ch.unisi.inf.datec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import junit.framework.TestCase;
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


public class StorageAssociationsTest extends TestCase {
	
	

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
		
		Loader l = LoaderBuilder.getLoader("src/test/resources/storage.zip");
		try {
			l.load("src/test/resources/storage.zip");
		} catch (DatecLoaderException e) {
			e.printStackTrace();
		}		
		assertEquals(2, ClassRegistry.getInstance().getClasses().keySet().size());
		assertEquals(8, ClassRegistry.getInstance().getAllMethodsToAnalyze().size());

		CallGraphConstructor cgc = new CallGraphConstructor();
		CallGraph cg = cgc.getCallGraph();

		InterProceduralReachingDefinitionAnalysis ia = new InterProceduralReachingDefinitionAnalysis(cg,new DatecMethodFilter(),cg.sourceMethods(),true);
		InterProceduralReachableUsesAnalysis ie = new InterProceduralReachableUsesAnalysis(cg , new DatecMethodFilter(), cg.sourceMethods() , true);
		
		//check reaching definitions
		for(SootMethod sm:ClassRegistry.getInstance().getAllMethodsToAnalyze()){
			HashMap<String,ArrayList<Definition>> rd = ClassRegistry.getInstance().getMethodData(sm).getReachingDefs();
			if(sm.getSignature().contains("Msg: void <init>")){
				assertEquals(1,rd.get("<Msg: byte info>").size());
				continue;
			}
			if(sm.getSignature().contains("Msg: void setInfo(byte)")){
				assertEquals(1,rd.get("<Msg: byte info>").size());
				continue;
			}
			if(sm.getSignature().contains("Msg: byte getInfo()")){
				assertNull(rd.get("<Msg: byte info>"));
				continue;
			}
			if(sm.getSignature().contains("Storage: void <init>()")){
				assertEquals(1,rd.get("<Storage: Msg msg>").size());
				assertEquals(1,rd.get("<Storage: byte stored>").size());
				continue;
			}
			if(sm.getSignature().contains("Storage: void setStored(byte)")){
				assertNull(rd.get("<Storage: Msg msg>"));
				assertEquals(1,rd.get("<Storage: byte stored>").size());
				continue;
			}
			if(sm.getSignature().contains("Storage: byte getStored")){
				assertNull(rd.get("<Storage: Msg msg>"));
				assertNull(rd.get("<Storage: byte stored>"));
				continue;
			}
			if(sm.getSignature().contains("Storage: void recvMsg(Msg)")){
				assertNull(rd.get("<Storage: Msg msg>"));
				assertNull(rd.get("<Storage: byte stored>"));
				continue;
			}
			if(sm.getSignature().contains("Storage: void storeMsg()")){
				assertNull(rd.get("<Storage: Msg msg>"));
				assertEquals(1,rd.get("<Storage: byte stored>").size());
				continue;
			}
			fail();
		}
		
		//check reachable uses
		for(SootMethod sm:ClassRegistry.getInstance().getAllMethodsToAnalyze()){
			HashMap<String,ArrayList<Use>> ru = ClassRegistry.getInstance().getMethodData(sm).getReachableUses();
			if(sm.getSignature().contains("Msg: void <init>")){
				assertNull(ru.get("<Msg: byte info>"));
				continue;
			}
			if(sm.getSignature().contains("Msg: void setInfo(byte)")){
				assertNull(ru.get("<Msg: byte info>"));
				continue;
			}
			if(sm.getSignature().contains("Msg: byte getInfo()")){
				assertEquals(1,ru.get("<Msg: byte info>").size());
				continue;
			}
			if(sm.getSignature().contains("Storage: void <init>()")){
				assertNull(ru.get("<Storage: Msg msg>"));
				assertNull(ru.get("<Storage: byte stored>"));
				continue;
			}
			if(sm.getSignature().contains("Storage: void setStored(byte)")){
				assertNull(ru.get("<Storage: Msg msg>"));
				assertNull(ru.get("<Storage: byte stored>"));
				continue;
			}
			if(sm.getSignature().contains("Storage: byte getStored")){
				assertNull(ru.get("<Storage: Msg msg>"));
				assertEquals(1,ru.get("<Storage: byte stored>").size());
				continue;
			}
			if(sm.getSignature().contains("Storage: void recvMsg(Msg)")){
				assertEquals(1,ru.get("<Storage: Msg msg>").size());
				assertNull(ru.get("<Storage: byte stored>"));
				continue;
			}
			if(sm.getSignature().contains("Storage: void storeMsg()")){
				assertEquals(1,ru.get("<Storage: Msg msg>").size());
				assertNull(ru.get("<Storage: byte stored>"));
				continue;
			}
			fail();
		}
		
		//check associations
		for(SootClass sc:ClassRegistry.getInstance().getClasses().keySet()){
			AssociationsBuilder.build(sc);
			ArrayList<Association> as = ClassRegistry.getInstance().getAssociations().get(sc);
			if(sc.getName().contains("Msg")){
				assertEquals(2, as.size());
				continue;
			}
			if(sc.getName().contains("Storage")){
				assertEquals(7, as.size());
				continue;
			}
			fail();
		}
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		Utilities.cleanAfterAnalysis("");
	}
	
	
}
