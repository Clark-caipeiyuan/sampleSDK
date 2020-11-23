package com.xixi.sdk.utils.network;

import com.xixi.sdk.LLSDKUtils;
import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.logger.Log;
import com.xixi.sdk.utils.file.IoCompletionListener1;
import com.xixi.sdk.utils.mem.LLBufferDescription;
import com.xixi.sdk.utils.thread.UIThreadDispatcher;

public abstract class LLSmartBoxRequestDataEx extends LLSmartConnectionRequest {

	@SuppressWarnings("unchecked")
	public LLSmartBoxRequestDataEx(String data, boolean urgentBit, int urgentData,
			IoCompletionListener1<? extends LLSmartConnectionRequest> ioCompletion) {

		super(data, urgentBit, urgentData, (IoCompletionListener1<LLSmartConnectionRequest>) ioCompletion);
		_generalIoCompletion = new IoCompletionListener1<LLSmartConnectionRequest>() {
			@Override
			public void onFinish(LLSmartConnectionRequest data, Object context) {
				LLBufferDescription bd = (LLBufferDescription)context ; 
				if (data.isInterestedPck(data, context) || !bd.isValid()) {
					if (data.getEnableTimeout() != 0) {
						UIThreadDispatcher.removeCallbacks(data);
					}
					((LLSmartBoxRequestDataEx) data).callIoCompletion(data, (LLBufferDescription) context);
				}
			}
		};
	}
  
	private void callIoCompletion(final LLSmartConnectionRequest data, final LLBufferDescription ld) {

		LLSDKUtils.runInMainThread(new Runnable() {
			public void run() {
				LLSmartBoxRequestDataEx.super.getIoCompletion().onFinish(data, ld);
			}
		});
	}

	private IoCompletionListener1<LLSmartConnectionRequest> getGeneralIoCompletion() {
		return  new IoCompletionListener1<LLSmartConnectionRequest>() {
			@Override
			public void onFinish(LLSmartConnectionRequest data, Object context) {
				LLBufferDescription bd = (LLBufferDescription)context ; 
				if (data.isInterestedPck(data, context) || !bd.isValid()) {
					if (data.getEnableTimeout() != 0) {
						UIThreadDispatcher.removeCallbacks(data);
					}
					((LLSmartBoxRequestDataEx) data).callIoCompletion(data, (LLBufferDescription) context);
				}
			}
		};
	}

	final IoCompletionListener1<LLSmartConnectionRequest> _generalIoCompletion;

	public IoCompletionListener1<LLSmartConnectionRequest> getIoCompletion() {
		return _generalIoCompletion;
	}

	@Override
	public void run() {
		LLSDKUtils.runInMainThread(new Runnable() {
			public void run() {
				Log.i(LongLakeApplication.DANIEL_TAG, this.toString() + " timeout");
				_generalIoCompletion.onFinish(LLSmartBoxRequestDataEx.this, LLBufferDescription.getFailureInstance());
			}
		});
	}

}
