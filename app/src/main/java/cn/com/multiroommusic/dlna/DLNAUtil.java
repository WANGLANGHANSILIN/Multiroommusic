package cn.com.multiroommusic.dlna;

import org.fourthline.cling.model.meta.Device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.auxdlna.dmclib.AuxDMControlInstance;
import cn.com.auxdlna.dmclib.callback.AuxQueryDeviceCallback;
import cn.com.multiroommusic.MultiRoomMusicApplication;
import cn.com.multiroommusic.utils.LogUtils;

/**
 * Created by wang l on 2017/6/12.
 */

public class DLNAUtil implements AuxQueryDeviceCallback {

    private Map<String,List<Device>> mDMSDeviceMap,mDMRDeviceMap;
    private DLNAUtil() {
        mDMSDeviceMap = new HashMap<>();
        mDMRDeviceMap = new HashMap<>();
    }

    public void initDLNA(){
        AuxDMControlInstance.newInstance().setDLNAContext(MultiRoomMusicApplication.getContext()).onStartWorking().searchDevice(this);
    }
    public static final DLNAUtil newInstance(){
        return DLNAInstance.DLNA_UTIL;
    }

    static class DLNAInstance{
        public static final DLNAUtil DLNA_UTIL = new DLNAUtil();
    }

    @Override
    public void onlineDmrDevicce(String s, Device device) {
        LogUtils.i("onlineDmrDevicce","ip:"+s+" , "+device.getDetails().getFriendlyName());
        if (mDMRDeviceMap != null){
            List<Device> deviceList = mDMRDeviceMap.get(s);
            if (deviceList == null)
                deviceList = new ArrayList<>();
            deviceList.add(device);
            mDMRDeviceMap.put(s,deviceList);
        }
    }

    @Override
    public void onlineDmsDevicce(String s, Device device) {
        LogUtils.i("onlineDmsDevicce","ip:"+s+" , "+device.getDetails().getFriendlyName());
        if (mDMSDeviceMap != null){
            List<Device> deviceList = mDMSDeviceMap.get(s);
            if (deviceList == null)
                deviceList = new ArrayList<>();
            deviceList.add(device);
            mDMSDeviceMap.put(s,deviceList);
        }
    }

    @Override
    public void onDropDmrDevicce(String s, Device device) {
        mDMRDeviceMap.remove(s);
    }

    @Override
    public void onDropDmsDevicce(String s, Device device) {
        mDMSDeviceMap.remove(s);
    }

    public Map<String, List<Device>> getDMSDeviceMap() {
        return mDMSDeviceMap;
    }

    public void setDMSDeviceMap(Map<String, List<Device>> DMSDeviceMap) {
        mDMSDeviceMap = DMSDeviceMap;
    }

    public Map<String, List<Device>> getDMRDeviceMap() {
        return mDMRDeviceMap;
    }

    public void setDMRDeviceMap(Map<String, List<Device>> DMRDeviceMap) {
        mDMRDeviceMap = DMRDeviceMap;
    }
}
