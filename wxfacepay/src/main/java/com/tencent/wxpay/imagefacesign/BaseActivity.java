package com.tencent.wxpay.imagefacesign;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity{

    private static final String TAG = "BaseActivity";

//    public static final String APP_ID = "wx2b029c08a6232582";//公众号
//    public static final String MCH_ID = "1900007081";//商户号
//    public static final String SUB_MCH_ID = "1487696602";//子商户号(非服务商模式不填)
//    public static final String STORE_ID = "12345";//门店编号
//    public static final String TOTAL_FEE = "1";//订单金额(数字), 单位分. FACEPAY时必填

    public static String APP_ID = "wx176de7a3403494ed";//公众号
    public static String MCH_ID = "1534092231";//商户号
//    public static String MCH_ID = "1502527911";//商户号
    public static String SUB_MCH_ID = "";//子商户号(非服务商模式不填)
    public static String STORE_ID = "12345";//门店编号
    public static String TOTAL_FEE = "1";//订单金额(数字), 单位分. FACEPAY时必填
    public static String OUT_TRADE_NO = "11111111";//商户订单号。FACEPAY时必填

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
