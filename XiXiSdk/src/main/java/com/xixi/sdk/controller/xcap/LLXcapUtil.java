package com.xixi.sdk.controller.xcap;

import com.xixi.sdk.controller.xcap.LLNetworkOverXcap;
import com.xixi.sdk.controller.xcap.LLNetworkOverXcap.XCAP_REQUEST_TYPE;
import com.xixi.sdk.controller.xcap.LLXcapRequest;
import com.xixi.sdk.model.LLBuddy;
import com.xixi.sdk.model.LLQrCodeParamSet.LLQrCodeAuthenticationParam;
import com.xixi.sdk.model.LLQrCodeParamSet.LLQrCodeShareDoorParam;
import com.xixi.sdk.model.LLQrCodeParamSet.LLQrCodeSmartDeviceParam;
import com.xixi.sdk.model.LLUserData;
import com.xixi.sdk.model.LLXmlNode;
import com.xixi.sdk.parser.LLGsonUtils;
import com.xixi.sdk.parser.XmlParser;
import com.xixi.sdk.utils.file.IoCompletionListener;
import com.xixi.sdk.utils.file.IoCompletionListener1;
import com.xixi.sdk.utils.network.LLCallback;
import com.xixi.sdk.utils.network.LLCallbackWith500Code;
import com.xixi.sdk.utils.thread.UIThreadDispatcher;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class LLXcapUtil {

	private static LLXcapUtil instance;

	public LLXcapUtil() {}

	public synchronized static LLXcapUtil getInstance() {
		if (instance == null) {
			instance = new LLXcapUtil();
		}
		return instance;
	}
	public void getContactsVersion(final String userId, final String sipAddr, final IoCompletionListener1<String> ioCall) {
		LLCallbackWith500Code callback = new LLCallbackWith500Code() {
			@Override
			protected void onErrorCode500(Call<ResponseBody> arg0, Throwable arg1) {
				ioCall.onFinish("", null);
			}
			@Override
			public void onLLResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1, String payload) {
				ioCall.onFinish(payload, null);
				Log.d("clark",userId+"---"+sipAddr+"clark getPayload="+payload);
			}
			@Override
			protected boolean onCustomizedError(Throwable arg1) { 
				ioCall.onFinish("", null);
				return false;
			}
		};
		LLXcapRequest request = new LLXcapRequest(XCAP_REQUEST_TYPE.XCAP_GET_CONTACTS_VERSION_USER,sipAddr,
				userId, callback, "");
		LLNetworkOverXcap.getInstance().routeRequest(request);
	}
}
