package com.xixi.sdk.sipmsg.params;

import com.xixi.sdk.parser.LLGsonUtils;

public class LLSmartAirRet extends LLSmartAir {
	
	public LLSmartAirRet(){ 
	
	}
	public static LLSmartAirRet makeInstance( final LLSmartAir instance) { 
		return (LLSmartAirRet)LLGsonUtils.fromJson(LLGsonUtils.getInstance().toJson(instance) , LLSmartAirRet.class);
	} 
 
	private  boolean  isSuccess ;
	
	public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	
	public void setSignAndTemp(Integer temp , Integer windLevl , Integer model ,Integer sign ,boolean isSuccess){ 
		super.setSignAndTemp(temp, windLevl, model, sign); 
		this.isSuccess = isSuccess ; 
	}
}
