package com.xixi.sdk.logger;

import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.globals.LLSdkGlobals;

import android.os.HandlerThread;
import android.widget.Toast;

public class LoggerHandlerThread extends HandlerThread {

	protected void onLooperPrepared() {
		super.onLooperPrepared();
//		if (!LLUtils.isInMainThread()) { 
//			Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
//		}
		
	}	
	
	private final UncaughtExceptionHandler exceptionHandler = new UncaughtExceptionHandler() {
		@Override
		public void uncaughtException(Thread arg0, Throwable arg1) {
			
			Toast.makeText(LongLakeApplication.getInstance() , arg0.toString() + android.util.Log.getStackTraceString(arg1), Toast.LENGTH_LONG).show();
			Log.e(LLSdkGlobals.DANIEL_TAG ,
					arg0.toString() + android.util.Log.getStackTraceString(arg1));
		}
	} ; 
	
	public LoggerHandlerThread(String name) {
		super(name);
		
	}
}
