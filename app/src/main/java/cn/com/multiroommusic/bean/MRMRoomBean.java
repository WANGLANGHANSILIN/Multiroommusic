package cn.com.multiroommusic.bean;

import cn.com.auxdio.protocol.bean.AuxRoomEntity;

/**
 * Created by wang l on 2017/5/19.
 * 房间类对象
 */

public class MRMRoomBean extends AuxRoomEntity{
    private boolean isCheck = true;
    private boolean isSelector;

    private MRMRoomBean(String roomName, int RoomID, String roomIP, int srcID, int volumeID, int oNOffState, int highPitch, int lowPitch,String srcName) {
        super(roomName, RoomID, roomIP, srcID, volumeID, oNOffState, highPitch, lowPitch,srcName);
    }

    public static MRMRoomBean convert(AuxRoomEntity auxRoomEntity){
        MRMRoomBean mrmRoomBean = new MRMRoomBean(auxRoomEntity.getRoomName(), auxRoomEntity.getRoomID(), auxRoomEntity.getRoomIP(), auxRoomEntity.getSrcID(),
                auxRoomEntity.getVolumeID(), auxRoomEntity.getoNOffState(), auxRoomEntity.getHighPitch(), auxRoomEntity.getLowPitch(),auxRoomEntity.getRoomSrcName());
        mrmRoomBean.setTimeOut(auxRoomEntity.getTimeOut());
        return mrmRoomBean;
    }

    public MRMRoomBean(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean isSelector() {
        return isSelector;
    }

    public void setSelector(boolean selector) {
        isSelector = selector;
    }
}
