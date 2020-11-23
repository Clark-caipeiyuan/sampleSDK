package com.xixi.sdk.parser;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.factory.LLXmlNodeKits.LLSortedNodeDeserializer;
import com.xixi.sdk.factory.LLXmlNodeKits.LLSortedNodeSerializer;
import com.xixi.sdk.factory.LLXmlNodeKits.LLXmlNodeDeserializer;
import com.xixi.sdk.factory.LLXmlNodeKits.LLXmlNodeSerializer;
import com.xixi.sdk.model.LLXmlNode;
import com.xixi.sdk.model.SortedNode;
import com.xixi.sdk.utils.file.IoCompletionListener1;
import com.xixi.sdk.utils.thread.LLThreadPool;
import com.xixi.sdk.utils.thread.UIThreadDispatcher;

import android.util.Log;

public class LLGsonUtils {

	private static LLGsonUtils instance = null;

	final private Gson mGsonObject;

	private LLGsonUtils() {

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(LLXmlNode.class, new LLXmlNodeSerializer());
		gsonBuilder.registerTypeAdapter(LLXmlNode.class, new LLXmlNodeDeserializer());
		gsonBuilder.registerTypeAdapter(SortedNode.class, new LLSortedNodeSerializer());
		gsonBuilder.registerTypeAdapter(SortedNode.class, new LLSortedNodeDeserializer());


		mGsonObject = gsonBuilder.create();
	}

	public void test_fromJson(final String strData, final Class<?> classInfo,
							  final IoCompletionListener1<Object> onComplete) {
		LLThreadPool.getInstance().dispatchRunnable(new Runnable() {
			@Override
			public void run() {
				Object ob = null;
				try {
					ob = getInstance().mGsonObject.fromJson(strData, classInfo);
				} catch (Exception e) {
				}
				final Object parsedOb = ob;
				final IoCompletionListener1<Object> _onComplete = onComplete;
				_onComplete.onFinish(parsedOb, null);
			}
		});
	}

	public void fromJson(final String strData, final Class<?> classInfo,
						 final IoCompletionListener1<Object> onComplete) {
		LLThreadPool.getInstance().dispatchRunnable(new Runnable() {
			@Override
			public void run() {
				Object ob = null;
				try {
					ob = getInstance().mGsonObject.fromJson(strData, classInfo);
				} catch (Exception e) {
				}
				final Object parsedOb = ob;
				final IoCompletionListener1<Object> _onComplete = onComplete;

				UIThreadDispatcher.dispatch(new Runnable() {
					public void run() {
						_onComplete.onFinish(parsedOb, null);
					}
				});
			}
		});
	}

	public static Object fromJson(String strData, Class<?> classInfo) {
		Object ob = null;
		try {
			ob = getInstance().mGsonObject.fromJson(strData, classInfo);
		} catch (Exception e) {
		}

		return ob;
	}

	public static Object fromJson(String strData, Type classInfo) {
		Object ob = null;
		try {
			ob = getInstance().mGsonObject.fromJson(strData, classInfo);
		} catch (Exception e) {
		}

		return ob;
	}

	public static synchronized LLGsonUtils getInstance() {
		if (instance == null) {
			instance = new LLGsonUtils();
		}

		return instance;
	}

	public static synchronized void rvInstance() {
		if (instance != null) {
			instance = null;
		}
	}

	public String toJson(Object o) {

		if (o == null)
			return "";
		String strConvertedString = "";
		try {
			strConvertedString = mGsonObject.toJson(o);
		} catch (Exception e) {
			Log.e(LongLakeApplication.DANIEL_TAG, Log.getStackTraceString(e));
		}
		;
		return strConvertedString;
	}

}
