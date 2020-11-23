package com.xixi.sdk.utils.network;

import com.xixi.sdk.controller.xcap.params.XCapParams;
import com.xixi.sdk.controller.xcap.params.XCapParams.IDeleteContactDoor;
import com.xixi.sdk.controller.xcap.params.XCapParams.IDeleteDoorLogsRecord;
import com.xixi.sdk.controller.xcap.params.XCapParams.IDeleteDoorRecord;
import com.xixi.sdk.controller.xcap.params.XCapParams.IGetAirconditionCode;
import com.xixi.sdk.controller.xcap.params.XCapParams.IGetAllowFloor;
import com.xixi.sdk.controller.xcap.params.XCapParams.IGetAuthenticatedCardsProfile;
import com.xixi.sdk.controller.xcap.params.XCapParams.IGetDetailedInfo;
import com.xixi.sdk.controller.xcap.params.XCapParams.IGetDeviceInfo;
import com.xixi.sdk.controller.xcap.params.XCapParams.IGetDeviceName;
import com.xixi.sdk.controller.xcap.params.XCapParams.IGetDeviceType;
import com.xixi.sdk.controller.xcap.params.XCapParams.IGetDoorLogsProfile;
import com.xixi.sdk.controller.xcap.params.XCapParams.IGetDoorLogsRecord;
import com.xixi.sdk.controller.xcap.params.XCapParams.IGetFriendList;
import com.xixi.sdk.controller.xcap.params.XCapParams.IGetHouseKeeperId;
import com.xixi.sdk.controller.xcap.params.XCapParams.IGetOpendoorPwd;
import com.xixi.sdk.controller.xcap.params.XCapParams.IGetOutdoorApkUrl;
import com.xixi.sdk.controller.xcap.params.XCapParams.IGetOutdoorDetailVersion;
import com.xixi.sdk.controller.xcap.params.XCapParams.IGetOutdoorSettingPwd;
import com.xixi.sdk.controller.xcap.params.XCapParams.IGetOutdoorTargetVersion;
import com.xixi.sdk.controller.xcap.params.XCapParams.IGetOutdoorVersionName;
import com.xixi.sdk.controller.xcap.params.XCapParams.IGetPhoneNumsJson;
import com.xixi.sdk.controller.xcap.params.XCapParams.IGetSchedulerDeviceLocation;
import com.xixi.sdk.controller.xcap.params.XCapParams.IGetShareFloor;
import com.xixi.sdk.controller.xcap.params.XCapParams.IGetSpecialFriend;
import com.xixi.sdk.controller.xcap.params.XCapParams.IGetUserContactsVersion;
import com.xixi.sdk.controller.xcap.params.XCapParams.IGetUserNickName;
import com.xixi.sdk.controller.xcap.params.XCapParams.IGetUserPriviledge;
import com.xixi.sdk.controller.xcap.params.XCapParams.IGetUserProfile;
import com.xixi.sdk.controller.xcap.params.XCapParams.IGetWYDeviceId;
import com.xixi.sdk.controller.xcap.params.XCapParams.IGetWYId;
import com.xixi.sdk.controller.xcap.params.XCapParams.IPutAirconditionCode;
import com.xixi.sdk.controller.xcap.params.XCapParams.IPutAuthenticatedCardsProfile;
import com.xixi.sdk.controller.xcap.params.XCapParams.IPutDevInfo;
import com.xixi.sdk.controller.xcap.params.XCapParams.IPutDoorLogsProfile;
import com.xixi.sdk.controller.xcap.params.XCapParams.IPutDoorLogsRecord;
import com.xixi.sdk.controller.xcap.params.XCapParams.IPutDoorRecord;
import com.xixi.sdk.controller.xcap.params.XCapParams.IPutLayout;
import com.xixi.sdk.controller.xcap.params.XCapParams.IPutOpendoorPwd;
import com.xixi.sdk.controller.xcap.params.XCapParams.IPutOutdoorVersionCode;
import com.xixi.sdk.controller.xcap.params.XCapParams.IPutPhoneNumsJson;
import com.xixi.sdk.controller.xcap.params.XCapParams.IPutShareFloor;
import com.xixi.sdk.controller.xcap.params.XCapParams.IPutUserContactsVersion;
import com.xixi.sdk.controller.xcap.params.XCapParams.IPutUserProfile;
import com.xixi.sdk.controller.xcap.params.XCapParams.IPutXiXiPhoneNumsProfile;
import com.xixi.sdk.controller.xcap.params.XCapParams.IQueryDoorRecords;
import com.xixi.sdk.controller.xcap.params.XCapParams.IReviseFriendInfo;
import com.xixi.sdk.controller.xcap.params.XCapParams.IRvDetailedInfo;
import com.xixi.sdk.globals.LLSdkGlobals;
import com.xixi.sdk.logger.Log;
import com.xixi.sdk.model.LLLayoutXmlNode;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class LLSdkRetrofitUtils extends LLSdkGlobals {

	private static final long DEFAULT_TIMEOUT = 20;

	public static void switchURL() {
	}

	protected static OkHttpClient client = new OkHttpClient.Builder().connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
			.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS).readTimeout(50, TimeUnit.SECONDS).build();

	protected final static Map<String, Retrofit> retrofit_map = new HashMap<String, Retrofit>();

	public static Call<ResponseBody> getLayoutOfSpeficiedUser(String sipAddr, String userId) {
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(), sipAddr);
		return makeXcapNetUtils(url).create(IGetFriendList.class).getLayout(userId);
	}


	public static class DeviceInfo {

		private String deviceName;
		private String deviceType;
		private Long timestamp;

		public String getDeviceName() {
			return deviceName;
		}

		public String getDeviceType() {
			return deviceType;
		}

		public Long getTimestamp() {
			return timestamp;
		}

		public void setDeviceName(String deviceName) {
			this.deviceName = deviceName;
		}

		public void setDeviceType(String deviceType) {
			this.deviceType = deviceType;
		}

		public void setTimestamp(Long timestamp) {
			this.timestamp = timestamp;
		}

		public DeviceInfo(String deviceName, String deviceType, Long timestamp) {
			this.deviceName = deviceName;
			this.deviceType = deviceType;
			this.timestamp = timestamp;
		}
	}

	public interface DeviceIPs {
		// 获得ip地址
		@POST("getIntranetMap")
		Call<ResponseBody> getDeviceIpInfo(@Body DeviceInfo devInfo);
	}

	public static Call<ResponseBody> getIps(Retrofit retrofit ,DeviceInfo devInfo) {
		return retrofit.create(DeviceIPs.class).getDeviceIpInfo(devInfo);
	}

	public static Call<ResponseBody> putDevInfo(String sipAddr, String devId, final String data) {
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(), sipAddr);
		return makeXcapNetUtils(url).create(IPutDevInfo.class).putDevInfo(devId,
				RequestBody.create(MediaType.parse("text/xml"), data));
	}
	
	public static Call<ResponseBody> getDetailsOfUser(String sipAddr,String deviceId) {
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(), sipAddr);
		return makeXcapNetUtils(url).create(IGetDetailedInfo.class).getDetails(deviceId);
	}
	
	public static Call<ResponseBody> getDoorDetailOfUser(String sipAddr, String userId,String buddyId) {
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(), sipAddr);
		return makeXcapNetUtils(url).create(IGetSpecialFriend.class).getSpecialLayout(userId, buddyId);
	}
      
	public static Call<ResponseBody> putLayout(String sipAddr, String userId, final String data) {
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(), sipAddr);
		return makeXcapNetUtils(url).create(IPutLayout.class).putLayout(userId,
				RequestBody.create(MediaType.parse("text/xml"), data));
	}

	public static Call<ResponseBody> reviseFriendInfo(String sipAddr, String userId, String buddyId,
			final String data) {
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(), sipAddr);
		return makeXcapNetUtils(url).create(IReviseFriendInfo.class).reviseFriendInfo(userId, LLLayoutXmlNode.ROOT_ELEMENT_NAME,
				buddyId, RequestBody.create(MediaType.parse("text/xml"), data));
	}
	public static Call<ResponseBody> getWYId(String sipAddr,String deviceId){
	    String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
	    return makeXcapNetUtils(url).create(IGetWYId.class).getWYId(deviceId);
	    
	  }
	
	public static Call<ResponseBody> getWYDeviceId(String sipAddr,String deviceId){
	    String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
	    return makeXcapNetUtils(url).create(IGetWYDeviceId.class).getWYDeviceId(deviceId);
	    
	  }
	public static Call<ResponseBody> getDeviceName(String sipAddr,String deviceId){
	      String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
	      return makeXcapNetUtils(url).create(IGetDeviceName.class).getDeviceName(deviceId);
	      
	    }
	public static Call<ResponseBody> getDeviceType(String sipAddr,String deviceId){
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
		return makeXcapNetUtils(url).create(IGetDeviceType.class).getDeviceType(deviceId);
		
	}
	
	public static Call<ResponseBody> getDeviceInfo(String sipAddr,String deviceId){
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
		return makeXcapNetUtils(url).create(IGetDeviceInfo.class).getDeviceInfo(deviceId);
		
	}
	public static Call<ResponseBody> deleteFriendInfo(String sipAddr, String userId, String doorId) {
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(), sipAddr);
		return makeXcapNetUtils(url).create(IDeleteContactDoor.class).delteContactDoor(userId, LLLayoutXmlNode.ROOT_ELEMENT_NAME,
				doorId);
	}

	public static Call<ResponseBody> rvDetailsOfUser(String sipAddr, String userId, String buddyId) {
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(), sipAddr);
		return makeXcapNetUtils(url).create(IRvDetailedInfo.class).rvDetails(userId, LLLayoutXmlNode.ROOT_ELEMENT_NAME, buddyId);
	}

	public static Call<ResponseBody> putUserProfile(String sipAddr, String userId, String data) {
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(), sipAddr);
		return makeXcapNetUtils(url).create(IPutUserProfile.class).putUserProfile(userId,
				RequestBody.create(MediaType.parse("text/xml"), data));
	}
 
	public static Call<ResponseBody> getUserProfile(String sipAddr,String userId) {
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(), sipAddr);
		return makeXcapNetUtils(url).create(IGetUserProfile.class).getUserProfile(userId);
	} 
	public static Call<ResponseBody> reviseUserProfile(String sipAddr, String userId, String data) {
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(), sipAddr);
		return makeXcapNetUtils(url).create(IPutUserProfile.class).putUserProfile(userId,
				RequestBody.create(MediaType.parse("text/xml"), data));
	} 
	public static Call<ResponseBody> deleteUserDoorRecord(String sipAddr, String userId,String unitKey) {
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(), sipAddr);
		return makeXcapNetUtils(url).create(IDeleteDoorRecord.class).deleteDoorRecord(userId,unitKey);
	}
	public static Call<ResponseBody> queryUserDoorRecord(String sipAddr,String userId) {
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(), sipAddr);
		return makeXcapNetUtils(url).create(IQueryDoorRecords.class).queryDoorRecords(userId);
	}
	public static Call<ResponseBody> putUserDoorRecord(String sipAddr, String userId,String unitKey, String data) {
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(), sipAddr);
		return makeXcapNetUtils(url).create(IPutDoorRecord.class).putDoorRecord(userId,unitKey,
				RequestBody.create(MediaType.parse("text/xml"), data));
	}
	public static Call<ResponseBody> putAuthenticatedCardsProfile(String sipAddr, String userId, String data) {
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(), sipAddr);
		return makeXcapNetUtils(url).create(IPutAuthenticatedCardsProfile.class).putAuthenticatedCardsProfile(userId,
				RequestBody.create(MediaType.parse("text/xml"), data));
	}
	public static Call<ResponseBody> getAuthenticatedCardsProfile(String sipAddr,String userId) {
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(), sipAddr);
		return makeXcapNetUtils(url).create(IGetAuthenticatedCardsProfile.class).getAuthenticatedCardsProfile(userId);
	}
	
	public static Call<ResponseBody> putLogsProfile(String sipAddr, String doorId_identify, String data) {
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(), sipAddr);
		return makeXcapNetUtils(url).create(IPutDoorLogsProfile.class).putDoorLogsProfile(doorId_identify,
				RequestBody.create(MediaType.parse("text/xml"), data));
	}
	public static Call<ResponseBody> getLogsProfile(String sipAddr,String doorId_identify) {
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(), sipAddr);
		return makeXcapNetUtils(url).create(IGetDoorLogsProfile.class).getDoorLogsProfile(doorId_identify);
	}

	public static Call<ResponseBody> checkBuddy(String sipAddr,String xixi_id,String menu_id){
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
		return makeXcapNetUtils(url).create(XCapParams.ICheckBuddy.class).checkBuddy(xixi_id,menu_id);
	}
	public static Call<ResponseBody> deleteDoorLogsRecord(String sipAddr, String doorId_identify,String key_expire) {
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(), sipAddr);
		return makeXcapNetUtils(url).create(IDeleteDoorLogsRecord.class).deleteDoorLogsRecord(doorId_identify,key_expire);
	}
	public static Call<ResponseBody> getDoorLogsRecord(String sipAddr,String doorId_identify, String key_expire) {
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(), sipAddr);
		return makeXcapNetUtils(url).create(IGetDoorLogsRecord.class).getDoorLogsRecord(doorId_identify,key_expire);
	}
	public static Call<ResponseBody> putDoorLogsRecord(String sipAddr, String doorId_identify,String key_expire, String data) {
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(), sipAddr);
		return makeXcapNetUtils(url).create(IPutDoorLogsRecord.class).putDoorLogsRecord(doorId_identify,key_expire,
				RequestBody.create(MediaType.parse("text/xml"), data));
	}
	public static Call<ResponseBody> getOutdoorSeetingPwd(String sipAddr,String deviceId){
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
		return makeXcapNetUtils(url).create(IGetOutdoorSettingPwd.class).getOutdoorSettingPwd(deviceId);
	}
	public static Call<ResponseBody> getUserPriviledge(String sipAddr,String userId){
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
		return makeXcapNetUtils(url).create(IGetUserPriviledge.class).getUserPriviledge(userId);
	}
	
	public static Call<ResponseBody>getUserContactsVersion(String sipAddr,String userId){
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
		Call<ResponseBody> call = makeXcapNetUtils(url).create(IGetUserContactsVersion.class).getUserContactsVersion(userId);
		return call;
	}
	public static Call<ResponseBody>putContactsVersion(String sipAddr,String userId,String timeStamp){
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
		return makeXcapNetUtils(url).create(IPutUserContactsVersion.class).
				putUserContactsVersion(userId,RequestBody.create(MediaType.parse("text/xml"), timeStamp));
	}
	public static Call<ResponseBody> getUserNickName(String sipAddr,String userId){
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
		return makeXcapNetUtils(url).create(IGetUserNickName.class).getUserNickName(userId);

	}
	public static Call<ResponseBody> getOutdoorVersionName(String sipAddr,String deviceId){
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
		return makeXcapNetUtils(url).create(IGetOutdoorVersionName.class).getOutdoorVersionName(deviceId);
	}
	public static Call<ResponseBody> getOutdoorTargetVersion(String sipAddr,String deviceId){
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
		Log.d("clark","getOutdoorDetailVersion"+url);
		return makeXcapNetUtils(url).create(IGetOutdoorTargetVersion.class).getOutdoorTargetVersion(deviceId);
	}
	public static Call<ResponseBody> getOutdoorApkUrl(String sipAddr,String deviceId){
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
		return makeXcapNetUtils(url).create(IGetOutdoorApkUrl.class).getOutdoorApkUrl(deviceId);
	}
	public static Call<ResponseBody> putOutdoorVersionCode(String sipAddr,String deviceId,String data){
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
		return makeXcapNetUtils(url).create(IPutOutdoorVersionCode.class).putOutdoorVersionCode(deviceId, RequestBody.create(MediaType.parse("text/xml"), data));
	}
	public static Call<ResponseBody> getOutdoorDetailVersion(String sipAddr,String deviceId){
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
		Log.i("clark","url="+url);
		return makeXcapNetUtils(url).create(IGetOutdoorDetailVersion.class).getOutdoorDetailVersion(deviceId);
	}
	public static Call<ResponseBody> putXiXiPhoneNumsProfile(String sipAddr, String xixi_id, String data) {
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(), sipAddr);
		return makeXcapNetUtils(url).create(IPutXiXiPhoneNumsProfile.class).putXiXiPhoneNumsProfile(xixi_id,
				RequestBody.create(MediaType.parse("text/xml"), data));
	}
	public static Call<ResponseBody> getPhoneNumsJson(String sipAddr,String xixi_id){
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
		return makeXcapNetUtils(url).create(IGetPhoneNumsJson.class).getPhoneNumsJson(xixi_id);
	}
	public static Call<ResponseBody>putPhoneNumsJson(String sipAddr,String xixi_id,String json){
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
		return makeXcapNetUtils(url).create(IPutPhoneNumsJson.class).
				putPhoneNumsJson(xixi_id,RequestBody.create(MediaType.parse("text/xml"),json));
	}
	public static Call<ResponseBody> putOpendoorPwd(String sipAddr,String xixi_id,String menu_id,String data){
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
		return makeXcapNetUtils(url).create(IPutOpendoorPwd.class).putOpendoorPwd(xixi_id,menu_id, RequestBody.create(MediaType.parse("text/xml"), data));
	}
	public static Call<ResponseBody> getOpendoorPwd(String sipAddr,String xixi_id,String menu_id){
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
		return makeXcapNetUtils(url).create(IGetOpendoorPwd.class).getOpendoorPwd(xixi_id,menu_id);
	}
	public static Call<ResponseBody> putAirconditionCode(String sipAddr,String xixi_id,String menu_id,String data){
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
		return makeXcapNetUtils(url).create(IPutAirconditionCode.class).putAirconditionCode(xixi_id,menu_id, RequestBody.create(MediaType.parse("text/xml"), data));
	}
	public static Call<ResponseBody> getAirconditionCode(String sipAddr,String xixi_id,String menu_id){
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
		return makeXcapNetUtils(url).create(IGetAirconditionCode.class).getAirconditionCode(xixi_id,menu_id);
	}
	public static Call<ResponseBody> getAllowFloor(String sipAddr,String xixi_id,String menu_id){
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
		return makeXcapNetUtils(url).create(IGetAllowFloor.class).getAllowFloor(xixi_id,menu_id);
	}
	public static Call<ResponseBody> getShareFloor(String sipAddr,String xixi_id,String menu_id){
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
		return makeXcapNetUtils(url).create(IGetShareFloor.class).getShareFloor(xixi_id,menu_id);
	}
	public static Call<ResponseBody> putShareFloor(String sipAddr,String xixi_id,String menu_id,String data){
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
		return makeXcapNetUtils(url).create(IPutShareFloor.class).putShareFloor(xixi_id,menu_id, RequestBody.create(MediaType.parse("text/xml"), data));
	}
	public static Call<ResponseBody> getHouseKeeperId(String sipAddr,String deviceId){
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
		return makeXcapNetUtils(url).create(IGetHouseKeeperId.class).getHouseKeeperId(deviceId);
	}
	public static Call<ResponseBody> getSchedulerDeviceLocation(String sipAddr,String deviceId){
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
		return makeXcapNetUtils(url).create(IGetSchedulerDeviceLocation.class).getSchedulerDeviceLocation(deviceId);
	}
	public static Call<ResponseBody> putWeatherData(String sipAddr,String xixi_id,String data){
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
		return makeXcapNetUtils(url).create(XCapParams.IPutWeatherData.class).putWeatherData(xixi_id, RequestBody.create(MediaType.parse("text/xml"), data));
	}
	public static Call<ResponseBody> getWeatherData(String sipAddr,String xixi_id){
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
		return makeXcapNetUtils(url).create(XCapParams.IGetWeatherData.class).getWeatherData(xixi_id);
	}
	public static Call<ResponseBody> getGateWayId(String sipAddr,String deviceId){
		String url = String.format(XCapParams.getXCAP_REQ_URL_FORMAT(),sipAddr);
		return makeXcapNetUtils(url).create(XCapParams.IGetGateWayId.class).getGateWayId(deviceId);
	}

	private static Retrofit makeXcapNetUtils(String _url) {
		Retrofit rf = null;
		rf = retrofit_map.get(_url);
		if (rf == null) {
			// addConverterFactory(GsonConverterFactory.create()).
			rf = new Retrofit.Builder().baseUrl(_url).client(client).build();
			retrofit_map.put(_url, rf);
		}
		return rf;
	}

}
