package com.xixi.sdk.utils.file;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.xixi.sdk.LLSDKUtils;
import com.xixi.sdk.cmds.LLCmdController;
import com.xixi.sdk.observer.IICleanable;
import com.xixi.sdk.observer.LLCleanResourceKits;
import com.xixi.sdk.utils.thread.LLThreadPool;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class LLFileUtils extends LLCacheUtils implements IICleanable {

    protected LLFileUtils(Context context) {
        super(context);

        LLCleanResourceKits.getInstance().registerAsObserver(this, true);
    }

    private static LLFileUtils instance;
    private static final int SUCCESS = 1;
    private static final int FAILED = 0;
    private static  final  String  SUCCESS_TAG = "Success";

    private String errorStr;

    public static synchronized LLFileUtils getInstance() {
        return instance;
    }

    public static synchronized LLFileUtils getInstance(Context context) {
        if (instance == null) {
            instance = new LLFileUtils(context);
        }
        return instance;
    }

    @Override
    public void cleanCache(Object o) {
        this.clean();
    }

    public interface IFileOp {
        public boolean opOnFile(File fs, FileFilter ff);
    }

    static FileFilter defaultFilter = new FileFilter() {

        @Override
        public boolean accept(File arg0) {
            return true;
        }
    };

    public static boolean listFiles(final File fs, final IFileOp fileOp) {
        return listFiles(fs, fileOp, defaultFilter);
    }

    public static boolean listFiles(final File fs, final IFileOp fileOp, final FileFilter ff) {
        return listFiles(new File[] {fs} , fileOp, ff) ;
    }

    public static boolean listFiles(final File[] files, final IFileOp fileOp, final FileFilter ff) {
        for (File fs : files) {
            if (fs.exists()) {
                if (fs.isDirectory()) {
                    File[] subs = fs.listFiles();
                    if (subs != null) {
                        if (!listFiles(subs, fileOp, ff)) {
                            return false;
                        }
                    }
                }
                if (ff.accept(fs)) {
                    if (!fileOp.opOnFile(fs, ff)) return false ;
                }
            }
        }
        return true;
    }


    private void  copyAssetsToSD(final String srcPath, final String sdPath , final IoCompletionListener1<Boolean> _io ) {
        LLThreadPool.getInstance().dispatchRunnable(new Runnable() {
            @Override
            public void run() {
                boolean bRet = false ;
                String errorMsg = "" ;
                try {
                    copyAssetsToDst(context, srcPath, sdPath);
                    bRet = true ;
                }
                catch(Exception e) {
                    errorMsg = android.util.Log.getStackTraceString(e) ;
                }
                _io.onFinish(bRet , errorMsg );
            }
        });
    }

    private boolean copyAssetsToDst(Context context, String srcPath, String dstPath) throws IOException {
            InputStream is = context.getAssets().open(srcPath);
            FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory()+"/"+dstPath);
            byte[] buffer = new byte[1024];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            is.close();
            fos.close();
        return true ;

    }

    public interface FileOperateCallback {
        void onSuccess();

        void onFailed(String error);
    }

    private boolean isAvailable( Context context, String packageName )
    {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for ( int i = 0; i < pinfo.size(); i++ )
        {
            if(pinfo.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

    public void  installTonePck(String packageName , String apkName,final IoCompletionListener1<Boolean> _io ){
        if (!isAvailable(context, packageName)) {
            final String path = Environment.getExternalStorageDirectory().toString() + "/"+apkName;
            copyAssetsToSD(apkName, path, new IoCompletionListener1<Boolean>() {
                @Override
                public void onFinish(Boolean data, final Object context) {

                    if ( data ) {
                        LLCmdController.getInstance().exeCmd(new String[] { "pm install /sdcard"+path },SUCCESS_TAG ,
                                new IoCompletionListener1<Boolean>() {
                                    @Override
                                    public void onFinish(final Boolean data, final Object context) {
                                        LLSDKUtils.runInMainThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                _io.onFinish(data , context);
                                            }
                                        });

                                    }
                                });
                    }
                    else {
                        LLSDKUtils.runInMainThread(new Runnable() {
                            @Override
                            public void run() {
                                _io.onFinish(false , context);
                            }
                        });
                    }
                }
            });
        }else{
            LLSDKUtils.runInMainThread(new Runnable() {
                @Override
                public void run() {
                    _io.onFinish(true , context);
                }
            });
        }
    }
}