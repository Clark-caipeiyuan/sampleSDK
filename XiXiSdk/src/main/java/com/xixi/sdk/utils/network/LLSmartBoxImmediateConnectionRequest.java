package com.xixi.sdk.utils.network;

import com.xixi.sdk.utils.file.IoCompletionListener1;

public class LLSmartBoxImmediateConnectionRequest extends LLSmartConnectionRequest implements Runnable {
 
	public LLSmartBoxImmediateConnectionRequest(String data, boolean urgentBit, int urgentData,
			IoCompletionListener1<LLSmartConnectionRequest> ioCompletion) {
		super(data, urgentBit, urgentData, ioCompletion); 
	}

	@Override
	public void run() {

	}

	@Override
	public boolean isInterestedPck(LLSmartConnectionRequest data, Object bd) { 
		return true;
	}
}
