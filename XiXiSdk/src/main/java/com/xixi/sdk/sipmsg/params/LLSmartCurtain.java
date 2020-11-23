package com.xixi.sdk.sipmsg.params;

import com.xixi.sdk.device.IDeviceData;
import com.xixi.sdk.parser.LLGsonUtils;

public class LLSmartCurtain implements IDeviceData {
	
	
	private int sign ;
	
	public int getSign() {
		return sign;
	}

	public void setSign(int sign) {
		this.sign = sign;
	}
	
	public void setLLsmartCurtain(int sign ){
		this.setSign(sign);
	}
	
	
	public LLSmartCurtain clone(){ 
		return (LLSmartCurtain)LLGsonUtils.fromJson(LLGsonUtils.getInstance().toJson(this) ,LLSmartCurtain.class) ; 
	}
}
