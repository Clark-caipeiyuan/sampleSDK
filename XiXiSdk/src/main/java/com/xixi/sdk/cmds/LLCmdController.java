package com.xixi.sdk.cmds;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.xixi.sdk.LLSDKUtils;
import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.utils.file.IoCompletionListener;
import com.xixi.sdk.utils.file.IoCompletionListener1;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Semaphore;

public class LLCmdController {

	private static LLCmdController instance;

	private final static String END_KEY = "daniel";
	private final static String END_SIGN_CMD = String.format("echo %s\n", END_KEY);

	public static synchronized LLCmdController getInstance() {
		if (instance == null) {
			instance = new LLCmdController();
		}
		return instance;
	}

	private HandlerThread cmdThread;
	private Handler workingThread;

	private LLCmdController() {
		cmdThread = new HandlerThread("cmd control task thread");
		cmdThread.start();
		workingThread = new Handler(cmdThread.getLooper());
	}

	Process _process;
	DataOutputStream _dos_pipe;
	BufferedReader _output_stream;

	public void exeCmd(final String[] cmds) {
		exeCmd(cmds, null);
	}

	public void exeCmd(final String[] cmds, final IoCompletionListener1<Boolean> callback) {
		workingThread.post(new Runnable() {
			public void run() {
				_exeCmd(cmds, callback);
			}
		});
	}

	private void _cleanCurrentProcess() {
		try {
			_dos_pipe.close();
		} catch (IOException e) {
		}
		try {
			_output_stream.close();
		} catch (IOException e) {
		}
		try {
			_process.destroy();
		} catch (Exception e) {

		}
		_dos_pipe = null;
		_output_stream = null;
		_process = null;
	}

	private final static int CONST_TRIES = 10;
	final Semaphore _s = new Semaphore(1);

	boolean boostReady = true;

	public boolean isBoostReady() {
		return boostReady;
	}

	public void boostSelf() {
		workingThread.post(new Runnable() {
			public void run() {
				_exeCmd(new String[] { END_SIGN_CMD }, new IoCompletionListener<Boolean>() {
					@Override
					public void onFinish(Boolean data, Object context) {
						boostReady = data;
					}

					@Override
					public void onError(String errReason, Throwable exceptionInstance) {
					}
				});
			}
		});
	}
	public boolean exeCmd(final String[] cmds, final String matchedStr ,  final IoCompletionListener1<Boolean> ioCompletion) {

		workingThread.post(new Runnable() {
			@Override
			public void run() {
				boolean contained = false ;
				try {
					if (_process == null) {
						_process = Runtime.getRuntime().exec("su");
						_dos_pipe = new DataOutputStream(_process.getOutputStream());
						_output_stream = new BufferedReader(new InputStreamReader(_process.getInputStream()));
					}

					for (String cmd : cmds) {
						_dos_pipe.writeBytes(cmd + "\n");
						_dos_pipe.flush();
					}

					try {
						Thread.sleep(1000);
					} catch (Exception e) {
					}

					StringBuffer sb = new StringBuffer();
					int i = 0 ;
					final int MAX_TRIES = 10 ;
					for( i = 0 ; i < MAX_TRIES; i++ ){
						while (_output_stream.ready()) {
							String str = _output_stream.readLine();
							LLSDKUtils.danielAssert(str != null);
							sb.append(str);
						}
						String strCombined = sb.toString();
						contained = strCombined.contains(matchedStr) ;
						if (contained) {
							break;
						} else {
							try {
								Thread.sleep(1000);
							} catch (Exception e) {
							}
						}
					}
				} catch (Exception localException) {
					Log.i(LongLakeApplication.DANIEL_TAG ,
							android.util.Log.getStackTraceString(localException));
					_cleanCurrentProcess();
				}

				if (ioCompletion != null) {
					ioCompletion.onFinish(contained, null);
				}

			}
		}) ;

		return true;
	}

	private  boolean _exeCmd(String[] cmds, final IoCompletionListener1<Boolean> ioCompletion) {

		try {

			if (_process == null) {
				_process = Runtime.getRuntime().exec("su");
				_dos_pipe = new DataOutputStream(_process.getOutputStream());
				_output_stream = new BufferedReader(new InputStreamReader(_process.getInputStream()));
			}

			for (String cmd : cmds) {
				_dos_pipe.writeBytes(cmd + "\n");
				_dos_pipe.flush();
			}

			_dos_pipe.writeBytes(END_SIGN_CMD);
			_dos_pipe.flush();

			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}

			StringBuffer sb = new StringBuffer();

			while (true) {

				while (_output_stream.ready()) {
					String str = _output_stream.readLine();
					LLSDKUtils.danielAssert(str != null);
					sb.append(str);
				}

				String strCombined = sb.toString();
				//Log.i(LongLakeApplication.DANIEL_TAG, "strCombined =>" + strCombined);
				if (strCombined.contains(END_KEY)) {
					break;
				} else {
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
					}
					_dos_pipe.writeBytes(END_SIGN_CMD);
					_dos_pipe.flush();
				}
			}
		} catch (Exception localException) {
			// Log.i(LongLakeApplication.DANIEL_TAG, " localexception");
//			 Log.i(LongLakeApplication.DANIEL_TAG ,
//			 android.util.Log.getStackTraceString(localException));
			_cleanCurrentProcess();
			if (ioCompletion != null) {
				LLSDKUtils.runInMainThread(new Runnable() {
					public void run() {
						ioCompletion.onFinish(false, null);
					}
				});
			}

			return _exeCmd(cmds, ioCompletion);
		}

		if (ioCompletion != null) {

			LLSDKUtils.runInMainThread(new Runnable() {
				public void run() {
					ioCompletion.onFinish(true, null);
				}
			});
		}

		return true;
	}

}
