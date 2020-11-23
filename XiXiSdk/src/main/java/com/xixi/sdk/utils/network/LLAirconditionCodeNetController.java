package com.xixi.sdk.utils.network;

import com.xixi.sdk.controller.xcap.LLNetworkOverXcap;
import com.xixi.sdk.controller.xcap.LLNetworkOverXcap.XCAP_REQUEST_TYPE;
import com.xixi.sdk.controller.xcap.LLXcapRequest;
import com.xixi.sdk.globals.LLSdkGlobals;
import com.xixi.sdk.sipmsg.params.LLSipCmds;
import com.xixi.sdk.utils.file.IoCompletionListener1;
import com.xixi.sdk.utils.file.LLFileUtils;
import com.xixi.sdk.utils.housekeeper.AirNameIndex;
import com.xixi.sdk.utils.housekeeper.LLAirconditionUtils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class LLAirconditionCodeNetController extends LLAirconditionUtils implements LLSipCmds {
	String menuId = LLDoorLockNetController.getInstance().menu_ID;
	String housekeeperId = LLSdkGlobals.getUserName();
	private final static String AIRCONDITON_INDEX = "AIRCONDITON_INDEX";
	private static LLAirconditionCodeNetController instance;

	public static synchronized LLAirconditionCodeNetController getInstance() {
		if (instance == null) {
			instance = new LLAirconditionCodeNetController();
		}
		return instance;
	}

	public void getAirconditionCodeFromXcap(final IoCompletionListener1<Integer> io) {
		LLNetworkOverXcap.getInstance().routeRequest(new LLXcapRequest(XCAP_REQUEST_TYPE.XCAP_GET_AIRCONDITONCODES,
				LLSdkGlobals.getMessagePushUrl(), housekeeperId, menuId, new LLCallback<ResponseBody>() {

					@Override
					public void onLLResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1, String payload) {
						if (!payload.isEmpty()) {
							int airconditionIndex = Integer.parseInt(payload);
							int airconditionCode = Integer.parseInt(AirNameIndex.air_index[airconditionIndex][1]);
							setAirconditionIndex(airconditionIndex);
							setAirconditionCode(airconditionCode);
							io.onFinish(airconditionIndex, SUCCESS_TAG);
						} else {
							io.onFinish(-1, INVALID_TAG);
						}
					}

					@Override
					public void onLLFailure(Call<ResponseBody> arg0, Throwable arg1) {
						io.onFinish(-1, INVALID_TAG);
					}
				}, ""));
	}

	public void putAirconditionCodeToXcap(String airconditionindex, final IoCompletionListener1<String> io) {
		setAirconditionIndex(Integer.parseInt(airconditionindex));
		setAirconditionCode(Integer.parseInt(AirNameIndex.air_index[Integer.parseInt(airconditionindex)][1]));
		LLFileUtils.getInstance().writeToFile(AIRCONDITON_INDEX, airconditionindex);
		LLNetworkOverXcap.getInstance().routeRequest(new LLXcapRequest(XCAP_REQUEST_TYPE.XCAP_PUT_AIRCONDITONCODE,
				LLSdkGlobals.getMessagePushUrl(), housekeeperId, menuId, new LLCallback<ResponseBody>() {

					@Override
					public void onLLResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1, String payload) {
						// io.onFinish("", SUCCESS_TAG );
					}

					@Override
					public void onLLFailure(Call<ResponseBody> arg0, Throwable arg1) {
						// io.onFinish("", SUCCESS_TAG );
					}
				}, airconditionindex));

	}

	public void fetchAirCondition(final IoCompletionListener1<Integer> io) {
		if (getAirconditionIndex() != -1) {
			io.onFinish(getAirconditionIndex(), SUCCESS_TAG);
		} else {
			LLFileUtils.getInstance().readFromFile(AIRCONDITON_INDEX, new IoCompletionListener1<String>() {
				@Override
				public void onFinish(String airconditionindex, Object context) {
					if (!airconditionindex.isEmpty()) {
						setAirconditionIndex(Integer.parseInt(airconditionindex.trim()));
						setAirconditionCode(Integer
								.parseInt(AirNameIndex.air_index[Integer.parseInt(airconditionindex.trim())][1]));
						io.onFinish(getAirconditionIndex(), SUCCESS_TAG);
					} else {
						LLNetworkOverXcap.getInstance()
								.routeRequest(new LLXcapRequest(XCAP_REQUEST_TYPE.XCAP_GET_AIRCONDITONCODES,
										LLSdkGlobals.getMessagePushUrl(), housekeeperId, menuId,
										new LLCallback<ResponseBody>() {

											@Override
											public void onLLResponse(Call<ResponseBody> arg0,
													Response<ResponseBody> arg1, String payload) {
												if (!payload.isEmpty()) {
													int airconditionIndex = Integer.parseInt(payload);
													int airconditionCode = Integer
															.parseInt(AirNameIndex.air_index[airconditionIndex][1]);
													setAirconditionIndex(airconditionIndex);
													setAirconditionCode(airconditionCode);
													LLFileUtils.getInstance().writeToFile(AIRCONDITON_INDEX, String.valueOf(airconditionIndex));
													io.onFinish(airconditionIndex, SUCCESS_TAG);
												} else {
													io.onFinish(-1, INVALID_TAG);
												}
											}

											@Override
											public void onLLFailure(Call<ResponseBody> arg0, Throwable arg1) {
												io.onFinish(-1, INVALID_TAG);
											}
										}, ""));
					}
				}
			});
		}
	} 
}
