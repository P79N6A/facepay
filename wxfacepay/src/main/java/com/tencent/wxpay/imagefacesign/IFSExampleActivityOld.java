package com.tencent.wxpay.imagefacesign;

import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mmfacepay.R;
import com.tencent.wxpay.utils.L;
import com.tencent.wxpayface.IWxPayfaceCallback;
import com.tencent.wxpayface.WxPayFace;
import com.tencent.wxpayface.WxfacePayCommonCode;

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

public class IFSExampleActivityOld extends BaseActivity implements View.OnClickListener {

    public static final String TAG = "IFSExampleActivity";

    //1:1相关
    private LinearLayout mPayLayout;
    private TextView mResultTxt;
    private Button mInitBtn;
    private Button mRawBtn;
    private Button mCodeBtn;
    private Button mReportInfoBtn;
    private Button mFacePayDelayBtn;
    private Button mReportOrderBtn;
    private Button mUpdatePayBtn;
    private EditText mMemberEdit;
    private Button mReleaseBtn;
    private Button mShowBanner;
    private Button mRemoveBanner;

    //1:N相关
    private Button mStartFaceRecognize;
    private Button mStartFaceOnceRecognize;
    private Button mStopFaceRecognize;
    private TextView mFaceCallback;

    private Button mExitExample;

    public static final String RETURN_CODE = "return_code";
    public static final String RETURN_SUCCESS = "SUCCESS";
    public static final String RETURN_FAILE = "SUCCESS";
    public static final String RETURN_MSG = "return_msg";

    private static final String PARAMS_FACE_AUTHTYPE = "face_authtype";
    private static final String PARAMS_APPID = "appid";
    private static final String PARAMS_SUB_APPID = "sub_appid";
    private static final String PARAMS_MCH_ID = "mch_id";
    private static final String PARAMS_MCH_NAME = "mch_name";
    private static final String PARAMS_SUB_MCH_ID = "sub_mch_id";
    private static final String PARAMS_STORE_ID = "store_id";
    private static final String PARAMS_AUTHINFO = "authinfo";
    private static final String PARAMS_OUT_TRADE_NO = "out_trade_no";
    private static final String PARAMS_TOTAL_FEE = "total_fee";
    private static final String PARAMS_TELEPHONE = "telephone";

    private static final String PARAMS_REPORT_ITEM = "item";
    private static final String PARAMS_REPORT_ITEMVALUE = "item_value";

    private static final String PARAMS_REPORT_MCH_ID = "mch_id";
    private static final String PARAMS_REPORT_SUT_MCH_ID = "sub_mch_id";
    private static final String PARAMS_REPORT_OUT_TRADE_NO = "out_trade_no";
    private static final String PARAMS_BANNER_STATE = "banner_state";

    private EditText mFaceMarginTop;
    private EditText mFaceNormalCount;
    private EditText mFaceCenterNum;
    private EditText mFaceThesholdPU;
    private EditText mFaceThesholdPD;
    private EditText mFaceThesholdY;
    private EditText mFaceThesholdR;
    private EditText mFaceSizeBig;
    private EditText mFaceSizeSmall;

    private String mAuthInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.d(TAG, "onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_example_old);

        mInitBtn = (Button) findViewById(R.id.init);
        mRawBtn = (Button) findViewById(R.id.raw);
        mCodeBtn = (Button) findViewById(R.id.code);
        mFacePayDelayBtn = (Button) findViewById(R.id.facepay_delay);
        mReportInfoBtn = (Button) findViewById(R.id.reportinfo);
        mReportOrderBtn = (Button) findViewById(R.id.reportorder);
        mReleaseBtn = (Button) findViewById(R.id.release);
        mMemberEdit = (EditText) findViewById(R.id.val_mem_txt);
        mPayLayout = (LinearLayout) findViewById(R.id.pay_page);
        mResultTxt = (TextView) findViewById(R.id.pay_result);
        mStartFaceRecognize = (Button) findViewById(R.id.start_face_recognize);
        mStartFaceOnceRecognize = (Button) findViewById(R.id.start_faceId_once_recognize);
        mStopFaceRecognize = (Button) findViewById(R.id.stop_face_recognize);
        mUpdatePayBtn = (Button) findViewById(R.id.updatepay);
        mShowBanner = (Button) findViewById(R.id.showbanner);
        mRemoveBanner = (Button) findViewById(R.id.removebanner);
        mExitExample = (Button) findViewById(R.id.exit_example);
        mFaceCallback = (TextView) findViewById(R.id.face_callback);

        mFaceMarginTop = (EditText) findViewById(R.id.val_margin_top);
        mFaceNormalCount = (EditText) findViewById(R.id.val_face_normal_count);
        mFaceCenterNum = (EditText) findViewById(R.id.val_face_center_num);
        mFaceThesholdPU = (EditText) findViewById(R.id.val_thresshold_pu);
        mFaceThesholdPD = (EditText) findViewById(R.id.val_thresshold_pd);
        mFaceThesholdY = (EditText) findViewById(R.id.val_thresshold_y);
        mFaceThesholdR = (EditText) findViewById(R.id.val_thresshold_r);
        mFaceSizeBig = (EditText) findViewById(R.id.face_size_big);
        mFaceSizeSmall = (EditText) findViewById(R.id.face_size_small);

        mInitBtn.setOnClickListener(this);
        mRawBtn.setOnClickListener(this);
        mCodeBtn.setOnClickListener(this);
        mFacePayDelayBtn.setOnClickListener(this);
        mReportInfoBtn.setOnClickListener(this);
        mReportOrderBtn.setOnClickListener(this);
        mReleaseBtn.setOnClickListener(this);
        mReleaseBtn.setOnClickListener(this);
        mStartFaceRecognize.setOnClickListener(this);
        mStartFaceOnceRecognize.setOnClickListener(this);
        mStopFaceRecognize.setOnClickListener(this);
        mUpdatePayBtn.setOnClickListener(this);
        mShowBanner.setOnClickListener(this);
        mRemoveBanner.setOnClickListener(this);
        mExitExample.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.init) {
            L.d(TAG, "onClick | init ");
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
                }
            });
        } else if (i == R.id.raw) {
            L.d(TAG, "onClick | raw ");
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
        } else if (i == R.id.facepay_delay) {
            L.d(TAG, "onClick | facepay_delay ");
            HashMap params_delay = new HashMap();
            params_delay.put(PARAMS_FACE_AUTHTYPE, "FACEPAY_DELAY");
            params_delay.put(PARAMS_APPID, APP_ID);
            params_delay.put(PARAMS_MCH_ID, MCH_ID);
            params_delay.put(PARAMS_STORE_ID, STORE_ID);
            params_delay.put(PARAMS_OUT_TRADE_NO, "" + (System.currentTimeMillis() / 100000));
            params_delay.put(PARAMS_TOTAL_FEE, TOTAL_FEE);
            params_delay.put(PARAMS_TELEPHONE, mMemberEdit.getText().toString());
            params_delay.put(PARAMS_AUTHINFO, mAuthInfo);
            params_delay.put("sub_mch_id", SUB_MCH_ID);////子商户号(非服务商模式不填)
            WxPayFace.getInstance().getWxpayfaceCode(params_delay, new IWxPayfaceCallback() {
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
                            mPayLayout.setVisibility(View.GONE);
                            mResultTxt.setVisibility(View.VISIBLE);
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
                            } else if (TextUtils.equals(code, WxfacePayCommonCode.VAL_RSP_PARAMS_FACEPAY_NOT_AUTH)) {
                                mResultTxt.setText("无即时支付无权限");
                            }
                            mResultTxt.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mPayLayout.setVisibility(View.VISIBLE);
                                    mResultTxt.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });
        } else if (i == R.id.code) {
            L.d(TAG, "onClick | code ");
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
                            mPayLayout.setVisibility(View.GONE);
                            mResultTxt.setVisibility(View.VISIBLE);
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
                            mResultTxt.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mPayLayout.setVisibility(View.VISIBLE);
                                    mResultTxt.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });
        } else if (i == R.id.reportinfo) {
            L.d(TAG, "onClick | reportinfo ");
            Map info1 = new HashMap();
            info1.put(PARAMS_REPORT_ITEM, "PAY");
            info1.put(PARAMS_REPORT_ITEMVALUE, 1000000);
            WxPayFace.getInstance().reportInfo(info1, new IWxPayfaceCallback() {
                @Override
                public void response(Map info) throws RemoteException {
                    if (!isSuccessInfo(info)) {
                        return;
                    }
                    L.d(TAG, "response | reportinfo");
                }
            });
        } else if (i == R.id.reportorder) {
            L.d(TAG, "onClick | reportorder ");
            Map info2 = new HashMap();
            info2.put(PARAMS_REPORT_MCH_ID, "1000");
            info2.put(PARAMS_REPORT_SUT_MCH_ID, "2000");
            info2.put(PARAMS_OUT_TRADE_NO, "3000");
            WxPayFace.getInstance().reportOrder(info2, new IWxPayfaceCallback() {
                @Override
                public void response(Map info) throws RemoteException {
                    if (!isSuccessInfo(info)) {
                        return;
                    }
                    L.d(TAG, "response | reportOrder");
                }
            });
        } else if (i == R.id.release) {
            L.d(TAG, "onClick | release ");
            WxPayFace.getInstance().releaseWxpayface(this);
        } else if (i == R.id.start_face_recognize) {
            L.d(TAG, "onClick | start_face_recognize ");
            doFaceRecognize(false);
        } else if (i == R.id.stop_face_recognize) {
            L.d(TAG, "onClick | stop_face_recognize ");
            HashMap map = new HashMap();
            WxPayFace.getInstance().stopWxpayface(map, new IWxPayfaceCallback() {
                @Override
                public void response(Map info) throws RemoteException {
                    if (!isSuccessInfo(info)) {
                        return;
                    }
                    L.d(TAG, "response | stopWxpayface");
                }
            });
        } else if (i == R.id.start_faceId_once_recognize) {
            L.d(TAG, "onClick | stop_face_recognize ");
            doFaceRecognize(true);
        } else if (i == R.id.updatepay) {
            L.d(TAG, "onClick | updatepay ");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(10 * 1000);
                        HashMap map = new HashMap();
                        WxPayFace.getInstance().updateWxpayfacePayResult(map, new IWxPayfaceCallback() {
                            @Override
                            public void response(Map info) throws RemoteException {
                                if (!isSuccessInfo(info)) {
                                    return;
                                }
                                L.d(TAG, "response | updateWxpayfacePayResult");
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else if (i == R.id.showbanner) {
            HashMap map2 = new HashMap();
            map2.put(PARAMS_BANNER_STATE, 0);
            WxPayFace.getInstance().updateWxpayfaceBannerState(map2, new IWxPayfaceCallback() {
                @Override
                public void response(Map info) throws RemoteException {
                    if (!isSuccessInfo(info)) {
                        return;
                    }
                    L.d(TAG, "response | showbanner");
                }
            });
        } else if (i == R.id.removebanner) {
            HashMap map3 = new HashMap();
            map3.put(PARAMS_BANNER_STATE, 1);
            WxPayFace.getInstance().updateWxpayfaceBannerState(map3, new IWxPayfaceCallback() {
                @Override
                public void response(Map info) throws RemoteException {
                    if (!isSuccessInfo(info)) {
                        return;
                    }
                    L.d(TAG, "response | removebanner");
                }
            });
        } else if (i == R.id.exit_example) {
            finish();
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
                                showToast("Get Authinfo Success");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            L.e(TAG, "onResponse | getAuthInfo " + mAuthInfo);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void doFaceRecognize(boolean once) {
        HashMap params2 = new HashMap();
        if (once) {
            params2.put(PARAMS_FACE_AUTHTYPE, "FACEID-ONCE");
        } else {
            params2.put(PARAMS_FACE_AUTHTYPE, "FACEPAY");
        }
        params2.put(PARAMS_APPID, APP_ID);
        params2.put(PARAMS_MCH_ID, MCH_ID);
        params2.put(PARAMS_MCH_NAME, "科脉自助收银");
//                params2.put(PARAMS_MCH_ID,"12306");
//                params2.put(PARAMS_STORE_ID,"12345");
//                params2.put(PARAMS_SUB_APPID,"33333");
//                params2.put(PARAMS_SUB_MCH_ID,"44444");
        params2.put(PARAMS_OUT_TRADE_NO, "" + (System.currentTimeMillis() / 100000));
        params2.put(PARAMS_TOTAL_FEE, TOTAL_FEE);
        String phone2 = mMemberEdit.getText().toString();
        params2.put(PARAMS_TELEPHONE, phone2);
        params2.put(PARAMS_AUTHINFO, mAuthInfo);
        WxPayFace.getInstance().getWxpayfaceUserInfo(params2, new IWxPayfaceCallback() {
            @Override
            public void response(final Map info) throws RemoteException {
                if (mFaceCallback != null) {
                    mFaceCallback.post(new Runnable() {
                        @Override
                        public void run() {
                            mFaceCallback.setText("response | getWxpayfaceUserInfo " + info.toString());
                        }
                    });
                }
                L.d(TAG, "response | getWxpayfaceUserInfo " + info.toString());
            }
        });
    }

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
            new RuntimeException("调用返回非成功信息: " + msg).printStackTrace();
            return false;
        }
        L.d(TAG, "调用返回成功");
        return true;
    }

    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(IFSExampleActivityOld.this, text, Toast.LENGTH_LONG).show();
            }
        });
    }
}