package com.xixi.sdk.utils.network;

import com.xixi.sdk.LLSDKUtils;
import com.xixi.sdk.utils.file.IoCompletionListener1;
import com.xixi.sdk.utils.mem.LLBufferDescription;

public abstract class IoCompletionListener2 implements IoCompletionListener1<LLSmartConnectionRequest> {
	
	public abstract void onNetworkIoCompletion(final LLSmartConnectionRequest request , LLBufferDescription bd) ; 

	public abstract void onFinishEx(final LLSmartConnectionRequest data, LLBufferDescription context) ;
	public  void onFinish(final LLSmartConnectionRequest data, Object context) {
		LLSDKUtils.danielAssert(LLSDKUtils.isInMainThread());
		LLBufferDescription bd = (LLBufferDescription) context;
		LLLongConnectionSocket.getInstance().unregister(data);
		if (!bd.isValid()) {
			onFinishEx(data, bd);
		} else {
			onNetworkIoCompletion(data , bd) ; 
			onFinishEx(data, bd); 
		}
	}
}
