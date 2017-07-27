package cn.com.multiroommusic.bean;

import cn.com.auxdio.protocol.bean.AuxDeviceEntity;

/**
 * Created by wang l on 2017/5/19.
 * 设备类对象
 */

public class MRMDeviceBean extends AuxDeviceEntity{
    private boolean isCheck;

    public MRMDeviceBean(String devIP, int devModel, int devID, String devName, String devMAC) {
        setDevIP(devIP);
        setDevModel(devModel);
        setDevID(devID);
        setDevName(devName);
        setDevMAC(devMAC);
    }

    public MRMDeviceBean(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
    public static MRMDeviceBean convert(AuxDeviceEntity auxDeviceEntity){
        return new MRMDeviceBean(auxDeviceEntity.getDevIP(),auxDeviceEntity.getDevModel(),auxDeviceEntity.getDevID(),auxDeviceEntity.getDevName()
        ,auxDeviceEntity.getDevMAC());
    }
}
