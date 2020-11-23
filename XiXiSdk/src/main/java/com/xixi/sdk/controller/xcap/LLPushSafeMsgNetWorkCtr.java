package com.xixi.sdk.controller.xcap;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import com.google.gson.JsonObject;
import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.logger.Log;
import com.xixi.sdk.model.LLSafeMsg;
import com.xixi.sdk.parser.LLGsonUtils;
import com.xixi.sdk.utils.file.IoCompletionListener1;
import com.xixi.sdk.utils.thread.UIThreadDispatcher;

import android.app.Application;
import okhttp3.Dispatcher;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LLPushSafeMsgNetWorkCtr {
	private int cursor = 0;
	private static final String baseUrl = "http://192.168.1.2:5060/api/msg";
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	
//	public void pushMsgTo(final String userId,final String msgcontent,final IoCompletionListener1<Response> iocallback) {
//
//		new Thread(new Runnable() {
//			public void run() {
//				OkHttpClient  client = new OkHttpClient();
//				LLSafeMsg data = new LLSafeMsg(userId, "", msgcontent, "");
//				RequestBody body = RequestBody.create(JSON, LLGsonUtils.getInstance().toJson(data));
//				 Request request = new Request.Builder().url(baseUrl).post(body).build();
//				try {
//					Response response = client.newCall(request).execute();
//					iocallback.onFinish(response, null);
//				} catch (IOException e) {
//					Log.i(LongLakeApplication.DANIEL_TAG,"Exception:"+ e);
//				}
//			}
//		}).start();
//		
//	}
	
	public void pushAllMsg(final ArrayList<String> userIds,final String msgcontent,final IoCompletionListener1<String> iocallback){
		
		if(cursor>userIds.size()){
			iocallback.onFinish("success", null);
		 return;
	 	}
		final String userId = userIds.get(cursor++);
		new Thread(new Runnable() {
			public void run() {
				OkHttpClient  client = new OkHttpClient();
				LLSafeMsg data = new LLSafeMsg(userId, "", msgcontent, "");
				RequestBody body = RequestBody.create(JSON, LLGsonUtils.getInstance().toJson(data));
				 Request request = new Builder().url(baseUrl).post(body).build();
				try {
					Response response = client.newCall(request).execute();
					//iocallback.onFinish(response, null);
//					if(response.isSuccessful()){
//						
//					}
					pushAllMsg(userIds, msgcontent, iocallback);
					
				} catch (IOException e) {
					Log.i(LongLakeApplication.DANIEL_TAG,"Exception:"+ e);
				}
			}
		}).start();
	  }
}
