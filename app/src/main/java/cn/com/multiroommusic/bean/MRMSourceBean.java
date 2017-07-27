package cn.com.multiroommusic.bean;

import cn.com.auxdio.protocol.bean.AuxSourceEntity;

/**
 * Created by wang l on 2017/5/24.
 * 音源类对象
 */

public class MRMSourceBean extends AuxSourceEntity {
    private boolean isCheck;
    public MRMSourceBean(int sourceID, String sourceName) {
        super(sourceID, sourceName);
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
