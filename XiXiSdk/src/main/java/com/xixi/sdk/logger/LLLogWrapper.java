/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xixi.sdk.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.xixi.sdk.LLSDKUtils;
import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.globals.LLConstantFile;
import com.xixi.sdk.globals.LLSdkGlobals;
import com.xixi.sdk.utils.download.LL_DownloadAPI;
import com.xixi.sdk.utils.file.LLFileUtils;
import com.xixi.sdk.utils.file.LLFileUtils.IFileOp;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

/**
 * Helper class which wraps Android's native Log utility in the Logger
 * interface. This way normal DDMS output can be one of the many targets
 * receiving and outputting logs simultaneously.
 */
public class LLLogWrapper extends LogWrapper implements LLConstantFile {

    private int logTag = L_TO_NULL;
    private Handler writeFileHandle;

    private HandlerThread writeFileThread;
    private static int _daysOfStored = 3;

    public static void setDaysOfStored(int daysOfStored) {
        _daysOfStored = daysOfStored;
        storedAsMillSec = daysOfStored * 24 * 3600 * 1000;
    }

    private static long storedAsMillSec = _daysOfStored * 24 * 3600 * 1000;

    private boolean test_if_enable_tag(int tag) {
        return 0 != (logTag & tag);
    }

    public boolean disable_tag_of_log() {
        return logTag == L_TO_NULL;
    }

    private void _prepareFileFolder() {
        File logFolder = new File(CONST_LOG_PATH);
        boolean bRet = false ;
        for (int i = 0; i < 10; i++) {
            try {
                 bRet = LLSDKUtils.preparePath(logFolder);
                if (bRet) break;
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
        }
        LLSDKUtils.danielAssert(bRet);
    }

    BufferedWriter bufferWriter;
    String logFileName;

    private void _prepareLogFile() {
        if (bufferWriter != null) {
            try {
                bufferWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bufferWriter = null;
        }

        logFileName = String.format("%s/%s_%s.log", CONST_LOG_PATH, LLSdkGlobals.getUserName(),
                String.valueOf(System.currentTimeMillis() / 1000));

        File file = new File(logFileName);

        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(file, true);
            bufferWriter = new BufferedWriter(fileWriter);
        } catch (IOException e) {
            Log.e(LongLakeApplication.DANIEL_TAG, Log.getStackTraceString(e)); // )e.printStackTrace();

        }
    }


    private void _writeBinaryVer() {
        _writeToFile(String.format("Ver Info:%d %s\r\n", LLSDKUtils.getVersionCode(), LLSDKUtils.getVerionName()));
    }


    private static void cleanRedundantFiles() {
        final long currentTime = System.currentTimeMillis();
        File[] opFiles = new File[]{
                new File(CONST_MONITOR_FILE_FOLDER), new File(CONST_LOG_PATH)
        };

        LLFileUtils.listFiles(opFiles, new IFileOp() {
            @Override
            public boolean opOnFile(File fs, FileFilter ff) {
                if (ff.accept(fs)) {
                    Log.i(LongLakeApplication.DANIEL_TAG, "fs=>" + fs.getName());
                    fs.delete();
                }
                return true;
            }
        }, new FileFilter() {

            @Override
            public boolean accept(File arg0) {
                if (arg0.isDirectory()) return false;
                return currentTime - arg0.lastModified() >= storedAsMillSec;
            }
        });
    }


    public LLLogWrapper(int logTag) {
        this.logTag = logTag;

        if (test_if_enable_tag(L_TO_FILE)) {
            writeFileThread = new HandlerThread("log");
            writeFileThread.start();
            writeFileHandle = new Handler(writeFileThread.getLooper());
            _prepareFileFolder();
            _prepareLogFile();
            cleanRedundantFiles();

            writeFileHandle.post(new Runnable() {
                public void run() {
                    _writeBinaryVer();

                }
            });
        }
    }

//	
//	private final Runnable dumpFileRunnable = new Runnable(){ 
//		public void run() {
//			FileReader fileReader ; 
//			try {
//				fileReader = new FileReader(file, true);
//				bufferWriter = new BufferedWriter(fileWriter);
//			} catch (IOException e) {
//				android.util.Log.e("failed to create BufferedWriter", Log.getStackTraceString(e)); // )e.printStackTrace();
//
//			}
//		}
//	} 

    public void dumpLogFile() {
//		try { 
//			writeFileHandle.post(new Runnable(){  
//				
//			})	
//		} catch(Exception e) { 
//			
//		}

    }

    public static final int L_TO_FILE = 0x1;
    public static final int L_TO_SYSTEM = 0x2;
    public static final int L_TO_ALL = 0x3;
    public static final int L_TO_NULL = 0x0;
    public static final boolean L_SYSTEM_LOG = false;

    public void sendViaHttp() {
        File logFolder = new File(CONST_LOG_PATH);
        if (logFolder.exists()) {
            LL_DownloadAPI.uploadFiles(LLSdkGlobals.getUPLOAD_URL(), LLSDKUtils.getFileList(CONST_LOG_PATH, new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return !logFileName.contains(pathname.getName());
                }
            }));
        }
    }

    public void flushLog(final Runnable r) {

        boolean sendLogTag = test_if_enable_tag(LLLogWrapper.L_TO_FILE);
        if (sendLogTag) {
            writeFileHandle.post(new Runnable() {
                public void run() {
                    try {
                        bufferWriter.close();
                    } catch (Exception e) {
                        Log.e(LongLakeApplication.DANIEL_TAG, Log.getStackTraceString(e)); // )e.printStackTrace();
                    }

                    r.run();
                }
            });
        } else {
            if (r != null) {
                r.run();
            }
        }
    }

    private void _writeToFile(final String strContent) {
        Runnable r = new Runnable() {
            public void run() {
                try {
                    bufferWriter.write(strContent);
                    bufferWriter.flush();
                } catch (Exception e) {
                    Log.e(LongLakeApplication.DANIEL_TAG, Log.getStackTraceString(e));
                }
            }
        };

        if (writeFileThread.getLooper().equals(Looper.myLooper())) {
            r.run();
        } else {
            writeFileHandle.post(r);
        }


    }

    public LLLogWrapper() {
        this(L_TO_FILE);
    }

    private final static SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss", Locale.ENGLISH);

    @Override
    public void println(int priority, String tag, String msg, Throwable tr) {
        if (disable_tag_of_log()) {
            return;
        }

        // There actually are log methods that don't take a msg parameter. For
        // now,
        // if that's the case, just convert null to the empty string and move
        // on.
        String useMsg = msg;
        if (useMsg == null) {
            useMsg = "";
        }

        // If an exeption was provided, convert that exception to a usable
        // string and attach
        // it to the end of the msg method.
        if (tr != null) {
            msg += "\n" + Log.getStackTraceString(tr);
        }

        if (test_if_enable_tag(L_TO_FILE)) {
            String time = sdf.format(new Date(System.currentTimeMillis()))
                    + "|";
            _writeToFile(time + tag + ":" + msg + "\r\n");
        }

        if (test_if_enable_tag(L_TO_SYSTEM)) {
            Log.println(priority, tag, useMsg);
        }
    }

}
