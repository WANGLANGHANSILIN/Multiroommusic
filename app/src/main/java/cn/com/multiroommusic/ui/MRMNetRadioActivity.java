package cn.com.multiroommusic.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.com.auxdio.protocol.bean.AuxNetRadioEntity;
import cn.com.auxdio.protocol.bean.AuxNetRadioTypeEntity;
import cn.com.auxdio.protocol.net.AuxUdpUnicast;
import cn.com.multiroommusic.R;
import cn.com.multiroommusic.adapters.SourceAdapter;
import cn.com.multiroommusic.bean.MRMRadioBean;
import cn.com.multiroommusic.dialog.ListDialog;
import cn.com.multiroommusic.utils.DataUitls;
import cn.com.multiroommusic.utils.RoomUtil;
import cn.com.multiroommusic.utils.TT;

/**
 * Created by wang l on 2017/5/27.
 */

public class MRMNetRadioActivity extends BaseCommonActivity {

    private SourceAdapter mSourceAdapter;
    private ListDialog mAddRadioDialog;

    @Override
    protected List<? extends Object> getData() {
        List<AuxNetRadioTypeEntity> netRadioTypeEntities = DataUitls.newInstance().getAuxNetRadioTypeEntities();
        return netRadioTypeEntities;
    }

    @Override
    public void onItemClick(Object dataBean, int position) {
        if (dataBean instanceof AuxNetRadioTypeEntity){
            TT.showToast(this,dataBean.toString());
            List<AuxNetRadioEntity> netRadioList = ((AuxNetRadioTypeEntity) dataBean).getNetRadioList();
            mSourceAdapter.setBaseDataBeenList(null);
            mSourceAdapter.setBaseDataBeenList(RoomUtil.getRadioList(netRadioList));
            mRightView.setVisibility(View.VISIBLE);
            mRightView.setImageResource(R.mipmap.checkbox_pressed);
        }else if(dataBean instanceof MRMRadioBean){
            boolean check = ((MRMRadioBean) dataBean).isCheck();
            if (check)
                ((MRMRadioBean) dataBean).setCheck(false);
            else
                ((MRMRadioBean) dataBean).setCheck(true);
        }else if(dataBean instanceof String){
            for (MRMRadioBean mrmRadioBean : getSelectorRadioList()) {
                if (position == 0)
                    AuxUdpUnicast.getInstance().addNetRadio(mrmRadioBean);
                else if (position == 1)
                    AuxUdpUnicast.getInstance().delNetRadio(mrmRadioBean);
            }
            mAddRadioDialog.dismiss();
        }
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        mSourceAdapter = new SourceAdapter(this, getData(), R.layout.item_rc_source_);
        mSourceAdapter.setOnItemClickEventCallback(this);
        return mSourceAdapter;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_left_icon:
                this.finish();
                break;
            case R.id.bt_right_icon:
                if (getSelectorRadioList() == null || getSelectorRadioList().size() < 1)
                    return;
                mAddRadioDialog = new ListDialog();
                mAddRadioDialog.setList(getDialogList());
                mAddRadioDialog.setListDialogTitle("添加或删除电台");
                mAddRadioDialog.show(getSupportFragmentManager(),"addRadioDialog");
                DataUitls.newInstance().setEventOprationCallback(this);
                break;
        }
    }

    private List<String> getDialogList(){
        List<String> stringList = new ArrayList<>();
        stringList.add("添加到设备");
        stringList.add("从设备中删除");
        stringList.add("取消...");
        return stringList;
    }

    private List<MRMRadioBean> getSelectorRadioList(){
        List<MRMRadioBean> selectorList = new ArrayList<>();
        List<MRMRadioBean> radioBeanList = mSourceAdapter.getBaseDataBeenList();
        if (radioBeanList == null)
            return null;
        for (MRMRadioBean mrmRadioBean : radioBeanList) {
            if (mrmRadioBean.isCheck()){
                selectorList.add(mrmRadioBean);
            }
        }
        return selectorList;
    }

}
