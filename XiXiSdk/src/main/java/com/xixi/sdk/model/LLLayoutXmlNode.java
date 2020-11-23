package com.xixi.sdk.model;

import java.io.Serializable;

public class LLLayoutXmlNode extends LLXmlNode implements Serializable{
	

	public LLLayoutXmlNode() {  } 
	public LLLayoutXmlNode(String id , String name) { 
		this.id = id ; 
		this.name = name ; 
	}
	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	private String id;
	private String name;
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = -6683672680233260170L;
	/**
	 * 
	 */ 
	/**
	 * 
	 */ 
	
}
