package com.xixi.sdk.sipmsg.params;

public class LLSmartEnvirRetResponse  extends LLDeviceRequestBaseResponse {

	String pm;
	String temp;
	String moisture;
	String voc;
	public String getPm() {
		return pm;
	}
	public void setPm(String pm) {
		this.pm = pm;
	}
	public String getTemp() {
		return temp;
	}
	public void setTemp(String temp) {
		this.temp = temp;
	}
	public String getMoisture() {
		return moisture;
	}
	public void setMoisture(String moisture) {
		this.moisture = moisture;
	}
	public String getVoc() {
		return voc;
	}
	public void setVoc(String voc) {
		this.voc = voc;
	}
	
	public void setLLSmartEnvirRetResponse(String op,int errorCode,String pm,String temp,String moisture,String voc){
	 	super.setErrorCode(errorCode);
		super.setOp(op);
		this.setPm(pm);
		this.setTemp(temp);
		this.setMoisture(moisture);
		this.setVoc(voc);
	}
}
