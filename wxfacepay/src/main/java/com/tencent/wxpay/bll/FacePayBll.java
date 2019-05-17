package com.tencent.wxpay.bll;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.tencent.wxpay.bean.AppConstants;
import com.tencent.wxpay.bean.FacePayInfoBean;
import com.tencent.wxpay.bean.FacePayRequestZbody;
import com.tencent.wxpay.imagefacesign.ReturnXMLParser;
import com.tencent.wxpay.okhttp.HttpGetRequest;
import com.tencent.wxpay.utils.L;
import com.tencent.wxpay.utils.Util;
import com.tencent.wxpayface.IWxPayfaceCallback;
import com.tencent.wxpayface.WxPayFace;
import com.tencent.wxpayface.WxfacePayCommonCode;

import org.json.JSONObject;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FacePayBll implements Handler.Callback {

    private static final String TAG = "FacePayBll";

    private volatile static FacePayBll facePayBll;

    private Context mContext;

    public static final String RETURN_CODE = "return_code";
    public static final String RETURN_MSG = "return_msg";
    private static final String PARAMS_FACE_AUTHTYPE = "face_authtype";
    private static final String PARAMS_APPID = "appid";
    private static final String PARAMS_MCH_ID = "mch_id";
    private static final String PARAMS_STORE_ID = "store_id";
    private static final String PARAMS_AUTHINFO = "authinfo";
    private static final String PARAMS_OUT_TRADE_NO = "out_trade_no";
    private static final String PARAMS_TOTAL_FEE = "total_fee";
    private static final String PARAMS_TELEPHONE = "telephone";
    private String mAuthInfo;
    private String corp_id, site_id, pay_type;
    private boolean isOk = false; //是否初始化完成（服务器是否返回appid等信息）

    /**
     * 服务器返回的参数
     */
    private String appid;//"wx176de7a3403494ed";//公众号
    private String mch_id;//"1534092231";//商户号
    //    public static String MCH_ID = "1502527911";//商户号
    private String sub_appid;//子商户公众账号ID(服务商模式)
    private String sub_mch_id;//子商户号(服务商模式)
    private String store_id;//"12345";//门店编号， 由商户定义， 各门店唯一
    private String store_name;//门店名称，由商户定义。（可用于展示）
    private String out_trade_no;//"123456";//商户订单号

    private FacePayBll() {

    }

    public static FacePayBll getInstance() {
        if (facePayBll == null) {
            synchronized (FacePayBll.class) {
                if (facePayBll == null) {
                    facePayBll = new FacePayBll();
                }
            }
        }
        return facePayBll;
    }

    public void init(Context mContext, String corp_id, String site_id, String pay_type) {
        isOk = false;
        this.mContext = mContext;
        this.corp_id = corp_id;
        this.site_id = site_id;
        this.pay_type = pay_type;

        Map<String, String> m1 = new HashMap<>();
//                m1.put("ip", "192.168.1.1"); //若没有代理,则不需要此行
//                m1.put("port", "8888");//若没有代理,则不需要此行
        WxPayFace.getInstance().initWxpayface(mContext, m1, new IWxPayfaceCallback() {
            @Override
            public void response(Map info) throws RemoteException {
                if (!isSuccessInfo(info)) {
                    return;
                }
                showToast(1, "初始化完成");
                L.d(TAG, "==========初始化完成=========");
                WxPayFace.getInstance().getWxpayfaceRawdata(new IWxPayfaceCallback() {
                    @Override
                    public void response(Map info) throws RemoteException {
                        if (!isSuccessInfo(info)) {
                            return;
                        }
                        L.d(TAG, "response | getWxpayfaceRawdata");
                        String rawdata = info.get("rawdata").toString();
                        try {
                            getAuthInfo(rawdata);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void getPayInfo(String corp_id, String site_id, String pay_type) {
        String url = "http://114.215.185.2:8086/ZhilaiPayPlat/wrist.action?";
        FacePayRequestZbody zbody = new FacePayRequestZbody();
        zbody.setCorp_id(corp_id);
        zbody.setSite_id(site_id);
        zbody.setPay_type(pay_type);
        HttpGetRequest.getRequest(AppConstants.FACE_PAY_CODE, zbody, url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                L.e(TAG, "====onFailure====" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String jsonstr = response.body().string();
                    L.d(TAG, "返回报文：" + jsonstr);
                    Gson gson = new Gson();

                    JSONObject jsonObject = new JSONObject(jsonstr);
                    JSONObject ZMSGObject = jsonObject.getJSONObject("ZMSG");
                    String reustCode = ZMSGObject.getJSONObject("ZHEAD").getString("RetCode");
                    String reustMsg = ZMSGObject.getJSONObject("ZHEAD").getString("RetMsg");
                    if ("0000".equals(reustCode)) {
                        FacePayInfoBean facePayInfoBean = gson.fromJson(ZMSGObject
                                .getJSONObject("ZBODY").toString(), FacePayInfoBean.class);
                        appid = facePayInfoBean.getAppid();
                        mch_id = facePayInfoBean.getMch_id();
                        sub_appid = facePayInfoBean.getSub_appid();
                        sub_mch_id = facePayInfoBean.getSub_mch_id();
                        store_id = facePayInfoBean.getStore_id();
                        store_name = facePayInfoBean.getStore_name();
                        out_trade_no = facePayInfoBean.getOut_trade_no();
                        L.d(TAG, "appid===" + appid);
                        isOk = true;
                        showToast(1, "初始化完成，可以进行人脸支付");
                    } else {
                        showToast(2, reustMsg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    L.e(TAG, "====Exception====" + e.getMessage());
                }
            }
        });
    }

    private OnFacePayListener onFacePayListener;

    public interface OnFacePayListener {
        void state(String state);
    }

    /**
     * 开始支付
     *
     * @param total_fee    订单金额(数字), 单位分
     * @param phone        用户手机号
     */
    public void startFacePay(String total_fee, String phone, OnFacePayListener onFacePayListener) {
        this.onFacePayListener = onFacePayListener;
        if (!isOk || !TextUtils.isEmpty(appid) && !TextUtils.isEmpty(mch_id)) {
            HashMap params = new HashMap();
            params.put(PARAMS_FACE_AUTHTYPE, "FACEPAY");
//                params.put(PARAMS_FACE_AUTHTYPE,"FACEID");
            params.put(PARAMS_APPID, appid);//公众号
            params.put(PARAMS_MCH_ID, mch_id);//商户号
            params.put(PARAMS_STORE_ID, store_id);//门店编号
            params.put(PARAMS_OUT_TRADE_NO, out_trade_no);//商户订单号。FACEPAY时必填
            params.put(PARAMS_TOTAL_FEE, total_fee);//订单金额(数字), 单位分. FACEPAY时必填
            params.put(PARAMS_TELEPHONE, phone);//用户手机号。用于传递会员手机，此手机将作为默认值， 填写到手机输入栏。
            params.put(PARAMS_AUTHINFO, mAuthInfo);//调用凭证。获取方式参见: get_wxpayface_authinfo

            WxPayFace.getInstance().getWxpayfaceCode(params, new IWxPayfaceCallback() {
                @Override
                public void response(Map info) throws RemoteException {
                    if (!isSuccessInfo(info)) {
                        return;
                    }
                    L.d(TAG, "response | getWxpayfaceCode");
                    final String code = (String) info.get(RETURN_CODE);
                    if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_SUCCESS)) {
                        L.d(TAG, "支付完成");
                        showToast(2, "支付完成");
                        try {
                            Thread.sleep(2000);
                        } catch (Exception e) {
                        }
                        WxPayFace.getInstance().updateWxpayfacePayResult(new HashMap(), new IWxPayfaceCallback() {
                            @Override
                            public void response(Map info) throws RemoteException {

                            }
                        });
                    } else if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_USER_CANCEL)) {
                        L.d(TAG, "用户取消");
                        showToast(2, "用户取消");
                    } else if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_SCAN_PAYMENT)) {
                        L.d(TAG, "扫码支付");
                        showToast(2, "扫码支付");
                    } else if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_ERROR)) {
                        L.d(TAG, "发生错误");
                        showToast(2, "发生错误");
                    }
                }
            });
        } else {
            showToast(1, "支付信息为空，正在重新获取，请稍后再试~");
            getPayInfo(corp_id, site_id, pay_type);
        }
    }

    private void getAuthInfo(String rawdata) throws IOException {
        L.d(TAG, "rawdata=============" + rawdata);
        //AuthInfo info =  new AuthInfo();
        L.d(TAG, "enter | getAuthInfo ");
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory)
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .build();

            RequestBody body = RequestBody.create(null, rawdata);

            Request request = new Request.Builder()
                    .url("https://wxpay.wxutil.com/wxfacepay/api/getWxpayFaceAuthInfo.php")
                    .post(body)
                    .build();

            client.newCall(request)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            L.e(TAG, "onFailure | getAuthInfo " + e.toString());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try {
                                mAuthInfo = ReturnXMLParser.parseGetAuthInfoXML(response.body().byteStream());
                                L.e(TAG, "onResponse | getAuthInfo " + mAuthInfo);
                                showToast(1, mAuthInfo);

                                if (!TextUtils.isEmpty(mAuthInfo)
                                        && !mAuthInfo.equals("无效的RawData数据")) {
                                    L.d(TAG, "===获取APPID===");
                                    getPayInfo(corp_id, site_id, pay_type);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 释放资源 释放人脸服务，断开连接
     *
     * @param mContext
     */
    public void releaseWxpayface(Context mContext) {
        WxPayFace.getInstance().releaseWxpayface(mContext);
    }

    private boolean isSuccessInfo(Map info) {
        if (info == null) {
            showToast(1, "调用返回为空, 请查看日志");
            new RuntimeException("调用返回为空").printStackTrace();
            return false;
        }
        String code = (String) info.get(RETURN_CODE);
        String msg = (String) info.get(RETURN_MSG);
        L.d(TAG, "response | getWxpayfaceRawdata " + code + " | " + msg);
        if (code == null || !code.equals(WxfacePayCommonCode.VAL_RSP_PARAMS_SUCCESS)) {
            showToast(1, "调用返回非成功信息, 请查看日志");
            L.d(TAG, "调用返回非成功信息: " + msg);
//            new RuntimeException("调用返回非成功信息: " + msg).printStackTrace();
            return false;
        }
        L.d(TAG, "调用返回成功");
        return true;
    }

    private void showToast(int what, String text) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = text;
        handler.sendMessage(msg);
    }

    private Handler handler = new Handler(this);
    private String text = "";

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case 1:
                text = (String) message.obj;
                Util.setToastMsg(mContext, text);
                break;
            case 2:
                text = (String) message.obj;
                Util.setToastMsg(mContext, text);
                if(onFacePayListener != null){
                    onFacePayListener.state(text);
                }
                break;
        }
        return false;
    }
}
