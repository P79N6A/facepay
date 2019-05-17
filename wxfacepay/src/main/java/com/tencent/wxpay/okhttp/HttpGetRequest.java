package com.tencent.wxpay.okhttp;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.tencent.wxpay.bean.AppConstants;
import com.tencent.wxpay.bean.BaseRequestModel;
import com.tencent.wxpay.bean.ZBODY;
import com.tencent.wxpay.bean.ZHEAD;
import com.tencent.wxpay.bean.ZMSG;
import com.tencent.wxpay.utils.L;

import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 异步请求
 *
 * @author KF0001
 * <p>
 * 用于即时请求
 */

public class HttpGetRequest {

    private static final String TAG = "HttpGetRequest";
    private static OkHttpClient mOkHttpClient;

    private static void initOkHttpClient() {
        if (mOkHttpClient == null) {
            mOkHttpClient = new OkHttpClient();
            mOkHttpClient.newBuilder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS);
        }
    }

    public static void getRequest(String tcode, ZBODY zbodyModel, String url, Callback callback) {
        BaseRequestModel baseRequest = new BaseRequestModel();
        ZMSG zmsg = new ZMSG();
        ZHEAD zhead = new ZHEAD(tcode);
        zmsg.setZHEAD(zhead);
        zmsg.setZBODY(zbodyModel);
        baseRequest.setZMSG(zmsg);
        Gson gson = new Gson();
        String json = gson.toJson(baseRequest);
        L.d(TAG, "请求地址：" + url);
        L.d(TAG, "请求发送数据：" + json);
        if (mOkHttpClient == null) {
            mOkHttpClient = new OkHttpClient();
        }

        FormBody.Builder builder = new FormBody.Builder();
        builder.add("method", AppConstants.METHOD);
        builder.add("auth_id", AppConstants.AUTH_ID);
        builder.add("auth_name", AppConstants.AUTH_NAME);
        builder.add("xml", json);

        FormBody formBody = builder.build();
        Request.Builder builder2 = new Request.Builder();
        builder2.url(url);
        builder2.post(formBody);
        Request request2 = builder2.build();

        mOkHttpClient.newCall(request2).enqueue(callback);
    }

    public static void getConfigRequest(Callback callback, final String jsonString, String url) {
        L.e("请求url: " + url);
        if (TextUtils.isEmpty(url))
            return;
        L.e("发送请求：" + jsonString);
        initOkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("method", "download")
                .add("applyJson", jsonString)
                .build();
        L.e("发送请求：" + body.toString());
        Request requset = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        mOkHttpClient.newCall(requset).enqueue(callback);
    }

    public static void getXMLConfigRequest(Callback callback, String url) {
        L.e("请求 XML url: " + url);
        if (TextUtils.isEmpty(url)) {
            return;
        }
        initOkHttpClient();
        RequestBody body = new FormBody.Builder()
                .build();

        L.e("发送请求：" + body.toString());
        Request requset = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        mOkHttpClient.newCall(requset).enqueue(callback);
    }
}
