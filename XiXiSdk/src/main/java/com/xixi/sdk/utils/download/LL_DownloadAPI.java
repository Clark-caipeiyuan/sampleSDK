package com.xixi.sdk.utils.download;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.xixi.sdk.LLSDKUtils;
import com.xixi.sdk.globals.LLSdkGlobals;
import com.xixi.sdk.logger.Log;
import com.xixi.sdk.utils.thread.UIThreadDispatcher;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;;

public class LL_DownloadAPI {

	private static final int DEFAULT_TIMEOUT = 15;
	private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("video/mp4");
	private OkHttpClient mOkHttpClient;// okHttpClient
	public Retrofit retrofit;

	private int downloadingStatus = 0;

	public int getDownloadingStatus() {
		return downloadingStatus;
	}

	public LL_DownloadAPI() {
	}

	private final LLDownloadProgressListener default_l = new LLDownloadProgressListener() {

		@Override
		public void update(long bytesRead, long contentLength, boolean done) {
			try {
				downloadingStatus = (int) (bytesRead * 100 / contentLength);
			} catch (Exception e) {
			}
		}

	};

	OkHttpClient httpHelper;
	String url;

	//
	// public static void test() {
	// LL_DownloadAPI api = new LL_DownloadAPI();
	// api.uploadFiles(LLGlobals.UPLOAD_URL,
	// LLUtils.getFileList("/sdcard/longlake/logger"));
	// }

	public LL_DownloadAPI(String url, LLDownloadProgressListener listener) {

		LLDownloadProgressInterceptor interceptor = new LLDownloadProgressInterceptor(
				listener == null ? default_l : listener);

		httpHelper = new OkHttpClient.Builder().addInterceptor(interceptor).retryOnConnectionFailure(true)
				.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS).build();
		this.url = url;
	}

	public void download(Callback callback) {
		Request req = new Request.Builder().url(url).build();
		httpHelper.newCall(req).enqueue(callback);
	}

	public void downloadIc(Callback callback) {
		Request req = new Request.Builder().url(url).build();
		httpHelper.newCall(req).enqueue(callback);
	}

	public interface INetworkEventListener {
		public void onSuccess(File file, String response);

		public void onFail(File file, String errorCode);
	}

	public void upLoadFile(String actionUrl, final File file, final INetworkEventListener download_callback) {
		try {
			LLSDKUtils.danielAssert(file.canRead());
		} catch (Exception e) {
			download_callback.onFail(file, e.getMessage());
			return;
		}
		if (file.length() == 0) {
			file.delete();
			download_callback.onSuccess(file, "");
		} else {
			 final String requestUrl = actionUrl;

			RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
					.addFormDataPart("file", file.getName(), RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
					.addFormDataPart("deviceName", LLSdkGlobals.getUserName()).build();

			// ����Request
			if(mOkHttpClient == null){
				OkHttpClient.Builder builder = new OkHttpClient.Builder();
				builder.connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).readTimeout(10,
						TimeUnit.SECONDS);
				mOkHttpClient = builder.build();
			}

			final Request request = new Request.Builder().url(requestUrl).post(body).build();
			final Call call = mOkHttpClient.newCall(request);
			call.enqueue(new Callback() {
				@Override
				public void onFailure(Call arg0, final IOException arg1) {

					UIThreadDispatcher.dispatch(new Runnable() {
						public void run() {
							download_callback.onFail(file, android.util.Log.getStackTraceString(arg1));
						}
					});

				}

				@Override
				public void onResponse(Call arg0, final Response response) throws IOException {
					if (response.isSuccessful()) {
						ResponseBody rb = null;
						String strRes = null;
						try {
							rb = response.body();
							strRes = rb.string();
							rb.close();
							rb = null;
						} catch (Exception e) {
						}
						try {
							if (rb != null) {
								rb.close();
							}
						} catch (Exception e) {
						}
						final String res = strRes == null ? "" : strRes;
						Log.d(LLSdkGlobals.DANIEL_TAG, "Succeed in uploading" + file.getAbsolutePath());
						UIThreadDispatcher.dispatch(new Runnable() {
							public void run() {
								download_callback.onSuccess(file, res);
							}
						});
					} else {
						UIThreadDispatcher.dispatch(new Runnable() {
							public void run() {
								download_callback.onFail(file, "" + response.code());
							}
						});

					}
				}

			});
		}

	}
	//
	// public void upLoadFile(String actionUrl, final String filePath) {
	//
	// String requestUrl = actionUrl;
	// final File file = new File(filePath);
	//
	// RequestBody body = new
	// MultipartBody.Builder().setType(MultipartBody.FORM)
	// .addFormDataPart("file", file.getName(),
	// RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
	// .addFormDataPart("deviceName",
	// LLUserInfoMng.getInstance().getUserInfo().getUserName()).build();
	//
	// // ����Request
	// mOkHttpClient = new OkHttpClient();
	// final Request request = new
	// Request.Builder().url(requestUrl).post(body).build();
	// final Call call = mOkHttpClient.newCall(request);
	// call.enqueue(new Callback() {
	// @Override
	// public void onFailure(Call arg0, IOException arg1) {
	// Log.logException(arg1);
	//
	// }
	// @Override
	// public void onResponse(Call arg0, Response response) throws IOException {
	// if (response.isSuccessful()) {
	// Log.e(MainApplication.DANIEL_TAG, "Succeed in uploading" + filePath);
	//
	// //file.delete();
	// } else {
	// Log.e(MainApplication.DANIEL_TAG, "failed to ----->" + response.code());
	// }
	// }
	//
	// });
	// }

	public static void uploadFiles(final String link, final String folder) {
		uploadFiles(link, LLSDKUtils.getFileList(folder));
	}

	public static void uploadFiles(final String link, final List<File> lists) {
		LL_DownloadAPI api = new LL_DownloadAPI();
		api._uploadFiles(link, lists);
	}

	private void _uploadFiles(final String link, final List<File> lists) {
		final LLCursor index = new LLCursor();
		if (lists.size() == 0) {
			return;
		}

		final INetworkEventListener download_kits = new INetworkEventListener() {
			@Override
			public void onSuccess(File file, String str) {
				file.delete();
				index.moveToNext();
				if (index.getCurrentPosition() >= lists.size())
					return;
				else {
					upLoadFile(link, lists.get(index.getCurrentPosition()), this);
				}
			}

			@Override
			public void onFail(File file, String errorCode) {
				Log.e(LLSdkGlobals.DANIEL_TAG, "failed to upload " + file.getAbsolutePath());
			}
		};

		upLoadFile(link, lists.get(index.getCurrentPosition()), download_kits);

	}
}
