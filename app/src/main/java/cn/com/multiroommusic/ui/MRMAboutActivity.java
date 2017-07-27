package cn.com.multiroommusic.ui;

import android.content.pm.PackageManager;
import android.view.View;
import android.widget.TextView;

import cn.com.multiroommusic.R;
import cn.com.multiroommusic.utils.AppUpDateUtil;

/**
 * Created by wang l on 2017/5/19.
 */

public class MRMAboutActivity extends BaseMRMActivity {
    @Override
    public int getTitleViewlayoutID() {

        // APP名称与版本
        TextView appNameTextView = (TextView)findViewById(R.id.tv_appname_about_activity);
        TextView versionTextView = (TextView)findViewById(R.id.tv_appversion_about_activity);

        View viewById = findViewById(R.id.tv_checkupgrad_about_activity);
        viewById.setBackgroundResource(R.drawable.iv_bg_selector);
        viewById.setOnClickListener(this);
        appNameTextView.setText(R.string.app_name);
        try {
            versionTextView.setText(AppUpDateUtil.getAppCurrentVersionName(this));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // 网站
        TextView urlTextView = (TextView)findViewById(R.id.tv_url_about_activity);
        urlTextView.setText("http://www.auxdio.com.cn/");

        // 版权
        TextView crTextView = (TextView)findViewById(R.id.tv_copyright_about_activity);
        crTextView.setText("Copyright (c) 2017 \nAUXDIO (China) Audio Co.,Ltd.");
        return R.id.include_layout_about;
    }

    @Override
    public int initRootViewLayoutID() {
        return R.layout.activity_settings_about;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_checkupgrad_about_activity)
            AppUpDateUtil.checkAppUpgrading(this,getSupportFragmentManager());
        else
            this.finish();
    }

}
