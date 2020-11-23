package com.xixi.sdk.model;

public class LLQrCodeParamSet {
	
	public static class LLQrCodeParam {
		
		String qrCodeMsg ;
 
		public LLQrCodeParam(String qrCodeMsg) {
			super();
			this.qrCodeMsg = qrCodeMsg;
		}

		public String getQrCodeMsg() {
			return qrCodeMsg;
		}

		public void setQrCodeMsg(String qrCodeMsg) {
			this.qrCodeMsg = qrCodeMsg;
		} 
	}
	
	public static class LLQrCodeLiftParam extends LLQrCodeParam {
		public final static String key_name = "qrLiftMsg" ;	
		public LLQrCodeLiftParam(String liftId , String type , String liftName , String shareFloor , String qrCodeMsg ,String time , String userId) {
			super(key_name);
			this.deviceId = liftId ;
			this.deviceType = type ;
			this.deviceName = liftName ;
			this.shareFloor = shareFloor ;
			this.time = time ;
			this.userId  = userId ;
		}
		String deviceId ;
		String deviceType ;
		String deviceName ;
		String shareFloor ;
		String userId ;
		String time ;
		public String getDeviceId() {
			return deviceId;
		}
		public void setDeviceId(String deviceId) {
			this.deviceId = deviceId;
		}
		public String getDeviceType() {
			return deviceType;
		}
		public void setDeviceType(String deviceType) {
			this.deviceType = deviceType;
		}
		public String getDeviceName() {
			return deviceName;
		}
		public void setDeviceName(String deviceName) {
			this.deviceName = deviceName;
		}
		public String getShareFloor() {
			return shareFloor;
		}
		public void setShareFloor(String shareFloor) {
			this.shareFloor = shareFloor;
		}
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		public static String getKeyName() {
			return key_name;
		}
		
	}
	
	public static class LLQrCodeAuthenticationParam extends LLQrCodeParam { 
		public final static String key_name = "qrAuthenticatedMsg" ;
		public LLQrCodeAuthenticationParam(String deviceId, String authenticatedPwd) {
			super(key_name);
			this.deviceId = deviceId;
			this.authenticatedPwd = authenticatedPwd;
		}
		
		public String getDeviceId() {
			return deviceId;
		}
		public void setDeviceId(String deviceId) {
			this.deviceId = deviceId;
		}
		public String getAuthenticatedPwd() {
			return authenticatedPwd;
		}
		public void setAuthenticatedPwd(String authenticatedPwd) {
			this.authenticatedPwd = authenticatedPwd;
		}
		String deviceId ; 
		String authenticatedPwd ; 
	}
	
	public static class LLQrCodeSmartDeviceParam extends LLQrCodeParam { 
		public final static String key_name = "qrSmartDeviceMsg" ;
		public LLQrCodeSmartDeviceParam(String deviceId, String type) {
			super(key_name);
			this.deviceId = deviceId;
			this.Type = type;
		}
		
		public String getDeviceId() {
			return deviceId;
		}
		public void setDeviceId(String deviceId) {
			this.deviceId = deviceId;
		}
		public String getType() {
			return Type;
		}
		public void setTpe(String type) {
			this.Type = type;
		}
		String deviceId ; 
		String Type ; 
	}
  
	public static class llQrCodeShareDoorOldParam extends LLQrCodeParam{
		 public final static String key_name = "qrSharedDoorMsg";
			public llQrCodeShareDoorOldParam(String doorId, String expireDate,String Type,String userId,String time,String doorName) {
				super(key_name);
				this.doorId = doorId ; 
				this.expireDate = expireDate ;
				this.Type = Type ;
				this.userId = userId ;
				this.time = time ;
				this.doorName = doorName ;
			}
			private String doorId;
			private String expireDate;//s
			private String Type;
			private String userId;
			private String time;
			private String doorName;
			public String getDoorId() {
				return doorId;
			}
			public void setDoorId(String doorId) {
				this.doorId = doorId;
			}
			public String getExpireDate() {
				return expireDate;
			}
			public void setExpireDate(String expireDate) {
				this.expireDate = expireDate;
			}
			public String getType() {
				return Type;
			}
			public void setType(String type) {
				Type = type;
			}
			public String getUserId() {
				return userId;
			}
			public void setUserId(String userId) {
				this.userId = userId;
			}
			public String getTime() {
				return time;
			}
			public void setTime(String time) {
				this.time = time;
			}
			public String getDoorName() {
				return doorName;
			}
			public void setDoorName(String doorName) {
				this.doorName = doorName;
			}
	}
	
	public static class LLQrCodeShareDoorParam extends LLQrCodeParam {
        public final static String key_name = "qrSharedDoorMsg";
		public LLQrCodeShareDoorParam(String doorId, String expireDate,String Type,String userId,String time,String doorName) {
			super(key_name);
			this.deviceId = doorId;
			this.expireDate = expireDate;
			this.deviceType = Type;
			this.userId = userId;
			this.time = time;
			this.deviceName = doorName;
		}
		private String deviceId;
		private String expireDate;//s
		private String deviceType;
		private String userId;
		private String time;
		private String deviceName;
		public String getDeviceId() {
			return deviceId;
		}
		public void setDeviceId(String deviceId) {
			this.deviceId = deviceId;
		}
		public String getExpireDate() {
			return expireDate;
		}
		public void setExpireDate(String expireDate) {
			this.expireDate = expireDate;
		}
		public String getDeviceType() {
			return deviceType;
		}
		public void setDeviceType(String deviceType) {
			this.deviceType = deviceType;
		}
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		public String getDeviceName() {
			return deviceName;
		}
		public void setDeviceName(String deviceName) {
			this.deviceName = deviceName;
		}
	}
}
