package com.xixi.sdk.device;

import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.logger.Log;
import com.xixi.sdk.utils.file.IoCompletionListener1;
import com.xixi.sdk.utils.thread.UIThreadDispatcher;

public class LLSdkTestCase {

	static int AT[] = { 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0,
			1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1 };


	private static int ___index = 1;


	static int BT[] = { 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0};
	
	private final static void test3() {

		final Runnable testRunnable = new Runnable() {
			@Override
			public void run() {
				Log.i(LongLakeApplication.DANIEL_TAG, "start to launch test case " + ___index);
				try {
					LLSmartBoxCentersFactory.createSmartBox().curtainOn(BT[___index],
							new IoCompletionListener1<IDeviceData>() {
								@Override
								public void onFinish(IDeviceData data, Object context) {
									Integer i = (Integer) context;
									if (i != -1) {  
										___index++;   
										Log.i(LongLakeApplication.DANIEL_TAG, "LGL complete case" + ___index);
									} else {
										Log.i(LongLakeApplication.DANIEL_TAG, " LGL failed and retry " + ___index);
									}
									try {
										Thread.sleep(10000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									test();
								}
							});
				} catch (Exception e) {
					Log.i(LongLakeApplication.DANIEL_TAG, android.util.Log.getStackTraceString(e));
				}
			}
		};
		UIThreadDispatcher.dispatch(testRunnable);
	}
 
	private final static void test2() {

		final Runnable testRunnable = new Runnable() {
			@Override
			public void run() {
				Log.i(LongLakeApplication.DANIEL_TAG, "start to launch test case " + ___index);
				try {
					Log.i(LongLakeApplication.DANIEL_TAG, "LGL complete case sign :" + AT[___index]);
//					LLSmartBoxCentersFactory.createSmartBox().airConditionOn(AT[___index], 25,
//							new IoCompletionListener1<IDeviceData>() {
//								@Override
//								public void onFinish(IDeviceData data, Object context) {
//									Integer i = (Integer) context;
//									if (i != -1) {
//										___index++;
//										Log.i(LongLakeApplication.DANIEL_TAG, "LGL complete case" + ___index);
//									} else {
//										Log.i(LongLakeApplication.DANIEL_TAG, " LGL failed and retry " + ___index);
//									}
//									try {
//										Thread.sleep(10000);
//									} catch (InterruptedException e) {
//										e.printStackTrace();
//									}
//									test2();
//								}
//							});
				} catch (Exception e) {
					Log.i(LongLakeApplication.DANIEL_TAG, android.util.Log.getStackTraceString(e));
				}
			}
		};
		UIThreadDispatcher.dispatch(testRunnable, 2000);
	}

	public final static void test1() {

		final Runnable testRunnable = new Runnable() {
			@Override
			public void run() {
				Log.i(LongLakeApplication.DANIEL_TAG, "start to launch test case " + ___index);
				try {
					LLSmartBoxCentersFactory.createSmartBox().enableWatchDog(0x1 == (___index & 0x1),
							LLDevPortMap.DOOR_WATCH_DOG, new IoCompletionListener1<IDeviceData>() {
								@Override
								public void onFinish(IDeviceData data, Object context) {
									Integer i = (Integer) context;
									if (i != -1) {
										___index++;
										Log.i(LongLakeApplication.DANIEL_TAG, "complete case" + ___index);
									} else {
										Log.i(LongLakeApplication.DANIEL_TAG, "failed and retry " + ___index);
									}
									// test1();
								}
							});
				} catch (Exception e) {
					Log.i(LongLakeApplication.DANIEL_TAG, android.util.Log.getStackTraceString(e));
				}
			}
		};
		UIThreadDispatcher.dispatch(testRunnable, 2000);
	}

	public final static void test() {

		final Runnable testRunnable = new Runnable() {
			@Override
			public void run() {
				Log.i(LongLakeApplication.DANIEL_TAG, "start to launch test case " + ___index);
				try {
					LLSmartBoxCentersFactory.createSmartBox().switchOn(0x1 == (___index & 0x1), "1587657524438178_1",
							new IoCompletionListener1<IDeviceData>() {
								@Override
								public void onFinish(IDeviceData data, Object context) {
									Integer i = (Integer) context;
									try {
										Thread.sleep(2000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									if (i != -1) {
										___index++;
										Log.i(LongLakeApplication.DANIEL_TAG, "start case " + ___index);
									} else {
										Log.i(LongLakeApplication.DANIEL_TAG, "failed and retry " + ___index);
										// try {
										// Thread.sleep(2000);
										// } catch (InterruptedException e) {
										// e.printStackTrace();
										// }
										// LLSmartBoxCentersFactory.createSmartBox().switchOn(0x1
										// == (___index & 0x1),
										// "1587657524438178_1", this);
									}
									test();

								}
							});
				} catch (Exception e) {
					Log.i(LongLakeApplication.DANIEL_TAG, android.util.Log.getStackTraceString(e));
				}
			}
		};
		UIThreadDispatcher.dispatch(testRunnable);
	}
}
