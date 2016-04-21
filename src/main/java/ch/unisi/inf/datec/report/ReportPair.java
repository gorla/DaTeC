package ch.unisi.inf.datec.report;

import java.util.StringTokenizer;

public class ReportPair {
	private int id;
	private int classId;
	private String field;
	private int defId;
	private String defContext;
	private int defLoc;
	private int useId;
	private String useContext;
	private int useLoc;
	private boolean covered;
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the classId
	 */
	public int getClassId() {
		return classId;
	}
	/**
	 * @param classId the classId to set
	 */
	public void setClassId(int classId) {
		this.classId = classId;
	}
	/**
	 * @return the field
	 */
	public String getField() {
		return field;
	}
	/**
	 * @param field the field to set
	 */
	public void setField(String field) {
		this.field = field;
	}
	/**
	 * @return the defId
	 */
	public int getDefId() {
		return defId;
	}
	/**
	 * @param defId the defId to set
	 */
	public void setDefId(int defId) {
		this.defId = defId;
	}
	/**
	 * @return the defContext
	 */
	public String getDefContext() {
		return defContext;
	}
	/**
	 * @param defContext the defContext to set
	 */
	public void setDefContext(String defContext) {
		this.defContext = defContext;
	}
	/**
	 * @return the defLoc
	 */
	public int getDefLoc() {
		return defLoc;
	}
	/**
	 * @param defLoc the defLoc to set
	 */
	public void setDefLoc(int defLoc) {
		this.defLoc = defLoc;
	}
	/**
	 * @return the useId
	 */
	public int getUseId() {
		return useId;
	}
	/**
	 * @param useId the useId to set
	 */
	public void setUseId(int useId) {
		this.useId = useId;
	}
	/**
	 * @return the useContext
	 */
	public String getUseContext() {
		return useContext;
	}
	/**
	 * @param useContext the useContext to set
	 */
	public void setUseContext(String useContext) {
		this.useContext = useContext;
	}
	/**
	 * @return the useLoc
	 */
	public int getUseLoc() {
		return useLoc;
	}
	/**
	 * @param useLoc the useLoc to set
	 */
	public void setUseLoc(int useLoc) {
		this.useLoc = useLoc;
	}
	/**
	 * @return the covered
	 */
	public boolean isCovered() {
		return covered;
	}
	/**
	 * @param covered the covered to set
	 */
	public void setCovered(boolean covered) {
		this.covered = covered;
	}
	
	/**
	 * @return the context represented only by the method containing the definition 
	 */
	public String getSimpleDefContext(){
		StringTokenizer st = new StringTokenizer(this.defContext,">");
		String token = "";
		while(st.hasMoreTokens())
			token = st.nextToken();
		token = token + ">";
		return token;
	}
	
	/**
	 * @return the context represented only by the method containing the use
	 */
	public String getSimpleUseContext(){
		StringTokenizer st = new StringTokenizer(this.getUseContext(),">");
		String token = "";
		while(st.hasMoreTokens())
			token = st.nextToken();
		token = token + ">";
		return token;
	}
	
}
