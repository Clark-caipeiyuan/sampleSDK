package com.xixi.sdk.sipmsg.params;

public class LLSmartDoorRetResponse  extends LLDeviceRequestBaseResponse{
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
	
	public void setLLSmartDoorRetResponse(int errorCode ,String op ,String pwd , String data){
	 	super.setErrorCode(errorCode);
		super.setOp(op);
		this.setPwd(pwd);
		this.setData(data);
	}
	
	public static String INVALID_PWD = "0";
	public static String OPEN_DOOR_FAIL = "2";
}
