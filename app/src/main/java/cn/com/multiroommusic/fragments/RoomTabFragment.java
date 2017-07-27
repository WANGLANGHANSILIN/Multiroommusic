package cn.com.multiroommusic.fragments;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.com.auxdio.protocol.bean.AuxDeviceEntity;
import cn.com.auxdio.protocol.bean.AuxNetModelEntity;
import cn.com.auxdio.protocol.bean.AuxNetRadioTypeEntity;
import cn.com.auxdio.protocol.bean.AuxPlayListEntity;
import cn.com.auxdio.protocol.bean.AuxRoomEntity;
import cn.com.auxdio.protocol.bean.AuxSongEntity;
import cn.com.auxdio.protocol.bean.AuxSoundEffectEntity;
import cn.com.auxdio.protocol.bean.AuxSourceEntity;
import cn.com.auxdio.protocol.interfaces.AuxRequestNetModelListener;
import cn.com.auxdio.protocol.interfaces.AuxRequestPlayListListener;
import cn.com.auxdio.protocol.interfaces.AuxRequestRadioListener;
import cn.com.auxdio.protocol.interfaces.AuxRequestSoundEffectListener;
import cn.com.auxdio.protocol.interfaces.AuxRequestSourceListener;
import cn.com.auxdio.protocol.interfaces.AuxRoomStateChangedListener;
import cn.com.auxdio.protocol.net.AuxUdpUnicast;
import cn.com.auxdio.protocol.protocol.AuxConfig;
import cn.com.auxdio.protocol.util.AuxRoomUtils;
import cn.com.multiroommusic.R;
import cn.com.multiroommusic.adapters.RoomFragmentAdapter;
import cn.com.multiroommusic.bean.MRMDeviceBean;
import cn.com.multiroommusic.bean.MRMRoomBean;
import cn.com.multiroommusic.callback.NotifyDeviceChangedCallback;
import cn.com.multiroommusic.callback.NotifyRoomStatusChangedCallBack;
import cn.com.multiroommusic.utils.DataUitls;
import cn.com.multiroommusic.utils.LogUtils;
import cn.com.multiroommusic.utils.RoomUtil;

public class RoomTabFragment extends BaseFragment implements AuxRoomStateChangedListener,NotifyDeviceChangedCallback, AuxRequestSourceListener, AuxRequestPlayListListener, AuxRequestRadioListener, AuxRequestNetModelListener.NetModelBindListListener, AuxRequestNetModelListener,AuxRequestSoundEffectListener {

    private List<MRMRoomBean> mMRMRoomBeanList;
    private RecyclerView mRoomListView;
    private RoomFragmentAdapter mRoomFragmentAdapter;
    private List<AuxRoomEntity> roomEntities;

    private NotifyRoomStatusChangedCallBack mNotifyRoomStatusChangedCallBack;

    @Override
    protected View initView() {
        View inflate = View.inflate(mContext, R.layout.fragment_room_tab, null);
        mRoomListView = (RecyclerView) inflate.findViewById(R.id.rc_frag_room);
        LogUtils.i("RoomTabFragment","initView......");
        return inflate;
    }


    @Override
    protected void initData() {
        super.initData();
        LogUtils.i("RoomTabFragment","initData......");
        mTitleTag = "正在搜索设备...";
        mMRMRoomBeanList = new ArrayList<>();
        roomEntities = new ArrayList<>();
        mRoomListView.setLayoutManager(new LinearLayoutManager(mContext));
        mRoomFragmentAdapter = new RoomFragmentAdapter(mContext, mMRMRoomBeanList, R.layout.item_rc_room_list);
        mRoomListView.setAdapter(mRoomFragmentAdapter);

        loadData();
    }

    private void loadData() {
        AuxDeviceEntity controlDeviceEntity = AuxUdpUnicast.getInstance().getControlDeviceEntity();
        if (controlDeviceEntity == null){
//            int deviceIndex = SPUtil.getSpUtil().getIntValue("deviceIndex");
            controlDeviceEntity = DataUitls.newInstance().getFistDevice();
        }
        LogUtils.i("RoomTabFragment","loadData......"+(controlDeviceEntity == null));
        if (controlDeviceEntity != null){
            LogUtils.i("RoomTabFragment","onResume   "+controlDeviceEntity.toString());
            AuxUdpUnicast.getInstance().setControlDeviceEntity(controlDeviceEntity);
            mTitleTag = controlDeviceEntity.getDevName();
            requestDeviceData(controlDeviceEntity);
        }

        DataUitls.newInstance().setmDeviceUpdataCallback(this);
        DataUitls.newInstance().getTitleChangedCallBack().onTitleChanged(mTitleTag);
    }

    private void requestDeviceData(AuxDeviceEntity controlDeviceEntity) {
        Map<Integer, List<AuxDeviceEntity>> deviceBeListMap = DataUitls.newInstance().getAuxDeviceListMap();
        if (deviceBeListMap == null)
            return;
        for (Integer integer : deviceBeListMap.keySet()) {
            if(integer == controlDeviceEntity.getDevModel()){
                List<? extends AuxDeviceEntity> auxDeviceEntities = deviceBeListMap.get(integer);
                for (AuxDeviceEntity auxDeviceEntity : auxDeviceEntities) {
                    AuxUdpUnicast.getInstance()
                            .requestDeviceRoomList(auxDeviceEntity.getDevIP(),this)
                            .requestDeviceSourceList(auxDeviceEntity.getDevIP(),this)
                            .requestDevicePlayList(auxDeviceEntity.getDevIP(),this)
                            .requestRadioData(this)
                            .requestNetModelList(auxDeviceEntity.getDevIP(),this)
                            .requestBindAllRoomForNetModel(this)
                            .requestSoundEffectList(this);
                }
            }
        }
    }

    @Override
    public void onRoomChange(AuxRoomEntity auxRoomEntity) {
        listChannelHandle(auxRoomEntity);
    }

    @Override
    public void OnRoomOffLine(AuxRoomEntity auxRoomEntity) {

        MRMRoomBean convert = MRMRoomBean.convert(auxRoomEntity);
        if (mMRMRoomBeanList.contains(convert)) {
            mMRMRoomBeanList.remove(convert);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DataUitls.newInstance().setAuxRoomEntities(mMRMRoomBeanList);
                    AuxUdpUnicast.getInstance().setControlRoomEntities(RoomUtil.convertArray(mMRMRoomBeanList));
                    mRoomFragmentAdapter.setBaseDataBeenList(DataUitls.newInstance().getRoomBeanOnLineList());
                }
            });
        }

    }

    @Override
    public void onDeviceChanged(MRMDeviceBean mrmDeviceBean) {
        DataUitls.newInstance().getNotifySettingFragmentCallBack().onNotifySetting();
        mMRMRoomBeanList.clear();
        roomEntities.clear();
        AuxUdpUnicast.getInstance().setControlRoomEntities(null);
        DataUitls.newInstance().setAuxPlayListMap(null);
        DataUitls.newInstance().setAuxSourceListMap(null);
        DataUitls.newInstance().setAuxNetModelEntityMap(null);

        requestDeviceData(mrmDeviceBean);

        mRoomFragmentAdapter.setBaseDataBeenList(mMRMRoomBeanList);

        mTitleTag = mrmDeviceBean.getDevName();
        DataUitls.newInstance().getTitleChangedCallBack().onTitleChanged(mTitleTag);
    }

    @Override
    public void onDeviceRoomShowChanged(List<MRMRoomBean> roomBeanList) {
        mMRMRoomBeanList = roomBeanList;
        mRoomFragmentAdapter.setBaseDataBeenList(mMRMRoomBeanList);
    }

    @Override
    public void onSourceList(String devIP,List<AuxSourceEntity> list) {
        LogUtils.i("RoomTabFragment",devIP+"   onSourceList..."+list.size());
        Map<String, List<? extends AuxSourceEntity>> auxSourceListMap = DataUitls.newInstance().getAuxSourceListMap();
        if (auxSourceListMap == null)
            auxSourceListMap = new HashMap<>();
        auxSourceListMap.put(devIP,list);
        DataUitls.newInstance().setAuxSourceListMap(auxSourceListMap);
    }

    private Map.Entry<String, List<? extends AuxSourceEntity>> getSourceListByIp(String devIP) {
        Map<String, List<? extends AuxSourceEntity>> auxSourceListMap1 = DataUitls.newInstance().getAuxSourceListMap();
        if (auxSourceListMap1 == null)
            return null;
        Iterator<Map.Entry<String, List<? extends AuxSourceEntity>>> iterator = auxSourceListMap1.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, List<? extends AuxSourceEntity>> next = iterator.next();
            if (next.getKey().equals(devIP))
                return next;
        }
        return null;
    }

    private void listChannelHandle(AuxRoomEntity convetChannelEntity) {
        mNotifyRoomStatusChangedCallBack = DataUitls.newInstance().getRoomStatusChangedCallBack();
        LogUtils.i("RoomTabFragment","listChannelHandle   roomCount:"+roomEntities.size()+"   "+ convetChannelEntity.toString());

        if(roomEntities.size() == 0)
            roomEntities.add(0,convetChannelEntity);
        else{
            int indexByID = -1;
            int devModel = AuxUdpUnicast.getInstance().getControlDeviceEntity().getDevModel();

            if (devModel == AuxConfig.DeciveModel.DEVICE_DM836 || devModel == AuxConfig.DeciveModel.DEVICE_DM838 || devModel == AuxConfig.DeciveModel.DEVICE_DM858)
                indexByID = AuxRoomUtils.getChannnelIndexByIP(roomEntities, convetChannelEntity.getRoomIP());
            else
                indexByID = AuxRoomUtils.getChannnelIndexByID(roomEntities, convetChannelEntity.getRoomID());
            LogUtils.i("RoomTabFragment","listChannelHandle   devModel:"+devModel+"   indexByID:"+indexByID+"   "+convetChannelEntity.getRoomIP());

            if (indexByID >= 0)
                roomEntities.set(indexByID, convetChannelEntity);
            else
                roomEntities.add(convetChannelEntity);
        }
        upDataControlFragment(convetChannelEntity);
        upDataRoomList(convetChannelEntity);
        LogUtils.i("RoomTabFragment","roomCount:"+roomEntities.size()+"   ChannelName:"+ convetChannelEntity.getRoomName()+"   ChannelIP:"+convetChannelEntity.getRoomIP());
        mMRMRoomBeanList = RoomUtil.convertMRMRoomBeanList(roomEntities);
        selectorHandle();
        DataUitls.newInstance().setAuxRoomEntities(mMRMRoomBeanList);
        mRoomFragmentAdapter.setBaseDataBeenList(mMRMRoomBeanList);

    }

    private void upDataRoomList(final AuxRoomEntity convetChannelEntity) {
        if (mNotifyRoomStatusChangedCallBack != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mNotifyRoomStatusChangedCallBack.onRoomStatusChanged(convetChannelEntity);
                }
            });
    }

    private void selectorHandle() {
        AuxRoomEntity[] controlRoomEntities = AuxUdpUnicast.getInstance().getControlRoomEntities();
        if (controlRoomEntities == null || controlRoomEntities.length < 1)
            return;
        for (AuxRoomEntity controlRoomEntity : controlRoomEntities) {
            for (MRMRoomBean mrmRoomBean : mMRMRoomBeanList) {
                if (mrmRoomBean.getRoomIP().equals(controlRoomEntity.getRoomIP()) && mrmRoomBean.getRoomID() == controlRoomEntity.getRoomID()){
                    if (mrmRoomBean.getoNOffState() == 0)
                        mrmRoomBean.setSelector(false);
                    else
                        mrmRoomBean.setSelector(true);
                }
            }
        }
    }

    private void upDataControlFragment(final AuxRoomEntity convetChannelEntity) {
        AuxRoomEntity[] controlRoomEntities = AuxUdpUnicast.getInstance().getControlRoomEntities();
        if (controlRoomEntities == null)
            LogUtils.e("RoomTabFragment","controlRoomEntities is null");

        if (controlRoomEntities != null && controlRoomEntities.length > 0){
            for (int i = 0; i < controlRoomEntities.length; i++) {
                if (convetChannelEntity.getRoomIP().equals(controlRoomEntities[i].getRoomIP()) && convetChannelEntity.getRoomName().equals(controlRoomEntities[i].getRoomName()) && convetChannelEntity.getRoomID() == (controlRoomEntities[i].getRoomID())){
                    controlRoomEntities[i] = convetChannelEntity;

                    if (convetChannelEntity.getoNOffState() == 0){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DataUitls.newInstance().getFragmentChangedCallBack().onFragmentChanged(0);
                            }
                        });
                    }
                    if (mNotifyRoomStatusChangedCallBack != null)
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mNotifyRoomStatusChangedCallBack.onRoomStatusChanged(convetChannelEntity);
                            }
                        });
                }
            }
        }
    }

    @Override
    public void onMusicList(String s, List<AuxPlayListEntity> list) {
        for (AuxPlayListEntity auxPlayListEntity : list) {
            LogUtils.i("RoomTabFragment",s+" ,onMusicList:"+auxPlayListEntity.toString());
            if (auxPlayListEntity.getMusicEntities() == null)
                continue;
            for (AuxSongEntity auxSongEntity : auxPlayListEntity.getMusicEntities()) {
                LogUtils.i("RoomTabFragment",s+" , onMusicList:"+auxSongEntity.toString());
            }
        }
        Map<String, List<? extends AuxPlayListEntity>> auxPlayListMap = new Hashtable<>();
        auxPlayListMap.put(s,list);
        DataUitls.newInstance().setAuxPlayListMap(auxPlayListMap);
    }

    @Override
    public void onRadioList(List<AuxNetRadioTypeEntity> list) {
        LogUtils.i("RoomTabFragment","onRadioList:"+list.toString());
        DataUitls.newInstance().setAuxNetRadioTypeEntities(list);
    }

    @Override
    public void onBindList(Map<Integer, AuxNetModelEntity> map) {
        LogUtils.i("RoomTabFragment","onBindList:"+map.toString());
        DataUitls.newInstance().setAuxNetModelEntityMap(map);
    }

    @Override
    public void onNetModelList(List<AuxNetModelEntity> list) {
        LogUtils.i("RoomTabFragment","onNetModelList:"+list.toString());
        DataUitls.newInstance().setAuxNetModelEntities(list);
    }

    @Override
    public void OnSoundEffetList(List<AuxSoundEffectEntity> list) {
        DataUitls.newInstance().setAuxSoundEffectEntities(list);
    }
}
