package com.xixi.sdk.utils.download;

public interface LLDownloadProgressListener {
	 void update(long bytesRead, long contentLength, boolean done);
}
