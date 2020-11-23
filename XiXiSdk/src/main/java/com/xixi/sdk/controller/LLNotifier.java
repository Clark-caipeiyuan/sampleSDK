package com.xixi.sdk.controller;

import java.util.WeakHashMap;

import com.xixi.sdk.LLSDKUtils;
import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.utils.thread.UIThreadDispatcher;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

public abstract class LLNotifier<T> {

	final Handler mDispatchedThread;
	final HandlerThread ht;

	protected boolean dispatchedInMainThread() {
		return false;
	}

	protected LLNotifier(Handler specifiedHandler) {
		LLSDKUtils.danielAssert(specifiedHandler != null );
		mDispatchedThread = specifiedHandler ;
		ht = null ;
	}

	protected LLNotifier() {

		if (dispatchedInMainThread()) {
			ht = null;
			mDispatchedThread = UIThreadDispatcher.getHandler();
		} else {
			ht = new HandlerThread(getClassName());
			ht.start();
			mDispatchedThread = new Handler(ht.getLooper());
		}
	}

	// private final Handler mMainHandler = new Handler() ;

	boolean synchBit = true;

	public String getClassName() {
		return getClass().toString();
	}

	protected abstract void invoke(T t, Object o1[]);

	final protected WeakHashMap<T, String> mMonitors = new WeakHashMap<T, String>();

	Context context;

	public Context getContext() {
		return context == null ? LongLakeApplication.getInstance() : null;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	protected void onEmptyList() {} ; 
	public void notifyOb(final Object changedOb[]) {
		synchronized (mMonitors) {
			if ( mMonitors.size() == 0 ) { 
				onEmptyList() ;
			}
			else {
				for (final T ob : mMonitors.keySet()) {
					mDispatchedThread.post(new Runnable() {
						public void run() {
							invoke(ob, changedOb);
						}
					});
				}
			}

		}

	}

	// public void cleanup() {
	// for (int i = mMonitors.size() - 1; i >= 0; i--) {
	// LLObserver cacheI = mMonitors.get(i).get();
	// if (cacheI != null) {
	// cacheI.onNotify(0 ,context);
	// } else {
	// mMonitors.remove(i);
	// }
	// }
	// }

	public boolean checkIfContained(T gbi) {
		synchronized (mMonitors) {
			return null != mMonitors.get(gbi);
		}
	}

	public void registerAsObserver(T gbi, boolean added) {
		synchronized (mMonitors) {
			String value = mMonitors.get(gbi);

			if (value == null) {
				if (added) {
					mMonitors.put(gbi, gbi.toString());
				}
			} else {
				if (!added) {
					mMonitors.remove(gbi);
				}
			}
		}
	}
}
