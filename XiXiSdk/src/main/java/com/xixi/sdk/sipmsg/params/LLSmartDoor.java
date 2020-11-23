package com.xixi.sdk.sipmsg.params;

import com.xixi.sdk.device.IDeviceData;

public class LLSmartDoor implements IDeviceData{
	
	
	private String pwd ;
	private String data ;
	
	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	public void setLLsmartDoor(String pwd , String data ){
		this.setData(data);
		this.setPwd(pwd);
	}
}
