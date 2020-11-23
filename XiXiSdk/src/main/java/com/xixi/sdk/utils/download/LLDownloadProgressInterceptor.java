package com.xixi.sdk.utils.download;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class LLDownloadProgressInterceptor implements Interceptor {
	
	private LLDownloadProgressListener listener;

    public LLDownloadProgressInterceptor(LLDownloadProgressListener listener) {
        this.listener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        return originalResponse.newBuilder()
                .body(new LLDownloadProgressResponseBody(originalResponse.body(), listener))
                .build();
    }
}
