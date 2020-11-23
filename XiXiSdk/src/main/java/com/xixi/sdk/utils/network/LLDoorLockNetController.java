package com.xixi.sdk.utils.network;
 
import com.xixi.sdk.controller.xcap.LLNetworkOverXcap;
import com.xixi.sdk.controller.xcap.LLNetworkOverXcap.XCAP_REQUEST_TYPE;
import com.xixi.sdk.controller.xcap.LLXcapRequest;
import com.xixi.sdk.globals.LLSdkGlobals;
import com.xixi.sdk.sipmsg.params.LLSipCmds;
import com.xixi.sdk.utils.file.IoCompletionListener1;
import com.xixi.sdk.utils.file.LLDoorLockUtils;

import android.text.TextUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class LLDoorLockNetController extends LLDoorLockUtils implements LLSipCmds {
	String menu_ID = "1587657524438178";
	
	public String getMenu_ID() {
		return menu_ID;
	}

	public void setMenu_ID(String menu_ID) {
		this.menu_ID = menu_ID;
	}

	private static LLDoorLockNetController instance;
 
	public static synchronized LLDoorLockNetController getInstance() {
		if (instance == null) {
			instance = new LLDoorLockNetController();
		}
		return instance;
	}

	public void setPassword(String password, final IoCompletionListener1<String> io) {
		putDoorLockPwd(password, menu_ID, new IoCompletionListener1<String>() {

			@Override
			public void onFinish(String data, Object context) {
				io.onFinish(data, context);
			}
		});
	} 

	public void initGetLockPwd() {
		getDoorLockPwd(menu_ID, new IoCompletionListener1<String>() {
			@Override
			public void onFinish(String data, Object context) {
			}
		});
	}

	public void getDoorLockPwd(String menuId, final IoCompletionListener1<String> io) {// XCAP_PUT_OPENDOOR_PWD,XCAP_GET_OPENDOOR_PWD

		String doorId = LLSdkGlobals.getUserName();
		LLCallbackWith500Code callbacks = new LLCallbackWith500Code() { 
			@Override
			public void onLLResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1, String payload) {
 
				if (!TextUtils.isEmpty(payload)) {
					setPassword(payload);
				} else {
					// getLockPwdOverFile(io);
				}
				String pwd = getPassword(); 
				io.onFinish(pwd, null);
			}

			@Override 
			protected void onErrorCode500(Call<ResponseBody> arg0, Throwable arg1) {  
				io.onFinish("fail-500", null);
			}

			@Override
			protected boolean onCustomizedError(Throwable arg1) { 
				io.onFinish("fail", null); 
				return false;
			}
		};
		LLNetworkOverXcap.getInstance().routeRequest(new LLXcapRequest(XCAP_REQUEST_TYPE.XCAP_GET_OPENDOOR_PWD,
				LLSdkGlobals.getMessagePushUrl(), doorId, menuId, callbacks, ""));
	}

	public void putDoorLockPwd(final String password, String menuId, final IoCompletionListener1<String> io) {
		String doorId = LLSdkGlobals.getUserName();
		LLNetworkOverXcap.getInstance().routeRequest(new LLXcapRequest(XCAP_REQUEST_TYPE.XCAP_PUT_OPENDOOR_PWD,
				LLSdkGlobals.getMessagePushUrl(), doorId, menuId, new LLCallback<ResponseBody>() {

					@Override
					public void onLLResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1, String payload) {
						setPassword(password);
						io.onFinish("success", SUCCESS_TAG);
					}

					@Override
					public void onLLFailure(Call<ResponseBody> arg0, Throwable arg1) {
						io.onFinish("fail",INVALID_TAG);

					}
				}, password));
	}
}
