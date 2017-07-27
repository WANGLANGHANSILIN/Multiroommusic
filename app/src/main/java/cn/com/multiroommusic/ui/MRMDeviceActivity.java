package cn.com.multiroommusic.ui;

import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.com.auxdio.protocol.bean.AuxDeviceEntity;
import cn.com.auxdio.protocol.net.AuxUdpUnicast;
import cn.com.multiroommusic.R;
import cn.com.multiroommusic.bean.MRMDeviceBean;
import cn.com.multiroommusic.callback.NotifyDeviceChangedCallback;
import cn.com.multiroommusic.utils.DataUitls;
import cn.com.multiroommusic.utils.LogUtils;
import cn.com.multiroommusic.utils.SPUtil;
import cn.com.multiroommusic.utils.TT;

/**
 * Created by wang l on 2017/5/19.
 */

public class MRMDeviceActivity extends BaseCommonActivity{
    private List<MRMDeviceBean> mDeviceBeanList;
    private Map<Integer, List<AuxDeviceEntity>> mDeviceBeListMap;
    private NotifyDeviceChangedCallback mDeviceUpdataCallback;

    private MRMDeviceBean getSelectorDevice() {
        for (MRMDeviceBean mrmDeviceBean : mDeviceBeanList) {
            if (mrmDeviceBean.isCheck())
                return mrmDeviceBean;
        }
        return null;
    }

    @Override
    protected List<? extends Object> getData() {
        mDeviceBeanList = new ArrayList<>();
        mDeviceUpdataCallback = DataUitls.newInstance().getmDeviceUpdataCallback();
        mDeviceBeListMap = DataUitls.newInstance().getAuxDeviceListMap();
        if (mDeviceBeListMap != null){
            for (Integer integer : mDeviceBeListMap.keySet()) {
                mDeviceBeanList.add(MRMDeviceBean.convert(mDeviceBeListMap.get(integer).get(0)));
            }
            for (MRMDeviceBean mrmDeviceBean : mDeviceBeanList) {
                AuxDeviceEntity controlDeviceEntity = AuxUdpUnicast.getInstance().getControlDeviceEntity();
                if (controlDeviceEntity == null)
                    return null;
                if (controlDeviceEntity.getDevModel() == (mrmDeviceBean.getDevModel())) {
                    mrmDeviceBean.setCheck(true);
                }
            }
        }
        return mDeviceBeanList;
    }

    private void resetDeviceState(int pos) {
        for (MRMDeviceBean mrmDeviceBean : mDeviceBeanList) {
            if (!mrmDeviceBean.equals(mDeviceBeanList.get(pos)))
                mrmDeviceBean.setCheck(false);
        }
        SPUtil.getSpUtil().putValue("deviceIndex",mDeviceBeanList.get(pos).getDevModel());
    }

    @Override
    public void onItemClick(Object baseDataBean, int position) {
        MRMDeviceBean mrmDeviceBean = (MRMDeviceBean) baseDataBean;
        if (mrmDeviceBean.isCheck())
            mrmDeviceBean.setCheck(false);
        else
            mrmDeviceBean.setCheck(true);
        resetDeviceState(position);
    }


    @Override
    public void onClick(View v) {
        if (mDeviceBeanList == null || mDeviceBeanList.size() == 0 || mMrmCommonAdapter.getItemCount() == 0) {
            this.finish();
            return;
        }
        if (getSelectorDevice() == null) {
            TT.showToast(this, getString(R.string.device_selector));
            return;
        }
        LogUtils.i("onClick","   "+(AuxUdpUnicast.getInstance().getControlDeviceEntity().toString()+"\n"+(getSelectorDevice().toString())));
        if (AuxUdpUnicast.getInstance().getControlDeviceEntity().toString().equals(getSelectorDevice().toString())) {
            this.finish();
            return;
        }
        AuxUdpUnicast.getInstance().setControlDeviceEntity(getSelectorDevice());
        for (Integer integer : mDeviceBeListMap.keySet()) {
            if (integer == getSelectorDevice().getDevModel()){
                LogUtils.i("onClick","   asds   "+getSelectorDevice().toString());
                mDeviceUpdataCallback.onDeviceChanged(getSelectorDevice());
                for (AuxDeviceEntity deviceEntity : mDeviceBeListMap.get(integer)) {
                    AuxUdpUnicast.getInstance().requestDeviceRoomList(deviceEntity.getDevIP(),AuxUdpUnicast.getInstance().getAuxRoomStateChangedListener());
                }
            }
        }

        this.finish();
    }
}
