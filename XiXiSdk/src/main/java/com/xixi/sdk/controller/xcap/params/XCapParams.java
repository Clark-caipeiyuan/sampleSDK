package com.xixi.sdk.controller.xcap.params;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
public class XCapParams {

	private static String XCAP_REQ_URL_FORMAT = "%s/xcap-root/resource-lists/users/";

	public static String getXCAP_REQ_URL_FORMAT() {
		return XCAP_REQ_URL_FORMAT;
	}

	public static interface IGetFriendList {
		@GET("{id}/buddy")
		public Call<ResponseBody> getLayout(@Path("id") String strId);
	}

	/*
	 * public static interface IGetDetailedInfo {
	 * 
	 * @GET("{id}/info") public Call<ResponseBody> getDetails(@Path("id") String
	 * strId); }
	 */
	/*
	 * public static interface IGetDetailedInfo {
	 * 
	 * @GET("{id}/buddy/~~/body/buddy[@id=\"{buddyId}\"]") public
	 * Call<ResponseBody> getDetails(@Path("id") String userId,@Path("buddyId")
	 * String buddyId); }
	 */
	public static interface IGetSpecialFriend {
		@GET("{userId}/buddy/~~/body/buddy[@id=\"{id}\"]")
		public Call<ResponseBody> getSpecialLayout(@Path("userId") String strId, @Path("id") String buddyId);
	}

	public static interface IGetDetailedInfo {
		@GET("{deviceId}/info")
		public Call<ResponseBody> getDetails(@Path("deviceId") String deviceId);
	}

	public static interface IPutDevInfo {
		@PUT("{deviceId}/devInfo")
		public Call<ResponseBody> putDevInfo(@Path("deviceId") String devId, @Body RequestBody data);
	}

	//
	// public static interface IReviseDevInfo{
	// @PUT("{deviceId}/devInfo/~~/device[@id=\"{devId}\"]")
	// public Call<ResponseBody> reviseDevInfo(@Path("deviceId") String devId,
	// @Body RequestBody data);
	// }

	public static interface IPutLayout {
		@PUT("{id}/buddy")
		public Call<ResponseBody> putLayout(@Path("id") String strId, @Body RequestBody data);
	}

	public static interface IReviseFriendInfo {
		@PUT("{id}/buddy/~~/{body}/buddy[@id=\"{buddyId}\"]")
		public Call<ResponseBody> reviseFriendInfo(@Path("id") String strId, @Path("body") String prefix,
                                                   @Path("buddyId") String buddyId, @Body RequestBody data);
	}

	public static interface IRvDetailedInfo {
		@DELETE("{id}/buddy/~~/{body}/buddy[@id=\"{buddyId}\"]")
		public Call<ResponseBody> rvDetails(@Path("id") String strId, @Path("body") String prefix,
                                            @Path("buddyId") String buddyId);
	}

	public static interface IDeleteContactDoor {
		@DELETE("{id}/buddy/~~/{body}/buddy[@id=\"{doorId}\"]")
		public Call<ResponseBody> delteContactDoor(@Path("id") String strId, @Path("body") String prefix,
                                                   @Path("doorId") String doorId);
	}

	public static interface IPutUserProfile {

		@PUT("{id}/info/")
		public Call<ResponseBody> putUserProfile(@Path("id") String userId, @Body RequestBody data);
	}

	public static interface IGetUserProfile {
		@GET("{id}/info/")
		public Call<ResponseBody> getUserProfile(@Path("id") String userId);
	}

	public static interface IGetAuthenticatedCardsProfile {
		@GET("{id}/authenticatedCards/")
		public Call<ResponseBody> getAuthenticatedCardsProfile(@Path("id") String userId);
	}
	/*
	 * public static interface IGetLogsProfile {
	 * 
	 * @GET("{id}@{domain}/logs/") public Call<ResponseBody>
	 * getLogsProfile(@Path("id") String userId, @Path("domain") String domain);
	 * }
	 */

	public static interface IReviseUserProfile extends IPutUserProfile {

	}

	public static interface IPutAuthenticatedCardsProfile {
		@PUT("{id}/authenticatedCards/")
		public Call<ResponseBody> putAuthenticatedCardsProfile(@Path("id") String userId, @Body RequestBody data);
	}

	// ĳ��������ķ�����־·��
	public static interface IGetDoorLogsProfile {
		@GET("{id}/authenticatedLogs/~~/Logs")
		public Call<ResponseBody> getDoorLogsProfile(@Path("id") String doorId_identify);
	}

	public static interface IPutDoorLogsProfile {
		@PUT("{id}/authenticatedLogs/")
		public Call<ResponseBody> putDoorLogsProfile(@Path("id") String doorId_identify, @Body RequestBody data);
	}

	public static interface IPutDoorLogsRecord {
		@PUT("{id}/authenticatedLogs/~~/Logs/log[@key=\"{key}\"]")
		public Call<ResponseBody> putDoorLogsRecord(@Path("id") String doorIdentify, @Path("key") String doorId_expire,
                                                    @Body RequestBody data);
	}

	public static interface IGetDoorLogsRecord {
		@GET("{id}/authenticatedLogs/~~/Logs/log[@key=\"{key}\"]")
		public Call<ResponseBody> getDoorLogsRecord(@Path("id") String userId, @Path("key") String doorId_expire);
	}

	public static interface IDeleteDoorLogsRecord {
		@DELETE("{id}/authenticatedLogs/~~/Logs/log[@key=\"{key}\"]")
		public Call<ResponseBody> deleteDoorLogsRecord(@Path("id") String userId, @Path("key") String doorId_expire);
	}

	public static interface IPutDoorRecord {
		@PUT("{id}/authenticatedCards/~~/doors/door[@unitKey=\"{unitKey}\"]")
		public Call<ResponseBody> putDoorRecord(@Path("id") String userId, @Path("unitKey") String unitKey,
                                                @Body RequestBody data);
	}

	public static interface IDeleteDoorRecord {
		@DELETE("{id}/authenticatedCards/~~/doors/door[@unitKey=\"{unitKey}\"]")
		public Call<ResponseBody> deleteDoorRecord(@Path("id") String userId, @Path("unitKey") String unitKey);
	}

	public static interface IQueryDoorRecords {
		@GET("{id}/authenticatedCards/~~/doors")
		public Call<ResponseBody> queryDoorRecords(@Path("id") String userId);
	}

	public static interface IGetOutdoorSettingPwd {
		@GET("{deviceId}/devInfo/~~/device[@id=\"{deviceId}\"]/@pwd_key")
		public Call<ResponseBody> getOutdoorSettingPwd(@Path("deviceId") String deviceId);
	}

	public static interface IGetUserPriviledge {
		@GET("{userId}/info/~~/buddy[@id=\"{userId}\"]/@priviledge")
		public Call<ResponseBody> getUserPriviledge(@Path("userId") String userId);
	}

	// http://118.144.86.70:5527/xcap-root/resource-lists/users/U29/buddy/~~/body/@ver
	public static interface IGetUserContactsVersion {
		@GET("{userId}/buddy/~~/body/@ver")
		public Call<ResponseBody> getUserContactsVersion(@Path("userId") String userId);
	}

	public static interface IPutUserContactsVersion {
		@PUT("{userId}/buddy/~~/body/@ver")
		public Call<ResponseBody> putUserContactsVersion(@Path("userId") String userId, @Body RequestBody data);
	}
	//getUserNickName
	public static interface IGetUserNickName {
		@GET("{userId}/info/~~/buddy[@id=\"{userId}\"]/@nick")
		public Call<ResponseBody> getUserNickName(@Path("userId") String userId);
	}
	//getOutdoorVersionCode
	public static interface IGetOutdoorVersionName {
		@GET("{deviceId}/devInfo/~~/device[@id=\"{deviceId}\"]/@versionName")
		public Call<ResponseBody> getOutdoorVersionName(@Path("deviceId") String deviceId);
	}
	//getOutdoorTargetVersion
	public static interface IGetOutdoorTargetVersion {
		@GET("{deviceId}/Info/~~/buddy[@id=\"{deviceId}\"]/@targetVersion")
		public Call<ResponseBody> getOutdoorTargetVersion(@Path("deviceId") String deviceId);
	}
	//getOutdoorApkUrl
	public static interface IGetOutdoorApkUrl {
		@GET("{deviceId}/Info/~~/buddy[@id=\"{deviceId}\"]/@apkUrl")
		public Call<ResponseBody> getOutdoorApkUrl(@Path("deviceId") String deviceId);
	}
	//putOutdoorVersionCode
	public static interface IPutOutdoorVersionCode {
		@PUT("{deviceId}/devInfo/~~/device[@id=\"{deviceId}\"]/@versionCode")
		public Call<ResponseBody> putOutdoorVersionCode(@Path("deviceId") String deviceId, @Body RequestBody data);
	}
	public static interface IGetOutdoorDetailVersion{
		@GET("{deviceId}/info/~~/buddy/@ver")
		public Call<ResponseBody> getOutdoorDetailVersion(@Path("deviceId") String deviceId);
	}
	public static interface IPutXiXiPhoneNumsProfile {
		@PUT("{xixi_id}/info/")
		public Call<ResponseBody> putXiXiPhoneNumsProfile(@Path("xixi_id") String xixi_id, @Body RequestBody data);
	}
	public static interface IGetPhoneNumsJson{
		@GET("{xixi_id}/info/~~/phoneNums/@userIds")
		public Call<ResponseBody> getPhoneNumsJson(@Path("xixi_id") String xixi_id);
	}
	public static interface IPutPhoneNumsJson {
		@PUT("{xixi_id}/info/~~/phoneNums/@userIds")
		public Call<ResponseBody> putPhoneNumsJson(@Path("xixi_id") String xixi_id, @Body RequestBody data);
	}
	public static interface IPutOpendoorPwd {
		@PUT("{xixi_id}/info/~~/buddy/menu[@id=\"{menu_id}\"]/@pwd")
		public Call<ResponseBody> putOpendoorPwd(@Path("xixi_id") String xixi_id, @Path("menu_id") String menu_id, @Body RequestBody data);
	}
	public static interface IGetOpendoorPwd {
		@GET("{xixi_id}/info/~~/buddy/menu[@id=\"{menu_id}\"]/@pwd")
		public Call<ResponseBody> getOpendoorPwd(@Path("xixi_id") String xixi_id, @Path("menu_id") String menu_id);
	}
	public static interface IPutAirconditionCode {
		@PUT("{xixi_id}/info/~~/buddy/menu[@id=\"{menu_id}\"]/@airconditionCode")
		public Call<ResponseBody> putAirconditionCode(@Path("xixi_id") String xixi_id, @Path("menu_id") String menu_id, @Body RequestBody data);
	}
	public static interface IGetAirconditionCode {
		@GET("{xixi_id}/info/~~/buddy/menu[@id=\"{menu_id}\"]/@airconditionCode")
		public Call<ResponseBody> getAirconditionCode(@Path("xixi_id") String xixi_id, @Path("menu_id") String menu_id);
	}
	public static interface IGetAllowFloor {
		@GET("{userId}/buddy/~~/body/buddy[@id=\"{buddyId}\"]/@floor")
		public Call<ResponseBody> getAllowFloor(@Path("userId") String userId, @Path("buddyId") String buddyId);
	}
	public static interface IGetShareFloor {
		@GET("{userId}/buddy/~~/body/buddy[@id=\"{buddyId}\"]/@sharefloor")
		public Call<ResponseBody> getShareFloor(@Path("userId") String userId, @Path("buddyId") String buddyId);
	}
	public static interface IPutShareFloor {
		@PUT("{userId}/buddy/~~/body/buddy[@id=\"{buddyId}\"]/@sharefloor")
		public Call<ResponseBody> putShareFloor(@Path("userId") String xixi_id, @Path("buddyId") String menu_id, @Body RequestBody data);
	}
	public static interface IGetWYId {
	    @GET("{deviceId}/info/~~/buddy[@id=\"{deviceId}\"]/@property_id")
	    public Call<ResponseBody> getWYId(@Path("deviceId") String deviceId);
	  }
	
	public static interface IGetWYDeviceId {
	    @GET("{deviceId}/info/~~/buddy[@id=\"{deviceId}\"]/@WYDeviceId")
	    public Call<ResponseBody> getWYDeviceId(@Path("deviceId") String deviceId);
	  }
	public static interface IGetDeviceName {
	      @GET("{deviceId}/info/~~/buddy[@id=\"{deviceId}\"]/@name")
	      public Call<ResponseBody> getDeviceName(@Path("deviceId") String deviceId);
	    }
	public static interface IGetDeviceType {
		@GET("{deviceId}/info/~~/buddy[@id=\"{deviceId}\"]/@type")
		public Call<ResponseBody> getDeviceType(@Path("deviceId") String deviceId);
	}
	public static interface IGetDeviceInfo{
		@GET("{deviceId}/info/~~/buddy[@id=\"{deviceId}\"]/@elevatorInfo")
		public Call<ResponseBody> getDeviceInfo(@Path("deviceId") String deviceId);
	}
	public static interface IGetHouseKeeperId {
		@GET("{deviceId}/Info/~~/buddy[@id=\"{deviceId}\"]/@boxId")
		public Call<ResponseBody> getHouseKeeperId(@Path("deviceId") String deviceId);
	}
	public static interface IGetSchedulerDeviceLocation {
		@GET("{deviceId}/Info/~~/buddy[@id=\"{deviceId}\"]/@schedulerLocation")
		public Call<ResponseBody> getSchedulerDeviceLocation(@Path("deviceId") String deviceId);
	}

	public static interface IGetGateWayId {
		@GET("{deviceId}/Info/~~/buddy[@id=\"{deviceId}\"]/@gateway_id")
		public Call<ResponseBody> getGateWayId(@Path("deviceId") String deviceId);
	}
//	ee71c0ccf626/Info/~~/buddy[@id=ee71c0ccf626]/@gateway_id



	public static interface ICheckBuddy {
		@GET("{userId}/buddy/~~/body/buddy[@id=\"{buddyId}\"]")
		public Call<ResponseBody> checkBuddy( @Path("userId") String userId,@Path("buddyId") String buddyId);
	}
	public static interface IPutWeatherData {
		@PUT("{xixi_id}/info/~~/buddy[@id=\"{xixi_id}\"]/@weatherdata")
		public Call<ResponseBody> putWeatherData(@Path("xixi_id") String xixi_id, @Body RequestBody data);
	}
	public static interface IGetWeatherData {
		@GET("{xixi_id}/info/~~/buddy[@id=\"{xixi_id}\"]/@weatherdata")
		public Call<ResponseBody> getWeatherData (@Path("xixi_id") String xixi_id);
	}
	
}
