package com.xixi.sdk.utils.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public abstract class LLXmlXcapResponseCallback extends LLCallback<ResponseBody> {

	public abstract void onXmlLLFailure(Call<ResponseBody> arg0, Throwable arg1) ; 
	public abstract void onXmlLLResponse() ; 
	
	@Override
	public void onLLFailure(Call<ResponseBody> arg0, Throwable arg1) {
		 
		
	}

	@Override
	public void onLLResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1, String payload) {
	}

}
