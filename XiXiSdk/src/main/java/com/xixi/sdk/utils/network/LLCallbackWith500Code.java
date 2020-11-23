package com.xixi.sdk.utils.network;

import com.longlake.xixisdk.R;
import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.utils.network.LLException.LLIncorrectHttpCodeException;
import com.xixi.sdk.utils.network.LLException.LLNetworkException;

import android.widget.Toast;
import okhttp3.ResponseBody;
import retrofit2.Call;

public abstract class LLCallbackWith500Code extends LLCallback<ResponseBody> {

	protected abstract void onErrorCode500(Call<ResponseBody> arg0, Throwable arg1);
 
	protected abstract boolean onCustomizedError(Throwable arg1) ; 
	protected void onError(Throwable arg1) {
		if ( !onCustomizedError(arg1)) { 
			if (arg1 instanceof LLNetworkException) {
				Toast.makeText(LongLakeApplication.getInstance(),
						LongLakeApplication.getInstance().getResources().getString(R.string.error_network),
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onLLFailure(final Call<ResponseBody> arg0, final Throwable arg1) {
		if (arg1 instanceof LLIncorrectHttpCodeException) {
			String error = arg1.getMessage();
			if (error.contains("404") || error.contains("500")) {
				onErrorCode500(arg0, arg1);
			} else {
				onError(arg1);
			}
		} else {
			onError(arg1);
		}
	}
}
