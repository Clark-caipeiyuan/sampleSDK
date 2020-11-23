package com.xixi.sdk.sipmsg.params; 

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class LLSipLiftManageCmdBase extends LLSipLiftManageResponse {

	final private List<String> macs = new ArrayList<String>() ; 
	
	public List<String> getMacs() {
		return macs;
	} 

    public void setPeerIds(String[] peerIds ) { 
    	this.setPeerIds(Arrays.asList(peerIds));
    }
    
	public void setPeerIds(Collection<String> peerIds) { 
		macs.clear(); 
		macs.addAll(peerIds); 
	} 
	
	public LLSipLiftManageCmdBase(String message,String op,String errorCode) {
		super();
		this.setMessage(message); 
		this.setOp(op);
		this.setErrorCode(errorCode); 
	}
}
