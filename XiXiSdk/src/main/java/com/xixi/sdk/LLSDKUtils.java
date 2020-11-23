package com.xixi.sdk;

import static android.content.Context.ACTIVITY_SERVICE;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.controller.LLNetworkReachable;
import com.xixi.sdk.logger.Log;
import com.xixi.sdk.serialpos.CmdErrorCode;
import com.xixi.sdk.serialpos.SerialDataParser.CmdRetKits;
import com.xixi.sdk.utils.download.LLCursorWithMaximum;
import com.xixi.sdk.utils.file.IoCompletionListener1;
import com.xixi.sdk.utils.thread.UIThreadDispatcher;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import okhttp3.ResponseBody;

public class LLSDKUtils {
	public final static List<File> getFileList(String folderName) {
		return getFileList(folderName, null);
	}

	public static final String SerialibleNumber = "SerialibleNumber";

	public static Bundle putSerializable(Serializable params[], Bundle bd) {

		if (params != null && params.length != 0) {
			bd.putInt(SerialibleNumber, params.length);

			int i = 0;
			for (Serializable param : params) {
				bd.putSerializable(SerialibleNumber + i, param);
				i++;
			}
		}
		return bd;
	}

	public static int getShowFloor(int firstFloor, int currentFloor) {
		if(currentFloor == 0){
			return 1 ;
		}
		int distance = firstFloor < 0 ? 1 - firstFloor : firstFloor - 1;
		int showFloor = 0;
		if (currentFloor - distance < 0) {
			showFloor = currentFloor - distance;
		} else {
			showFloor = distance != 0 ? currentFloor - distance + 1 : currentFloor;
		}
		return showFloor;
	}

	public static boolean string2List(String floor, Collection<Integer> allowedFloors) {
		if (floor.length() != 0) {
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

	public static int getElevatorFloor(int firstFloor, int floor) {
		int setFloor = floor - firstFloor + (LLSDKUtils.oppositeSigns(floor, firstFloor) ? 0 : 1);
		return setFloor;
	}

	public static int string2Floor(String floor) {
		if (floor.length() != 0) {
			floor = floor.toLowerCase(Locale.ENGLISH);
			int s = 0;
			int num = 0;
			for (int i = floor.length() - 1; i >= 0; i--) {
				s = LLSDKUtils.char2Int(floor.charAt(i));
				for (int j = 0; j < 4; j++) {
					if ((s & (0x1 << j)) != 0) {
						num = 4 * (floor.length() - 1 - i) + j;
						return num + 1;
					}
				}
			}
		}
		return 0;
	}

	public static int removeSign(byte b) {
		return (b < 0 ? (b + 256) : b);
	}

	public static List<Serializable> getSerializable(Bundle bd) {
		List<Serializable> serializableParams = new ArrayList<Serializable>();
		int serialNum = bd.getInt(LLSDKUtils.SerialibleNumber);
		for (int i = 0; i < serialNum; i++) {
			serializableParams.add(bd.getSerializable(LLSDKUtils.SerialibleNumber + i));
		}
		return serializableParams;
	}

	public static void launchActivityEx(FragmentActivity ac, Class<?> acClass, Serializable params[], int flag) {
		Intent intent = new Intent(ac, acClass);

		Bundle bd = new Bundle();
		putSerializable(params, bd);
		intent.putExtras(bd);

		if (flag != 0)
			intent.setFlags(flag);
		ac.startActivityForResult(intent, 0);
	}

	public static boolean oppositeSigns(int x, int y) {
		return ((x ^ y) >> 31) != 0;
	}

	public static void launchActivityEx(FragmentActivity ac, Class<?> acClass, String params[], int flag) {
		Intent intent = new Intent(ac, acClass);
		if (params != null) {
			Bundle bd = new Bundle();
			bd.putStringArray(PARAMS_AMONG_ACTIVITY, params);
			intent.putExtras(bd);
		}

		if (flag != 0)
			intent.setFlags(flag);
		ac.startActivityForResult(intent, 0);
	}

	public static final String PARAMS_AMONG_ACTIVITY = "PARAMS_AMONG_ACTIVITY";

	public static void launchActivity(Context ac, Class<?> acClass, String params[], int flag) {
		Intent intent = new Intent(ac, acClass);
		if (params != null) {
			Bundle bd = new Bundle();
			bd.putStringArray(PARAMS_AMONG_ACTIVITY, params);
			intent.putExtras(bd);
		}

		if (flag != 0)
			intent.setFlags(flag);
		ac.startActivity(intent);
	}

	public static int getOSVersion() {

		return android.os.Build.VERSION.SDK_INT;
	}

	public static boolean checkBit(long toBeChecked, long bit) {
		return (toBeChecked & bit) != 0;
	}

	public static void boostSelf() {
		exeCmd(new String[] { "exit" });
	}

	public static int exeCmd(String cmds[] ) {
		DataOutputStream dos = null;
		BufferedReader outputStream = null;
		try {
			Process process = Runtime.getRuntime().exec("su");
			dos = new DataOutputStream(process.getOutputStream());
			for (String cmd : cmds) {
				dos.writeBytes(cmd + "\n");
				dos.flush();
			}

			process.waitFor();
			outputStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			return 0;
		} catch (Exception localException) {
			localException.printStackTrace();
			return -1;
		} finally {
			try {
				if (dos != null) {
					dos.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (IOException e) {
			}
		}
	}

	public static String getPckName() {
		return getContext().getPackageName();
	}

	public static Context getContext() {
		return LongLakeApplication.getInstance();
	}

	public static String getVerionName() {

		Context context = getContext();
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (Exception e) {
		}
		return "1.0";

	}

	public static int getVersionCode() {
		PackageManager packageManager = LongLakeApplication.getInstance().getPackageManager();
		PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(LongLakeApplication.getInstance().getPackageName(), 0);
			return packInfo.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;

	}

	public final static List<File> getFileList(String folderName, FileFilter ff) {
		File folder = new File(folderName);
		File[] files = (ff == null ? folder.listFiles() : folder.listFiles(ff));
		return Arrays.asList(files == null ? new File[] {} : files);
	}

	public static boolean preparePath(File file) {

		try {
			if (!file.exists()) {
				boolean bRet = preparePath(file.getParentFile());
				if (bRet) {
					bRet = file.mkdir();
				}
				return bRet;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void runInMainThread(Runnable r, int delay) {
		if (isInMainThread() && delay == 0) {
			r.run();
		} else {
			UIThreadDispatcher.dispatch(r, delay);
		}
	}

	public static void runInMainThread(Runnable r) {
		runInMainThread(r, 0);
	}

	public static boolean isInMainThread() {
		return Looper.myLooper() == Looper.getMainLooper();
	}

	public static String getAppName(Context context, int pID) {
		String processName = "";
		ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> l = am.getRunningAppProcesses();
		PackageManager pm = context.getPackageManager();
		for (ActivityManager.RunningAppProcessInfo appNode : l) {
			ActivityManager.RunningAppProcessInfo info = appNode;
			try {
				if (info.pid == pID) {
					CharSequence c = pm
							.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
					processName = info.processName;
				}
				// Log.i("daniel" , info.processName + info.pid );
			} catch (Exception e) {
				// Log.d("daniel", "Error>> :"+ e.toString());
			}
		}
		return processName;
	}

	public static void danielAssert(boolean runtimeException) {
		if (!runtimeException) {
			throw new RuntimeException();
		}
	}

	public static String getPckName(Context context) {
		return context.getPackageName();
	}

	public static String getVerionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (Exception e) {
		}
		return "1.0";

	}

	public static String getPackageVer(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode + ".0";
		} catch (Exception e) {
		}

		return "1.0";
	}

	public static void test() {

		String strTestString = "�ҵ�<>fasdfasdf<>>>fasdf";

		// String strTemp1 = LonglakeUtils.fromStringHex1(strTestString) ;
		String strTemp2 = convertToStringInHex(strTestString);
		parseFromHexString(strTemp2);
	}

	public static String fromStringHex1(String s) {

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			sb.append(Integer.toHexString(c));

		}
		return sb.toString();
	}

	public static String getBroadcastIp() {
		return LLNetworkReachable.getAddrsInfo().get(1);
	}

	public static String getLocalIpAddress() {
		return LLNetworkReachable.getAddrsInfo().get(0);
	}

	public static long convertToInt(String str) {
		long converted = -1;
		try {
			converted = Long.parseLong(str);
		} catch (Exception e) {
		}

		return converted;
	}

	public static String parseFromHexString(String s) {
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			s = new String(baKeyword, "utf-8");// UTF-16le:Not
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return s;
	}

	public static Integer convertToInt(char c) {
		return (c >= '0' && c <= '9') ? (c - '0') : ((c >= 'A' && c <= 'E') ? c - 'A' : 0);
	}

	private static String HEX[] = { "A", "B", "C", "D", "E", "F" };

	private static String convertToHex(int _temp_) {
		return _temp_ >= 10 ? HEX[_temp_ - 10] : ("" + _temp_);

	}

	public static String convertToStringInHex(String strInput) {

		byte[] byBuffer = null;

		try {
			byBuffer = strInput.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < byBuffer.length; i++) {
			int temp_ = byBuffer[i] < 0 ? byBuffer[i] + 256 : byBuffer[i];
			sb.append(convertToHex(temp_ / 16) + convertToHex(temp_ % 16));
		}
		return sb.toString();
	}

	public static String getString(int id) {
		return getResources().getString(id);
	}
	public static int getColor(int id) {
		return getResources().getColor(id);
	}
	public static Resources getResources() {
		return LongLakeApplication.getInstance().getResources();
	}

	public static boolean writeResponseBodyToDisk(ResponseBody body, String fileName) {
		boolean bRet = false;

		File futureStudioIconFile = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			futureStudioIconFile = new File(fileName);
			byte[] fileReader = new byte[4096];
			long fileSize = body.contentLength();
			long fileSizeDownloaded = 0;

			inputStream = body.byteStream();
			outputStream = new FileOutputStream(futureStudioIconFile);

			while (true) {

				int read = inputStream.read(fileReader);

				if (read == -1) {
					break;
				}
				outputStream.write(fileReader, 0, read);
				fileSizeDownloaded += read;
			}
			outputStream.flush();
			bRet = true;

		} catch (Exception e) {
		}

		try {
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (Exception e) {
		}
		try {
			if (outputStream != null) {
				outputStream.close();
			}
		} catch (Exception e) {
		}

		if (!bRet) {
			File tempF = new File(fileName);
			tempF.delete();
		}
		return bRet;
	}

	public static long compareTo(String left, String right) {
		long _left = 0;
		long _right = 0;
		_left = LLSDKUtils.convertToInt(left);
		_right = LLSDKUtils.convertToInt(right);
		return _left - _right;
	}

	public static int char2Int(char c) {
		return (c >= '0' && c <= '9') ? (c - '0') : ((c >= 'a' && c <= 'f') ? c - 'a' + 10 : 0);
	}

	public static String num2Floors(int num) {
		// StringBuffer sb = new StringBuffer("");
		// for (int i = 0; i < num; i++) {
		// sb.append("ff");
		// }
		return StringUtils.join(Collections.nCopies(num, "ff"), "");
	}

	final private static String[] reboot_cmds = new String[] { "reboot" };

	public static void reboot() {
		final LLCursorWithMaximum counter = new LLCursorWithMaximum(3);
		CmdRetKits.enableWatchDogIo(false, new IoCompletionListener1<byte[]>() {
			@Override
			public void onFinish(byte[] data, Object context) {
				final CmdErrorCode errorCode = (CmdErrorCode) context;
				if (!errorCode.needToResend()) {
					LLSDKUtils.exeCmd(reboot_cmds);
				} else { 
					counter.moveToNext();
					if (counter.hitMax()) {
						LLSDKUtils.exeCmd(reboot_cmds);
					} else {
						Log.i(LongLakeApplication.DANIEL_TAG, "try close watch dog" + counter.getPosition());
						CmdRetKits.enableWatchDogIo(false, this);
					}
				}
			}
		});
	}
}
