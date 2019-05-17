package com.tencent.wxpay.imagefacesign;

import android.app.Application;
import android.os.Environment;

import com.tencent.wxpay.utils.L;

/**
 * Created by admin on 2018/4/27.
 */

public class IFSApplication extends Application{

    public static final String TAG = "IFinitWxpayfaceSApplication";

    @Override
    public void onCreate() {
        super.onCreate();
//        initXlog();

    }

    private void initXlog(){
        L.i(TAG, "initXlog");
        System.loadLibrary("stlport_shared");
        System.loadLibrary("marsxlog");

        final String SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
        final String logPath = SDCARD + "/imageFaceSign/log";

        // this is necessary, or may cash for SIGBUS
        final String cachePath = this.getFilesDir() + "/xlog";

//        //init xlog
//        if (BuildConfig.DEBUG) {
//            Xlog.appenderOpen(Xlog.LEVEL_DEBUG, Xlog.AppednerModeAsync, cachePath, logPath, "ImageFaceSign", "");
//            Xlog.setConsoleLogOpen(true);
//
//        } else {
//            Xlog.appenderOpen(Xlog.LEVEL_INFO, Xlog.AppednerModeAsync, cachePath, logPath, "ImageFaceSign", "");
//            Xlog.setConsoleLogOpen(false);
//        }
//
//        Log.setLogImp(new Xlog());
    }

    @Override
    public void onTerminate() {
        L.i(TAG, "application terminated");
        super.onTerminate();
//        Log.appenderClose();

    }

}
