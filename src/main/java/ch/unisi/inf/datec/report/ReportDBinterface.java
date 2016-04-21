/**
 * 
 */
package ch.unisi.inf.datec.report;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import ch.unisi.inf.datec.DatecProperties;

/**
 * @author Alessandra Gorla
 *
 */
public class ReportDBinterface {

	private static String filename = DatecProperties.getInstance().getProjectDirectory()+"datec.db";;
	private static Connection conn;
	
	
	public static ArrayList<ReportClass> getAllClasses(){
		String query = "SELECT * FROM classes;";
		
		ArrayList<ReportClass> list = new ArrayList<ReportClass>();
		Statement stat,stat1;
		ResultSet rs = null;
		
		try {
			Class.forName("org.sqlite.JDBC");
			if(conn == null || conn.isClosed()){
				conn = DriverManager.getConnection("jdbc:sqlite:"+filename);
			}
			stat = conn.createStatement();
			rs = stat.executeQuery(query);
			while (rs.next()){
				ReportClass c = new ReportClass();
				c.setNo(rs.getInt("id"));
				c.setClassName(rs.getString("name"));
				c.setIsabstract(rs.getBoolean("abstract"));
				c.setIsinterface(rs.getBoolean("interface"));
				
				query = "SELECT COUNT (id) FROM pairs WHERE class = "+ c.getNo() +";";
				stat1 = conn.createStatement();
				ResultSet r1 = stat1.executeQuery(query);
				r1.next();
				c.setCtxPairs(r1.getInt("COUNT (id)"));
				
				query = "SELECT COUNT (id) FROM pairs WHERE class = "+ c.getNo() +" AND covered = \"1\";";
				ResultSet r2 = stat1.executeQuery(query);
				r2.next();
				c.setCtxCovered(r2.getInt("COUNT (id)"));
				
				query = "SELECT COUNT (id) FROM nonctxpairs WHERE class = "+ c.getNo() +";";
				ResultSet r3 = stat1.executeQuery(query);
				r3.next();
				c.setNnCtxPairs(r3.getInt("COUNT (id)"));
				
				query = "SELECT COUNT (id) FROM nonctxpairs WHERE class = "+ c.getNo() +" AND covered = \"1\";";
				ResultSet r4 = stat1.executeQuery(query);
				r4.next();
				c.setNnCtxCovered(r4.getInt("COUNT (id)"));
				list.add(c);
			}
			rs.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * Returns all the fields listed in the db for the specified class 
	 * @param method
	 * @return
	 */
	public static ArrayList<String> getAllFields(int classId){
		String query = "SELECT DISTINCT field FROM pairs WHERE class="+classId+";";
		
		ArrayList<String> fields = new ArrayList<String>();
		Statement stat;
		ResultSet rs = null;
		
		try {
			Class.forName("org.sqlite.JDBC");
			if(conn == null || conn.isClosed()){
				conn = DriverManager.getConnection("jdbc:sqlite:"+filename);
			}
			stat = conn.createStatement();
			rs = stat.executeQuery(query);
			while (rs.next()){
				String f = rs.getString("field");
				fields.add(f);
			}
			rs.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return fields;
	}
	
	/**
	 * if all = false the method returns just the covered pairs
	 * @param method
	 * @return
	 */
	public static ArrayList<ReportPair> getAllPairs(String field, int classId, boolean all, boolean contextual){
		
		String query = "";
		if(all && contextual)
			query = "SELECT * FROM pairs WHERE field=\""+field+"\" AND class="+classId+";";
		if(!all && contextual)
			query = "SELECT * FROM pairs WHERE covered = 0 AND field=\""+field+"\" AND class="+classId+";";
		if(all && !contextual)
			query = "SELECT * FROM nonctxpairs WHERE field=\""+field+"\" AND class="+classId+";";
		if(!all && !contextual)
			query = "SELECT * FROM nonctxpairs WHERE covered = 0 AND field=\""+field+"\" AND class="+classId+";";
		
		ArrayList<ReportPair> list = new ArrayList<ReportPair>();
		Statement stat,stat1,stat2;
		ResultSet rs = null;
		
		try {
			Class.forName("org.sqlite.JDBC");
			if(conn == null || conn.isClosed()){
				conn = DriverManager.getConnection("jdbc:sqlite:"+filename);
			}
			stat = conn.createStatement();
			rs = stat.executeQuery(query);
			while (rs.next()){
				ReportPair c = new ReportPair();
				c.setId(rs.getInt("id"));
				c.setField(field);
				c.setClassId(classId);
				c.setDefId(rs.getInt("def"));
				c.setUseId(rs.getInt("use"));
				c.setCovered(rs.getBoolean("covered"));				
								
				query = "SELECT * FROM definitions WHERE idUniv = "+ c.getDefId() +";";
				stat1 = conn.createStatement();
				ResultSet r1 = stat1.executeQuery(query);
				r1.next();
				
				c.setDefContext(r1.getString("path"));
				c.setDefLoc(r1.getInt("loc"));
				
				query = "SELECT * FROM uses WHERE idUniv = "+ c.getUseId() +";";
				stat2 = conn.createStatement();
				ResultSet r2 = stat2.executeQuery(query);
				r2.next();
				
				c.setUseContext(r2.getString("path"));
				c.setUseLoc(r2.getInt("loc"));
				list.add(c);
			}
			rs.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return list;
	}	
}
