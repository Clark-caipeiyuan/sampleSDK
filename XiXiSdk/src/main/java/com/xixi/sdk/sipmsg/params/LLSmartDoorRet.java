package com.xixi.sdk.sipmsg.params;

import com.xixi.sdk.parser.LLGsonUtils;

public class LLSmartDoorRet extends LLSmartDoor {
	
	public LLSmartDoorRet(){
		
	}
	public static LLSmartDoorRet makeInstance( final LLSmartDoor instance) { 
		return (LLSmartDoorRet)LLGsonUtils.fromJson(LLGsonUtils.getInstance().toJson(instance) , LLSmartDoorRet.class);
	}
	
	private boolean isSuccess;
	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public void setLLsmartDoor(String pwd , String data , boolean isSuccess){
		super.setLLsmartDoor(pwd, data);
		this.setSuccess(isSuccess);
	}
}
