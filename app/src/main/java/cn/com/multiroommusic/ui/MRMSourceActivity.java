package cn.com.multiroommusic.ui;

import android.view.View;

import java.util.List;

import cn.com.auxdio.protocol.net.AuxUdpUnicast;
import cn.com.multiroommusic.utils.DataUitls;
import cn.com.multiroommusic.utils.TT;

/**
 * Created by wang l on 2017/5/19.
 */

public class MRMSourceActivity extends BaseCommonActivity{


    @Override
    protected List<? extends Object> getData() {
        return DataUitls.newInstance().getControlDeviceSourceList(AuxUdpUnicast.getInstance().getControlDeviceEntity().getDevIP());
    }

    @Override
    public void onItemClick(Object dataBean, int position) {
        TT.showToast(this,dataBean.toString());
    }

    @Override
    public void onClick(View v) {
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
