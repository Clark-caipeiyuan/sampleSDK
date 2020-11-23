package com.xixi.sdk.device;

import com.xixi.sdk.utils.network.IISocketUtils;
import com.xixi.sdk.utils.network.LLLongConnectionSocket;

public class LLDevPortMap {
	
	private static LLDevPortMap instance ; 
	public static synchronized LLDevPortMap getInstance(){ 
		if ( instance == null ) { 
			instance = new LLDevPortMap() ;
		}
		return instance ; 
	} 
	
	protected LLDevPortMap() { 
//		port_set.put(LIGHT_SWITCHER_1, new LLLongConnectionSocket());
//		port_set.put(DOOR_WATCH_DOG , new LLLongConnectionSocket());
	}
	
	public final static String LIGHT_SWITCHER_1 = "light1";
	public final static String DOOR_WATCH_DOG = "watchDog1" ;
	public final static String AIR_CONDITION = "aircondition" ;
	public final static String CURTAINER = "curtain";
	public final static String OPEN_DOOR = "openDoor";
//	private final Map<String, IISocketUtils> port_set = new HashMap<String, IISocketUtils>();
 
	public IISocketUtils getDevPort(String devName) {
//		return port_set.get(devName);
		return LLLongConnectionSocket.getInstance() ; 
	}
}
