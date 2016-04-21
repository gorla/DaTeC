package ch.unisi.inf.datec.report;

import java.text.DecimalFormat;

public class ReportClass {
	private int no;
	private String className;
	private int ctxPairs;
	private int ctxCovered;
	private int nnCtxPairs;
	private int nnCtxCovered;
	private boolean isabstract;
	private boolean isinterface;
	
	/**
	 * @return the no
	 */
	public int getNo() {
		return no;
	}
	/**
	 * @param no the no to set
	 */
	public void setNo(int no) {
		this.no = no;
	}
	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}
	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}
	/**
	 * @return the ctxPairs
	 */
	public int getCtxPairs() {
		return ctxPairs;
	}
	/**
	 * @param ctxPairs the ctxPairs to set
	 */
	public void setCtxPairs(int ctxPairs) {
		this.ctxPairs = ctxPairs;
	}
	/**
	 * @return the ctxCovered
	 */
	public int getCtxCovered() {
		return ctxCovered;
	}
	/**
	 * @param ctxCovered the ctxCovered to set
	 */
	public void setCtxCovered(int ctxCovered) {
		this.ctxCovered = ctxCovered;
	}
	/**
	 * @return the nnCtxPairs
	 */
	public int getNnCtxPairs() {
		return nnCtxPairs;
	}
	/**
	 * @param nnCtxPairs the nnCtxPairs to set
	 */
	public void setNnCtxPairs(int nnCtxPairs) {
		this.nnCtxPairs = nnCtxPairs;
	}
	/**
	 * @return the nnCtxCovered
	 */
	public int getNnCtxCovered() {
		return nnCtxCovered;
	}
	/**
	 * @param nnCtxCovered the nnCtxCovered to set
	 */
	public void setNnCtxCovered(int nnCtxCovered) {
		this.nnCtxCovered = nnCtxCovered;
	}
	/**
	 * @return the isabstract
	 */
	public boolean isIsabstract() {
		return isabstract;
	}
	/**
	 * @param isabstract the isabstract to set
	 */
	public void setIsabstract(boolean isabstract) {
		this.isabstract = isabstract;
	}
	/**
	 * @return the isinterface
	 */
	public boolean isIsinterface() {
		return isinterface;
	}
	/**
	 * @param isinterface the isinterface to set
	 */
	public void setIsinterface(boolean isinterface) {
		this.isinterface = isinterface;
	}
	
	public String getPercCovered(boolean contextual){
		float pairsTot = 0;
		float pairsCov = 0;
		if(contextual){
			pairsTot = this.getCtxPairs();		
			pairsCov = this.getCtxCovered();
		}
		else{
			pairsTot = this.getNnCtxPairs();
			pairsCov = this.getNnCtxCovered();
		}
		float perc = (pairsCov/pairsTot)*100;
		String percS = new DecimalFormat("0.##").format((double)perc);
		return percS;
	}
	
	

}
