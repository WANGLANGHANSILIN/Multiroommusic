package cn.com.multiroommusic.bean;

import cn.com.auxdio.protocol.bean.AuxSongEntity;

/**
 * Created by wang l on 2017/5/26.
 * 歌曲类对象
 */

public class MRMSongBean extends AuxSongEntity {
    private boolean isCheck;

    public MRMSongBean(AuxSongEntity auxSongEntity) {
        this.setSongName(auxSongEntity.getSongName());
        this.setContentID(auxSongEntity.getContentID());
        this.setSongTag(auxSongEntity.getSongTag());
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
