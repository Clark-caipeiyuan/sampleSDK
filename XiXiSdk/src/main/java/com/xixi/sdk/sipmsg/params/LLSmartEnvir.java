package com.xixi.sdk.sipmsg.params;

import com.xixi.sdk.device.IDeviceData;

public class LLSmartEnvir implements IDeviceData {

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
	public LLSmartEnvir(String pm, String temp, String moisture, String voc) {
		super();
		this.pm = pm;
		this.temp = temp;
		this.moisture = moisture;
		this.voc = voc;
	}
	
}
