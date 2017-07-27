package cn.com.multiroommusic.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.fourthline.cling.model.meta.Device;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.com.auxdio.protocol.bean.AuxDeviceEntity;
import cn.com.auxdio.protocol.bean.AuxRoomEntity;
import cn.com.auxdio.protocol.bean.AuxSoundEffectEntity;
import cn.com.auxdio.protocol.bean.AuxSourceEntity;
import cn.com.auxdio.protocol.net.AuxUdpBroadcast;
import cn.com.auxdio.protocol.net.AuxUdpUnicast;
import cn.com.auxdio.protocol.protocol.AuxConfig;
import cn.com.auxdlna.dmclib.AuxDMControlInstance;
import cn.com.auxdlna.dmclib.util.FiletypeUtil;
import cn.com.multiroommusic.MultiRoomMusicApplication;
import cn.com.multiroommusic.R;
import cn.com.multiroommusic.bean.MRMSoundEffect;
import cn.com.multiroommusic.bean.MRMSourceBean;
import cn.com.multiroommusic.callback.FragmentChangedCallBack;
import cn.com.multiroommusic.callback.TitleChangedCallBack;
import cn.com.multiroommusic.dialog.HighLowDialog;
import cn.com.multiroommusic.dialog.InquiryDialog;
import cn.com.multiroommusic.dialog.ListDialog;
import cn.com.multiroommusic.dlna.DLNAPlayControlActivity;
import cn.com.multiroommusic.dlna.DLNAUtil;
import cn.com.multiroommusic.fragments.BaseFragment;
import cn.com.multiroommusic.fragments.ControlTabFragment;
import cn.com.multiroommusic.fragments.RoomTabFragment;
import cn.com.multiroommusic.fragments.SettingsTabFragment;
import cn.com.multiroommusic.utils.AppUpDateUtil;
import cn.com.multiroommusic.utils.DataUitls;
import cn.com.multiroommusic.utils.LogUtils;
import cn.com.multiroommusic.utils.SPUtil;
import cn.com.multiroommusic.utils.TT;

public class MainActivity extends BaseCommonActivity implements TitleChangedCallBack, View.OnClickListener,FragmentChangedCallBack {

    private RadioGroup mRadioGroup;
    private ArrayList<BaseFragment> mBaseFragments;
    private int position; //当前选中的位置
    private BaseFragment mFragment;//刚显示的Fragment
    private ImageView mLeftView,mDLNAView;
    private ImageView mRightView;
    private TextView mTvTitleView;
    private ListDialog mSourceDialog;
    private ListDialog mSoundEffectDialog,mDLNADeviceDialog;
    private boolean isShowControl = false;
    private InquiryDialog mInquiryDialog;
    private HighLowDialog mHighLowDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AppUpDateUtil.checkAppUpgrading(MainActivity.this,getSupportFragmentManager());
                initControlDevice();
                checkPermission();
                initData();
                initView();
                setListener();
                initDialog();
            }
        },800);

    }

    private void initControlDevice() {
        AuxDeviceEntity deviceEntity = null;
        List<AuxDeviceEntity> mrmDeviceBeenList = DataUitls.newInstance().getMRMDeviceBeenList();
        if (mrmDeviceBeenList != null && mrmDeviceBeenList.size() > 0){
            int deviceIndex = SPUtil.getSpUtil().getIntValue("deviceIndex");
            deviceEntity = DataUitls.newInstance().getControlDevice(deviceIndex);
        }

        if (deviceEntity == null)
            deviceEntity = DataUitls.newInstance().getFistDevice();

        if (deviceEntity != null) {
            LogUtils.i("MainActivity",deviceEntity.toString());
            AuxUdpUnicast.getInstance().setControlDeviceEntity(deviceEntity);
        }

        if (deviceEntity == null)
        {
            AuxUdpBroadcast.getInstace().startWorking().searchDevice();
            AuxUdpUnicast.getInstance().startWorking();
        }
        LogUtils.i("MainActivity","onCreate.........onCreate............"+(DataUitls.newInstance().getAuxDeviceListMap().size()));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        AuxUdpBroadcast.getInstace().searchDevice();
        LogUtils.i("MainActivity","onRestart.........onRestart............");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.i("MainActivity","onResume.........onResume............");
    }


    private void setListener() {
        mRadioGroup.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        //默认选中第一个
        mRadioGroup.check(R.id.rb_room);
    }

    private void initData() {

        mBaseFragments = new ArrayList<>();
        mBaseFragments.add(new RoomTabFragment());
        mBaseFragments.add(new ControlTabFragment());
        mBaseFragments.add(new SettingsTabFragment());

        DataUitls.newInstance().setTitleChangedCallBack(this);
        DataUitls.newInstance().setFragmentChangedCallBack(this);
    }

    private void initView() {
        mRadioGroup = (RadioGroup) findViewById(R.id.rg_main);
        View viewById = findViewById(R.id.include_layout_main);
        mTvTitleView = (TextView) viewById.findViewById(R.id.tv_cent_title);
        mLeftView = (ImageView) viewById.findViewById(R.id.bt_left_icon);
        mRightView = (ImageView) viewById.findViewById(R.id.bt_right_icon);
        mDLNAView = (ImageView) viewById.findViewById(R.id.bt_left_icon_dlna);
        mRightView.setBackgroundResource(R.drawable.iv_bg_selector);
        mLeftView.setBackgroundResource(R.drawable.iv_bg_selector);
        mDLNAView.setBackgroundResource(R.drawable.iv_bg_selector);
        mRightView.setOnClickListener(this);
        mLeftView.setOnClickListener(this);
        mDLNAView.setOnClickListener(this);
    }
    
    private void initDialog(){
        mSourceDialog = ListDialog.newInstance();
        mSoundEffectDialog = ListDialog.newInstance();
        mDLNADeviceDialog = ListDialog.newInstance();
        mInquiryDialog = new InquiryDialog();
        mHighLowDialog = new HighLowDialog();
        DataUitls.newInstance().setSourceListDialog(mSourceDialog);
    }


    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 100;
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};

            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_GRANTED) {
                    if (permissions[0].equals(permission))
                        DLNAUtil.newInstance().initDLNA();
                }else
                {
                    //申请权限
                    MainActivity.this.requestPermissions(new String[]{permission}, REQUEST_CODE_CONTACT);
                }
            }
        }else
            DLNAUtil.newInstance().initDLNA();

    }

    @Override
    public void onTitleChanged(String title) {
        mTvTitleView.setText(title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_right_icon:
                mSourceDialog.show(getSupportFragmentManager(),"sourceDialog");

                List<? extends AuxSourceEntity> sourceBeanList = DataUitls.newInstance().getCurrentRoomSourceList();
                if (sourceBeanList == null)
                    sourceBeanList = new ArrayList<>();
                DataUitls.newInstance().setEventOprationCallback(this);
                mSourceDialog.setListDialogTitle(getString(R.string.source_list)+"("+sourceBeanList.size()+")");
                mSourceDialog.setList(sourceBeanList);
                break;
            case R.id.bt_left_icon:
                int devModel = AuxUdpUnicast.getInstance().getControlDeviceEntity().getDevModel();

                if (devModel == AuxConfig.DeciveModel.DEVICE_DM838){
                    TT.showToast(MainActivity.this,"DLNA界面");
                    mLeftView.setImageResource(R.mipmap.dlna_device);
                    startActivity(new Intent(MainActivity.this, DLNAPlayControlActivity.class));
                }else if (devModel == AuxConfig.DeciveModel.DEVICE_DM836){
                    mLeftView.setImageResource(R.mipmap.control_eq);
                    mSoundEffectDialog.show(getSupportFragmentManager(),"soundeffectdialog");
                    DataUitls.newInstance().setEventOprationCallback(this);
                    mSoundEffectDialog.setListDialogTitle(getString(R.string.eq_source))
                            .setList(DataUitls.newInstance().getAuxSoundEffectEntities());
                }else{
                    mLeftView.setImageResource(R.mipmap.control_eq);
                    mHighLowDialog.show(getSupportFragmentManager(),"hightdialog");
                }
                break;
            case R.id.bt_left_icon_dlna:
                Collection<List<Device>> values = DLNAUtil.newInstance().getDMSDeviceMap().values();
                List<Device> deviceList = new ArrayList<>();
                for (List<Device> value : values) {
                    for (Device device : value) {
                        deviceList.add(device);
                    }
                }
                mDLNADeviceDialog.setListDialogTitle(getString(R.string.my_library)).setList(deviceList).show(getSupportFragmentManager(),"dlnadialog");
                DataUitls.newInstance().setEventOprationCallback(this);
                break;
        }
    }

    @Override
    public void onFragmentChanged(int pos) {
        this.position = pos;
        if (position == 0){
            mRadioGroup.check(R.id.rb_room);
        }else if (position == 1) {
            this.isShowControl = true;
            mRadioGroup.check(R.id.rb_control);
        }
//        showFragment(getFragment(),getSupportFragmentManager().beginTransaction());
    }

    @Override
    public void onItemClick(Object dataBean, int position) {
        if (dataBean instanceof MRMSourceBean) {
            if (((MRMSourceBean) dataBean).getSourceID() > 0xB0 && ((MRMSourceBean) dataBean).getSourceID() < 0xC0)
                mDLNAView.setVisibility(View.VISIBLE);
            else
                mDLNAView.setVisibility(View.INVISIBLE);
            DataUitls.newInstance().setCurrentAuxSourceEntity((AuxSourceEntity) dataBean);
            AuxUdpUnicast.getInstance().setAudioSource(AuxUdpUnicast.getInstance().getControlRoomEntities(), (AuxSourceEntity) dataBean);
            mSourceDialog.dismiss();
        }else if (dataBean instanceof MRMSoundEffect) {
            DataUitls.newInstance().setCurrentAuxSoundEffectEntity((AuxSoundEffectEntity) dataBean);
            AuxUdpUnicast.getInstance().setCurrentSoundEffect(AuxUdpUnicast.getInstance().getControlRoomEntities(), (AuxSoundEffectEntity) dataBean);
            mSoundEffectDialog.dismiss();
        }if (dataBean instanceof Device){
            AuxDMControlInstance.newInstance().setServiceDevice((Device) dataBean).setQueryContainerType(FiletypeUtil.FILETYPE_AUDIO).requestDeviceRootContent(DataUitls.newInstance().getContainerCallback());
            mDLNADeviceDialog.dismiss();
        }
    }

    @Override
    protected List<? extends Object> getData() {
        return null;
    }

    private class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            switch (checkedId) {
                case R.id.rb_room:
                    position = 0;
                    break;
                case R.id.rb_control:
                    controlShow();
                    break;
                case R.id.rb_setting:
                    position = 2;
                    break;
            }
            getViewVisibikity();

//            if (isShowControl){
//                position = 1;
//                isShowControl = false;
//            }
            //根据位置得到对应的Fragment
            BaseFragment currentFragment = getFragment();
            mTvTitleView.setText(currentFragment.getTitleTag());
            if (currentFragment instanceof ControlTabFragment)
            {
                mTvTitleView.setText(DataUitls.newInstance().getControlRoomName());
                ((ControlTabFragment) currentFragment).queryPlayMode_PlayStatus();
                AuxDeviceEntity controlDeviceEntity = AuxUdpUnicast.getInstance().getControlDeviceEntity();
                if (controlDeviceEntity != null){
                    AuxRoomEntity[] controlRoomEntities = AuxUdpUnicast.getInstance().getControlRoomEntities();
                    if (controlDeviceEntity.getDevModel() == AuxConfig.DeciveModel.DEVICE_DM838){
                        if (controlRoomEntities.length == 1)
                            mLeftView.setVisibility(View.VISIBLE);
                        else
                            mLeftView.setVisibility(View.INVISIBLE);
                    }else{
                        if (controlRoomEntities != null && controlRoomEntities.length == 1 && controlRoomEntities[0].getSrcID() > 0xB0 && controlRoomEntities[0].getSrcID() < 0xC0){
                            mDLNAView.setVisibility(View.VISIBLE);
                        }else
                            mDLNAView.setVisibility(View.INVISIBLE);
                    }
                }

            }

            //替换fragment
            replaceFragment(mFragment,currentFragment);
        }

    }

    private void controlShow() {

        if (AuxUdpUnicast.getInstance().getControlRoomEntities() == null || AuxUdpUnicast.getInstance().getControlRoomEntities().length == 0) {
            position = 0;
            mRadioGroup.check(R.id.rb_room);
            TT.showToast(MainActivity.this,getString(R.string.select_rooms));
        }
        else
        {
            boolean isMultiControl = SPUtil.getSpUtil().getBooleanValue("isMultiControl");
            if (AuxUdpUnicast.getInstance().getControlRoomEntities().length > 1 && isMultiControl){
                mInquiryDialog.setTitle("询问").setContent("是否要控制一下房间"+"\n"+DataUitls.newInstance().getControlRoomName()).setButtonText("confirm","cancle");
                if (mInquiryDialog.isAdded())
                    mInquiryDialog.getFragmentManager().beginTransaction().show(mInquiryDialog).commit();
                else
                    mInquiryDialog.show(getSupportFragmentManager(),"multidialog");

                LogUtils.i("MainActivity","controlShow:"+!isShowControl);
                if (!isShowControl){
                    position = 0;
                    mRadioGroup.check(R.id.rb_room);
                }else{
                    position = 1;
                }
            }else {
                position = 1;
            }
        }
    }

    private void getViewVisibikity() {
        if (position == 1){
            mLeftView.setVisibility(View.VISIBLE);
            mRightView.setVisibility(View.VISIBLE);
        }else{
            mLeftView.setVisibility(View.INVISIBLE);
            mRightView.setVisibility(View.INVISIBLE);
        }
    }

    private void replaceFragment(BaseFragment lastFragment, BaseFragment currentFragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        LogUtils.i("MainActivity","replaceFragment..."+(lastFragment != currentFragment)+"   position:"+position);
        //如果两个不相等,说明切换了Fragment
        if(lastFragment != currentFragment){
            mFragment = currentFragment;

            //隐藏刚显示的Fragment
            if(lastFragment != null){
                transaction.hide(lastFragment);
            }
            /**
             * 显示 或者 添加当前要显示的Fragment
             *
             * 如果当前要显示的Fragment没添加过 则 添加
             * 如果当前要显示的Fragment被添加过 则 隐藏
             */
            showFragment(currentFragment, transaction);

            if (position == 0)
                isShowControl = false;
        }
    }

    private void showFragment(BaseFragment currentFragment, FragmentTransaction transaction) {
        if(currentFragment != null){
            LogUtils.i("MainActivity","  showFragment: "+(!currentFragment.isAdded()));
            if(!currentFragment.isAdded())
                transaction.add(R.id.fl_main,currentFragment).commit();//commitAllowingStateLoss
            else{
                transaction.show(currentFragment).commit();
            }
        }
    }

    /**
     * 根据返回到对应的Fragment
     * @return
     */
    private BaseFragment getFragment() {
        return mBaseFragments.get(position);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                DLNAUtil.newInstance().initDLNA();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MultiRoomMusicApplication application = (MultiRoomMusicApplication) getApplication();
        application.onDestorySDK();
    }

    private long firstTime=0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN){
            if (System.currentTimeMillis()-firstTime>2000){
                Toast.makeText(MainActivity.this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
                firstTime=System.currentTimeMillis();
            }else{
                AuxUdpUnicast.getInstance().stopWorking();
                AuxUdpBroadcast.getInstace().stopWorking();
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
