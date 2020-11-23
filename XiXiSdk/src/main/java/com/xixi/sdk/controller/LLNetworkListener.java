package com.xixi.sdk.controller;

public interface LLNetworkListener {
	public void onStatusOfNetwork(boolean isReachable, boolean isWifiOnly);
}
