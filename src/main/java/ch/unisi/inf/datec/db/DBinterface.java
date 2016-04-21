/**
 * 
 */
package ch.unisi.inf.datec.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import soot.SootClass;
import soot.SootMethod;
import ch.unisi.inf.datec.DatecProperties;
import ch.unisi.inf.datec.data.Association;
import ch.unisi.inf.datec.data.ClassRegistry;
import ch.unisi.inf.datec.data.Definition;
import ch.unisi.inf.datec.data.Use;

/**
 * @author Alessandra Gorla
 *
 */
public class DBinterface {

	private static String filename = DatecProperties.getInstance().getProjectDirectory()+"datec.db";
	private static Connection conn;
	
	private static void createSchema() throws ClassNotFoundException, SQLException{
	    Statement stat = conn.createStatement();
	    
	    stat.executeUpdate("create table classes (id, name, interface, abstract, locs);");
	    stat.executeUpdate("create table methods (id, name, class, constructor, abstract, static);");
	    stat.executeUpdate("create table definitions (idUniv, id, field, method, loc, path, predDef);");
	    stat.executeUpdate("create table uses (idUniv, id, field, method, loc, path, predUse);");
	    stat.executeUpdate("create table pairs (id, field, class, def, use, covered);");
	    stat.executeUpdate("create table nonctxpairs (id, field, class, def, use, covered);");
	}
	
	/**
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 * 
	 */
	private static void storeClasses() throws ClassNotFoundException, SQLException{
	    PreparedStatement prep = conn.prepareStatement("insert into classes values (?, ?, ?, ?, ?);");
	    
	    int classId = 1;
	    for(SootClass sc:ClassRegistry.getInstance().getClasses().keySet()){
	    	prep.setInt(1, classId);
	    	prep.setString(2, sc.getName());
	    	prep.setBoolean(3, sc.isInterface());
	    	prep.setBoolean(4, sc.isAbstract());
	    	prep.setInt(5, 0);// TODO set the loc of class
	    	prep.addBatch();
	    	classId++;
	    }
	    conn.setAutoCommit(false);
	    prep.executeBatch();
	    conn.setAutoCommit(true);
	}
	
	/**
	 * Store all the methods information into the methods table
	 * 
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	private static void storeMethods() throws SQLException, ClassNotFoundException{

	    PreparedStatement prep = conn.prepareStatement("insert into methods values (?, ?, ?, ?, ?, ?);");
	    
	    int methodId = 1;
	    
	    for(SootClass sc:ClassRegistry.getInstance().getClasses().keySet()){
	    	Statement stat = conn.createStatement();
	    	ResultSet rs = stat.executeQuery("select id from classes where name=\""+ sc.getName()+"\";");
	    	String classId = "";
	    	while (rs.next())
		        classId = rs.getString("id");
		    rs.close();
	    	for(SootMethod sm:ClassRegistry.getInstance().getClasses().get(sc)){
	    		prep.setInt(1, methodId);
	    		prep.setString(2, sm.getSignature());
	    		prep.setString(3, classId);
	    		prep.setBoolean(4, sm.getName().contains("<init>"));
	    		prep.setBoolean(5, sm.isAbstract());
	    		prep.setBoolean(6, sm.isStatic());
	    		prep.addBatch();
	    		methodId++;
	    	}
	    }
	    conn.setAutoCommit(false);
	    prep.executeBatch();
	    conn.setAutoCommit(true);
	      
	}
	
	/**
	 * Store all the definitions into the definitions table
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private static void storeDefs() throws ClassNotFoundException, SQLException{
		//create table definitions (idUniv, id, field, method, loc, path, predDef);

	    PreparedStatement prep = conn.prepareStatement("insert into definitions values (?, ?, ?, ?, ?, ?, ?);");
	    int idUniv = 1;
	    for(SootMethod sm:ClassRegistry.getInstance().getMethods().keySet()){
	    	HashMap<String, ArrayList<Definition>> defs = ClassRegistry.getInstance().getMethodData(sm).getReachingDefs();
	    	for(String field:defs.keySet()){
	    		ArrayList<Definition> defsArrayList = defs.get(field);
	    		for(Definition d:defsArrayList){
	    			prep.setInt(1, idUniv);
	    			prep.setString(2, d.getIdDef());
	    			
	    			if(d.getName().contains("*")){
	    				d.getName().replace('*', ' ');
	    				String r = "";

	    				for (int i = 0; i < d.getName().length(); i ++) {
	    					if (d.getName().charAt(i) != '*') r += d.getName().charAt(i);
	    				}
	    				prep.setString(3, r);
	    			}else
	    				prep.setString(3, d.getName());
	    			
	    			String query = "select * from methods where name=\""+ sm.getSignature()+"\";"; 
	    			ResultSet rs = executeQuery(query);
	    			int methodId = 0;
	    	    	while (rs.next())
	    		        methodId = rs.getInt("id");
	    		    rs.close();
	    			prep.setInt(4, methodId);
	    			prep.setInt(5, d.getLineNumber());
	    			prep.setString(6, d.getContextAsString());
	    			prep.setInt(7, 0); //TODO set predDef id if necessary
	    			prep.addBatch();
	    			idUniv++;
	    		}
	    	}
	    	
	    }
	    conn.setAutoCommit(false);
	    prep.executeBatch();
	    conn.setAutoCommit(true);
	      
	}
	
	/**
	 * Store all the uses into the uses table
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private static void storeUses() throws ClassNotFoundException, SQLException{
		//create table uses (idUniv, id, field, method, loc, path, predUse);

	    PreparedStatement prep = conn.prepareStatement("insert into uses values (?, ?, ?, ?, ?, ?, ?);");
	    int idUniv = 1;
	    for(SootMethod sm:ClassRegistry.getInstance().getMethods().keySet()){
	    	HashMap<String, ArrayList<Use>> uses = ClassRegistry.getInstance().getMethodData(sm).getReachableUses();
	    	for(String field:uses.keySet()){
	    		ArrayList<Use> usesArrayList = uses.get(field);
	    		for(Use u:usesArrayList){
	    			prep.setInt(1, idUniv);
	    			prep.setString(2, u.getIdUse());
	    			prep.setString(3, u.getName());
	    			String query = "select id from methods where name=\""+ sm.getSignature()+"\";"; 
	    			ResultSet rs = executeQuery(query);
	    			int methodId = 0;
	    	    	while (rs.next())
	    		        methodId = rs.getInt("id");
	    		    rs.close();
	    			prep.setInt(4, methodId);
	    			prep.setInt(5, u.getLineNumber());
	    			prep.setString(6, u.getContextAsString());
	    			prep.setInt(7, 0); //TODO set predUse id if necessary
	    			prep.addBatch();
	    			idUniv++;	    			
	    		}
	    	}
	    }
	    conn.setAutoCommit(false);
	    prep.executeBatch();
	    conn.setAutoCommit(true);
	}
	
	/**
	 * Store all the pairs into the pairs table
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private static void storePairs() throws ClassNotFoundException, SQLException{
		//create table pairs (id, field, class, def, use);

	    PreparedStatement prep = conn.prepareStatement("insert into pairs values (?, ?, ?, ?, ?, ?);");
	    
	    int idPair = 1;
	    for(SootClass sc:ClassRegistry.getInstance().getAssociations().keySet()){
	    	ArrayList<Association> pairs = ClassRegistry.getInstance().getAssociations().get(sc);
	    	for(Association a:pairs){			
	    		prep.setInt(1, idPair);
	    		prep.setString(2, a.getDef().getName());
	    		
	    		String query = "select id from classes where name=\""+ sc.getName() +"\";"; 
    			ResultSet rs = executeQuery(query);
    			int classId = 0;

    			
    	    	if (rs.next()){
    	    		classId = rs.getInt("id");
    	    	}
    		    rs.close();
	    		prep.setInt(3, classId);
	    		
	    		query = "select idUniv from definitions where id=\""+ a.getDef().getIdDef() +"\" and path=\""+ a.getDef().getContextAsString() +"\";"; 
    			rs = executeQuery(query);
    			int defId = 0;
    	    	while (rs.next())
    	    		defId = rs.getInt("idUniv");
    		    rs.close();
	    		prep.setInt(4, defId);
	    		
	    		
	    		query = "select idUniv from uses where id=\""+ a.getUse().getIdUse() +"\" and path=\""+ a.getUse().getContextAsString() +"\";";
    			rs = executeQuery(query);
    			int useId = 0;
    	    	while (rs.next())
    	    		useId = rs.getInt("idUniv");
    		    rs.close();
	    		prep.setInt(5, useId);
	    		prep.setBoolean(6, false);
	    		idPair++;
	    		prep.addBatch();
	    	}	    	
	    }
	    conn.setAutoCommit(false);
	    prep.executeBatch();
	    conn.setAutoCommit(true);
	      
	}
	
	/**
	 * Store all the pairs into the pairs table
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private static void storeNonCtxPairs() throws ClassNotFoundException, SQLException{
		//create table nonctxpairs (id, field, class, def, use);
		
		PreparedStatement prep = conn.prepareStatement("insert into nonctxpairs values (?, ?, ?, ?, ?, ?);");
		String query = "SELECT pairs.field, pairs.class, pairs.def, pairs.use, definitions.id, uses.id FROM pairs INNER JOIN definitions ON pairs.def=definitions.idUniv INNER JOIN uses ON pairs.use=uses.idUniv GROUP BY definitions.id, uses.id, pairs.class ORDER BY pairs.class ASC;";
		ResultSet rs = executeQuery(query);
		
		int id = 1;
		while(rs.next()){
			prep.setInt(1, id);
			prep.setString(2, rs.getString("field"));
			prep.setInt(3, rs.getInt("class"));
			prep.setInt(4, rs.getInt("def"));
			prep.setInt(5, rs.getInt("use"));
			prep.setBoolean(6, false);
			prep.addBatch();
			id ++;
		}
		conn.setAutoCommit(false);
	    prep.executeBatch();
	    conn.setAutoCommit(true);
	    rs.close();
	}
	
	/**
	 * Executes the query and returns the result set
	 * @param query
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private static ResultSet executeQuery(String query){   
		Statement stat;
		ResultSet rs = null;
		try {
			Class.forName("org.sqlite.JDBC");
			if(conn.isClosed())
			  conn = DriverManager.getConnection("jdbc:sqlite:"+filename);
			stat = conn.createStatement();
			rs = stat.executeQuery(query);	    	
		} catch (SQLException e) {
		//	e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	    return rs;
	}
	
	/**
	 * Store the class registry in a new database database.
	 */
	public static void write(){
		try {
			Class.forName("org.sqlite.JDBC");
			if(conn == null || conn.isClosed()){
				conn = DriverManager.getConnection("jdbc:sqlite:"+filename);
			}
			createSchema();
			storeClasses();
			storeMethods();
			storeDefs();
			storeUses();
			storePairs();
			storeNonCtxPairs();
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks whether the method should be instrumented or not. 
	 * @param method
	 * @return
	 */
	public static boolean shouldBeInstrumented(String method){
		String query = "SELECT id FROM methods WHERE name=\""+ method+"\";";
		
    	//if we find the method in the table in means that it has been analyzed => we have to instrument it
		// TODO instrument only if the method contains some def or use.
		try {
			Class.forName("org.sqlite.JDBC");
			if(conn == null || conn.isClosed()){
				conn = DriverManager.getConnection("jdbc:sqlite:"+filename);
			}
			ResultSet rs = executeQuery(query);
			if (rs.next()){
				rs.close();
				conn.close();
				return true;
			}
			rs.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Checks whether the definition should be instrumented or not. 
	 * @param id of the def/use
	 * @param type 1 for definition, 2 for use
	 * @return
	 */
	public static boolean shouldBeInstrumented(String id, int type){
		String query = null;
		if(type == 1)
			query = "SELECT * FROM definitions WHERE id=\""+ id+"\";";
		else
			query = "SELECT * FROM uses WHERE id=\""+ id+"\";";		
    	//if we find the definition/use in the table in means that it has been analyzed => we have to instrument it
		try {
			Class.forName("org.sqlite.JDBC");
			if(conn == null || conn.isClosed()){
				conn = DriverManager.getConnection("jdbc:sqlite:"+filename);
			}
			ResultSet rs = executeQuery(query);
			if (rs.next()){
				rs.close();
				conn.close();
				return true;
			}
			rs.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Returns all the pairs that include the definition provided as a parameter
	 * @param id of the def
	 * @return
	 */
	public static ArrayList<Integer> getAllPairs(String idDef, String context, boolean contextual){
		ArrayList<Integer> pairs = new ArrayList<Integer>();
		String query = "SELECT * FROM definitions WHERE id=\""+ idDef+"\";";
		//if we find the definition/use in the table in means that it has been analyzed => we have to instrument it
		try {
			Class.forName("org.sqlite.JDBC");
			if(conn == null || conn.isClosed())
				conn = DriverManager.getConnection("jdbc:sqlite:"+filename);
			ResultSet rs = executeQuery(query);
			ArrayList<Integer> ll = new ArrayList<Integer>();
			while (rs.next()){
				String path = rs.getString("path");
				int id = rs.getInt("idUniv");
				//check the context only for contextual pairs
				if(contextual && context.contains(path))
				    ll.add(id);
				if(!contextual)
					ll.add(id);
			}
			rs.close();
			for (Integer ii:ll){
				//select all the pairs that contain that def
				if(contextual)
					query = "SELECT id FROM pairs WHERE covered=0 AND def="+ ii+";"; 
				else
					query = "SELECT id FROM nonctxpairs WHERE covered=0 AND def="+ ii+";"; 
				
				ResultSet rsPairs = executeQuery(query);
				while(rsPairs.next()){
					int idPair = rsPairs.getInt("id");
					pairs.add(idPair);
				}
			}

			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return pairs;
	}

	public static ArrayList<Integer> getAllKilledPairs(String field, boolean contextual) {
		ArrayList<Integer> pairs = new ArrayList<Integer>();
		String query = "SELECT idUniv FROM definitions WHERE field=\""+ field+"\";";

		try {
			Class.forName("org.sqlite.JDBC");
			if(conn == null || conn.isClosed())
				conn = DriverManager.getConnection("jdbc:sqlite:"+filename);
			ResultSet rs = executeQuery(query);
			ArrayList<Integer> fieldDefs = new ArrayList<Integer>();
			while (rs.next()){
				int id = rs.getInt("idUniv");
				fieldDefs.add(id);
			}
			rs.close();
			
			for(Integer fdef:fieldDefs){
				//select all the pairs that contain that def
				String query2 = "";
				if(contextual)
					query2 = "SELECT id FROM pairs WHERE covered=0 AND def="+ fdef+";";
				else
					query2 = "SELECT id FROM nonctxpairs WHERE covered=0 AND def="+ fdef+";";
				ResultSet rsPairs = executeQuery(query2);
				while(rsPairs.next()){
					int idPair = rsPairs.getInt("id");
					pairs.add(idPair);
				}
				rsPairs.close();
			}
			
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return pairs;
	}

	public static ArrayList<Integer> getCovered(String useid, Set<Integer> pairDefCov, String context, boolean contextual) {
		ArrayList<Integer> pairs = new ArrayList<Integer>();
		String query = "SELECT * FROM uses WHERE id=\""+ useid+"\";";
		//if we find the definition/use in the table in means that it has been analyzed => we have to instrument it
		try {
			Class.forName("org.sqlite.JDBC");
			if(conn == null || conn.isClosed())
				conn = DriverManager.getConnection("jdbc:sqlite:"+filename);
			ResultSet rs = executeQuery(query);
			ArrayList<Integer> ll = new ArrayList<Integer>();
			while (rs.next()){
				String path = rs.getString("path");
				int id = rs.getInt("idUniv");
				//check context only on contextual pairs
				if(contextual && context.contains(path))
				    ll.add(id);
				if(!contextual)
					ll.add(id);
			}
			rs.close();
			for (Integer ii:ll){
				//select all the pairs that contain that def
				if(contextual)
					query = "SELECT id FROM pairs WHERE covered=0 AND use="+ ii+";";
				else
					query = "SELECT id FROM nonctxpairs WHERE covered=0 AND use="+ ii+";";
				
				ResultSet rsPairs = executeQuery(query);
				ArrayList<Integer> covUse = new ArrayList<Integer>();
				while(rsPairs.next()){					
					int idPair = rsPairs.getInt("id");
					covUse.add(idPair);
				}
				for(int in:covUse){
					if(pairDefCov.contains(in)){
						pairs.add(in);
						if(contextual)
							query = "UPDATE pairs SET covered='1' WHERE ROWID="+ in+";";
						else
							query = "UPDATE nonctxpairs SET covered='1' WHERE ROWID="+ in+";";
						executeQuery(query);
					}
				}
			}

			conn.close();
		} catch (SQLException e) {
//			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return pairs;

	}
	
}
