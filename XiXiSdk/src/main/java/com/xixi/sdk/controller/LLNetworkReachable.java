package com.xixi.sdk.controller;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.xixi.sdk.LLSDKUtils;
import com.xixi.sdk.app.LongLakeApplication;

import android.util.Log;

public class LLNetworkReachable extends LLNotifier<LLNetworkListener> {
	private static LLNetworkReachable instance;
	boolean bNetworkStatus = false;

	public boolean isbNetworkStatus() {
		return bNetworkStatus;
	}

	public void setbNetworkStatus(boolean bNetworkStatus) {
		if (!Boolean.valueOf(this.bNetworkStatus).equals(bNetworkStatus)) {
			this.bNetworkStatus = bNetworkStatus;
			notifyOb(new Object[] { bNetworkStatus });
		}
	}

	public static synchronized LLNetworkReachable getInstance() {
		if (instance == null) {
			instance = new LLNetworkReachable();
		}
		return instance;
	}

	private LLNetworkReachable() {
	}

	protected boolean dispatchedInMainThread() {
		return true;
	}

	public static synchronized void rvInstance() {
		instance = null;
	}

	private static Set<String> networkInf = new HashSet<String>(Arrays.asList(new String[] { "wlan0", "eth0" }));

	private final static String DEFAULT_INVALID_IP = "0.0.0.0";

	public final static String toIpString(byte[] bb) {
		try {
			return String.format("%d.%d.%d.%d", LLSDKUtils.removeSign(bb[0]), LLSDKUtils.removeSign(bb[1]),
					LLSDKUtils.removeSign(bb[2]), LLSDKUtils.removeSign(bb[3]));
		} catch (Exception e) {
			return DEFAULT_INVALID_IP;
		}

	}

	public final static int toIp(byte[] bb) {
		try {
			return (bb[0] << 24 | bb[1] << 16 | bb[2] << 8 | bb[3]);
		} catch (Exception e) {
			return 0;
		}
	}

	public static List<String> getAddrsInfo() {

		final List<String> addresses = new ArrayList<String>(
				Arrays.asList(new String[] { DEFAULT_INVALID_IP, DEFAULT_INVALID_IP }));

		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				if (networkInf.contains(intf.getName()) && intf.isUp() && !intf.isLoopback()) {
					List<InterfaceAddress> interfaceAddresses = intf.getInterfaceAddresses();
					for (InterfaceAddress interfaceAddress : interfaceAddresses) {
						try {
							if (interfaceAddress.getBroadcast() == null) {
								continue;
							}

							byte[] bb = interfaceAddress.getAddress().getAddress();
							int ip = toIp(bb);
							
							if (ip == 0) continue;
							
							String str1 = toIpString(bb) ; 
							addresses.set(0, str1);
							String str2 = toIpString(interfaceAddress.getBroadcast().getAddress()) ;
							addresses.set(1, str2 );
							Log.i(LongLakeApplication.DANIEL_TAG, String.format("ip %s %s" ,  str1 , str2)); 

						} catch (Exception e) {
							Log.i(LongLakeApplication.DANIEL_TAG, Log.getStackTraceString(e));
						}
					}
				}
			}
		} catch (SocketException ex) {
		}
		return addresses;
	}

	@Override
	protected void invoke(LLNetworkListener t, Object[] o1) {
		// NetworkInfo eventInfo = (NetworkInfo) o1[0];
		// if (eventInfo == null || eventInfo.getState() ==
		// NetworkInfo.State.DISCONNECTED) {
		// t.onStatusOfNetwork((boolean) false, (boolean) false);
		// } else if (eventInfo.getState() == NetworkInfo.State.CONNECTED) {
		// t.onStatusOfNetwork((boolean) true, (boolean) false);
		// }
		t.onStatusOfNetwork((Boolean) o1[0], (boolean) false);
	}
}