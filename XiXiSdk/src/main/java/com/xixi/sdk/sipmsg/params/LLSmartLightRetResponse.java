package com.xixi.sdk.sipmsg.params;

public class LLSmartLightRetResponse  extends LLDeviceRequestBaseResponse{
	private  String cur ;
	private  String id  ;
	private  String data ; 
	
	public String getCur() {
		return cur;
	}
	public void setCur(String cur) {
		this.cur = cur;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
     
	public  void  setLLSmartLightRetResponse(int errorCode , String op ,String id  ,String cur, String data){
	 	super.setErrorCode(errorCode);
		super.setOp(op);
		this.setCur(cur);
		this.setData(data);
		this.setId(id);
	}
}
