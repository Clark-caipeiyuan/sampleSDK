package com.xixi.sdk.sipmsg.params;

import com.xixi.sdk.parser.LLGsonUtils;

public class LLSmartCurtainRet extends LLSmartCurtain {
	
	public LLSmartCurtainRet(){
		
	}
	
	public static LLSmartCurtainRet makeInstance( final LLSmartCurtain instance) { 
		return (LLSmartCurtainRet)LLGsonUtils.fromJson(LLGsonUtils.getInstance().toJson(instance) , LLSmartCurtainRet.class);
	}
	private boolean isSuccess;
	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	
	public void setLLSmartCurtain(int sign , boolean isSuccess){
		super.setLLsmartCurtain(sign);
		this.setSuccess(isSuccess);
	}
}
