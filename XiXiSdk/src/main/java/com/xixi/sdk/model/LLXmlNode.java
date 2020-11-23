package com.xixi.sdk.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class LLXmlNode implements Serializable{
	
	/**
	 * 
	 */ 

	public final static String ROOT_ELEMENT_NAME = "body" ; 
	public String getId() { return null ; }
	public String getName() { return null ; }

	private String xmlNodeName ; 
	public String getXmlNodeName() {
		return xmlNodeName;
	}
 	public void setXmlNodeName(String xmlName) {
		this.xmlNodeName = xmlName;
	}

	public LLXmlNodeMap getChildren() {
		return children;
	}

	private final  LLXmlNodeMap  children = new LLXmlNodeMap() ;
	public void setChildren(LLXmlNodeMap children) {
		this.children.clear();
		if ( children !=null ) { 
			this.children.putAll(children);
		}
	} 
	 
	
	public static class LLXmlNodeMap extends HashMap<String, List<LLXmlNode>>{

		/**
		 * 
		 */
		private static final long serialVersionUID = 245741717189284932L; 
		
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -3297963405979542239L;

}
