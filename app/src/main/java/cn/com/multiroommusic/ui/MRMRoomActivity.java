package cn.com.multiroommusic.ui;

import android.view.View;

import java.util.List;

import cn.com.auxdio.protocol.bean.AuxRoomEntity;
import cn.com.multiroommusic.R;
import cn.com.multiroommusic.bean.MRMRoomBean;
import cn.com.multiroommusic.callback.NotifyRoomStatusChangedCallBack;
import cn.com.multiroommusic.utils.DataUitls;
import cn.com.multiroommusic.utils.TT;

/**
 * Created by wang l on 2017/5/19.
 */

public class MRMRoomActivity extends BaseCommonActivity implements NotifyRoomStatusChangedCallBack {

    private List<MRMRoomBean> mRoomEntities;

    @Override
    protected List<MRMRoomBean> getData() {
        mRoomEntities = DataUitls.newInstance().getAuxRoomEntities();
        return mRoomEntities;
    }

    @Override
    public void onItemClick(Object dataBean, int position) {
        MRMRoomBean mrmRoomBean = (MRMRoomBean) dataBean;
        if (mrmRoomBean.isCheck())
            mrmRoomBean.setCheck(false);
        else
            mrmRoomBean.setCheck(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DataUitls.newInstance().setRoomStatusChangedCallBack(this);
        TT.showToast(this,getString(R.string.channel_rename));
    }

    @Override
    public void onClick(View v) {
        DataUitls.newInstance().getmDeviceUpdataCallback().onDeviceRoomShowChanged(DataUitls.newInstance().getRoomBeanOnLineList());
        this.finish();
    }

    @Override
    public void onRoomStatusChanged(AuxRoomEntity roomBean) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMrmCommonAdapter.setBaseDataBeenList(getData());
            }
        });
    }

}
