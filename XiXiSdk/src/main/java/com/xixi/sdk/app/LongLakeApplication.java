
package com.xixi.sdk.app;

import java.io.File;
import java.lang.ref.WeakReference;

import com.xixi.sdk.globals.LLSdkGlobals;
import com.xixi.sdk.logger.LLLogWrapper;
import com.xixi.sdk.logger.Log;

import android.app.Application;
import android.os.Environment;

public abstract class LongLakeApplication extends Application {

	public static final String DANIEL_TAG = "wutong::";

	private final Thread.UncaughtExceptionHandler exceptionHandler = new Thread.UncaughtExceptionHandler() {

		@Override
		public void uncaughtException(Thread arg0, Throwable arg1) {
			onExceptionHandler(arg0, arg1);
		}
	};

	public Thread.UncaughtExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	/** Set up targets to receive log data */
	public void initializeLogging() {
		// Using Log, front-end to the logging chain, emulates android.util.log
		// method signatures.
		// Wraps Android's native log framework
		LLLogWrapper logWrapper = new LLLogWrapper(LLSdkGlobals.getLogTag());
		Log.setLogNode(logWrapper);
	}

	protected abstract void initSdk();

	protected abstract void onExceptionHandler(Thread arg0, Throwable arg1);

	@Override
	public void onCreate() {
		super.onCreate();
		instance = new WeakReference<Application>(this);
		if (enableCheckValidityOfDisk()) {
			verifyDiskReady(
					new String[] { Environment.getDataDirectory().getAbsolutePath() , Environment.getExternalStorageDirectory().toString() }
			);

		}
		initSdk();
		initializeLogging();
		if (LLSdkGlobals.isEnableLocalExceptionHandler()) {
			Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
		}

	}

	protected boolean enableCheckValidityOfDisk() {
		return true;
	}

	private void verifyDiskReady(String str[] ) {
		for(String node : str ) {
			verifyDiskReady(node) ;
		}
	}
	//Environment.getDataDirectory().getAbsolutePath();
	private void verifyDiskReady(String disk) {
		File file = new File(disk);
		android.util.Log.i(DANIEL_TAG, "verifyDiskReady: " + disk);
		while (true) {
			try {
				if (file.exists()) {
					break;
				}
			} catch (Exception e) {
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	static WeakReference<Application> instance;

	public static Application getInstance() {
		return instance != null ? instance.get() : null;
	}

}
