
package com.xixi.sdk.utils.network;
import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.utils.network.LLException.LLIncorrectHttpCodeException;
import com.xixi.sdk.utils.network.LLException.LLNetworkException;

import android.os.Handler;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public abstract class LLCallback<T> implements Callback<ResponseBody>{

	final Handler mHandler = new Handler() ; 
	
	public abstract void onLLFailure(Call<ResponseBody> arg0, Throwable arg1) ; 
	public abstract void onLLResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1 , String payload) ; 
	
 
	@Override
	public void onFailure(final Call<ResponseBody> arg0, final Throwable arg1) {
		mHandler.post(new Runnable() { 
			public void run() {
				onLLFailure(arg0 , arg1 instanceof LLIncorrectHttpCodeException ? arg1 : new LLNetworkException(android.util.Log.getStackTraceString(arg1))) ;
			}
		}) ;
	}
	
	@Override
	public void onResponse(final Call<ResponseBody> arg0, final Response<ResponseBody> arg1) {
		if (arg1.isSuccessful()) {
			ResponseBody rb = arg1.body() ;
			String payload = "" ; 
			try{
				byte bt[] = rb.bytes();
				rb.close() ;
				payload = new String(bt , "utf-8");
				if ( payload.contains("gb2312")) { 
					payload = new String(bt , "gb2312"); 
				}
				rb = null ;  
			} catch(Exception e) { 
				payload = "" ; 
			}  
			
			try { if ( rb != null ) rb.close(); } catch(Exception e) { rb = null ; }
			final String payloadData = payload ; 
			mHandler.post(new Runnable() { 
				public void run() { 
					onLLResponse(arg0 , arg1 , payloadData == null ? null : payloadData) ; 
				}
			}) ;
		}
		else { 
			onFailure(arg0,new LLIncorrectHttpCodeException( arg1.code())) ; 
		}
	} 
}
