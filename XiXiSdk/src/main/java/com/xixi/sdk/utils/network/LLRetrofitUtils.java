package com.xixi.sdk.utils.network;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class LLRetrofitUtils extends LLSdkRetrofitUtils {

	public static void switchURL() {
	}

	private static String XG_URL = "http://118.144.86.70:5527/api/";
	private final static Map<String, Retrofit> retrofit_map = new HashMap<String, Retrofit>();
	public static Retrofit retrofit = new Retrofit.Builder().baseUrl(XG_URL)
			.addConverterFactory(GsonConverterFactory.create()).client(client).build();

	public interface PushXG_Msg {
		@POST("msg")
		Call<ResponseBody> Push(@Body PushXG_MsgParams params);
	}

	public static Call<ResponseBody> pushXG_Msg(String strLink, String userId, String msg, String callback, int type) { 
		Retrofit retr = rawReq(strLink, "msg");
		return retr.create(PushXG_Msg.class)
				.Push(new PushXG_MsgParams(userId, userId, msg, "testSign", callback, type)) ; 
	}

	public static class PushXG_MsgParams {

		public PushXG_MsgParams(String usr, String msgid, String msg, String sign, String callBack, Integer type) {
			super();
			this.usr = usr;
			this.msgid = msgid;
			this.msg = msg;
			this.sign = sign;
			this.callBack = callBack;
			this.type = type;
		}

		public String getUsr() {
			return usr;
		}

		public void setUsr(String usr) {
			this.usr = usr;
		}

		public String getMsgid() {
			return msgid;
		}

		public void setMsgid(String msgid) {
			this.msgid = msgid;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}

		public String getSign() {
			return sign;
		}

		public void setSign(String sign) {
			this.sign = sign;
		}

		public String getCallBack() {
			return callBack; 
		}

		public void setCallBack(String callBack) {
			this.callBack = callBack;
		}

		public Integer getType() {
			return type;
		}

		public void setType(Integer type) {
			this.type = type;
		}

		private String usr;
		private String msgid;
		private String msg;
		private String sign;
		private String callBack;
		private Integer type;
	}

	private static Retrofit rawReq(String strLink, String key) {
		String _url;
		Retrofit rf;
		strLink = strLink.toLowerCase(Locale.ENGLISH);
		try {
			_url = strLink.substring(0, strLink.lastIndexOf(key));
			rf = retrofit_map.get(_url);
			if (rf == null) {
				rf = new Retrofit.Builder().baseUrl(_url).addConverterFactory(GsonConverterFactory.create()).build();
				retrofit_map.put(_url, rf);
			}
		} catch (Exception e) {
			rf = retrofit;
		}
		return rf;

	}

}
