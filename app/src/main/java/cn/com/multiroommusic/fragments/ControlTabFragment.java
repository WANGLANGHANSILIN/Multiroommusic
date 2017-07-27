package cn.com.multiroommusic.fragments;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;

import java.util.ArrayList;
import java.util.List;

import cn.com.auxdio.protocol.bean.AuxNetModelEntity;
import cn.com.auxdio.protocol.bean.AuxNetRadioEntity;
import cn.com.auxdio.protocol.bean.AuxNetRadioTypeEntity;
import cn.com.auxdio.protocol.bean.AuxPlayListEntity;
import cn.com.auxdio.protocol.bean.AuxRoomEntity;
import cn.com.auxdio.protocol.bean.AuxSongEntity;
import cn.com.auxdio.protocol.bean.AuxSoundEffectEntity;
import cn.com.auxdio.protocol.bean.AuxSourceEntity;
import cn.com.auxdio.protocol.interfaces.AuxControlActionListener;
import cn.com.auxdio.protocol.net.AuxUdpUnicast;
import cn.com.auxdio.protocol.protocol.AuxConfig;
import cn.com.auxdio.protocol.util.AuxRoomUtils;
import cn.com.auxdlna.dmclib.AuxDMControlInstance;
import cn.com.auxdlna.dmclib.callback.AuxBaseActionCallback;
import cn.com.auxdlna.dmclib.callback.AuxQueryContainerCallback;
import cn.com.auxdlna.dmclib.handle.callback.AUXActionConstants;
import cn.com.multiroommusic.R;
import cn.com.multiroommusic.bean.MRMRadioBean;
import cn.com.multiroommusic.bean.MRMSongBean;
import cn.com.multiroommusic.callback.NotifyRoomStatusChangedCallBack;
import cn.com.multiroommusic.callback.OnItemClickEventCallback;
import cn.com.multiroommusic.dialog.ListDialog;
import cn.com.multiroommusic.dlna.utils.DeviceUtils;
import cn.com.multiroommusic.ui.MRMNetRadioActivity;
import cn.com.multiroommusic.utils.DataUitls;
import cn.com.multiroommusic.utils.LogUtils;
import cn.com.multiroommusic.utils.MusicUtils;
import cn.com.multiroommusic.utils.RoomUtil;
import cn.com.multiroommusic.utils.TT;

public class ControlTabFragment extends BaseFragment implements View.OnClickListener, AuxControlActionListener.ControlProgramNameListener, AuxControlActionListener.ControlPlayModeListener, AuxControlActionListener.ControlPlayStateListener,NotifyRoomStatusChangedCallBack, OnItemClickEventCallback, AuxControlActionListener.SoundEffectListener, AuxControlActionListener.ControlSourceEntityListener, AuxBaseActionCallback, AuxQueryContainerCallback {

    private ImageView iv_Logo,iv_Play_Folder,iv_Play_Preious,iv_Play_Pause,iv_play_Next,iv_Play_List,iv_Play_Mode,iv_Volume_Mute;
    private TextView tv_Programe_Name,tv_Source_Name;
    private AuxRoomEntity[] mControlRoomEntities;
    private int playStatus,playMode;
    private AuxSourceEntity mCurrentSourceEntity;
    private int[] playStatusArray = new int[]{R.mipmap.player_pause,R.mipmap.player_play};
    private int[] playModeArray = new int[]{R.mipmap.playmode_one_only,R.mipmap.playmode_single_loop,R.mipmap.playmode_order,R.mipmap.playmode_list_loop,R.mipmap.playmode_random};
    private int[] muteArray = new int[]{R.mipmap.volume_mute,R.mipmap.volume1};
    private int[] sourceIconArray = new int[]{R.mipmap.icon_source_usb,R.mipmap.icon_source_dvd,R.mipmap.icon_source_aux,R.mipmap.icon_source_dlna,R.mipmap.icon_source_radio};
    private SeekBar mVolumeSeekBar;
    private ListDialog mFolderDialog;
    private ListDialog mPlayListDialog;

    private List<AuxPlayListEntity> mPlayListEntities;
    private List<AuxNetRadioTypeEntity> mNetRadioTypeEntities;
    private AuxNetRadioTypeEntity mCurrentAuxNetRadioTypeEntity;
    private AuxPlayListEntity mCurrentAuxPlayListEntity;
    private List<Item> mItemList;
    private List<Container> mContainerList;


    @Override
    protected View initView() {

        View inflate = View.inflate(mContext, R.layout.fragment_control_tab, null);
        iv_Logo = (ImageView) inflate.findViewById(R.id.iv_control_icon_logo);
        iv_Play_Folder = (ImageView) inflate.findViewById(R.id.iv_control_play_folder);
        iv_Play_Preious = (ImageView) inflate.findViewById(R.id.iv_control_previous);
        iv_Play_Pause = (ImageView) inflate.findViewById(R.id.iv_control_play_pause);
        iv_play_Next = (ImageView) inflate.findViewById(R.id.iv_control_next);
        iv_Play_List = (ImageView) inflate.findViewById(R.id.iv_control_play_list);
        iv_Play_Mode = (ImageView) inflate.findViewById(R.id.iv_control_playmode);
        iv_Volume_Mute = (ImageView) inflate.findViewById(R.id.iv_control_volume_mute);

        mVolumeSeekBar = (SeekBar) inflate.findViewById(R.id.sb_volume_setting);

        iv_Play_Folder.setOnClickListener(this);
        iv_Play_Preious.setOnClickListener(this);
        iv_Play_Pause.setOnClickListener(this);
        iv_play_Next.setOnClickListener(this);
        iv_Play_List.setOnClickListener(this);
        iv_Play_Mode.setOnClickListener(this);
        iv_Volume_Mute.setOnClickListener(this);

        iv_Play_Pause.setTag(playStatusArray[1]);

        mVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBar.setProgress(progress);
                showMute(progress==0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                AuxUdpUnicast.getInstance().setVolume(mControlRoomEntities,seekBar.getProgress());
            }
        });

        tv_Source_Name = (TextView) inflate.findViewById(R.id.tv_control_program_name);
        tv_Programe_Name = (TextView) inflate.findViewById(R.id.tv_control_src_name);
        tv_Source_Name.setTextColor(getResources().getColor(R.color.color_checked));
        return inflate;
    }

    @Override
    protected void initData() {
        super.initData();
        DataUitls.newInstance().initDLNA(this,this);
    }

    @Override
    public void onResume() {
        super.onResume();
        AuxUdpUnicast.getInstance().requestCurrentSoundEffect(getControlRooms(),this);
        AuxUdpUnicast.getInstance().requestAudioSource(getControlRooms(),this);
        queryPlayMode_PlayStatus();

        setDLNAPlayDevice();
        mItemList = new ArrayList<>();
        mContainerList = new ArrayList<>();

    }

    private void initDailog() {
        initListData();
        mFolderDialog = ListDialog.newInstance();
        mPlayListDialog = ListDialog.newInstance();
        DataUitls.newInstance().setEventOprationCallback(this);
    }

    private void initListData() {
        mPlayListEntities = (List<AuxPlayListEntity>) DataUitls.newInstance().getCurrentRoomPlayList();
        if (mPlayListEntities == null)
            mPlayListEntities = new ArrayList<>();
        mNetRadioTypeEntities = DataUitls.newInstance().getAuxNetRadioTypeEntities();
    }

    @Override
    public void onClick(View v) {
        if (mControlRoomEntities == null || mControlRoomEntities.length <= 0 || mCurrentSourceEntity == null) {
            TT.showToast(getActivity(),getString(R.string.channel_exist));
            return;
        }
        LogUtils.i("onClick","mCurrentSourceEntity:"+mCurrentSourceEntity.toString());
        switch (v.getId()){
            case R.id.iv_control_play_folder:
                mFolderDialog.show(getChildFragmentManager(),"playfolder");
                DataUitls.newInstance().setEventOprationCallback(this);
                if (mCurrentSourceEntity.getSourceID() > 0xB0 && mCurrentSourceEntity.getSourceID() < 0xC0){
                    mFolderDialog.setListDialogTitle(getString(R.string.my_library)+"("+mContainerList.size()+")");
                    mFolderDialog.setOnBackViewVisibility(false).setList(mContainerList);
                }else if(mCurrentSourceEntity.getSourceID() > 0xC0 && mCurrentSourceEntity.getSourceID() < 0xD0){
                    mFolderDialog.setListDialogTitle(getString(R.string.network_radio)+"("+mNetRadioTypeEntities.size()+")");
                    mFolderDialog.setOnBackViewVisibility(true).setList(mNetRadioTypeEntities).setImageViewTagListener("radioTypeList",this);
                }else{
                    LogUtils.i("onClick","PlayListEntities:"+mPlayListEntities.size());
                    mFolderDialog.setListDialogTitle(getString(R.string.my_music)+"("+mPlayListEntities.size()+")");
                    mFolderDialog.setOnBackViewVisibility(false).setList(mPlayListEntities);
                }
                break;
            case R.id.iv_control_previous:
                if (mCurrentSourceEntity.getSourceID() > 0xB0 && mCurrentSourceEntity.getSourceID() < 0xC0){
                    AuxDMControlInstance.newInstance().requestPlayAction(AUXActionConstants.ACTION_PRE,this);
                }else
                    AuxUdpUnicast.getInstance().prevProgram(mControlRoomEntities,this);
                break;
            case R.id.iv_control_play_pause:
                if (mCurrentSourceEntity.getSourceID() > 0xB0 && mCurrentSourceEntity.getSourceID() < 0xC0){
                    String action = AUXActionConstants.ACTION_PLAY;
                    if (iv_Play_Pause.getTag() == null) {
                        TT.showToast(getContext(),"请先从播放列表选一首歌曲播放");
                        iv_Play_Pause.setImageResource(playStatusArray[1]);
                    }

                    if(iv_Play_Pause.getTag().equals(playStatusArray[0])){
                        action = AUXActionConstants.ACTION_PAUSE;
                    }else
                        action = AUXActionConstants.ACTION_PLAY;
                    AuxDMControlInstance.newInstance().requestPlayAction(action,this);
                }else
                    AuxUdpUnicast.getInstance().setPlayState(mControlRoomEntities,playStatus >= 2?1:++playStatus,this);
                break;
            case R.id.iv_control_next:
                if (mCurrentSourceEntity.getSourceID() > 0xB0 && mCurrentSourceEntity.getSourceID() < 0xC0){
                    AuxDMControlInstance.newInstance().requestPlayAction(AUXActionConstants.ACTION_NEXT,this);
                }else
                    AuxUdpUnicast.getInstance().nextProgram(mControlRoomEntities,this);
                break;
            case R.id.iv_control_play_list:
                mPlayListDialog.show(getChildFragmentManager(),"playlist");
                DataUitls.newInstance().setEventOprationCallback(this);
                if (mCurrentSourceEntity.getSourceID() > 0xB0 && mCurrentSourceEntity.getSourceID() < 0xC0){
                    mPlayListDialog.setListDialogTitle(getString(R.string.recent)).setList(mItemList);
                }else if(mCurrentSourceEntity.getSourceID() > 0xC0 && mCurrentSourceEntity.getSourceID() < 0xD0){
                    if (mCurrentAuxNetRadioTypeEntity == null)
                        return;
                    List<MRMRadioBean> radioBeanList = MusicUtils.convertMRMRadioList(mCurrentAuxNetRadioTypeEntity.getNetRadioList());
                    if (radioBeanList == null)
                        radioBeanList = new ArrayList<>();
                    mPlayListDialog.setListDialogTitle(mCurrentAuxNetRadioTypeEntity.getRadioType()+"("+radioBeanList.size()+")");
                    mPlayListDialog.setList(radioBeanList);
                }else{
                    if (mCurrentAuxPlayListEntity != null && mCurrentAuxPlayListEntity.getMusicEntities() != null){
                        List<MRMSongBean> mSongBeanList = MusicUtils.convertMRMSongList(mCurrentAuxPlayListEntity.getMusicEntities());
                        if (mSongBeanList == null)
                            mSongBeanList = new ArrayList<>();
                        mPlayListDialog.setListDialogTitle(getResources().getString(R.string.play_list)+"("+ mSongBeanList.size()+")");
                        mPlayListDialog.setList(mSongBeanList);
                    }
                }

                break;
            case R.id.iv_control_playmode:
                if (playMode >= 5)
                    playMode = 1;
                else
                    ++playMode;
                AuxUdpUnicast.getInstance().setPlayMode(mControlRoomEntities,playMode,this);
                showPlayMode(mCurrentSourceEntity, playMode-1);
                break;
            case R.id.iv_control_volume_mute:
                boolean isMute;
                if (iv_Volume_Mute.getTag().equals(muteArray[1]))
                    isMute = false;
                else
                    isMute = true;
                AuxUdpUnicast.getInstance().setMuteState(mControlRoomEntities,isMute);
                showMute(isMute);
                mVolumeSeekBar.setProgress(isMute?0:AuxUdpUnicast.getInstance().getControlRoomEntities()[0].getVolumeID());
                break;
            case R.id.bt_left_icon:
                if (mFolderDialog.getImageTag() == null) {
                    LogUtils.e("mFolderDialog","mFolderDialog.getImageTag()   ");
                    return;
                }
                LogUtils.i("mFolderDialog","mFolderDialog...."+mFolderDialog.getImageTag());
                if (mFolderDialog.getImageTag().equals("radioTypeList")){
                    getActivity().startActivity(new Intent(getActivity(), MRMNetRadioActivity.class));
                    mFolderDialog.dismiss();
                }else if (mFolderDialog.getImageTag().equals("radioList")){
                    mFolderDialog.setList(mNetRadioTypeEntities).setImageViewTagListener("radioTypeList",this);
                }else if(mFolderDialog.getImageTag().equals("playList")){
                    mFolderDialog.setList(mPlayListEntities);
                }
                break;
        }
    }

    @Override
    public void onProgramName(AuxSourceEntity auxSourceEntity, String s) {
        LogUtils.i("ControlTabFragment","onProgramName:   "+s+"   "+auxSourceEntity.toString());
        mCurrentSourceEntity = auxSourceEntity;
        showProgramName(auxSourceEntity,s);
    }

    @Override
    public void onPlayModel(AuxSourceEntity auxSourceEntity, int i) {
        LogUtils.i("ControlTabFragment","onPlayModel:   "+i+"   "+auxSourceEntity.toString());
        playMode = i;
        mCurrentSourceEntity = auxSourceEntity;
        showPlayMode(auxSourceEntity,i-1);
    }

    @Override
    public void onPlayState(AuxSourceEntity auxSourceEntity, int i) {
        LogUtils.i("ControlTabFragment","onPlayState:   "+i+"   "+auxSourceEntity.toString());
        if (i == 4)
            i = 2;
        playStatus = i;
        mCurrentSourceEntity = auxSourceEntity;
        showPlayStatus(auxSourceEntity,i-1);
    }

    //显示节目名称和音源名称
    private void showProgramName(final AuxSourceEntity auxSourceEntity, final String programName) {
        if (getActivity() == null)
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_Programe_Name.setText(auxSourceEntity.getProgramName());
                tv_Source_Name.setText(auxSourceEntity.getSourceName());

            }
        });

    }

    //显示播放状态
    private void showPlayStatus(final AuxSourceEntity auxSourceEntity, final int playState) {
        if (getActivity() == null)
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iv_Play_Pause.setImageResource(playStatusArray[playState >=2?0:playState]);
                tv_Source_Name.setText(auxSourceEntity.getSourceName());
            }
        });

    }

    //显示播放模式
    private void showPlayMode(final AuxSourceEntity auxSourceEntity, final int playMode) {
        if (getActivity() == null)
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iv_Play_Mode.setImageResource(playModeArray[playMode]);
                tv_Source_Name.setText(auxSourceEntity.getSourceName());
            }
        });

    }

    //显示静音
    private void showMute(final boolean isMute) {
        if (getActivity() == null)
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iv_Volume_Mute.setTag(isMute?muteArray[1]:muteArray[0]);
                iv_Volume_Mute.setImageResource(isMute?muteArray[0]:muteArray[1]);
            }
        });
    }

    //房间改变，主动通知控制界面
    @Override
    public void onRoomStatusChanged(AuxRoomEntity roomBean) {
        LogUtils.i("ControlTabFragment","onDeviceRoomShowChanged  "+roomBean.toString());
        AuxRoomEntity[] controlRoomEntities = AuxUdpUnicast.getInstance().getControlRoomEntities();
        if (controlRoomEntities == null)
            return;
        LogUtils.i("ControlTabFragment","onDeviceRoomShowChanged  isSameSource():  "+isSameSource()+"   is...   "+(mCurrentSourceEntity != null));

        if (isSameSource())
        {
            mVolumeSeekBar.setProgress(roomBean.getVolumeID());
            showMute(mVolumeSeekBar.getProgress() == 0);
            tv_Programe_Name.setText("");
            tv_Source_Name.setText(roomBean.getRoomSrcName());
            mCurrentSourceEntity = DataUitls.newInstance().getCurrentAuxSourceEntity();
//            mCurrentSourceEntity = AuxSouceUtils.getSourceEntityByID(DataUitls.newInstance().getCurrentRoomSourceList(),roomBean.getSrcID());
            queryPlayMode_PlayStatus();
        }
        showLayoutView();
    }

    //点击事件处理(对话框)
    @Override
    public void onItemClick(Object dataBean, int position) {
        if (dataBean instanceof AuxPlayListEntity) {
            mCurrentAuxPlayListEntity = (AuxPlayListEntity) dataBean;
            LogUtils.i("playSong",""+mCurrentAuxPlayListEntity.getContentsName());
            List<MRMSongBean> mrmSongBeen = MusicUtils.convertMRMSongList(mCurrentAuxPlayListEntity.getMusicEntities());

            if (mrmSongBeen == null)
                mrmSongBeen = new ArrayList<>();
            LogUtils.i("playSong",""+mrmSongBeen.size());
            mFolderDialog.setList(mrmSongBeen)
                    .setListDialogTitle(mCurrentAuxPlayListEntity.getPlayListName()+"("+mrmSongBeen.size()+")")
                    .setOnBackViewVisibility(true)
                    .setImageViewTagListener("playList",this);
        }else if (dataBean instanceof MRMSongBean){
            LogUtils.i("playSong",""+((MRMSongBean) dataBean).getSongName());
            DataUitls.newInstance().setCurrentAuxSongEntity((AuxSongEntity) dataBean);
            AuxUdpUnicast.getInstance().playSong((AuxSongEntity) dataBean);
            if (mFolderDialog != null && mFolderDialog.isResumed())
                mFolderDialog.dismiss();
            if (mPlayListDialog != null && mPlayListDialog.isResumed())
                mPlayListDialog.dismiss();
        }else if (dataBean instanceof AuxNetRadioTypeEntity){
            mCurrentAuxNetRadioTypeEntity = (AuxNetRadioTypeEntity) dataBean;
            List<MRMRadioBean> radioBeanList = MusicUtils.convertMRMRadioList(mCurrentAuxNetRadioTypeEntity.getNetRadioList());
            if (radioBeanList == null)
                radioBeanList = new ArrayList<>();
            mFolderDialog.setListDialogTitle(mCurrentAuxNetRadioTypeEntity.getRadioType()+"("+radioBeanList.size()+")")
                    .setOnBackViewVisibility(true)
                    .setList(radioBeanList)
                    .setImageViewTagListener("radioList",this);
        }else if (dataBean instanceof AuxNetRadioEntity){
            DataUitls.newInstance().setCurrentAuxNetRadioEntity((AuxNetRadioEntity) dataBean);
            for (AuxRoomEntity auxRoomEntity : AuxUdpUnicast.getInstance().getControlRoomEntities()) {
                int modelID = DataUitls.newInstance().getBindRoomModelID(auxRoomEntity.getRoomID());
                AuxUdpUnicast.getInstance().playRadio(modelID, (AuxNetRadioEntity) dataBean);
            }
            if (mFolderDialog != null && mFolderDialog.isResumed())
                mFolderDialog.dismiss();
            if (mPlayListDialog != null && mPlayListDialog.isResumed())
                mPlayListDialog.dismiss();
        }else if (dataBean instanceof Container){
            mItemList = ((Container) dataBean).getItems();
            mFolderDialog.setList(mItemList).setListDialogTitle(((Container) dataBean).getTitle());
        }else if (dataBean instanceof Item){
            if (setDLNAPlayDevice()) {
                TT.showToast(getActivity(),"dlna play Device is isEmty");
                return;
            }
            AuxDMControlInstance.newInstance().requestPlayMedia((Item) dataBean);
            tv_Programe_Name.setText(((Item) dataBean).getTitle());
            if (mFolderDialog.isResumed())
                mFolderDialog.dismiss();
            if (mPlayListDialog.isResumed())
                mPlayListDialog.dismiss();
        }
    }

    private boolean setDLNAPlayDevice() {
        if (AuxDMControlInstance.newInstance().getPlayDevie() == null){
            if (!isSameSource()){
                return true;
            }
            AuxNetModelEntity netModelByroomID = RoomUtil.findNetModelByroomID(AuxUdpUnicast.getInstance().getControlRoomEntities()[0].getRoomID());
            if (netModelByroomID != null){
                LogUtils.i("onItemClick",""+netModelByroomID.toString());
                AuxDMControlInstance.newInstance().setPlayDevie(DeviceUtils.getPlayDeviceByModelId(netModelByroomID));
            }
        }
        return false;
    }


    //是否控制多个房间
    private boolean isMutilControl(){
        AuxRoomEntity[] controlRoomEntities = AuxUdpUnicast.getInstance().getControlRoomEntities();
        if (controlRoomEntities == null || controlRoomEntities.length < 1)
            return false;
        if (controlRoomEntities.length > 1)
            return true;
        return false;
    }

    //控制的音源是否相同
    private boolean isSameSource(){
        AuxRoomEntity[] controlRoomEntities = AuxUdpUnicast.getInstance().getControlRoomEntities();
        if (controlRoomEntities == null || controlRoomEntities.length < 1)
            return false;

        for (int i = 1; i < controlRoomEntities.length; i++) {
            if (controlRoomEntities[i-1].getSrcID() != controlRoomEntities[i].getSrcID())
                return false;
        }
        return true;
    }

    //获取当前的音源ID
    private int getCurrentSourceID(){
        if (isSameSource())
            return AuxUdpUnicast.getInstance().getControlRoomEntities()[0].getSrcID();
        return 0;
    }

    //获取设备模式
    private int getDeviceMode(){
        return AuxUdpUnicast.getInstance().getControlDeviceEntity().getDevModel();
    }
    private AuxRoomEntity[] getControlRooms(){
        AuxRoomEntity[] controlRoomEntities = AuxUdpUnicast.getInstance().getControlRoomEntities();
        if (controlRoomEntities != null && controlRoomEntities.length > 0)
            return controlRoomEntities;
        return controlRoomEntities;
    }

    //显示布局样式
    private void showLayoutView(){
        int currentSourceID = getCurrentSourceID();
        if (currentSourceID == 0) {
            show_DVD_Style();
            return;
        }
        if (currentSourceID > 0xD0){
            iv_Logo.setImageResource(sourceIconArray[0]);
            showUSB_SD_Style(View.VISIBLE);
            if (getDeviceMode() == AuxConfig.DeciveModel.DEVICE_DM838)
                show_DVD_Style();
        }else if (currentSourceID > 0xC0){
            iv_Logo.setImageResource(sourceIconArray[4]);
            show_NetRadio_Style();
            if (getDeviceMode() == AuxConfig.DeciveModel.DEVICE_DM838)
                show_DVD_Style();
        }else if(currentSourceID > 0xB0){
            iv_Logo.setImageResource(sourceIconArray[3]);
            show_NetMusic_Style();
        }else if (currentSourceID == 0xA1){
            iv_Logo.setImageResource(sourceIconArray[2]);
            show_AUX_Style();
        }else if (currentSourceID == 0x91 || currentSourceID == 0x81 || currentSourceID == 0x01){
            iv_Logo.setImageResource(sourceIconArray[0]);
            showUSB_SD_Style(View.VISIBLE);
            if (getDeviceMode() == AuxConfig.DeciveModel.DEVICE_DM836 || getDeviceMode() == AuxConfig.DeciveModel.DEVICE_DM838){
                if (isMutilControl()) {
                    iv_Play_List.setVisibility(View.INVISIBLE);
                    iv_Play_Folder.setVisibility(View.INVISIBLE);
                }
            }

        }else if (currentSourceID == 0x51 || currentSourceID == 0x52){
            iv_Logo.setImageResource(sourceIconArray[2]);
            show_AUX_Style();
        }else if (currentSourceID == 0x41){
            iv_Logo.setImageResource(sourceIconArray[1]);
            show_DVD_Style();
        }
    }

    private void showUSB_SD_Style(int visible){
        iv_Play_Mode.setVisibility(visible);
        iv_Volume_Mute.setVisibility(visible);

        iv_Play_Folder.setVisibility(visible);
        iv_Play_List.setVisibility(visible);

        iv_Play_Preious.setVisibility(visible);
        iv_play_Next.setVisibility(visible);
        iv_Play_Pause.setVisibility(visible);


        tv_Source_Name.setVisibility(visible);
        tv_Programe_Name.setVisibility(visible);
    }

    private void show_DVD_Style(){
        showUSB_SD_Style(View.INVISIBLE);
        if (isSameSource())
            tv_Source_Name.setVisibility(View.VISIBLE);
        else
            tv_Programe_Name.setVisibility(View.INVISIBLE);
        iv_Volume_Mute.setVisibility(View.VISIBLE);
    }

    private void show_AUX_Style(){
        show_DVD_Style();
        tv_Programe_Name.setVisibility(View.VISIBLE);
    }

    private void show_NetMusic_Style(){
        showUSB_SD_Style(View.VISIBLE);
        iv_Play_Mode.setVisibility(View.INVISIBLE);
    }

    private void show_NetRadio_Style(){
        show_NetMusic_Style();
        iv_Play_Pause.setVisibility(View.INVISIBLE);
    }

    public void queryPlayMode_PlayStatus() {
        mControlRoomEntities = AuxUdpUnicast.getInstance().getControlRoomEntities();
        if (mControlRoomEntities == null || mControlRoomEntities.length <= 0) {
            LogUtils.i("ControlTabFragment","mControlRoomEntities == null   ");
            return;
        }
        initDailog();
        if (isResumed()) {
            showLayoutView();
            mVolumeSeekBar.setProgress(mControlRoomEntities[0].getVolumeID());
        }
        mTitleTag = DataUitls.newInstance().getControlRoomName();


        LogUtils.i("ControlTabFragment","queryPlayMode_PlayStatus....   "+mControlRoomEntities[0].toString());
        DataUitls.newInstance().setRoomStatusChangedCallBack(this);
        AuxUdpUnicast.getInstance()
                .requestProgramName(mControlRoomEntities,this)
                .requestPlayMode(mControlRoomEntities,this)
                .requestPlayState(mControlRoomEntities,this);
    }

    @Override
    public void onCurrentSoundEffect(AuxRoomEntity auxRoomEntity, AuxSoundEffectEntity auxSoundEffectEntity) {
        AuxRoomEntity channnelIndexByIP = AuxRoomUtils.getChannnelIndexByIP(getControlRooms(), auxRoomEntity.getRoomIP());
        if (channnelIndexByIP != null)
            DataUitls.newInstance().setCurrentAuxSoundEffectEntity(auxSoundEffectEntity);
    }

    @Override
    public void onSourceEntity(AuxRoomEntity auxRoomEntity, AuxSourceEntity auxSourceEntity) {
        AuxRoomEntity channnelIndexByIP = AuxRoomUtils.getChannnelIndexByIP(getControlRooms(), auxRoomEntity.getRoomIP());
        if (channnelIndexByIP != null)
            DataUitls.newInstance().setCurrentAuxSourceEntity(auxSourceEntity);
    }

    @Override
    public void onActionFailure(String s) {

    }

    @Override
    public void onActionSuccess(String s) {
        if (s.equals(AUXActionConstants.ACTION_PLAY)){
            iv_Play_Pause.setImageResource(playStatusArray[0]);
            iv_Play_Pause.setTag(playStatusArray[0]);
        }else {
            iv_Play_Pause.setImageResource(playStatusArray[1]);
            iv_Play_Pause.setTag(playStatusArray[1]);
        }
    }

    @Override
    public void onContainerList(List<Container> list) {
        for (Container container : list) {
            Log.i("onContainerList", "onContainerList: "+container.getTitle()+container.getItems().size());
        }
        mContainerList = list;
    }
}
