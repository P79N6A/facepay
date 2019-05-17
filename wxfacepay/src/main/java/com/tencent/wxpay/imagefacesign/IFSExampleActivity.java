package com.tencent.wxpay.imagefacesign;

import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mmfacepay.R;
import com.tencent.wxpay.utils.L;
import com.tencent.wxpayface.IWxPayfaceCallback;
import com.tencent.wxpayface.WxPayFace;
import com.tencent.wxpayface.WxfacePayCommonCode;

import java.io.IOException;
import java.io.InputStream;
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

public class IFSExampleActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = "IFSExampleActivity";

    private TextView mFaceCallback;
    private TextView mResultTxt;
    private Button mCodeBtn;
    private Button mFacePayDelayBtn;
    private EditText mMemberEdit;
    private Button mStartFaceOnceRecognize;

    public static final String RETURN_CODE = "return_code";
    public static final String RETURN_SUCCESS = "SUCCESS";
    public static final String RETURN_FAILE = "SUCCESS";
    public static final String RETURN_MSG = "return_msg";

    private static final String PARAMS_FACE_AUTHTYPE = "face_authtype";
    private static final String PARAMS_APPID = "appid";
    private static final String PARAMS_MCH_ID = "mch_id";
    private static final String PARAMS_MCH_NAME = "mch_name";
    private static final String PARAMS_STORE_ID = "store_id";
    private static final String PARAMS_AUTHINFO = "authinfo";
    private static final String PARAMS_OUT_TRADE_NO = "out_trade_no";
    private static final String PARAMS_TOTAL_FEE = "total_fee";
    private static final String PARAMS_TELEPHONE = "telephone";
    private String mAuthInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.d(TAG, "onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_example);

        mFaceCallback = (TextView) findViewById(R.id.face_callback);
        mCodeBtn = (Button) findViewById(R.id.code);
        mFacePayDelayBtn = (Button) findViewById(R.id.facepay_delay);
        mMemberEdit = (EditText) findViewById(R.id.val_mem_txt);
        mResultTxt = (TextView) findViewById(R.id.pay_result);
        mStartFaceOnceRecognize = (Button) findViewById(R.id.start_faceId_once_recognize);
        mCodeBtn.setOnClickListener(this);
        mFacePayDelayBtn.setOnClickListener(this);
        mStartFaceOnceRecognize.setOnClickListener(this);

        setPayInfo(APP_ID, MCH_ID, TOTAL_FEE, OUT_TRADE_NO);
    }

    /**
     * 设置支付信息
     * @param app_id 公众号
     * @param mch_id 商户号
     * @param total_fee 订单金额(数字), 单位分. FACEPAY时必填
     * @param out_trade_no 商户订单号。FACEPAY时必填
     */
    private void setPayInfo(String app_id, String mch_id
            , String total_fee, String out_trade_no) {
        APP_ID = app_id;//公众号
        MCH_ID = mch_id;//商户号
        TOTAL_FEE = total_fee;//订单金额(数字), 单位分. FACEPAY时必填
        OUT_TRADE_NO = out_trade_no;//商户订单号。FACEPAY时必填

        init();
    }

    private void init() {
        Map<String, String> m1 = new HashMap<>();
//                m1.put("ip", "192.168.1.1"); //若没有代理,则不需要此行
//                m1.put("port", "8888");//若没有代理,则不需要此行
        WxPayFace.getInstance().initWxpayface(this, m1, new IWxPayfaceCallback() {
            @Override
            public void response(Map info) throws RemoteException {
                if (!isSuccessInfo(info)) {
                    return;
                }
                showToast("初始化完成");
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.code) {
            L.d(TAG, "onClick | code ");
//                if(mAuthInfo == null){
//                    L.d(TAG, "=====请重新初始化=====");
//                    showToast("请重新初始化");
//                    return;
//                }
            HashMap params = new HashMap();
            params.put(PARAMS_FACE_AUTHTYPE, "FACEPAY");
//                params.put(PARAMS_FACE_AUTHTYPE,"FACEID");
            params.put(PARAMS_APPID, APP_ID);//公众号
            params.put(PARAMS_MCH_ID, MCH_ID);//商户号
            params.put(PARAMS_STORE_ID, STORE_ID);//门店编号
            params.put(PARAMS_OUT_TRADE_NO
                    , "" + (System.currentTimeMillis() / 100000));//商户订单号。FACEPAY时必填
            params.put(PARAMS_TOTAL_FEE, TOTAL_FEE);//订单金额(数字), 单位分. FACEPAY时必填
            String phone = mMemberEdit.getText().toString();
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_SUCCESS)) {
                                mResultTxt.setText("支付完成");
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
                                mResultTxt.setText("用户取消");
                            } else if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_SCAN_PAYMENT)) {
                                mResultTxt.setText("扫码支付");
                            } else if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_ERROR)) {
                                mResultTxt.setText("发生错误");
                            }
                        }
                    });
                }
            });
            //            case R.id.start_faceId_once_recognize:
//                L.d(TAG, "onClick | stop_face_recognize ");
//                doFaceRecognize(true);
//                break;
//            case R.id.facepay_delay:
//                L.d(TAG, "onClick | facepay_delay ");
//                HashMap params_delay = new HashMap();
//                params_delay.put(PARAMS_FACE_AUTHTYPE, "FACEPAY_DELAY");
//                params_delay.put(PARAMS_APPID, APP_ID);
//                params_delay.put(PARAMS_MCH_ID, MCH_ID);
//                params_delay.put(PARAMS_STORE_ID, STORE_ID);
//                params_delay.put(PARAMS_OUT_TRADE_NO, "" + (System.currentTimeMillis() / 100000));
//                params_delay.put(PARAMS_TOTAL_FEE, TOTAL_FEE);
//                params_delay.put(PARAMS_TELEPHONE, mMemberEdit.getText().toString());
//                params_delay.put(PARAMS_AUTHINFO, mAuthInfo);
//                params_delay.put("sub_mch_id", SUB_MCH_ID);////子商户号(非服务商模式不填)
//                WxPayFace.getInstance().getWxpayfaceCode(params_delay, new IWxPayfaceCallback() {
//                    @Override
//                    public void response(Map info) throws RemoteException {
//                        if (!isSuccessInfo(info)) {
//                            return;
//                        }
//                        L.d(TAG, "response | getWxpayfaceCode");
//                        final String code = (String) info.get(RETURN_CODE);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_SUCCESS)) {
//                                    mResultTxt.setText("支付完成");
//                                    try {
//                                        Thread.sleep(2000);
//                                    } catch (Exception e) {
//                                    }
//                                    WxPayFace.getInstance().updateWxpayfacePayResult(new HashMap(), new IWxPayfaceCallback() {
//                                        @Override
//                                        public void response(Map info) throws RemoteException {
//                                        }
//                                    });
//                                } else if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_USER_CANCEL)) {
//                                    mResultTxt.setText("用户取消");
//                                } else if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_SCAN_PAYMENT)) {
//                                    mResultTxt.setText("扫码支付");
//                                } else if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_FACEPAY_NOT_AUTH)) {
//                                    mResultTxt.setText("无即时支付无权限");
//                                }
//                            }
//                        });
//                    }
//                });
//                break;
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
//                                L.d(TAG, "response.body().string()==="
//                                        + response.body().string());
                                InputStream inputStream = response.body().byteStream();
                                mAuthInfo = ReturnXMLParser.parseGetAuthInfoXML(inputStream);
                                L.e(TAG, "mAuthInfo===" + mAuthInfo);
                                showToast("AuthInfo===" + mAuthInfo);
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

//    private void doFaceRecognize(boolean once) {
//        HashMap params2 = new HashMap();
//        if (once) {
//            params2.put(PARAMS_FACE_AUTHTYPE, "FACEID-ONCE");
//        } else {
//            params2.put(PARAMS_FACE_AUTHTYPE, "FACEPAY");
//        }
//        params2.put(PARAMS_APPID, APP_ID);
//        params2.put(PARAMS_MCH_ID, MCH_ID);
//        params2.put(PARAMS_MCH_NAME, "科脉自助收银");
////                params2.put(PARAMS_MCH_ID,"12306");
////                params2.put(PARAMS_STORE_ID,"12345");
////                params2.put(PARAMS_SUB_APPID,"33333");
////                params2.put(PARAMS_SUB_MCH_ID,"44444");
//        params2.put(PARAMS_OUT_TRADE_NO, "" + (System.currentTimeMillis() / 100000));
//        params2.put(PARAMS_TOTAL_FEE, TOTAL_FEE);
//        String phone2 = mMemberEdit.getText().toString();
//        params2.put(PARAMS_TELEPHONE, phone2);
//        params2.put(PARAMS_AUTHINFO, mAuthInfo);
//        WxPayFace.getInstance().getWxpayfaceUserInfo(params2, new IWxPayfaceCallback() {
//            @Override
//            public void response(final Map info) throws RemoteException {
//                if (mFaceCallback != null) {
//                    mFaceCallback.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            mFaceCallback.setText("response | getWxpayfaceUserInfo " + info.toString());
//                        }
//                    });
//                }
//                L.d(TAG, "response | getWxpayfaceUserInfo " + info.toString());
//            }
//        });
//    }

    @Override
    protected void onPause() {
        super.onPause();
        L.d(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        L.d(TAG, "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        L.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.d(TAG, "onDestroy");
        WxPayFace.getInstance().releaseWxpayface(this);
    }

    private boolean isSuccessInfo(Map info) {
        if (info == null) {
            showToast("调用返回为空, 请查看日志");
            new RuntimeException("调用返回为空").printStackTrace();
            return false;
        }
        String code = (String) info.get(RETURN_CODE);
        String msg = (String) info.get(RETURN_MSG);
        L.d(TAG, "response | getWxpayfaceRawdata " + code + " | " + msg);
        if (code == null || !code.equals(WxfacePayCommonCode.VAL_RSP_PARAMS_SUCCESS)) {
            showToast("调用返回非成功信息, 请查看日志");
            L.d(TAG, "调用返回非成功信息: " + msg);
//            new RuntimeException("调用返回非成功信息: " + msg).printStackTrace();
            return false;
        }
        L.d(TAG, "调用返回成功");
        return true;
    }

    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(IFSExampleActivity.this, text, Toast.LENGTH_LONG).show();
            }
        });
    }
}