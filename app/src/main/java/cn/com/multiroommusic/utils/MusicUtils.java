package cn.com.multiroommusic.utils;

import java.util.ArrayList;
import java.util.List;

import cn.com.auxdio.protocol.bean.AuxNetRadioEntity;
import cn.com.auxdio.protocol.bean.AuxSongEntity;
import cn.com.multiroommusic.bean.MRMRadioBean;
import cn.com.multiroommusic.bean.MRMSongBean;

/**
 * Created by wang l on 2017/6/3.
 */

public class MusicUtils {

    public static List<MRMSongBean> convertMRMSongList(List<AuxSongEntity> auxSongEntities){

        if (auxSongEntities == null)
            return null;
        List<MRMSongBean> songBeenList = new ArrayList<>();
        for (AuxSongEntity auxSongEntity : auxSongEntities) {
            songBeenList.add(new MRMSongBean(auxSongEntity));
        }
        AuxSongEntity currentAuxSongEntity = DataUitls.newInstance().getCurrentAuxSongEntity();
        if (currentAuxSongEntity != null){
            for (MRMSongBean mrmSongBean : songBeenList) {
                if (mrmSongBean.getSongName().equals(currentAuxSongEntity.getSongName()) && mrmSongBean.getContentID() == currentAuxSongEntity.getContentID())
                    mrmSongBean.setCheck(true);
            }
        }
        return songBeenList;
    }

    public static List<MRMRadioBean> convertMRMRadioList(List<AuxNetRadioEntity> auxNetRadioEntities){

        if (auxNetRadioEntities == null)
            return null;

        List<MRMRadioBean> radioBeanList = new ArrayList<>();
        for (AuxNetRadioEntity auxNetRadioEntity : auxNetRadioEntities) {
            radioBeanList.add(new MRMRadioBean(auxNetRadioEntity));
        }
        AuxNetRadioEntity currentAuxNetRadioEntity = DataUitls.newInstance().getCurrentAuxNetRadioEntity();
        if (currentAuxNetRadioEntity != null){
            for (MRMRadioBean mrmRadioBean : radioBeanList) {
                if (currentAuxNetRadioEntity.getRadioAddress().equals(mrmRadioBean.getRadioAddress()))
                    mrmRadioBean.setCheck(true);
            }
        }
        return radioBeanList;
    }
}
