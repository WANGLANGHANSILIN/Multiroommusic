package cn.com.multiroommusic.fragments;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.com.auxdio.protocol.net.AuxUdpUnicast;
import cn.com.multiroommusic.R;
import cn.com.multiroommusic.adapters.SettingFragmentAdapter;
import cn.com.multiroommusic.bean.MRMSettingBean;
import cn.com.multiroommusic.callback.NotifySettingFragmentCallBack;
import cn.com.multiroommusic.callback.OnItemClickEventCallback;
import cn.com.multiroommusic.ui.MRMAboutActivity;
import cn.com.multiroommusic.ui.MRMDeviceActivity;
import cn.com.multiroommusic.ui.MRMInquiryActivity;
import cn.com.multiroommusic.ui.MRMLanguageActivity;
import cn.com.multiroommusic.ui.MRMNetModelBindActivity;
import cn.com.multiroommusic.ui.MRMNetRadioActivity;
import cn.com.multiroommusic.ui.MRMRoomActivity;
import cn.com.multiroommusic.ui.MRMSourceActivity;
import cn.com.multiroommusic.utils.DataUitls;


public class SettingsTabFragment extends BaseFragment implements NotifySettingFragmentCallBack, OnItemClickEventCallback {

    private RecyclerView mSettingRecycleView;
    private SettingFragmentAdapter mAdapter;

    @Override
    protected View initView() {
        View inflate = View.inflate(mContext, R.layout.fragment_settings, null);
        mSettingRecycleView = (RecyclerView)inflate.findViewById(R.id.rc_frag_setting);
        return inflate;
    }

    @Override
    protected void initData() {
        super.initData();
        mTitleTag =getString(R.string.setting_list);
        DataUitls.newInstance().getTitleChangedCallBack().onTitleChanged(getString(R.string.setting_list));
        DataUitls.newInstance().setNotifySettingFragmentCallBack(this);

        mAdapter = new SettingFragmentAdapter(mContext, getUpDateList(),android.R.layout.simple_list_item_1);
        mAdapter.setOnItemClickEventCallback(this);
        mSettingRecycleView.setLayoutManager(new LinearLayoutManager(mContext));
        mSettingRecycleView.setAdapter(mAdapter);
    }

    private List<MRMSettingBean> getUpDateList() {
        int devModel;
        String[] stringArray = new String[0];
        List<MRMSettingBean> settingBeenList = new ArrayList<>();
        if (AuxUdpUnicast.getInstance().getControlDeviceEntity() != null){
            devModel = AuxUdpUnicast.getInstance().getControlDeviceEntity().getDevModel();
            if (devModel == 6 || devModel == 5){
                stringArray = getResources().getStringArray(R.array.am8328_setting_name);
                for (int i = 0; i < stringArray.length; i++) {
                    settingBeenList.add(new MRMSettingBean(getAM_8328_ActivityList().get(i),stringArray[i]));
                }
                return settingBeenList;
            }
        }
        stringArray = getResources().getStringArray(R.array.dm838_setting_name);
        for (int i = 0; i < stringArray.length; i++) {
            settingBeenList.add(new MRMSettingBean(getDM_836_ActivityList().get(i),stringArray[i]));
        }
        return settingBeenList;
    }

    private List<Activity> getDM_836_ActivityList() {
        List<Activity> activities = new ArrayList<>();
        activities.add(new MRMDeviceActivity());
        activities.add(new MRMRoomActivity());
//        activities.add(new MRMSourceActivity());
        activities.add(new MRMInquiryActivity());
        activities.add(new MRMLanguageActivity());
        activities.add(new MRMAboutActivity());
        return activities;
    }

    private List<Activity> getAM_8328_ActivityList() {
        List<Activity> dm_836_activityList = getDM_836_ActivityList();
        dm_836_activityList.add(2,new MRMSourceActivity());
        dm_836_activityList.add(3,new MRMNetRadioActivity());
        dm_836_activityList.add(4,new MRMNetModelBindActivity());
        return dm_836_activityList;
    }

    @Override
    public void onNotifySetting() {
        if (mAdapter != null)
            mAdapter.setBaseDataBeenList(getUpDateList());
    }

    @Override
    public void onItemClick(Object dataBean, int position) {
        MRMSettingBean MRMSettingBean = (MRMSettingBean) dataBean;
        Intent[] intents = new Intent[mAdapter.getBaseDataBeenList().size()];
        for (int i = 0; i < mAdapter.getBaseDataBeenList().size(); i++) {
            intents[i] = new Intent(getContext(), MRMSettingBean.getSettingItem().getClass());
            intents[i].putExtra("activityName", MRMSettingBean.getSettingName());
        }
        this.getActivity().startActivity(intents[position]);
    }
}
