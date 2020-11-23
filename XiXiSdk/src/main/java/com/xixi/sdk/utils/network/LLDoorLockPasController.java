package com.xixi.sdk.utils.network;

public class LLDoorLockPasController {

	private static  LLDoorLockPasController instance ;
	public static synchronized LLDoorLockPasController getInstance(){
		if (instance == null) {
			instance = new  LLDoorLockPasController() ;
		}
		return instance ;
	}
	
	private String doorLockPas ;
	public String getDoorLockPas() {
		return doorLockPas;
	}

	public void setDoorLockPas(String doorLockPas) {
		this.doorLockPas = doorLockPas;
	}
	
}
