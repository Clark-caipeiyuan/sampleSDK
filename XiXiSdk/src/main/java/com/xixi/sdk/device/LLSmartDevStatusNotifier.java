package com.xixi.sdk.device;
import com.xixi.sdk.controller.LLNotifier;
import com.xixi.sdk.utils.file.IoCompletionListener1;

public class LLSmartDevStatusNotifier extends LLNotifier<IoCompletionListener1<IDeviceData>>{


	protected boolean dispatchedInMainThread() {
		return true;
	}
	@Override
	protected void invoke(IoCompletionListener1<IDeviceData> t, Object[] o1) {
		try {
			t.onFinish((IDeviceData)o1[0], null);
		}
		catch(Exception e) { 
			t.onFinish(null, null);	
		}
	}

}
