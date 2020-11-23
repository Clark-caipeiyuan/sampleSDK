
package com.xixi.sdk.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

public class LLRoomMenu extends LLLayoutXmlNode implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -174846386661146763L;
	public final static String CLASS_TAG = "menu";
	private String op; 
	private String url; 
	
	public boolean isCall() {
		if ( op == null ) return false ; 
		op = op.toLowerCase() ; 
		return op.equals("call");
	}
	public boolean isUrl(){ 
		return !TextUtils.isEmpty(url);
	}
	
	public List<LLXmlNode> getDevices() { 
		
		LLXmlNodeMap children = this.getChildren() ;
		List<LLXmlNode> total_list = new ArrayList<LLXmlNode>() ; 
		for(List<LLXmlNode> _lNode : children.values()) { 
			total_list.addAll(_lNode); 
		} 
		return total_list ; 
	}
  

	public LLRoomMenu() {
	}

	public LLRoomMenu(String id, String name, String url) {
		super(id , name );
	 
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}
 
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
