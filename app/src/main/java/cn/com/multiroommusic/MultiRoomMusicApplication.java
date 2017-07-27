package cn.com.multiroommusic;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;

import java.util.List;
import java.util.Map;

import cn.com.auxdio.protocol.bean.AuxDeviceEntity;
import cn.com.auxdio.protocol.interfaces.AuxSreachDeviceListener;
import cn.com.auxdio.protocol.net.AuxUdpBroadcast;
import cn.com.auxdio.protocol.net.AuxUdpUnicast;
import cn.com.auxdlna.dmclib.AuxDMControlInstance;
import cn.com.multiroommusic.utils.DataUitls;
import cn.com.multiroommusic.utils.LanguageUtils;
import cn.com.multiroommusic.utils.SPUtil;

/**
 * Created by wang l on 2017/6/9.
 */

public class MultiRoomMusicApplication extends Application implements AuxSreachDeviceListener {
    private static final String TAG = MultiRoomMusicApplication.class.getSimpleName();
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        mContext = this;

        initAuxdioSDK();
        SPUtil.getSpUtil().onCreate(this);
        LanguageUtils.changedLanguage(this);
        Log.i(TAG, "onCreate: ");
    }

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onSreachDevice(Map<Integer, List<AuxDeviceEntity>> map) {
        Log.i(TAG, "onSreachDevice: "+map.size());
        DataUitls.newInstance().setDeviceBeListMap(map);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i(TAG, "onTerminate: ");
        onDestorySDK();
    }

    public void initAuxdioSDK() {
        AuxUdpUnicast.getInstance().startWorking();
        AuxUdpBroadcast.getInstace().startWorking().searchDevice(this);
    }

    public void onDestorySDK(){
        AuxUdpUnicast.getInstance().stopWorking();
        AuxUdpBroadcast.getInstace().stopWorking();
        AuxDMControlInstance.newInstance().onStopWorking();
    }
}
