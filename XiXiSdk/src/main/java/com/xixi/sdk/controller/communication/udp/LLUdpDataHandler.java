package com.xixi.sdk.controller.communication.udp;

import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.controller.LLNotifier;
import com.xixi.sdk.controller.communication.udp.LLUdpController.IDataDrainer;
import com.xixi.sdk.controller.communication.udp.LLUdpController.LLUdpMsgBody;

import android.util.Log; 

public class LLUdpDataHandler extends LLNotifier<IDataDrainer> implements IUdpPortReceiver {

	private LLUdpDataHandler() {
		LLUdpController.getInstance().setReceiverDelegate(this);
	}
	 
	private static LLUdpDataHandler instance;
 	
	public static synchronized LLUdpDataHandler getInstance() {
		if (instance == null) {
			instance = new LLUdpDataHandler();
		}
		return instance;
	}

	@Override
	public void onReceive(String ip, byte[] data, int length) { 
		this.notifyOb(new Object[] { new LLUdpMsgBody(length, data, ip) });  
	}

	@Override
	protected void invoke(IDataDrainer t, Object[] o1) {
		t.onReceive((LLUdpMsgBody) o1[0]);
	}

}
