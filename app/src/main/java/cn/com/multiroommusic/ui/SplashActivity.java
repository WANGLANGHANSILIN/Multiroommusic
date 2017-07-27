package cn.com.multiroommusic.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import cn.com.multiroommusic.R;
import cn.com.multiroommusic.net.CheckUpdateThread;
import cn.com.multiroommusic.utils.AppUpDateUtil;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new CheckUpdateThread(this).start();

        try {
            String appCurrentVersionName = AppUpDateUtil.getAppCurrentVersionName(this);
            ((TextView)findViewById(R.id.tv_splash_version)).setText("V"+ appCurrentVersionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        //延时跳转到主页面，splash用来做引导页
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out); //要在主线程中执行
            }
        },1000*2);
    }
}
