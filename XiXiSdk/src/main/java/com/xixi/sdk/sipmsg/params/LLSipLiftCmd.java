package com.xixi.sdk.sipmsg.params;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.xixi.sdk.CustomizedCRCConverter;
import com.xixi.sdk.LLSDKUtils;
import com.xixi.sdk.globals.LLSdkGlobals;
import com.xixi.sdk.serialpos.ISerialDataEnum;
import com.xixi.sdk.serialpos.SerialPosClient;
import com.xixi.sdk.utils.file.IoCompletionListener1;

import android.os.Handler;
import android.os.HandlerThread;

public class LLSipLiftCmd implements ISerialDataEnum {

	private HandlerThread handlerThread;
	private Handler mLiftThreadHandler;
	private static LLSipLiftCmd instance;

	public synchronized static LLSipLiftCmd getInstance() {
		if (instance == null) {
			instance = new LLSipLiftCmd();
		}
		return instance;
	}

	List<Integer> allowedFloors = new ArrayList<Integer>();
	
	public static boolean string2List(String floor, List<Integer> allowedFloors) {
	    if(floor.length() != 0){
	      floor = floor.toLowerCase(Locale.ENGLISH);
	      int s = 0;
	      int num = 0;
	      for (int i = floor.length() - 1; i >= 0; i--) {
	        s = LLSDKUtils.char2Int(floor.charAt(i));
	        for (int j = 0; j < 4; j++) {
	          if ((s & (0x1 << j)) != 0) {
	            num = 4 * (floor.length() - 1 - i) + j;
	            allowedFloors.add(num + 1);
	          }
	        }
	      }
	      return true;
	    }
	    return false;
	    
	  }

}
