package com.xixi.sdk.utils.network;

import com.longlake.xixisdk.R;
import com.xixi.sdk.LLSDKUtils;
import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.controller.xcap.LLNetworkOverXcap;
import com.xixi.sdk.controller.xcap.LLNetworkOverXcap.XCAP_REQUEST_TYPE;
import com.xixi.sdk.controller.xcap.LLXcapRequest;
import com.xixi.sdk.globals.LLSdkGlobals;
import com.xixi.sdk.logger.Log;
import com.xixi.sdk.sipmsg.params.LLSipCmds;
import com.xixi.sdk.utils.file.IoCompletionListener1;
import com.xixi.sdk.utils.file.LLDoorSafeUserUtils;

import android.text.TextUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class LLDoorSafeNetController extends LLDoorSafeUserUtils implements LLSipCmds {

	protected LLDoorSafeNetController() {
	}
	
	private String houseKeeperId = "1587657524438178";
	public String getHouseKeeperId() {
		return houseKeeperId;
	}
	public void setHouseKeeperId(String houseKeeperId) {
		this.houseKeeperId = houseKeeperId;
	}
	//
	// public static void getAllUserDataOverXcap(){
	//
	// LLCallbackWith500Code callbacks = new LLCallbackWith500Code() {
	// @Override
	// public void onLLResponse(Call<ResponseBody> arg0, Response<ResponseBody>
	// arg1, String payload) {
	// // TODO Auto-generated method stub
	// Log.d(LongLakeApplication.DANIEL_TAG,"XCAP->"+payload);
	//
	// LLDoorSafeUserUtils.getInstance().setSubscribers(payload);
	//
	// }
	//
	// @Override
	// protected void onErrorCode500(Call<ResponseBody> arg0, Throwable arg1) {
	// LLNetworkOverXcap.getInstance().routeRequest(new
	// LLXcapRequest(XCAP_REQUEST_TYPE.XCAP_PUT_XIXI_PHONE_NUMS_PROFILE,
	// LLSdkGlobals.getMessagePushUrl(), "1587657524438178", new
	// LLCallback<ResponseBody>() {
	// public void onLLFailure(Call<ResponseBody> arg0, Throwable arg1) {
	// Log.i(LongLakeApplication.DANIEL_TAG, "init Safe_Phone_Nums failed ");
	// }
	// public void onLLResponse(Call<ResponseBody> arg0, Response<ResponseBody>
	// arg1,
	// String payload) {
	// Log.i(LongLakeApplication.DANIEL_TAG,
	// "on response : init Safe_Phone_Nums success " + "\n" + payload);
	// }
	// },phone_nums_xml));
	// }
	//
	// @Override
	// protected boolean onCustomizedError(Throwable arg1) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	// }
	// }

	private static LLDoorSafeNetController instance;

	public static synchronized LLDoorSafeNetController getInstance() {
		if (instance == null) {
			instance = new LLDoorSafeNetController();
		}
		return instance;
	}

	public int operateUser(final String user, final boolean isAdd, final IoCompletionListener1<String> ioCall) {
		boolean iscontaint = isContain(user);
		do {
			if (iscontaint) {
				if (isAdd) {
					ioCall.onFinish("safe", SUCCESS_TAG);
					break;
				} else {
					// remove from xcap
					operateUserData(user, isAdd);
				}
			} else {
				if (isAdd) {
					// add to xcap
					operateUserData(user, isAdd);
				} else {
					ioCall.onFinish("unsafe", SUCCESS_TAG);
					break;
				}
			}
			String jsonStr = getAllUserDataOverCatch();
			LLNetworkOverXcap.getInstance()
					.routeRequest(new LLXcapRequest(XCAP_REQUEST_TYPE.XCAP_PUT_XIXI_PHONE_NUMS_JSON,
							LLSdkGlobals.getMessagePushUrl(), houseKeeperId, new LLCallback<ResponseBody>() {
								@Override
								public void onLLResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1,
										String payload) {
									if(ioCall!=null){
										ioCall.onFinish(isAdd ? "safe" :
											 "unsafe", SUCCESS_TAG);
											//Log.i(LongLakeApplication.DANIEL_TAG, "putXcap->success");
									}else {
										
									}
								}

								@Override
								public void onLLFailure(Call<ResponseBody> arg0, Throwable arg1) {
									//operateData rollback
									 operateUserData(user,!isAdd);
									 ioCall.onFinish(isAdd ? "unsafe" :
									 "safe", INVALID_TAG);
									//Log.i(LongLakeApplication.DANIEL_TAG, "putXcap->Faild");
								}
							}, jsonStr));

			
		} while (false);

		return this.sizeOfSubscribers();
	}
	private  final String getPhoneNumXml(){
		String name = LLSDKUtils.getContext().getString(R.string.name_devices);
		String phoneNumXml = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
			+ "<phoneNums id=\"%s\" name=\"%s\" userIds=\"{}\"/>",houseKeeperId ,name);
		return phoneNumXml ;
	}
	public void getAllUsers(){
		
		getAllUserDataOverFile(new IoCompletionListener1<String>() {

			@Override
			public void onFinish(String data, Object context) {
				// TODO Auto-generated method stub
				Log.i(LongLakeApplication.DANIEL_TAG,data);
				if(data.length()==0||TextUtils.equals(data, "[]")){
					getAllUserDataOverXcap();
				}else{
					setSubscribers(data,false);
				}
			}
		});
	}
	private void getAllUserDataOverXcap() {

		LLCallbackWith500Code callbacks = new LLCallbackWith500Code() {
			@Override
			public void onLLResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1, String payload) {
				// TODO Auto-generated method stub
				setSubscribers(payload,true);
			}

			@Override
			protected void onErrorCode500(Call<ResponseBody> arg0, Throwable arg1) {
				LLNetworkOverXcap.getInstance()
						.routeRequest(new LLXcapRequest(XCAP_REQUEST_TYPE.XCAP_PUT_XIXI_PHONE_NUMS_PROFILE,
								LLSdkGlobals.getMessagePushUrl(), houseKeeperId, new LLCallback<ResponseBody>() {
									public void onLLFailure(Call<ResponseBody> arg0, Throwable arg1) {
										//Log.i(LongLakeApplication.DANIEL_TAG, "init Safe_Phone_Nums failed ");
									}

									public void onLLResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1,
											String payload) {
//										Log.i(LongLakeApplication.DANIEL_TAG,
//												"on response : init Safe_Phone_Nums success " + "\n" + payload);
									}
								}, getPhoneNumXml()));
			}

			@Override
			protected boolean onCustomizedError(Throwable arg1) {
				return false;
			}
		};
		LLNetworkOverXcap.getInstance().routeRequest(new LLXcapRequest(XCAP_REQUEST_TYPE.XCAP_GET_XIXI_PHONE_NUMS_JSON,
				LLSdkGlobals.getMessagePushUrl(), houseKeeperId, callbacks));

	}
}
