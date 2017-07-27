package cn.com.multiroommusic.dlna;

import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.com.auxdio.protocol.net.AuxUdpUnicast;
import cn.com.auxdlna.dmclib.AuxDMControlInstance;
import cn.com.auxdlna.dmclib.callback.AuxBaseActionCallback;
import cn.com.auxdlna.dmclib.callback.AuxQueryContainerCallback;
import cn.com.auxdlna.dmclib.callback.AuxQueryPlayPositionInfoCallback;
import cn.com.auxdlna.dmclib.callback.AuxQueryVolumeCallback;
import cn.com.auxdlna.dmclib.handle.callback.AUXActionConstants;
import cn.com.multiroommusic.R;
import cn.com.multiroommusic.callback.OnItemClickEventCallback;
import cn.com.multiroommusic.dialog.ListDialog;
import cn.com.multiroommusic.ui.BaseMRMActivity;
import cn.com.multiroommusic.utils.DataUitls;
import cn.com.multiroommusic.utils.LogUtils;
import cn.com.multiroommusic.utils.TT;

import static cn.com.multiroommusic.dlna.utils.DeviceUtils.getPlayDeviceByRoomId;

public class DLNAPlayControlActivity extends BaseMRMActivity implements OnItemClickEventCallback, AuxQueryContainerCallback, AuxBaseActionCallback, AuxQueryVolumeCallback, AuxQueryPlayPositionInfoCallback {

    private ImageView iv_Logo,iv_Play_Folder,iv_Play_Preious,iv_Play_Pause,iv_play_Next,iv_Play_List,iv_Play_Mode,iv_Volume_Mute;
    private TextView tv_Programe_Name,tv_Source_Name;
    private SeekBar mVolumeSeekBar;

    private ListDialog mPlayFolder,mPlayListDialog, mDeviceListDialog;
    private List<Container> mContainerList;
    private List<Item> mItemList;

    private int[] playStatusArray = new int[]{R.mipmap.player_pause,R.mipmap.player_play};


    @Override
    protected void onStart() {
        super.onStart();

        DataUitls.newInstance().initDLNA(this,this);
        initView();
        initDialog();
        DataUitls.newInstance().setEventOprationCallback(this);
    }

    @Override
    public int getTitleViewlayoutID() {
        return R.id.include_layout_dlna;
    }

    @Override
    public int initRootViewLayoutID() {
        return R.layout.activity_dlna_control;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_control_play_folder:
                mPlayFolder.setListDialogTitle(getString(R.string.my_library)).setList(mContainerList).show(getSupportFragmentManager(),"dlnafolderdialog");
                break;
            case R.id.iv_control_previous:
                AuxDMControlInstance.newInstance().requestPlayAction(AUXActionConstants.ACTION_PRE,DLNAPlayControlActivity.this);
                break;
            case R.id.iv_control_play_pause:
                if (iv_Play_Pause.getTag() == null || (int)iv_Play_Pause.getTag() == playStatusArray[1])
                    AuxDMControlInstance.newInstance().requestPlayAction(AUXActionConstants.ACTION_PLAY,DLNAPlayControlActivity.this);
                else
                    AuxDMControlInstance.newInstance().requestPlayAction(AUXActionConstants.ACTION_PAUSE,DLNAPlayControlActivity.this);
                break;
            case R.id.iv_control_next:
                AuxDMControlInstance.newInstance().requestPlayAction(AUXActionConstants.ACTION_NEXT,DLNAPlayControlActivity.this)
                .requestVolumeAction(this);
                break;
            case R.id.iv_control_play_list:
                mPlayListDialog.setListDialogTitle(getString(R.string.recent)).setList(mItemList).show(getSupportFragmentManager(),"dlnaplaydialog");
                break;
            case R.id.bt_left_icon:
                this.finish();
                break;
            case R.id.bt_right_icon:
                Collection<List<Device>> values = DLNAUtil.newInstance().getDMSDeviceMap().values();
                List<Device> deviceList = new ArrayList<>();
                for (List<Device> value : values) {
                    for (Device device : value) {
                        deviceList.add(device);
                    }
                }
                mDeviceListDialog.setListDialogTitle(getString(R.string.my_library)).setList(deviceList).show(getSupportFragmentManager(),"dlnadevicedialog");
                break;

        }
    }

    private void initView() {
        iv_Play_Folder = (ImageView) findViewById(R.id.iv_control_play_folder);
        iv_Play_Preious = (ImageView) findViewById(R.id.iv_control_previous);
        iv_Play_Pause = (ImageView) findViewById(R.id.iv_control_play_pause);
        iv_play_Next = (ImageView) findViewById(R.id.iv_control_next);
        iv_Play_List = (ImageView) findViewById(R.id.iv_control_play_list);
        iv_Play_Mode = (ImageView) findViewById(R.id.iv_control_playmode);
        iv_Volume_Mute = (ImageView) findViewById(R.id.iv_control_volume_mute);

        tv_Source_Name = (TextView) findViewById(R.id.tv_control_program_name);
        tv_Programe_Name = (TextView) findViewById(R.id.tv_control_src_name);

        mVolumeSeekBar = (SeekBar) findViewById(R.id.sb_volume_setting);

        iv_Play_Folder.setOnClickListener(this);
        iv_Play_Preious.setOnClickListener(this);
        iv_Play_Pause.setOnClickListener(this);
        iv_play_Next.setOnClickListener(this);
        iv_Play_List.setOnClickListener(this);
        iv_Play_Mode.setOnClickListener(this);
        iv_Volume_Mute.setOnClickListener(this);

        iv_Play_Mode.setVisibility(View.INVISIBLE);
        mRightView.setVisibility(View.VISIBLE);
        mRightView.setImageResource(R.mipmap.dlna_device);
        tv_Source_Name.setText(getString(R.string.dlna_set));
        showPlayState("");
        mVolumeSeekBar.setProgress(AuxUdpUnicast.getInstance().getControlRoomEntities()[0].getVolumeID());
        mVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                AuxUdpUnicast.getInstance().setVolume(AuxUdpUnicast.getInstance().getControlRoomEntities(),seekBar.getProgress());
            }
        });
    }
    private void initDialog(){
        mPlayListDialog = new ListDialog();
        mPlayFolder = new ListDialog();
        mDeviceListDialog = new ListDialog();
    }

    @Override
    public void onItemClick(Object dataBean, int position) {
        if (dataBean instanceof Device){
            AuxDMControlInstance.newInstance().setServiceDevice((Device) dataBean).requestDeviceRootContent(this);
            mDeviceListDialog.dismiss();
        }else if (dataBean instanceof Container){
            this.mItemList = ((Container) dataBean).getItems();
            mPlayFolder.setList(mItemList);
        }else if (dataBean instanceof Item){
//            if (AuxDMControlInstance.newInstance().getPlayDevie() == null)
            Device playDeviceByRoomId = getPlayDeviceByRoomId();
            if (playDeviceByRoomId == null)
                TT.showToast(DLNAPlayControlActivity.this,"not find play dlna device");
            AuxDMControlInstance.newInstance().setPlayDevie(playDeviceByRoomId);
//            LogUtils.i("onItemClick",""+playDeviceByRoomId.getDetails().getFriendlyName());
            AuxDMControlInstance.newInstance().requestPlayMedia((Item) dataBean);
            tv_Programe_Name.setText(((Item) dataBean).getTitle());
            if (mPlayFolder.isResumed())
                mPlayFolder.dismiss();
            if (mPlayListDialog.isResumed())
                mPlayListDialog.dismiss();
        }
    }

    @Override
    public void onContainerList(List<Container> list) {
        for (Container container : list) {
            LogUtils.i("onContainerList", "onContainerList: "+(container.getTitle()));
        }
        this.mContainerList = list;
    }

    @Override
    public void onActionFailure(String s) {
        TT.showToast(this,s);
    }

    @Override
    public void onActionSuccess(String s) {
        LogUtils.i("onActionSuccess","action:"+s);
        if (s.equals(AUXActionConstants.ACTION_PLAY))
            AuxDMControlInstance.newInstance().requestPlayPositionInfoAction(this);
        showPlayState(s);
        int playItemIndex = AuxDMControlInstance.newInstance().getPlayItemIndex();
        if (mItemList != null && playItemIndex < mItemList.size()) {
            tv_Programe_Name.setText(mItemList.get(playItemIndex).getTitle());
        }
    }

    private void showPlayState(String s) {
        if (s.equals(AUXActionConstants.ACTION_PLAY)){
            iv_Play_Pause.setImageResource(playStatusArray[0]);
            iv_Play_Pause.setTag(playStatusArray[0]);
        }else if (s.equals(AUXActionConstants.ACTION_PAUSE) || s.equals(AUXActionConstants.ACTION_STOP)){
            iv_Play_Pause.setImageResource(playStatusArray[1]);
            iv_Play_Pause.setTag(playStatusArray[1]);
        }
    }

    @Override
    public void onVolumeSuccess(int i) {
        LogUtils.i("onVolumeSuccess","action:"+i);
//        mVolumeSeekBar.setProgress(i);
    }

    @Override
    public void onPlayPositionInfoSuccess(String currentTime, String totalTime, long percent) {
        String[] split = currentTime.split(":");
        String[] split1 = totalTime.split(":");
        LogUtils.i("split[0] = "+split[0]+"split[1]"+split[1]+"split[2]"+split[2]);
        int i = Integer.valueOf(split[0]) * 60 * 60 + Integer.valueOf(split[1]) * 60 + Integer.valueOf(split[2]);
        int i1 = Integer.valueOf(split1[0]) * 60 * 60 + Integer.valueOf(split1[1]) * 60 + Integer.valueOf(split1[2]);
        if (i != 0 && i1 != 0){
            showPlayState(AUXActionConstants.ACTION_PLAY);
            LogUtils.i("totalTime:"+i1+" , currentTime:"+i+" , "+(((i*1.0)/i1)*100));
            if(((i*1.0)/i1)*100 >= 99.4)
                AuxDMControlInstance.newInstance().requestPlayAction(AUXActionConstants.ACTION_NEXT,this);
        }
    }

}
