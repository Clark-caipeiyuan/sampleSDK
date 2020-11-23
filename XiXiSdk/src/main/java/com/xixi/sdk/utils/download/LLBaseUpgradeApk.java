package com.xixi.sdk.utils.download;

import java.io.File;
import java.io.IOException;

import com.xixi.sdk.LLSDKUtils;
import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.controller.LLResetController;
import com.xixi.sdk.controller.xcap.LLNetworkOverXcap;
import com.xixi.sdk.controller.xcap.LLNetworkOverXcap.XCAP_REQUEST_TYPE;
import com.xixi.sdk.controller.xcap.LLXcapRequest;
import com.xixi.sdk.logger.Log;
import com.xixi.sdk.model.LLUserData;
import com.xixi.sdk.utils.network.LLCallback;
import com.xixi.sdk.utils.thread.UIThreadDispatcher;

import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class LLBaseUpgradeApk {
	
	protected LLBaseUpgradeApk() {
		boolean bRet = LLSDKUtils.preparePath(new File(APK_FOLDER));
		LLSDKUtils.danielAssert(bRet);
	}

	private final static String APK_FOLDER = Environment.getExternalStorageDirectory().toString() + "/longlake/apk";
	private final static String apkPath = APK_FOLDER + "/update.apk";
	private static final String cmd_install = "pm install -r ";
	private final static int PERIODIC = 24 * 3600;
	private static final long time_upgrade = 12600;
	private final static String indoor_url = "%s?id=%s&type=Indoor&targetVersion=%s";
	private final static String outdoor_url = "%s?id=%s&type=Outdoor&targetVersion=%s";

	public abstract String getInstallCmd();
    public abstract String getUrl() ;
    public abstract String getSIP();
    public abstract LLUserData getUserData() ; 
	private int clientInstall(String apkPath) {

		final String cmds[] = { cmd_install + apkPath, "export LD_LIBRARY_PATH=/vendor/lib:/system/lib", getInstallCmd(),
				"exit" };

		return LLSDKUtils.exeCmd(cmds);
	}

	LL_DownloadAPI downloaderApi;
	final Handler mainHandler = new Handler();

	public boolean isDownloading() {
		return downloaderApi != null;
	}

	private void resetApiTag() {
		UIThreadDispatcher.dispatch(new Runnable() {
			@Override
			public void run() {
				downloaderApi = null;
			}
		});
	}

	public int getProgress() {
		if (downloaderApi == null)
			return 0;
		return downloaderApi.getDownloadingStatus();

	}

	public void upGradeApkImmediately() {
		
		final LLUserData userInfo = getUserData() ; 
		LLNetworkOverXcap.getInstance().routeRequest(new LLXcapRequest(XCAP_REQUEST_TYPE.XCAP_GET_OUTDOOR_TARGETVERSION,
				getSIP(), userInfo.getUserName(), new LLCallback<ResponseBody>() {
					@Override
					public void onLLResponse(retrofit2.Call<ResponseBody> arg0, retrofit2.Response<ResponseBody> arg1,
							final String payload) {
						if (TextUtils.isEmpty(payload)) {
							Log.i(LongLakeApplication.DANIEL_TAG, "invalid version");
							return;
						}
						if (TextUtils.equals(LLSDKUtils.getVerionName(), payload)) {
							Log.i(LongLakeApplication.DANIEL_TAG, "identical version");
							return;
						}
						Log.d("clark",payload);
						LLUserData ud = getUserData()  ;
						String sipaddress= ud.getSipAddr();
						LLNetworkOverXcap.getInstance()
								.routeRequest(new LLXcapRequest(XCAP_REQUEST_TYPE.XCAP_GET_OUTDOOR_APKURL,
										getSIP(), userInfo.getUserName(), new LLCallback<ResponseBody>() {
											@Override
											public void onLLResponse(retrofit2.Call<ResponseBody> arg0,
													retrofit2.Response<ResponseBody> arg1, final String url) {
												if (!url.isEmpty()) {
													final String request_url = String.format(
															getUrl(), url,
															userInfo.getUserName(), payload);
													UIThreadDispatcher.dispatch(new Runnable() {
														public void run() {
															upgradeApk(request_url);
														}
													});

												} else {
													Log.i(LongLakeApplication.DANIEL_TAG, "invalid url");
												}
											}

											@Override
											public void onLLFailure(retrofit2.Call<ResponseBody> arg0, Throwable arg1) {
											}
										}));

					}

					@Override
					public void onLLFailure(retrofit2.Call<ResponseBody> arg0, Throwable arg1) {
						Log.i(LongLakeApplication.DANIEL_TAG, android.util.Log.getStackTraceString(arg1));
					}
				}));
	}

	public void upGradeApkOnTime() {
		long currentTimestamp = LLResetController.getInstance().convertTime(System.currentTimeMillis());
		UIThreadDispatcher.dispatch(new Runnable() {
			public void run() {
				upGradeApkImmediately();
			}

		}, ((PERIODIC - currentTimestamp + time_upgrade) % PERIODIC) * 1000);
	}

	public void upgradeApk(final String url) {
		Log.d("clark",url);
		if (downloaderApi != null)
			return;
		downloaderApi = new LL_DownloadAPI(url, null);
		downloaderApi.download(new Callback() {
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				resetApiTag();
			}

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {

				if (arg1.isSuccessful()) {
					LLSDKUtils.writeResponseBodyToDisk(arg1.body(), apkPath);
					clientInstall(apkPath);
					resetApiTag();
				} else {
					onFailure(arg0, new IOException("failed to download " + url));
				}
			}
		});

	}
}
