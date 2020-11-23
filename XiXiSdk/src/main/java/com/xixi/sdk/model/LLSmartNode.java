package com.xixi.sdk.model;

import java.io.Serializable;

public class LLSmartNode extends LLXmlNode implements Serializable {

	public LLSmartNode(){
		 
	}
	
	public LLSmartNode(String userId) {
		super();
		this.userId = userId;
	}

	private  String userId ;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}
