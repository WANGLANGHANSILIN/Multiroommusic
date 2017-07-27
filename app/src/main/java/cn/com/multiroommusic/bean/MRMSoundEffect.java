package cn.com.multiroommusic.bean;

import cn.com.auxdio.protocol.bean.AuxSoundEffectEntity;

/**
 * Created by wang l on 2017/6/3.
 */

public class MRMSoundEffect extends AuxSoundEffectEntity {

    private boolean isCheck;
    public MRMSoundEffect(int soundID, String soundName) {
        super(soundID, soundName);
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
