package cn.com.multiroommusic.callback;

import cn.com.auxdio.protocol.bean.AuxRoomEntity;

/**
 * Created by wang l on 2017/5/22.
 */

public interface NotifyRoomStatusChangedCallBack {
    void onRoomStatusChanged(AuxRoomEntity roomBean);
}
