package cn.com.multiroommusic.dlna.utils;

import org.fourthline.cling.model.meta.Device;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import cn.com.auxdio.protocol.bean.AuxNetModelEntity;
import cn.com.auxdio.protocol.net.AuxUdpUnicast;
import cn.com.auxdlna.dmslib.utils.IPUtil;
import cn.com.multiroommusic.MultiRoomMusicApplication;
import cn.com.multiroommusic.dlna.DLNAUtil;

/**
 * Created by wang l on 2017/6/12.
 *
 */

public class DeviceUtils {
    public static Device getLocalServiceDevice(){
        try {
            Map<String, List<Device>> dmsDeviceMap = DLNAUtil.newInstance().getDMSDeviceMap();
            if (dmsDeviceMap == null)
                return null;

            String hostAddress = IPUtil.getLocalIpAddress(MultiRoomMusicApplication.getContext()).getHostAddress();
            List<Device> deviceList = dmsDeviceMap.get(hostAddress);
            if (deviceList != null && deviceList.size() > 0)
            return deviceList.get(0);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Device getPlayDeviceByRoomId(){
        if (AuxUdpUnicast.getInstance().getControlDeviceEntity() != null && AuxUdpUnicast.getInstance().getControlRoomEntities() != null && AuxUdpUnicast.getInstance().getControlRoomEntities().length > 0){
            return getPlayDevice(AuxUdpUnicast.getInstance().getControlRoomEntities()[0].getRoomIP());
        }
        return null;
    }

    public static Device getPlayDeviceByModelId(AuxNetModelEntity modelEntity){
        return getPlayDevice(modelEntity.getModelIP());
    }

    private static Device getPlayDevice(String hostAddress){
        Map<String, List<Device>> dmrDeviceMap = DLNAUtil.newInstance().getDMRDeviceMap();
        if (dmrDeviceMap == null)
            return null;
        List<Device> deviceList = dmrDeviceMap.get(hostAddress);
        if (deviceList != null && deviceList.size() > 0){
            return deviceList.get(0);
        }
        return null;
    }
}
