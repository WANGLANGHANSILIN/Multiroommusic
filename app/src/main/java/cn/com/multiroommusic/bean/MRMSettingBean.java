package cn.com.multiroommusic.bean;

import android.app.Activity;

/**
 * Created by wang l on 2017/5/19.
 * 设置item类对象
 */

public class MRMSettingBean {
    private Activity mSettingItem;
    private String mSettingName;

    public MRMSettingBean(Activity settingItem, String mSettingName) {
        mSettingItem = settingItem;
        this.mSettingName = mSettingName;
    }

    public Activity getSettingItem() {
        return mSettingItem;
    }

    public void setSettingItem(Activity settingItem) {
        mSettingItem = settingItem;
    }

    public String getSettingName() {
        return mSettingName;
    }

    public void setSettingName(String settingName) {
        this.mSettingName = settingName;
    }
}
