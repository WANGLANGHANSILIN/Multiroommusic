package cn.com.multiroommusic.bean;

import cn.com.auxdio.protocol.bean.AuxNetRadioEntity;

/**
 * Created by wang l on 2017/5/27.
 * 电台类对象
 */

public class MRMRadioBean extends AuxNetRadioEntity {

    public MRMRadioBean(AuxNetRadioEntity auxNetRadioEntity) {
        this.setRadioAddress(auxNetRadioEntity.getRadioAddress());
        this.setRadioName(auxNetRadioEntity.getRadioName());
    }

    private boolean isCheck;

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
