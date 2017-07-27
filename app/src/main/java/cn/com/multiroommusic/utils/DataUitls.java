package cn.com.multiroommusic.utils;

import android.support.annotation.Nullable;

import org.fourthline.cling.model.meta.Device;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.com.auxdio.protocol.bean.AuxDeviceEntity;
import cn.com.auxdio.protocol.bean.AuxNetModelEntity;
import cn.com.auxdio.protocol.bean.AuxNetRadioEntity;
import cn.com.auxdio.protocol.bean.AuxNetRadioTypeEntity;
import cn.com.auxdio.protocol.bean.AuxPlayListEntity;
import cn.com.auxdio.protocol.bean.AuxRoomEntity;
import cn.com.auxdio.protocol.bean.AuxSongEntity;
import cn.com.auxdio.protocol.bean.AuxSoundEffectEntity;
import cn.com.auxdio.protocol.bean.AuxSourceEntity;
import cn.com.auxdio.protocol.net.AuxUdpUnicast;
import cn.com.auxdio.protocol.protocol.AuxConfig;
import cn.com.auxdlna.dmclib.AuxDMControlInstance;
import cn.com.auxdlna.dmclib.callback.AuxBaseActionCallback;
import cn.com.auxdlna.dmclib.callback.AuxQueryContainerCallback;
import cn.com.auxdlna.dmclib.util.FiletypeUtil;
import cn.com.multiroommusic.bean.MRMRoomBean;
import cn.com.multiroommusic.bean.MRMSoundEffect;
import cn.com.multiroommusic.bean.MRMSourceBean;
import cn.com.multiroommusic.callback.DownloadCompleteCallBack;
import cn.com.multiroommusic.callback.FragmentChangedCallBack;
import cn.com.multiroommusic.callback.NetModelChangedCallBack;
import cn.com.multiroommusic.callback.NotifyDeviceChangedCallback;
import cn.com.multiroommusic.callback.NotifyRoomStatusChangedCallBack;
import cn.com.multiroommusic.callback.NotifySettingFragmentCallBack;
import cn.com.multiroommusic.callback.OnItemClickEventCallback;
import cn.com.multiroommusic.callback.TitleChangedCallBack;
import cn.com.multiroommusic.dialog.ListDialog;
import cn.com.multiroommusic.dlna.utils.DeviceUtils;

/**
 * Created by wang l on 2017/5/22.
 */

public class DataUitls{
    private List<MRMRoomBean> mRoomBeanList;//当前在线房间列表
    private List<AuxNetRadioTypeEntity> mAuxNetRadioTypeEntities;//电台类型和电台列表
    private Map<Integer,List<AuxDeviceEntity>> mAuxDeviceListMap;//所有设备集合列表
    private Map<String,List<?extends AuxPlayListEntity>> mAuxPlayListMap;//当前所有设备的歌曲列表
    private Map<Integer,AuxNetModelEntity> mAuxNetModelEntityMap;//模块与分区绑定列表
    private Map<String,List<? extends AuxSourceEntity>> mAuxSourceListMap;//音源列表
    private List<AuxNetModelEntity> mAuxNetModelEntities;//网络模块列表
    private List<AuxSoundEffectEntity> mAuxSoundEffectEntities;
    private AuxSoundEffectEntity mCurrentAuxSoundEffectEntity;
    private AuxSourceEntity mCurrentAuxSourceEntity;
    private AuxSongEntity mCurrentAuxSongEntity;
    private AuxNetRadioEntity mCurrentAuxNetRadioEntity;

    private NotifyDeviceChangedCallback mDeviceChangedCallback;//切换设备回调接口
    private NotifyRoomStatusChangedCallBack mRoomStatusChangedCallBack;//房间状态改变回调接口
    private TitleChangedCallBack mTitleChangedCallBack;//Title改变回调接口
    private OnItemClickEventCallback mOnItemClickEventCallback;//RecyclerView 点击事件处理回调接口
    private DownloadCompleteCallBack mDownloadCompleteCallBack;//更新下载完成回调接口
    private FragmentChangedCallBack mFragmentChangedCallBack;//多房间控制询问接口
    private NotifySettingFragmentCallBack mNotifySettingFragmentCallBack;//切换设备回调接口通知设置界面
    private NetModelChangedCallBack mNetModelChangedCallBack;//网络模块改变回调接口
    private AuxQueryContainerCallback containerCallback;


    private ListDialog mSourceListDialog;
    private String appUpdateVersion;
    private int mModelBindType;

    private DataUitls() {
        mAuxDeviceListMap = new Hashtable<>();
        mAuxNetModelEntityMap = new Hashtable<>();
        mAuxNetModelEntities = new ArrayList<>();
        mAuxSourceListMap = new Hashtable<>();
        mAuxPlayListMap = new Hashtable<>();
        mRoomBeanList = new ArrayList<>();
        mAuxNetRadioTypeEntities = new ArrayList<>();
        mAuxSoundEffectEntities= new ArrayList<>();
    }
    private static DataUitls mDataUitls = new DataUitls();
    public static DataUitls newInstance(){
        return mDataUitls;
    }

    public NotifySettingFragmentCallBack getNotifySettingFragmentCallBack() {
        return mNotifySettingFragmentCallBack;
    }

    public void setNotifySettingFragmentCallBack(NotifySettingFragmentCallBack notifySettingFragmentCallBack) {
        mNotifySettingFragmentCallBack = notifySettingFragmentCallBack;
    }

    public FragmentChangedCallBack getFragmentChangedCallBack() {
        return mFragmentChangedCallBack;
    }

    public void setFragmentChangedCallBack(FragmentChangedCallBack fragmentChangedCallBack) {
        mFragmentChangedCallBack = fragmentChangedCallBack;
    }

    public DownloadCompleteCallBack getDownloadCompleteCallBack() {
        return mDownloadCompleteCallBack;
    }

    public void setDownloadCompleteCallBack(DownloadCompleteCallBack downloadCompleteCallBack) {
        mDownloadCompleteCallBack = downloadCompleteCallBack;
    }

    public OnItemClickEventCallback getEventOprationCallback() {
        return mOnItemClickEventCallback;
    }

    public void setEventOprationCallback(OnItemClickEventCallback onItemClickEventCallback) {
        mOnItemClickEventCallback = onItemClickEventCallback;
    }

    public TitleChangedCallBack getTitleChangedCallBack() {
        return mTitleChangedCallBack;
    }

    public void setTitleChangedCallBack(TitleChangedCallBack titleChangedCallBack) {
        mTitleChangedCallBack = titleChangedCallBack;
    }

    public NotifyDeviceChangedCallback getmDeviceUpdataCallback() {
        return mDeviceChangedCallback;
    }

    public void setmDeviceUpdataCallback(NotifyDeviceChangedCallback mDeviceUpdataCallback) {
        this.mDeviceChangedCallback = mDeviceUpdataCallback;
    }

    public NotifyRoomStatusChangedCallBack getRoomStatusChangedCallBack() {
        return mRoomStatusChangedCallBack;
    }

    public void setRoomStatusChangedCallBack(NotifyRoomStatusChangedCallBack roomStatusChangedCallBack) {
        mRoomStatusChangedCallBack = roomStatusChangedCallBack;
    }

    public AuxDeviceEntity getControlDevice(int model){
        if (mAuxDeviceListMap != null && mAuxDeviceListMap.size() > 0){
            List<AuxDeviceEntity> auxDeviceEntities = mAuxDeviceListMap.get(model);
            if (auxDeviceEntities != null && auxDeviceEntities.size() > 0)
                return auxDeviceEntities.get(0);
        }
        return null;
    }

    public void setDeviceBeListMap(Map<Integer, List<AuxDeviceEntity>> deviceBeListMap) {
        mAuxDeviceListMap = deviceBeListMap;
    }

    public Map<Integer, List<AuxDeviceEntity>> getAuxDeviceListMap() {
        return mAuxDeviceListMap;
    }

    public AuxDeviceEntity getFistDevice(){
        List<AuxDeviceEntity> mrmDeviceBeanList = getMRMDeviceBeenList();

        if (mrmDeviceBeanList == null || mrmDeviceBeanList.size() <= 0){
            return null;
        }
        return mrmDeviceBeanList.get(0);
    }

    @Nullable
    public List<AuxDeviceEntity> getMRMDeviceBeenList() {
        List<AuxDeviceEntity> mrmDeviceBeanList = new ArrayList<>();

        if (mAuxDeviceListMap == null)
            return null;

        Iterator<Map.Entry<Integer, List<AuxDeviceEntity>>> iterator = mAuxDeviceListMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<Integer, List<AuxDeviceEntity>> next = iterator.next();
            mrmDeviceBeanList.addAll(next.getValue());
        }
        return mrmDeviceBeanList;
    }

    public AuxDeviceEntity getEndDevice(){
        List<AuxDeviceEntity> mrmDeviceBeanList = getMRMDeviceBeenList();
        if (mrmDeviceBeanList == null) return null;
        return mrmDeviceBeanList.get(mrmDeviceBeanList.size()-1);
    }

    public List<MRMRoomBean> getAuxRoomEntities() {
        return mRoomBeanList;
    }

    public void setAuxRoomEntities(List<MRMRoomBean> auxRoomEntities) {
        mRoomBeanList = auxRoomEntities;
   }

    public List<MRMRoomBean> getRoomBeanOnLineList() {
        List<MRMRoomBean> mrmRoomBeanList = new ArrayList<>();
        for (MRMRoomBean mrmRoomBean : mRoomBeanList) {
            if (mrmRoomBean.isCheck())
                mrmRoomBeanList.add(mrmRoomBean);
        }
        return mrmRoomBeanList;
    }

    public Map<String, List<? extends AuxPlayListEntity>> getAuxPlayListMap() {
        return mAuxPlayListMap;
    }

    public void setAuxPlayListMap(Map<String, List<? extends AuxPlayListEntity>> auxPlayListMap) {
        mAuxPlayListMap = auxPlayListMap;
    }

    public List<? extends AuxPlayListEntity> getCurrentRoomPlayList(){
        if (mAuxPlayListMap == null)
            return null;
        AuxRoomEntity[] controlRoomEntities = AuxUdpUnicast.getInstance().getControlRoomEntities();
        if (controlRoomEntities == null || controlRoomEntities.length < 1)
            return null;

        int devModel = AuxUdpUnicast.getInstance().getControlDeviceEntity().getDevModel();
        if (devModel == AuxConfig.DeciveModel.DEVICE_DM838 || devModel == AuxConfig.DeciveModel.DEVICE_DM836){
            if (controlRoomEntities.length > 1)
                return null;
        }
        return mAuxPlayListMap.get(controlRoomEntities[0].getRoomIP());
    }

    public String getAppUpdateVersion() {
        return appUpdateVersion;
    }

    public void setAppUpdateVersion(String appUpdateVersion) {
        this.appUpdateVersion = appUpdateVersion;
    }

    public ListDialog getSourceListDialog() {
        return mSourceListDialog;
    }

    public void setSourceListDialog(ListDialog sourceListDialog) {
        mSourceListDialog = sourceListDialog;
    }

    //获取当前控制的房间名称
    public String getControlRoomName(){
        AuxRoomEntity[] controlRoomEntities = AuxUdpUnicast.getInstance().getControlRoomEntities();
        if (controlRoomEntities == null || controlRoomEntities.length < 1)
            return null;
        StringWriter stringWriter = new StringWriter();
        for (AuxRoomEntity controlRoomEntity : controlRoomEntities) {
            stringWriter.append(controlRoomEntity.getRoomName()+",");
        }
        String toString = stringWriter.toString();
        return toString.substring(0,toString.length()-1);
    }

    public List<AuxNetRadioTypeEntity> getAuxNetRadioTypeEntities() {
        return mAuxNetRadioTypeEntities;
    }

    public void setAuxNetRadioTypeEntities(List<AuxNetRadioTypeEntity> auxNetRadioTypeEntities) {
        mAuxNetRadioTypeEntities = auxNetRadioTypeEntities;
    }

    public Map<Integer, AuxNetModelEntity> getAuxNetModelEntityMap() {
        return mAuxNetModelEntityMap;
    }

    public int getBindRoomModelID(int roomID){
        if (mAuxNetModelEntityMap != null){
            Iterator<Map.Entry<Integer, AuxNetModelEntity>> iterator = mAuxNetModelEntityMap.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<Integer, AuxNetModelEntity> next = iterator.next();
                if (next.getKey() == roomID)
                    return next.getValue().getModelID();
            }
        }
        return 0;
    }

    public void setAuxNetModelEntityMap(Map<Integer, AuxNetModelEntity> auxNetModelEntityMap) {
        mAuxNetModelEntityMap = auxNetModelEntityMap;
        if (mNetModelChangedCallBack != null)
            mNetModelChangedCallBack.onNetModelChanged();
    }

    public List<AuxNetModelEntity> getAuxNetModelEntities() {
        return mAuxNetModelEntities;
    }

    public void setAuxNetModelEntities(List<AuxNetModelEntity> auxNetModelEntities) {
        mAuxNetModelEntities = auxNetModelEntities;
    }

    public NetModelChangedCallBack getNetModelChangedCallBack() {
        return mNetModelChangedCallBack;
    }

    public void setNetModelChangedCallBack(NetModelChangedCallBack netModelChangedCallBack) {
        mNetModelChangedCallBack = netModelChangedCallBack;
    }

    public int getModelBindType() {
        return mModelBindType;
    }

    public void setModelBindType(int modelBindType) {
        mModelBindType = modelBindType;
    }

    public Map<String, List<? extends AuxSourceEntity>> getAuxSourceListMap() {
        return mAuxSourceListMap;
    }

    public void setAuxSourceListMap(Map<String, List<? extends AuxSourceEntity>> auxSourceListMap) {
        mAuxSourceListMap = auxSourceListMap;
    }

    public List<MRMSourceBean> getCurrentRoomSourceList(){
        AuxDeviceEntity controlDeviceEntity = AuxUdpUnicast.getInstance().getControlDeviceEntity();
        LogUtils.i("getCurrentRoomSourceList----mAuxSourceListMap = null  "+(mAuxSourceListMap == null)+",controlDeviceEntity = null "+(controlDeviceEntity == null));
        if (mAuxSourceListMap == null ||  controlDeviceEntity == null)
            return null;
        AuxRoomEntity[] controlRoomEntities = AuxUdpUnicast.getInstance().getControlRoomEntities();
        LogUtils.i("getCurrentRoomSourceList----controlRoomEntities = null "+(controlRoomEntities == null));
        if (controlRoomEntities == null || controlRoomEntities.length < 1)
            return null;

        String devIP = "";
        if (controlDeviceEntity.getDevModel() == AuxConfig.DeciveModel.DEVICE_DM838 ||controlDeviceEntity.getDevModel() == AuxConfig.DeciveModel.DEVICE_DM836){
            devIP = controlRoomEntities[0].getRoomIP();
        }else
            devIP = controlDeviceEntity.getDevIP();
        LogUtils.i("getCurrentRoomSourceList----"+controlDeviceEntity.toString()+","+controlRoomEntities[0].toString());
        return getControlDeviceSourceList(devIP);
    }

    @Nullable
    public List<MRMSourceBean> getControlDeviceSourceList(String devIP) {
        if (mAuxDeviceListMap == null)
            return null;
        List<? extends AuxSourceEntity> auxSourceEntities = mAuxSourceListMap.get(devIP);
        if (auxSourceEntities == null || auxSourceEntities.size() < 1)
            return null;
        List<MRMSourceBean> sourceBeanList = new ArrayList<>();
        for (AuxSourceEntity auxSourceEntity : auxSourceEntities) {
            LogUtils.i("getCurrentRoomSourceList----"+auxSourceEntity.getSourceName());
            sourceBeanList.add(new MRMSourceBean(auxSourceEntity.getSourceID(),auxSourceEntity.getSourceName()));
        }
        if (mCurrentAuxSourceEntity == null)
            return sourceBeanList;

        for (MRMSourceBean mrmSourceBean : sourceBeanList) {
            if (mrmSourceBean.getSourceID() == mCurrentAuxSourceEntity.getSourceID())
                mrmSourceBean.setCheck(true);
        }
        return sourceBeanList;
    }

    public List<MRMSoundEffect> getAuxSoundEffectEntities() {
        if (mAuxSoundEffectEntities != null){
            List<MRMSoundEffect> soundEffectList = new ArrayList<>();
            for (AuxSoundEffectEntity auxSoundEffectEntity : mAuxSoundEffectEntities) {
                soundEffectList.add(new MRMSoundEffect(auxSoundEffectEntity.getSoundID(),auxSoundEffectEntity.getSoundName()));
            }
            if (getCurrentAuxSoundEffectEntity() == null)
                return soundEffectList;
            for (MRMSoundEffect mrmSoundEffect : soundEffectList) {
                if (mrmSoundEffect.getSoundID() == getCurrentAuxSoundEffectEntity().getSoundID())
                    mrmSoundEffect.setCheck(true);
            }
            return soundEffectList;
        }
        return null;
    }

    public void setAuxSoundEffectEntities(List<AuxSoundEffectEntity> auxSoundEffectEntities) {
        mAuxSoundEffectEntities = auxSoundEffectEntities;
    }

    public AuxSoundEffectEntity getCurrentAuxSoundEffectEntity() {
        return mCurrentAuxSoundEffectEntity;
    }

    public void setCurrentAuxSoundEffectEntity(AuxSoundEffectEntity currentAuxSoundEffectEntity) {
        mCurrentAuxSoundEffectEntity = currentAuxSoundEffectEntity;
    }

    public AuxSourceEntity getCurrentAuxSourceEntity() {
        return mCurrentAuxSourceEntity;
    }

    public void setCurrentAuxSourceEntity(AuxSourceEntity currentAuxSourceEntity) {
        mCurrentAuxSourceEntity = currentAuxSourceEntity;
    }

    public AuxSongEntity getCurrentAuxSongEntity() {
        return mCurrentAuxSongEntity;
    }

    public void setCurrentAuxSongEntity(AuxSongEntity currentAuxSongEntity) {
        mCurrentAuxSongEntity = currentAuxSongEntity;
    }

    public AuxNetRadioEntity getCurrentAuxNetRadioEntity() {
        return mCurrentAuxNetRadioEntity;
    }

    public void setCurrentAuxNetRadioEntity(AuxNetRadioEntity currentAuxNetRadioEntity) {
        mCurrentAuxNetRadioEntity = currentAuxNetRadioEntity;
    }

    public AuxQueryContainerCallback getContainerCallback() {
        return containerCallback;
    }

    public void setContainerCallback(AuxQueryContainerCallback containerCallback) {
        this.containerCallback = containerCallback;
    }

    public void initDLNA(AuxQueryContainerCallback containerCallback, AuxBaseActionCallback baseActionCallback) {
        setContainerCallback(containerCallback);
        Device localServiceDevice = DeviceUtils.getLocalServiceDevice();
        if (localServiceDevice == null)
            localServiceDevice = AuxDMControlInstance.newInstance().getFirstDMSDevice();
        AuxDMControlInstance.newInstance().setServiceDevice(localServiceDevice).setQueryContainerType(FiletypeUtil.FILETYPE_AUDIO).requestDeviceRootContent(containerCallback).setAuxBaseActionCallback(baseActionCallback);
    }
}
