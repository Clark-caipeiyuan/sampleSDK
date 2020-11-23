package com.xixi.sdk.sipmsg.params;

public class LLSmartAirRetResponse extends  LLDeviceRequestBaseResponse {
	private  int sign ;
	private  int temp ;
	private  int model;
	private  int windLevl;  
	public Integer getWindLevl() {
		return windLevl;
	}
	public void setWindLevl(Integer windLevl) {
		this.windLevl = windLevl;
	}

	public Integer getSign() {
		return sign;
	}
	public void setSign(Integer sign) {
		this.sign = sign;
	}
	public Integer getTemp() {
		return temp;
	}
	public void setTemp(Integer temp) {
		this.temp = temp;
	}
	public int getModel() {
		return model;
	}
	public void setModel(int model) {
		this.model = model;
	}
	public void setLLSmartAirRetResponse(String op,int errorCode ,int temp , int windLevl , int model ,int sign ){
		super.setErrorCode(errorCode); 
		super.setOp(op);
		this.setSign(sign);
		this.setTemp(temp);
		this.setModel(model);
		this.setWindLevl(windLevl); 
	}
	public LLSmartAirRetResponse(int sign, int temp,int windLevl, int model){
		this.setSign(sign);
		this.setTemp(temp);
		this.setModel(model);
		this.setWindLevl(windLevl); 
	}
	
	public LLSmartAirRetResponse() {
		super();
	}
	
	public static final int MODEL_SNOW = 2;
	public static final int MODEL_SUN = 5;
	public static final int SWITCH_ON = 1;
	public static final int SWITCH_OFF = 0;
}
