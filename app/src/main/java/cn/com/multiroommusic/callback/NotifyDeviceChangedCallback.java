package cn.com.multiroommusic.callback;

import java.util.List;

import cn.com.multiroommusic.bean.MRMDeviceBean;
import cn.com.multiroommusic.bean.MRMRoomBean;

/**
 * Created by wang l on 2017/5/22.
 */

public interface NotifyDeviceChangedCallback {
    void onDeviceChanged(MRMDeviceBean mrmDeviceBean);
    void onDeviceRoomShowChanged(List<MRMRoomBean> roomBeanList);
}
