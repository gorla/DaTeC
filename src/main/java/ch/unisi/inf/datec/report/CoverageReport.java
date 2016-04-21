package ch.unisi.inf.datec.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import ch.unisi.inf.datec.DatecProperties;

/**
 * HTML report creator
 * @author Alessandra Gorla
 *
 */
public class CoverageReport {
	
	/**
	 * Report output directory
	 */
	private static String reportDir = DatecProperties.getInstance().getPathReport();
	
	public static void createReport(){
		checkReportPath();
		printClassesReport();
	}

	
	/**
	 * Creates the classes report file
	 */
	private static void printClassesReport() {
		BufferedWriter out = null;
		try{
		    // Create file 
		    FileWriter fstream = new FileWriter(reportDir+"DaTeC-CoverageReport.html");
		    out = new BufferedWriter(fstream);
		 }catch (Exception e){
		      System.err.println("Error: " + e.getMessage());
		 }
		 
		String s = "";
		s = s+"<html><head><title>DaTeC coverage report</title>"+'\n';
		s = s+"<link rel='stylesheet' type='text/css' media='screen' href='style.css' /></head>";
		
		s = s+"<body>";
		
		s = s+"<div id='header'><div class='wrapper'><strong id='blog-title'>"+ DatecProperties.getInstance().getPrjName() +"</strong>";
		s = s+"<p id='blog-description'>DaTeC Coverage Report</p></div><!-- .wrapper -->";
		s = s+"</div><!-- #header --><div id='sub-header'></div>";
		s = s+"<div class='wrapper'><div id='content'><h1> </h1>";
		
		s = s+"<table>";
		s = s+"<tr><th>No.</th><th>Class</th><th>Ctx Pairs</th><th>% Ctx Covered</th><th>Non-Ctx Pairs</th><th>% Non-Ctx Covered</th></tr>";
		ArrayList<ReportClass> classesList = ReportDBinterface.getAllClasses();
		float totPairs = 0;
		float totCov = 0;
		float totPairsNC = 0;
		float totCovNC = 0;
		
		for(ReportClass c :classesList){			
			s = s+"<tr>";
			s = s+"<td>" + c.getNo() + "</td>";
			s = s+"<td>";
			if(c.isIsabstract())
				s = s+" -A- "; //TODO image
			if(c.isIsinterface())
				s = s+" -I- "; //TODO image
			s = s+ replaceTags(c.getClassName()) + "</td>"; //TODO link
			s = s+"<td><a href='"+ c.getClassName()+"-AllAssociations.html'>" + c.getCtxPairs() + "</a></td>";
			s = s+"<td><a href='"+ c.getClassName()+"-AssociationsToCover.html'>" + c.getPercCovered(true) + "% ("+ (c.getCtxPairs()-c.getCtxCovered()) + " to cover)</a></td>";
			s = s+"<td><a href='"+ c.getClassName()+"-AllAssociations-NC.html'>" + c.getNnCtxPairs() + "</a></td>";
			s = s+"<td><a href='"+ c.getClassName()+"-AssociationsToCover-NC.html'>" + c.getPercCovered(false) + "% ("+ (c.getNnCtxPairs()-c.getNnCtxCovered()) + " to cover)</a></td>";
			s = s+"</tr>"+'\n';			
			totPairs = totPairs + c.getCtxPairs();
			totCov = totCov + c.getCtxCovered();
			
			totPairsNC = totPairsNC + c.getNnCtxPairs();
			totCovNC = totCovNC + c.getNnCtxCovered();
			
			//for each class print the class details:
			printClassAssociationsReport(c.getNo(),c.getClassName(),true);
			printClassAssociationsReport(c.getNo(),c.getClassName(),false);
			printClassAssociationsNCReport(c.getNo(),c.getClassName(),true);
			printClassAssociationsNCReport(c.getNo(),c.getClassName(),false);
		}
		s = s+"<tr><td colspan=6><br></td></tr>";
		s = s+"<tr><td colspan=2>Total:</td><td>"+new DecimalFormat("0").format((double)(totPairs))+"</td><td>"+new DecimalFormat("0.##").format((double)(totCov/totPairs)*100)+"% ("+new DecimalFormat("0").format((double)((totPairs-totCov))) +" to cover)</td>";
		s = s+"<td>"+new DecimalFormat("0").format((double)(totPairsNC))+"</td><td>"+new DecimalFormat("0.##").format((double)(totCovNC/totPairsNC)*100)+"% ("+new DecimalFormat("0").format((double)((totPairsNC-totCovNC))) +" to cover)</td></tr>";
		s = s+"</div></div></table></body></html>";
		
		 try{
			out.write(s);
			s = "";
		    out.close();
		 }catch (Exception e){
		      System.err.println("Error: " + e.getMessage());
		 }

	}


	/**
	 * Creates the classes report file
	 */
	private static void printClassAssociationsReport(int classId, String className, boolean all) {
		
		BufferedWriter out = null;
		
		try{
		    // Create file 
		    FileWriter fstream = null;
		    if(all){
		    	fstream = new FileWriter(reportDir+className+"-AllAssociations.html");
		    }else
		    	fstream = new FileWriter(reportDir+className+"-AssociationsToCover.html");
		    out = new BufferedWriter(fstream);
		 }catch (Exception e){
		      System.err.println("Error: " + e.getMessage());
		 }
		 
		String s = "";
		if(all)
			s = s+"<html><head><title>DaTeC - "+className +" all associations</title>"+'\n';
		else
			s = s+"<html><head><title>DaTeC - "+className +" associations to cover</title>"+'\n';
		
		s = s+"<link rel='stylesheet' type='text/css' media='screen' href='style.css' /></head>";
		
		s = s+"<body>";
		
		s = s+"<div id='header'><div class='wrapper'><strong id='blog-title'>"+ DatecProperties.getInstance().getPrjName() +"</strong>";
		if(all)
			s = s+"<p id='blog-description'>DaTeC - "+className +" all associations</p></div><!-- .wrapper -->";
		else
			s = s+"<p id='blog-description'>DaTeC - "+className +" associations to cover</p></div><!-- .wrapper -->";
		
		s = s+"</div><!-- #header --><div id='sub-header'></div>";
		s = s+"<div class='wrapper'><div id='content'><h1> </h1>";
		
		s = s+"<table>";
		s = s+"<tr><th>Instance variables</th><th>Pairs</th></tr>";
		
		ArrayList<String> fields = ReportDBinterface.getAllFields(classId);
		
		for(String f :fields){
			ArrayList<ReportPair> pairs = null;
			if(all)
				pairs = ReportDBinterface.getAllPairs(f, classId, true, true);
			else
				pairs = ReportDBinterface.getAllPairs(f, classId, false, true);
			s = s+"<tr>";
			s = s+"<td class='field'>" + replaceTags(f) + "</td>";
			s = s+"<td class= 'field'>"+ pairs.size() +"</td></tr>";
			HashMap<String,ArrayList<ReportPair>> hm = groupPairsPerDefs(pairs, true);
			s = s+"<tr><td colspan=2 class='fieldContent'><div><table>";
			for(String dc:hm.keySet()){
				s = s +"<tr><td><b>DEF</b>: "+replaceTags(dc)+"</td></tr>";
				s = s + "<tr><td><div><table>";  
				ArrayList<ReportPair> rp = hm.get(dc);
				for(ReportPair p:rp){
					String u = p.getUseContext()+"<Line: "+p.getUseLoc()+">";
					if(p.isCovered())
						s = s+"<tr><td class='covered'>";
					else 
						s = s+"<tr><td>";
					s= s+ "<b>USE</b>: "+replaceTags(u) +"</td></tr>";
				}
				
				s = s+"</table></div></td></tr>";
			}
			s = s+"</table></div>";
			s = s+"</td></tr>"+'\n';			
		}
		
		s = s+"</table></div></div></body></html>";
		
		 try{
			out.write(s);
			s = "";
		    out.close();
		 }catch (Exception e){
		      System.err.println("Error: " + e.getMessage());
		 }
	}
	
	/**
	 * Creates the classes report file
	 */
	private static void printClassAssociationsNCReport(int classId, String className, boolean all) {
		
		BufferedWriter out = null;
		
		try{
		    // Create file 
		    FileWriter fstream = null;
		    if(all){
		    	fstream = new FileWriter(reportDir+className+"-AllAssociations-NC.html");
		    }else
		    	fstream = new FileWriter(reportDir+className+"-AssociationsToCover-NC.html");
		    out = new BufferedWriter(fstream);
		 }catch (Exception e){
		      System.err.println("Error: " + e.getMessage());
		 }
		 
		String s = "";
		if(all)
			s = s+"<html><head><title>DaTeC - "+className +" all non contextual associations</title>"+'\n';
		else
			s = s+"<html><head><title>DaTeC - "+className +" non contextual associations to cover</title>"+'\n';
		
		s = s+"<link rel='stylesheet' type='text/css' media='screen' href='style.css' /></head>";
		
		s = s+"<body>";
		
		s = s+"<div id='header'><div class='wrapper'><strong id='blog-title'>"+ DatecProperties.getInstance().getPrjName() +"</strong>";
		if(all)
			s = s+"<p id='blog-description'>DaTeC - "+className +" all non contextual associations</p></div><!-- .wrapper -->";
		else
			s = s+"<p id='blog-description'>DaTeC - "+className +" non contextual associations to cover</p></div><!-- .wrapper -->";
		
		s = s+"</div><!-- #header --><div id='sub-header'></div>";
		s = s+"<div class='wrapper'><div id='content'><h1> </h1>";
		
		s = s+"<table>";
		s = s+"<tr><th>Instance variables</th><th>Pairs</th></tr>";
		
		ArrayList<String> fields = ReportDBinterface.getAllFields(classId);
		
		for(String f :fields){
			ArrayList<ReportPair> pairs = null;
			if(all)
				pairs = ReportDBinterface.getAllPairs(f, classId, true, false);
			else
				pairs = ReportDBinterface.getAllPairs(f, classId, false, false);
			s = s+"<tr>";
			s = s+"<td class='field'>" + replaceTags(f) + "</td>";
			s = s+"<td class= 'field'>"+ pairs.size() +"</td></tr>";
			HashMap<String,ArrayList<ReportPair>> hm = groupPairsPerDefs(pairs,false);
			s = s+"<tr><td colspan=2 class='fieldContent'><div><table>";
			for(String dc:hm.keySet()){
				s = s +"<tr><td><b>DEF</b>: "+replaceTags(dc)+"</td></tr>";
				s = s + "<tr><td><div><table>";  
				ArrayList<ReportPair> rp = hm.get(dc);
				for(ReportPair p:rp){
					String u = p.getSimpleUseContext()+"<Line: "+p.getUseLoc()+">";
					if(p.isCovered())
						s = s+"<tr><td class='covered'>";
					else 
						s = s+"<tr><td>";
					s= s+ "<b>USE</b>: "+replaceTags(u) +"</td></tr>";
				}
				
				s = s+"</table></div></td></tr>";
			}
			s = s+"</table></div>";
			s = s+"</td></tr>"+'\n';			
		}
		
		s = s+"</table></div></div></body></html>";
		
		 try{
			out.write(s);
			s = "";
		    out.close();
		 }catch (Exception e){
		      System.err.println("Error: " + e.getMessage());
		 }
	}
		
	/**
	 * Replace the less than and greater than tags to create HTML reports
	 * @param s
	 * @return
	 */
	private static String replaceTags(String s){
		s = s.replaceAll("<", "&lt;");
		s = s.replaceAll(">", "&gt;");
		return s;
	}
	
	/**
	 * Create the report path if it does not exist yet
	 */
	private static void checkReportPath(){
		File reportDirF = new File(reportDir);
		if(!reportDirF.exists()){
			reportDirF.mkdirs();
		}
	}
	
	private static HashMap<String, ArrayList<ReportPair>> groupPairsPerDefs(ArrayList<ReportPair> pairs, boolean contextual){
		HashMap<String, ArrayList<ReportPair>> hm = new HashMap<String, ArrayList<ReportPair>>();
		for(ReportPair rp:pairs){
			String dc = "";
			if(contextual)
				dc = rp.getDefContext();
			else
				dc = rp.getSimpleDefContext();
			int loc = rp.getDefLoc();
			dc = dc.concat("<Line: "+loc+">");
			ArrayList<ReportPair> hmrp = null;
			if(hm.containsKey(dc))
				hmrp = hm.get(dc);
			else
				hmrp = new ArrayList<ReportPair>();
			hmrp.add(rp);
			hm.put(dc, hmrp);			
		}				
		return hm;
	}

}
