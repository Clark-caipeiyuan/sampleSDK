package com.xixi.sdk.sipmsg.params;

import com.xixi.sdk.device.IDeviceData;

public class LLSmartCurtainRetResponse extends LLDeviceRequestBaseResponse implements IDeviceData {
    private int sign ;
	
	public int getSign() {
		return sign;
	}

	public void setSign(int sign) {
		this.sign = sign;
	}
	
	public void setLLSmartCurtainReturnResponse( int errorCode ,String op ,int sign){ 
		super.setErrorCode(errorCode);
		super.setOp(op);
		this.setSign(sign);
	}
}
