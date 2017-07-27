package cn.com.multiroommusic.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.com.auxdio.protocol.bean.AuxNetModelEntity;
import cn.com.auxdio.protocol.bean.AuxNetRadioEntity;
import cn.com.auxdio.protocol.bean.AuxRoomEntity;
import cn.com.multiroommusic.bean.MRMRadioBean;
import cn.com.multiroommusic.bean.MRMRoomBean;

/**
 * Created by wang l on 2017/5/23.
 */

public class RoomUtil {
    public static List<AuxRoomEntity> convertList(AuxRoomEntity[] roomEntities){
        List<AuxRoomEntity> roomEntityList = new ArrayList<>();
        if (roomEntities == null || roomEntities.length == 0)
            return roomEntityList;
        for (AuxRoomEntity roomEntity : roomEntities) {
            roomEntityList.add(roomEntity);
        }
        return roomEntityList;
    }

    public static AuxRoomEntity[] convertArray(List<?extends AuxRoomEntity> roomEntities){
        AuxRoomEntity[] roomEntityList = new AuxRoomEntity[roomEntities.size()];
        if (roomEntities == null || roomEntities.size() == 0)
            return roomEntityList;

        for (int i = 0; i < roomEntities.size(); i++) {
            roomEntityList[i] = roomEntities.get(i);
        }
        return roomEntityList;
    }

    public static List<MRMRoomBean> convertMRMRoomBeanList(List<AuxRoomEntity> roomEntities){
        List<MRMRoomBean> roomEntityList = new ArrayList<>();
        if (roomEntities == null || roomEntities.size() == 0)
            return roomEntityList;
        for (AuxRoomEntity roomEntity : roomEntities) {
            roomEntityList.add(MRMRoomBean.convert(roomEntity));
        }
        return roomEntityList;
    }

    public static MRMRoomBean roombyModelID(int modelID){
        List<MRMRoomBean> auxRoomEntities = DataUitls.newInstance().getAuxRoomEntities();
        Map<Integer, AuxNetModelEntity> netModelEntityMap = DataUitls.newInstance().getAuxNetModelEntityMap();
        Iterator<Map.Entry<Integer, AuxNetModelEntity>> iterator = netModelEntityMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<Integer, AuxNetModelEntity> next = iterator.next();
            for (MRMRoomBean auxRoomEntity : auxRoomEntities) {
                if (next.getKey() == auxRoomEntity.getRoomID() && next.getValue().getModelID() == modelID){
                    return auxRoomEntity;
                }
            }
        }
        return null;
    }

    public static AuxNetModelEntity findNetModelByroomID(int roomID){
        Map<Integer, AuxNetModelEntity> netModelEntityMap = DataUitls.newInstance().getAuxNetModelEntityMap();
        if (netModelEntityMap == null)
            return null;
        Iterator<Map.Entry<Integer, AuxNetModelEntity>> iterator = netModelEntityMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<Integer, AuxNetModelEntity> next = iterator.next();
            if (next.getKey() == roomID){
                return next.getValue();
            }
        }
        return null;
    }

    public static List<MRMRadioBean> getRadioList(List<AuxNetRadioEntity> auxNetRadioEntities){
        List<MRMRadioBean> mrmRadioBeen = new ArrayList<>();
        for (AuxNetRadioEntity auxNetRadioEntity : auxNetRadioEntities) {
            mrmRadioBeen.add(new MRMRadioBean(auxNetRadioEntity));
        }
        return mrmRadioBeen;
    }
}
