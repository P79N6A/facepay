package com.tencent.wxpay.imagefacesign;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import com.tencent.mmfacepay.R;
import com.tencent.wxpay.bll.FacePayBll;

public class IFSMainActivity extends BaseActivity implements View.OnClickListener {

    Button mExample;
    Button mFactoryTest;
    Button mExit;
    private LinearLayout ll;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mExample = (Button) findViewById(R.id.example);
        mExample.setOnClickListener(this);
        mFactoryTest = (Button) findViewById(R.id.factory_test);
        mFactoryTest.setOnClickListener(this);
        mExit = (Button) findViewById(R.id.exit);
        mExit.setOnClickListener(this);

        ll = findViewById(R.id.ll);

        FacePayBll.getInstance().init(IFSMainActivity.this
                , "face_pay", "123456", "01");
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        int i = view.getId();
        if (i == R.id.example) {
            intent = new Intent(this, IFSExampleActivityOld.class);
            startActivity(intent);
        } else if (i == R.id.factory_test) {
            intent = new Intent(this, IFSFactoryTestActivity.class);
            startActivity(intent);
        } else if (i == R.id.exit) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        FacePayBll.getInstance().releaseWxpayface(IFSMainActivity.this);
        super.onDestroy();
    }
}