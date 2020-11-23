package com.xixi.sdk.utils.network;


import com.xixi.sdk.parser.LLGsonUtils;
import com.xixi.sdk.utils.thread.UIThreadDispatcher;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class LLCallbackAsJsonString<T> implements Callback<ResponseBody> {

	public LLCallbackAsJsonString() {

	}

	Object[] param;

	public Object[] getParam() {
		return param;
	}

	public void setParam(Object[] param) {
		this.param = param;
	}

	public LLCallbackAsJsonString(Object[] o) {
		param = o;
	}

	public void onGeneralFailure(Call<ResponseBody> arg0, Throwable arg1) {
		onGeneralHandleError(arg1);
		onLLFailure(arg0, arg1);
	}


	public abstract void onLLFailure(Call<ResponseBody> arg0, Throwable arg1);

	public abstract void onLLResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1, T o);

	private boolean onGeneralHandleError(Throwable arg1) {
		if (arg1 instanceof LLException.LLNetworkException) {
			return true;
		} else
			return false;
	}

	@Override
	public void onFailure(final Call<ResponseBody> arg0, final Throwable arg1) {
		// if (!onLLNetWorkError(arg1))
		// {
		UIThreadDispatcher.dispatch(new Runnable() {
			public void run() {

				arg1.printStackTrace();
				onGeneralFailure(arg0, new LLException.LLNetworkException("networkerror"));
			}
		}, 1000);

	}

	public abstract Class<T> _getClass();

	@SuppressWarnings("unchecked")
	@Override
	public void onResponse(final Call<ResponseBody> arg0, final Response<ResponseBody> arg1) {
		String strJson = null;
		T o = null;
		ResponseBody res = null;
		try {
			if (!arg1.isSuccessful()) {
				throw new LLException.LLIncorrectHttpCodeException(arg1.code());
			}
			res = arg1.body();
			strJson = arg1.body().string();
			o = (T) LLGsonUtils.fromJson(strJson, _getClass());
			if (o == null) {
				throw new LLException.LLMalFormatOfJsonException(strJson);
			}

			final T o1 = o;
			UIThreadDispatcher.dispatch(new Runnable() {
				public void run() {
					onLLResponse(arg0, arg1, o1);
				}
			});
		} catch (final Exception e) {

			UIThreadDispatcher.dispatch(new Runnable() {
				public void run() {
					onGeneralFailure(arg0, e);
				}
			});
		} finally {
			try {
				try {
					if (res != null)
						res.close();
				} catch (Exception e) {
				}
			} catch (Exception e) {
			}

		}
	}

}
