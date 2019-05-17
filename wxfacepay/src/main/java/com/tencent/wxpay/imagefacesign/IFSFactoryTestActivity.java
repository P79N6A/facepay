package com.tencent.wxpay.imagefacesign;

import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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

public class IFSFactoryTestActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = "IFSFactoryTestActivity";

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
    private static final String PARAMS_FACTORY = "factory";

    private static final String PARAMS_REPORT_ITEM = "item";
    private static final String PARAMS_REPORT_ITEMVALUE = "item_value";

    private static final String PARAMS_REPORT_MCH_ID = "mch_id";
    private static final String PARAMS_REPORT_SUT_MCH_ID = "sub_mch_id";
    private static final String PARAMS_REPORT_OUT_TRADE_NO = "out_trade_no";
    private static final String PARAMS_BANNER_STATE = "banner_state";

    private TextView mOboTestTips;
    private Button mOboTest;
    private TextView mOboTestResult;
    private TextView mObnTestTips;
    private Button mObnTest;
    private Button mObnTestStop;
    private TextView mObnTestResult;
    private Button mExitTest;

    private String mRawData;
    private String mAuthInfo;
    private boolean mHasInited;

    private int mOboTestCount;
    private int mOboTestSuccessCount;

    private int mObnTestCount;
    private int mObnTestSuccessCount;

    private static final int TEST_TYPE_NONE = 0;
    private static final int TEST_TYPE_OBO = 1;
    private static final int TEST_TYPE_OBN = 2;
    private static final int TEST_TYPE_OBN_ONCE = 3;
    private int mTestType = TEST_TYPE_NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.d(TAG, "onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_factory_test);
        mOboTestTips = (TextView) findViewById(R.id.factory_test_obo_result);
        mOboTest = (Button) findViewById(R.id.factory_test_obo_start);
        mOboTestResult = (TextView) findViewById(R.id.factory_test_obo_result);
        mObnTestTips = (TextView) findViewById(R.id.factory_test_obn_result);
        mObnTest = (Button) findViewById(R.id.factory_test_obn_start);
        mObnTestStop = (Button) findViewById(R.id.factory_test_obn_stop);
        mObnTestResult = (TextView) findViewById(R.id.factory_test_obn_result);
        mExitTest = (Button) findViewById(R.id.factory_test_exit);
        mOboTest.setOnClickListener(this);
        mObnTest.setOnClickListener(this);
        mExitTest.setOnClickListener(this);
        mObnTestStop.setOnClickListener(this);
        initPayFace();
    }

    private void initPayFace() {
        WxPayFace.getInstance().initWxpayface(this, new IWxPayfaceCallback() {
            @Override
            public void response(Map info) throws RemoteException {
                if (!isSuccessInfo(info)) {
                    return;
                }
                mHasInited = true;
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (!mHasInited) {
            showToast("正在初始化，请稍后");
            initPayFace();
        }
        int i = view.getId();
        if (i == R.id.factory_test_obo_start) {
            L.d(TAG, "onClick | factory_test_start ");
            mTestType = TEST_TYPE_OBO;
            mOboTestCount++;
            getRawData();
            updateTestResult();
        } else if (i == R.id.factory_test_obn_start) {
            mTestType = TEST_TYPE_OBN;
            mObnTestCount++;
            getRawData();
            updateTestResult();
        } else if (i == R.id.factory_test_obn_stop) {
            mTestType = TEST_TYPE_NONE;
            stopFaceRecognize();
            updateTestResult();
        } else if (i == R.id.factory_test_exit) {
            finish();
        }
    }

    private void getRawData() {
        L.d(TAG, "enter | getRawData ");
        WxPayFace.getInstance().getWxpayfaceRawdata(new IWxPayfaceCallback() {
            @Override
            public void response(Map info) throws RemoteException {
                L.d(TAG, "response | getWxpayfaceRawdata");
                if (!isSuccessInfo(info)) {
                    return;
                }
                mRawData = info.get("rawdata").toString();
                if (mRawData == null) {
                    showToast("rawdata为空");
                }
                try {
                    getAuthInfo(mRawData);
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast("authinfo获取出错");
                }
            }
        });
    }

    private void getAuthInfo(String rawdata) throws IOException {
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
                            L.d(TAG, "onFailure | getAuthInfo " + e.toString());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try {
                                mAuthInfo = ReturnXMLParser.parseGetAuthInfoXML(response.body().byteStream());
                                if (mAuthInfo == null) {
                                    showToast("authinfo为空");
                                    return;
                                }
                                switch (mTestType) {
                                    case TEST_TYPE_OBO:
                                        doPay();
                                        break;
                                    case TEST_TYPE_OBN:
                                        doFaceRecognize(false);
                                        break;
                                    case TEST_TYPE_OBN_ONCE:
                                        doFaceRecognize(true);
                                        break;
                                    default:
                                        break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            L.d(TAG, "onResponse | getAuthInfo " + mAuthInfo);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private boolean doPay() {
        L.d(TAG, "enter | doPay ");
        HashMap params = new HashMap();
        params.put(PARAMS_FACE_AUTHTYPE, "FACEPAY");
//                params.put(PARAMS_FACE_AUTHTYPE,"FACEID");
        params.put(PARAMS_APPID, APP_ID);
        params.put(PARAMS_MCH_ID, MCH_ID);
        params.put(PARAMS_STORE_ID, STORE_ID);
        params.put(PARAMS_OUT_TRADE_NO, "" + (System.currentTimeMillis() / 100000));
        params.put(PARAMS_TOTAL_FEE, TOTAL_FEE);
        params.put(PARAMS_TELEPHONE, "");
        params.put(PARAMS_AUTHINFO, mAuthInfo);
        params.put(PARAMS_FACTORY, true);

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
                            mOboTestSuccessCount++;
                            updateTestResult();
                            showToast("[1:1]流程测试成功，请稍后点击知道了回到测试页");
                        }
                    }
                });
            }
        });
        return true;
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
        String phone2 = "";
        params2.put(PARAMS_TELEPHONE, phone2);
        params2.put(PARAMS_AUTHINFO, mAuthInfo);
        params2.put(PARAMS_FACTORY, true);
        WxPayFace.getInstance().getWxpayfaceUserInfo(params2, new IWxPayfaceCallback() {
            @Override
            public void response(Map info) throws RemoteException {
                isSuccessInfo(info);
                L.d(TAG, "response | getWxpayfaceUserInfo " + info.toString());
                String openid = (String) info.get("openid");
                if (!TextUtils.isEmpty(openid)) {
                    mObnTestSuccessCount++;
                    updateTestResult();
                    showToast("[1:N]测试成功，可重新测试，或点击停止[1:N]测试");
                }
            }
        });
    }

    private void stopFaceRecognize() {
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
    }

    private boolean isSuccessInfo(Map info) {
        if (info == null) {
            showToast("调用返回为空, 请查看日志");
            new RuntimeException("调用返回为空").printStackTrace();
            return false;
        }
        String code = (String) info.get(RETURN_CODE);
        String msg = (String) info.get(RETURN_MSG);
        L.d(TAG, "response info :: " + code + " | " + msg);
        if (code == null || !code.equals(WxfacePayCommonCode.VAL_RSP_PARAMS_SUCCESS)) {
            showToast("调用返回非成功信息, 请查看日志");
            new RuntimeException("调用返回非成功信息: " + msg).printStackTrace();
            return false;
        }
        L.d(TAG, "调用返回成功");
        return true;
    }

    private void updateTestResult() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mOboTestCount > 0) mOboTestResult.setText("测试结果：\n测试次数：" + mOboTestCount + "\n成功次数：" + mOboTestSuccessCount);
                if (mObnTestCount > 0) mObnTestResult.setText("测试结果：\n测试次数：" + mObnTestCount + "\n成功次数：" + mObnTestSuccessCount);
            }
        });
    }

    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(IFSFactoryTestActivity.this, text, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.d(TAG, "onDestroy");
        releasePayFace();
    }

    private void releasePayFace() {
        WxPayFace.getInstance().releaseWxpayface(this);
    }
}