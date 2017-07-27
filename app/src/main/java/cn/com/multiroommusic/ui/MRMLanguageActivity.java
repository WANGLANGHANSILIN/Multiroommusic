package cn.com.multiroommusic.ui;

import android.content.Intent;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.com.auxdio.protocol.net.AuxUdpBroadcast;
import cn.com.auxdio.protocol.net.AuxUdpUnicast;
import cn.com.auxdlna.dmclib.AuxDMControlInstance;
import cn.com.multiroommusic.R;
import cn.com.multiroommusic.utils.LanguageUtils;
import cn.com.multiroommusic.utils.SPUtil;

import static cn.com.multiroommusic.utils.SPUtil.getSpUtil;

/**
 * Created by wang l on 2017/5/19.
 */

public class MRMLanguageActivity extends BaseCommonActivity{


    private List<LanguageBean> mLanguageBeenList;


    private int getSelectorPostion() {
        for (int i = 0; i < mLanguageBeenList.size(); i++) {
            if (mLanguageBeenList.get(i).isCheck())
                return i;
        }
        return 0;
    }

    @Override
    protected List<? extends Object> getData() {
        mLanguageBeenList = new ArrayList<>();
        String[] stringArray = getResources().getStringArray(R.array.setting_language);
        List<String> stringList = Arrays.asList(stringArray);
        for (String s : stringList) {
            mLanguageBeenList.add(new LanguageBean(s));
        }
        int pos = SPUtil.getSpUtil().getIntValue("positiom");
        mLanguageBeenList.get(pos).setCheck(true);
        return mLanguageBeenList;
    }

    @Override
    public void onItemClick(Object dataBean, int position) {
        LanguageBean languageBean = (LanguageBean) dataBean;
        if (languageBean.isCheck())
            languageBean.setCheck(false);
        else
            languageBean.setCheck(true);

        resetLanguageSeletor(position);

    }

    private void resetLanguageSeletor(int pos) {
        for (LanguageBean languageBean : mLanguageBeenList) {
            if (!languageBean.equals(mLanguageBeenList.get(pos)))
                languageBean.setCheck(false);
        }
    }

    @Override
    public void onClick(View v) {
        int selectorPostion = getSelectorPostion();
        if (selectorPostion == getSpUtil().getIntValue("positiom")){
            this.finish();
            return;
        }
        getSpUtil().putValue("positiom",selectorPostion);
        LanguageUtils.changedLanguage(this);

        AuxUdpUnicast.getInstance().stopWorking();
        AuxUdpBroadcast.getInstace().stopWorking();
        AuxDMControlInstance.newInstance().onStopWorking();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        // 杀掉进程
//        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);

    }

    private void initAuxdioSDK() {
        AuxUdpUnicast.getInstance().startWorking();
        AuxUdpBroadcast.getInstace().startWorking().searchDevice();
    }

    public static class LanguageBean{
        private String languageName;
        private boolean isCheck;

        public LanguageBean(String languageName) {
            this.languageName = languageName;
        }

        public String getLanguageName() {
            return languageName;
        }

        public void setLanguageName(String languageName) {
            this.languageName = languageName;
        }

        public boolean isCheck() {
            return isCheck;
        }

        public void setCheck(boolean check) {
            isCheck = check;
        }
    }

}
