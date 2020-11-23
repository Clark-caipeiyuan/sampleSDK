package com.xixi.sdk.sipmsg.params;

public class DeviceNode {

	String room ; 
	String device ;
	String id ;
	public DeviceNode() {
		// TODO Auto-generated constructor stub
	}
	public DeviceNode(String room ,String device ,String id) {
		this.room = room ;
		this.device = device ;
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRoom() {
		return room;
	}
	public void setRoom(String room) {
		this.room = room;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	} 
	
	
}
