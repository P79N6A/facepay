package com.tencent.wxpay.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static android.content.Context.WIFI_SERVICE;
import static android.view.View.VISIBLE;

public class Util {

    private static final String TAG = "Util";

    private static Toast toast = null;

    public static void setToastMsg(Context mContext, String msg) {
        L.d(TAG, "toast====" + toast);
        if (toast != null) {
            toast.setText(msg);
            toast.setDuration(Toast.LENGTH_SHORT);
        } else {
            toast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    public static void setToastMsg(Context mContext, int id) {
        String msg = mContext.getResources().getString(id);
        if (toast != null) {
            toast.setText(msg);
            toast.setDuration(Toast.LENGTH_SHORT);
        } else {
            toast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    public static void showErrorInfo(Context mContext, int errorCode) {
        setToastMsg(mContext, getErrorString(mContext, errorCode));
    }

    /**
     * 获取错误码对应的提示信息
     *
     * @param context
     * @param errorCode
     * @return
     */
    public static String getErrorString(Context context, int errorCode) {
        Resources res = context.getResources();
        final String packageName = context.getPackageName();
        int nameResId = res.getIdentifier("error_" + String.valueOf(errorCode).replace("-", ""), "string", packageName);
        //        int imageResIdByAnotherForm = res.getIdentifier(packageName + ":drawable/ic_launcher", null, null);
        //        int musicResId = res.getIdentifier("beep", "raw", packageName);
        String msg = "";
        if (nameResId == 0) {
            nameResId = res.getIdentifier("error_unknown", "string", packageName);
            msg = context.getResources().getString(nameResId) + "  " + errorCode;
        } else {
            msg = context.getResources().getString(nameResId);
        }
        return msg;
    }

    /**
     * 获取String资源文件
     *
     * @param context
     * @param name
     * @return
     */
    public static int getResString(Context context, String name) {
        Resources res = context.getResources();
        final String packageName = context.getPackageName();
        int nameResId = res.getIdentifier(name, "string", packageName);
        return nameResId;
    }

    public static void sendMsg(Handler handler, int what) {
        Message msg = new Message();
        msg.what = what;
        handler.sendMessage(msg);
    }

}
