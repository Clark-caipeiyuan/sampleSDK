package com.xixi.sdk.globals;

import android.os.Environment;

public interface LLConstantFile {
	public final static String CONST_LOG_PATH = Environment.getExternalStorageDirectory().toString()
			+ "/longlake/logger";
	public final static String CONST_MONITOR_FILE_FOLDER = Environment.getExternalStorageDirectory()
			+ "/longlake/monitors/";

}
