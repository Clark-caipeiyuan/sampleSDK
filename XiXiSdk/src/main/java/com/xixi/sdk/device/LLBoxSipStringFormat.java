package com.xixi.sdk.device;

import com.xixi.sdk.parser.LLGsonUtils;
import com.xixi.sdk.sipmsg.params.LLSmartAirRet;
import com.xixi.sdk.sipmsg.params.LLSmartCurtain;
import com.xixi.sdk.sipmsg.params.LLSmartCurtainRet;
import com.xixi.sdk.sipmsg.params.LLSmartDev;
import com.xixi.sdk.sipmsg.params.LLSmartDoorRet;
import com.xixi.sdk.sipmsg.params.LLSmartEnvir;
import com.xixi.sdk.sipmsg.params.LLSmartSafe;

import android.text.TextUtils;

public class LLBoxSipStringFormat {
	public static String makeLightStringForApp(LLSmartDev data) {
		String state = TextUtils.equals(data.getState(), "1")?"open":"close";
		String strContent = String.format("<sts><l cur=\"%s\" id=\"%s\" data=\"%s\"/></sts>", state,data.getDeviceId()
		,data.getData());
		return strContent;
	}
	
	public static String makeCurtainStringForApp(LLSmartCurtain llSmartCurtains) {
		String strContent = String.format("{\"state\":\"%s\"}", TextUtils.equals(llSmartCurtains.getSign()+"", "1") ? "open":TextUtils.equals(llSmartCurtains.getSign()+"", 0+"") ? "close":"stop");
		return strContent;
	}
	
	public static String makeAirdationStringForApp(){
		String strContent = String.format("{ \"cur\":\"%s\" \"id\":\"%s\" \"data\":\"\"}", "",
				"");
		return strContent;
	}

	public static String makeAirConditionStringForApp(LLSmartAirRet llSmartAir){
		String ret = String.format("{\"sign\":%d,\"temp\":%d,\"model\":\"%d\",\"windLevl\":\"%d\",\"isSuccess\":\"%s\"}", 
				llSmartAir.getSign(),llSmartAir.getTemp(),llSmartAir.getModel(),llSmartAir.getWindLevl(),llSmartAir.isSuccess() ? "success" : "fail");
		return ret;
	}
	
	public static String makeDoorSafeStringForApp(String isSafe){
		String strContent = String.format("{\"state\":\"%s\"}", isSafe);
		return strContent;
	}
	public static String makeSmartEnvir(LLSmartEnvir data){
		String strContent = LLGsonUtils.getInstance().toJson(data);
		return strContent;
	}
	public static String makeSmartCurtain(LLSmartCurtainRet llSmartCurtain){
		String strContent = String.format("{\"sign\":\"%d\",\"isSuccess\":\"%s\"}", llSmartCurtain.getSign(),llSmartCurtain.isSuccess() ? "success" : "fail"); 
		return strContent;
	}
	public static String makeDoorControlStringForApp(LLSmartDoorRet llSmartDoor){
		String strContent = String.format("{ \"isSuccess\":\"%s\" ,\"pwd\":\"\" ,\"data\":\"%s\"}", llSmartDoor.isSuccess() ? "success" : "fail",
				llSmartDoor.getData());
		return strContent;
	}
 
	public static String makeSmartSafeString(LLSmartSafe data){
		return LLGsonUtils.getInstance().toJson(data) ; //("{\"alert\":\"%s\",\"id\":\"%s\"}", data.getAlert(),data.getId());
	}

}
