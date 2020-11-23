
package com.xixi.sdk.controller.xcap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.google.gson.Gson;
import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.factory.LLInstanceCreatorMap;
import com.xixi.sdk.globals.LLSdkGlobals;
import com.xixi.sdk.model.LLBuddy;
import com.xixi.sdk.model.LLXmlNode;
import com.xixi.sdk.model.LayoutCfg;
import com.xixi.sdk.parser.XmlParser;
import com.xixi.sdk.parser.XmlParserAdapter;
import com.xixi.sdk.utils.network.LLCallback;
import com.xixi.sdk.utils.network.LLSdkRetrofitUtils;
import com.xixi.sdk.utils.thread.UIThreadDispatcher;
import android.content.res.AssetManager;
import android.util.Log;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class LLNetworkOverXcap {

	private static LLNetworkOverXcap instance;

	public static enum XCAP_REQUEST_TYPE {
		XCAP_GET_TOTAL_LAYOUT, XCAP_GET_DETAILS, XCAP_PUT_LAYOUT, XCAP_ERASE_DETAILS, XCAP_GET_UESR_PROFILE,

		XCAP_REVISE_UESR_PROFILE, XCAP_PUT_DEV_INFO, XCAP_GET_DEV_INFO,

		XCAP_PUT_USER_DOOR_RECORD, XCAP_DELETE_USER_DOOR_RECORD, XCAP_QUERY_USER_DOOR_RECORD,

		XCAP_REVISE_FRINED_DOOR_INFO, XCAP_DELETE_FRINED_DOOR_INFO,

		XCAP_PUT_AUTHENTICATED_CARDS, XCAP_GET_AUTHENTICATED_CARDS,

		XCAP_GET_DOOR_DETAILS_USER,
		XCAP_PUT_DOOR_LOGS_RECORD, XCAP_DELETE_DOOR_LOGS_RECORD, XCAP_GET_DOOR_LOGS_RECORD,

		XCAP_PUT_DOOR_LOGS_PROFILE, XCAP_GET_DOOR_LOGS_PROFILE,

		XCAP_GET_OUTDOOR_SETTINGS_PWD ,XCAP_GET_USER_PROFILE_PRIVILEDGE,

		XCAP_GET_CONTACTS_VERSION_USER,XCAP_PUT_CONTACTS_VERSION_USER,

		XCAP_GET_USER_PROFILE_NICKNAME,XCAP_PUT_OUTDOOR_VERSIONCODE,

		XCAP_GET_OUTDOOR_APKURL ,XCAP_GET_OUTDOOR_VERSIONNAME ,XCAP_GET_OUTDOOR_TARGETVERSION,

		XCAP_GET_DOOR_DETAILS_VERSION,

		XCAP_PUT_XIXI_PHONE_NUMS_PROFILE,XCAP_GET_XIXI_PHONE_NUMS_JSON,

		XCAP_PUT_XIXI_PHONE_NUMS_JSON ,

		XCAP_PUT_OPENDOOR_PWD,XCAP_GET_OPENDOOR_PWD,
		XCAP_PUT_AIRCONDITONCODE ,XCAP_GET_AIRCONDITONCODES,

		XCAP_GET_DEVICE_WY_ID, XCAP_GET_DEVICE_NAME , XCAP_GET_DEVICE_WYDEVICEID,
		XCAP_GET_ALLOWFLOOR, XCAP_GET_SHAREFLOOR , XCAP_PUT_SHAREFLOOR, XCAP_GET_DEVICE_TYPE,
		XCAP_GET_DEVICE_INFO,
		XCAP_GET_HOUSEKEEPERID, XCAP_GET_SCHEDULER_DEVICE_LOCATION ,XCAP_CHECK_BUDDY,
		XCAP_PUT_WEATHERDATA,XCAP_GET_WEATHERDATA,
		XCAP_GET_GATWAYID
	};

	public static synchronized LLNetworkOverXcap getInstance() {
		if (instance == null) {
			instance = new LLNetworkOverXcap();
		}
		return instance;
	}

	public static String readTestXml() {
		AssetManager mngr = LongLakeApplication.getInstance().getAssets();
		InputStream input;
		try {
			input = mngr.open("layout.xml");

			int size = input.available();
			byte[] buffer = new byte[size];
			input.read(buffer);
			input.close();

			return new String(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void test(String sipAddr, String userId, String param) {
		//
		// String strTest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		// + "<buddy name=\"falflajklf\" type=\"VI\" id=\"aca21368c901\"
		// image=\"nw.png\"></buddy>";

		// getInstance().routeRequest(
		// new LLXcapRequest(XCAP_REQUEST_TYPE.XCAP_PUT_LAYOUT, sipAddr, userId,
		// null, readTestXml()));
		//
		// XmlParser.parseXml(param) ;
		// getInstance().routeRequest(
		// new LLXcapRequest(XCAP_REQUEST_TYPE.XCAP_PUT_USER_PROFILE, sipAddr,
		// userId, (LLCallback<ResponseBody>)null,
		// param ));

	}

	public static synchronized void rvInstance() {
		instance = null;
	}

	private static LLCallback<ResponseBody> default_handler = new LLCallback<ResponseBody>() {

		@Override
		public void onLLFailure(Call<ResponseBody> arg0, Throwable arg1) {
			Log.d(LLSdkGlobals.DANIEL_TAG, arg1.toString());
		}

		@Override
		public void onLLResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1, String payload) {
			Log.d(LLSdkGlobals.DANIEL_TAG, payload);
		}

	};

	private final static String HTTP_HEADER = "http://";

	public void routeRequest(final LLXcapRequest request) {

		String sipAddr = request.getSipAddr().toLowerCase(Locale.ENGLISH);
		if (!sipAddr.startsWith(HTTP_HEADER)) {
			sipAddr = HTTP_HEADER + sipAddr;
		}
		LLCallback<ResponseBody> netReqCallback = null;
		switch (request.getRequestType()) {

			case XCAP_GET_DEV_INFO:

				break;

			case XCAP_PUT_DEV_INFO:

				netReqCallback = new LLCallback<ResponseBody>() {

					@Override
					public void onLLFailure(Call<ResponseBody> arg0, final Throwable arg1) {
						UIThreadDispatcher.dispatch(new Runnable() {
							public void run() {
								request.getOnStringReady().onError(Log.getStackTraceString(arg1) , arg1 );
							}
						});
					}

					@Override
					public void onLLResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1, final String payload) {

						UIThreadDispatcher.dispatch(new Runnable() {
							public void run() {
								request.getOnStringReady().onFinish(payload, request);
							}
						});
					}
				};

				LLSdkRetrofitUtils.putDevInfo(sipAddr,request.getUserId(),(String)request.getParams()).enqueue(netReqCallback);
				break;

			case XCAP_GET_TOTAL_LAYOUT: {

				netReqCallback = new LLCallback<ResponseBody>() {
					@Override
					public void onLLFailure(Call<ResponseBody> arg0, final Throwable arg1) {
						Log.d("clark",arg1.getMessage());
						request.getOnDataReadyHandle().onError(Log.getStackTraceString(arg1) , arg1 );
					}

					@Override
					public void onLLResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1, String payload) {
						XmlParserAdapter ad = XmlParserAdapter.getInstance();
						final LayoutCfg layout = ad.parseData(payload);
						if (layout != null) {

							Log.d("clark",layout.getDeviceTypes().size()+"");
							for(int m=0;m<layout.getDeviceTypes().size();m++){

								Log.d("clark",new Gson().toJson(layout.getDeviceTypes().get(m)));
							}




							request.getOnDataReadyHandle().onFinish(layout.getDeviceTypes(), request);


						} else {
							request.getOnDataReadyHandle().onError("failed to parse" , new RuntimeException("incorrect layout"));
						}
					}
				};
				LLSdkRetrofitUtils.getLayoutOfSpeficiedUser(sipAddr, request.getUserId()).enqueue(netReqCallback);
				break;
			}
			case XCAP_GET_DOOR_DETAILS_USER:
				LLSdkRetrofitUtils.getDoorDetailOfUser(sipAddr, request.getUserId(), request.getDoorId())
						.enqueue(request.getOnRetHandle());
				break;
			case XCAP_GET_DETAILS:   //获取物业等信息
				netReqCallback = new LLCallback<ResponseBody>() {

					@Override
					public void onLLFailure(Call<ResponseBody> arg0, final Throwable arg1) {
						request.getOnStringReady().onError(Log.getStackTraceString(arg1) ,arg1 );
					}

					@Override
					public void onLLResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1, final String payload) {

						if (payload != null) {
							request.getOnStringReady().onFinish(payload, request);
						}
					}

				};

				LLSdkRetrofitUtils.getDetailsOfUser(sipAddr, request.getBuddyId()).enqueue(netReqCallback);
				break;
			case XCAP_GET_DOOR_DETAILS_VERSION:
				LLSdkRetrofitUtils.getOutdoorDetailVersion(sipAddr, request.getDeviceId()).enqueue(request.getOnRetHandle());
				break;
			case XCAP_PUT_LAYOUT:
				LLSdkRetrofitUtils.putLayout(sipAddr, request.getUserId(), (String) request.getParams())
						.enqueue(default_handler);
				break;
			case XCAP_PUT_AUTHENTICATED_CARDS:// ��ʼ����Ȩ����Ϣ
				LLSdkRetrofitUtils.putAuthenticatedCardsProfile(sipAddr, request.getUserId(), (String) request.getParams())
						.enqueue(request.getOnRetHandle());
				break;

			case XCAP_ERASE_DETAILS:
				LLSdkRetrofitUtils.rvDetailsOfUser(sipAddr, request.getUserId(), (String) request.getParams())
						.enqueue(default_handler);
				break;
			// ��ӵ�ͨѶ¼
			case XCAP_REVISE_FRINED_DOOR_INFO:
				LLSdkRetrofitUtils
						.reviseFriendInfo(sipAddr, request.getUserId(), request.getDoorId(), (String) request.getParams())
						.enqueue(request.getOnRetHandle());
				break;
			// ɾ������Ȩ��ͨѶ¼�еķ�������Ϣ
			case XCAP_DELETE_FRINED_DOOR_INFO:
				LLSdkRetrofitUtils.deleteFriendInfo(sipAddr, request.getUserId(), request.getDoorId())
						.enqueue(request.getOnRetHandle());
				break;
			case XCAP_PUT_USER_DOOR_RECORD:
				LLSdkRetrofitUtils
						.putUserDoorRecord(sipAddr, request.getUserId(), request.getUnitkey(), (String) request.getParams())
						.enqueue(request.getOnRetHandle());
				break;
			case XCAP_DELETE_USER_DOOR_RECORD:
				LLSdkRetrofitUtils.deleteUserDoorRecord(sipAddr, request.getUserId(), request.getUnitkey())
						.enqueue(request.getOnRetHandle());
				break;
			case XCAP_QUERY_USER_DOOR_RECORD:
				LLCallback<ResponseBody> doorCallback = new LLCallback<ResponseBody>() {

					@Override
					public void onLLFailure(Call<ResponseBody> arg0, final Throwable arg1) {
						request.getOnStringReady().onError(Log.getStackTraceString(arg1) , arg1 );
					}

					@Override
					public void onLLResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1, final String payload) {

						if (payload != null) {
							request.getOnStringReady().onFinish(payload, request);
						}
					}

				};
				LLSdkRetrofitUtils.queryUserDoorRecord(sipAddr, request.getUserId()).enqueue(doorCallback);
				break;
			case XCAP_PUT_DOOR_LOGS_RECORD:
				LLSdkRetrofitUtils.putDoorLogsRecord(sipAddr, request.getDoorId_identify(), request.getKey_expire(),
						(String) request.getParams()).enqueue(request.getOnRetHandle());
				break;
			case XCAP_DELETE_DOOR_LOGS_RECORD:
				LLSdkRetrofitUtils.deleteDoorLogsRecord(sipAddr, request.getDoorId_identify(), request.getKey_expire())
						.enqueue(request.getOnRetHandle());
				break;
			case XCAP_GET_DOOR_LOGS_RECORD:
				LLCallback<ResponseBody> doorLogsCallback = new LLCallback<ResponseBody>() {

					@Override
					public void onLLFailure(Call<ResponseBody> arg0, final Throwable arg1) {
						request.getOnStringReady().onError(Log.getStackTraceString(arg1) , arg1);
					}

					@Override
					public void onLLResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1, final String payload) {

						if (payload != null) {
							request.getOnStringReady().onFinish(payload, request);
						}
					}
				};
				LLSdkRetrofitUtils.getDoorLogsRecord(sipAddr, request.getDoorId_identify(), request.getKey_expire())
						.enqueue(doorLogsCallback);
				break;
			case XCAP_PUT_DOOR_LOGS_PROFILE:
				LLSdkRetrofitUtils.putLogsProfile(sipAddr, request.getDoorId_identify(), (String) request.getParams())
						.enqueue(request.getOnRetHandle());
				break;
			case XCAP_GET_DOOR_LOGS_PROFILE:
				LLSdkRetrofitUtils.getLogsProfile(sipAddr, request.getDoorId_identify()).enqueue(request.getOnRetHandle());
				break;
			case XCAP_GET_AUTHENTICATED_CARDS:
				LLCallback<ResponseBody> cardsCallback = new LLCallback<ResponseBody>() {

					@Override
					public void onLLFailure(Call<ResponseBody> arg0, final Throwable arg1) {
						request.getOnStringReady().onError(Log.getStackTraceString(arg1), arg1);
					}

					@Override
					public void onLLResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1, String payload) {
						request.getOnStringReady().onFinish(payload, request);
					}

				};
				LLSdkRetrofitUtils.getAuthenticatedCardsProfile(sipAddr, request.getUserId()).enqueue(cardsCallback);
				break;
			case XCAP_GET_UESR_PROFILE:
				netReqCallback = new LLCallback<ResponseBody>() {

					@Override
					public void onLLFailure(Call<ResponseBody> arg0, final Throwable arg1) {
						request.getOnDataReadyHandle().onError(Log.getStackTraceString(arg1), arg1);
					}

					@Override
					public void onLLResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1, String payload) {
						Object params[] = (Object[]) request.getParams();
						final List<LLXmlNode> xmlNodes = LLBuddy.getBuddiesFromInfo(
								XmlParser.parseXml(payload, (LLInstanceCreatorMap) params[0], (String[][]) params[1]));
						if (xmlNodes == null || xmlNodes.size() == 0) {
							request.getOnDataReadyHandle().onError("invalid xml" , new RuntimeException("invalid xml"));
						} else {
							request.getOnDataReadyHandle().onFinish(xmlNodes, request);
						}
					}

				};
				LLSdkRetrofitUtils.getUserProfile(sipAddr, request.getUserId()).enqueue(netReqCallback);
				break;
			case XCAP_REVISE_UESR_PROFILE:
				LLSdkRetrofitUtils.reviseUserProfile(sipAddr, request.getUserId(), (String) request.getParams())
						.enqueue(request.getOnRetHandle());
				break;
			case XCAP_GET_OUTDOOR_SETTINGS_PWD:
				LLSdkRetrofitUtils.getOutdoorSeetingPwd(sipAddr,request.getDeviceId()).enqueue(request.getOnRetHandle());
				break;
			case XCAP_GET_USER_PROFILE_PRIVILEDGE:
				LLSdkRetrofitUtils.getUserPriviledge(sipAddr, request.getDeviceId()).enqueue(request.getOnRetHandle());
				break;

			case XCAP_GET_CONTACTS_VERSION_USER:
				LLSdkRetrofitUtils.getUserContactsVersion(sipAddr, request.getUserId()).enqueue(request.getOnRetHandle());;
				break;
			case XCAP_PUT_CONTACTS_VERSION_USER:
				//SystemStamp
				long time = request.getTimeStamp();
				LLSdkRetrofitUtils.putContactsVersion(sipAddr, request.getUserId(),Long.toString(time)).enqueue(request.getOnRetHandle());
				break;
			case XCAP_GET_USER_PROFILE_NICKNAME:
				LLSdkRetrofitUtils.getUserNickName(sipAddr, request.getDeviceId()).enqueue(request.getOnRetHandle());
				break;
			case XCAP_GET_OUTDOOR_VERSIONNAME:
				LLSdkRetrofitUtils.getOutdoorVersionName(sipAddr,request.getDeviceId()).enqueue(request.getOnRetHandle());
				break;
			case XCAP_GET_OUTDOOR_TARGETVERSION:
				com.xixi.sdk.logger.Log.d("clark","getOutdoorDetailVersion");
				LLSdkRetrofitUtils.getOutdoorTargetVersion(sipAddr,request.getDeviceId()).enqueue(request.getOnRetHandle());
				break;
			case XCAP_GET_OUTDOOR_APKURL:
				LLSdkRetrofitUtils.getOutdoorApkUrl(sipAddr,request.getDeviceId()).enqueue(request.getOnRetHandle());
				break;
			case XCAP_GET_DEVICE_WY_ID:
				LLSdkRetrofitUtils.getWYId(sipAddr,request.getDeviceId()).enqueue(request.getOnRetHandle());
				break;
			case XCAP_GET_DEVICE_WYDEVICEID :
				LLSdkRetrofitUtils.getWYDeviceId(sipAddr,request.getDeviceId()).enqueue(request.getOnRetHandle());
				break ;
			case XCAP_GET_DEVICE_NAME:
				LLSdkRetrofitUtils.getDeviceName(sipAddr,request.getDeviceId()).enqueue(request.getOnRetHandle());
				break;
			case XCAP_GET_DEVICE_TYPE:
				LLSdkRetrofitUtils.getDeviceType(sipAddr,request.getDeviceId()).enqueue(request.getOnRetHandle());
				break;
			case XCAP_GET_DEVICE_INFO:
				LLSdkRetrofitUtils.getDeviceInfo(sipAddr,request.getDeviceId()).enqueue(request.getOnRetHandle());
				break;
			case XCAP_PUT_OUTDOOR_VERSIONCODE:
				LLSdkRetrofitUtils.putOutdoorVersionCode(sipAddr, request.getDeviceId(),(String) request.getParams()).enqueue(request.getOnRetHandle());
				break;
			case XCAP_PUT_XIXI_PHONE_NUMS_PROFILE:
				LLSdkRetrofitUtils.putXiXiPhoneNumsProfile(sipAddr, request.getUserId(), (String) request.getParams())
						.enqueue(request.getOnRetHandle());
				break;
			case XCAP_GET_XIXI_PHONE_NUMS_JSON:
				LLSdkRetrofitUtils.getPhoneNumsJson(sipAddr, request.getDeviceId()).enqueue(request.getOnRetHandle());
				break;
			case XCAP_PUT_XIXI_PHONE_NUMS_JSON:
				LLSdkRetrofitUtils.putPhoneNumsJson(sipAddr, request.getUserId(),(String)request.getParams()).enqueue(request.getOnRetHandle());
				//LLSdkRetrofitUtils.putContactsVersion(sipAddr, request.getUserId(),Long.toString(time)).enqueue(request.getOnRetHandle());
				break;
			case XCAP_PUT_OPENDOOR_PWD:
				LLSdkRetrofitUtils.putOpendoorPwd(sipAddr, request.getDoorId_identify(),request.getKey_expire(),(String) request.getParams()).enqueue(request.getOnRetHandle());
				break;
			case XCAP_GET_OPENDOOR_PWD:
				LLSdkRetrofitUtils.getOpendoorPwd(sipAddr, request.getDoorId_identify(),request.getKey_expire()).enqueue(request.getOnRetHandle());
				break;
			case XCAP_PUT_AIRCONDITONCODE:
				LLSdkRetrofitUtils.putAirconditionCode(sipAddr, request.getDoorId_identify(),request.getKey_expire(),(String) request.getParams()).enqueue(request.getOnRetHandle());
				break;
			case XCAP_GET_AIRCONDITONCODES:
				LLSdkRetrofitUtils.getAirconditionCode(sipAddr, request.getDoorId_identify(),request.getKey_expire()).enqueue(request.getOnRetHandle());
				break;
			case XCAP_GET_ALLOWFLOOR:
				LLSdkRetrofitUtils.getAllowFloor(sipAddr, request.getDoorId_identify(),request.getKey_expire()).enqueue(request.getOnRetHandle());
				break;
			case XCAP_GET_SHAREFLOOR :
				LLSdkRetrofitUtils.getShareFloor(sipAddr, request.getDoorId_identify(),request.getKey_expire()).enqueue(request.getOnRetHandle());
				break ;
			case XCAP_PUT_SHAREFLOOR :
				LLSdkRetrofitUtils.putShareFloor(sipAddr, request.getDoorId_identify(),request.getKey_expire(),(String) request.getParams()).enqueue(request.getOnRetHandle());
				break ;
			case XCAP_GET_SCHEDULER_DEVICE_LOCATION :
				LLSdkRetrofitUtils.getSchedulerDeviceLocation(sipAddr,request.getDeviceId()).enqueue(request.getOnRetHandle());
				break;
			case XCAP_GET_HOUSEKEEPERID :
				LLSdkRetrofitUtils.getHouseKeeperId(sipAddr,request.getDeviceId()).enqueue(request.getOnRetHandle());
				break;
			case XCAP_CHECK_BUDDY :
				LLSdkRetrofitUtils.checkBuddy(sipAddr, request.getDoorId_identify(),request.getKey_expire()).enqueue(request.getOnRetHandle());
				break ;
			case XCAP_GET_GATWAYID:
				LLSdkRetrofitUtils.getGateWayId(sipAddr,request.getDeviceId()).enqueue(request.getOnRetHandle());
				break;
			case XCAP_GET_WEATHERDATA:
				LLSdkRetrofitUtils.getWeatherData(sipAddr, request.getUserId()).enqueue(request.getOnRetHandle());
				break;
			case XCAP_PUT_WEATHERDATA:
				LLSdkRetrofitUtils.putWeatherData(sipAddr, request.getUserId(),(String) request.getParams()).enqueue(request.getOnRetHandle());
				break;
			default:
				break;
		}
	}
}
