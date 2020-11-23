package com.xixi.sdk.sipmsg.params;

import com.xixi.sdk.device.IDeviceData;

public class LLSmartSafe implements IDeviceData{
	
	private String alert;
	private String id;
	
	public String getAlert() {
		return alert;
	}
	public void setAlert(String alert) {
		this.alert = alert;
	}
	public String getId() {
		return id;
	}
	public LLSmartSafe(String alert, String id) {
		super();
		this.alert = alert;
		this.id = id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public void SetLLSmartSafe(String alert , String id){
		this.setAlert(alert);
		this.setId(id);
	}
}
