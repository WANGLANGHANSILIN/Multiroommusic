package cn.com.multiroommusic.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import cn.com.auxdio.protocol.bean.AuxNetModelEntity;
import cn.com.auxdio.protocol.bean.AuxRoomEntity;
import cn.com.auxdio.protocol.interfaces.AuxRequestNetModelListener;
import cn.com.auxdio.protocol.net.AuxUdpUnicast;
import cn.com.multiroommusic.R;
import cn.com.multiroommusic.adapters.ModelBindAdpater;
import cn.com.multiroommusic.bean.MRMRoomBean;
import cn.com.multiroommusic.dialog.ListDialog;
import cn.com.multiroommusic.utils.DataUitls;
import cn.com.multiroommusic.utils.LogUtils;

/**
 * Created by wang l on 2017/5/27.
 */

public class MRMNetModelBindActivity extends BaseCommonActivity implements AuxRequestNetModelListener.NetModelBindTypeListener {

    private TextView mTv_typeDefault;
    private TextView mTv_typeSync;
    private TextView mTv_typeCustom;
    private ModelBindAdpater mModelBindAdpater;
    private ListDialog mListDialog;
    private int onClickRoomPosition = -1;
    private int mModelBindType;

    @Override
    public int getTitleViewlayoutID() {
        mTv_typeDefault = (TextView) findViewById(R.id.tv_model_bind_type_defautl);
        mTv_typeSync = (TextView) findViewById(R.id.tv_model_bind_type_async);
        mTv_typeCustom = (TextView) findViewById(R.id.tv_model_bind_type_coustom);
        mTv_typeCustom.setOnClickListener(this);
        mTv_typeSync.setOnClickListener(this);
        mTv_typeDefault.setOnClickListener(this);
        resetSelector(mModelBindType);
        return super.getTitleViewlayoutID();
    }

    @Override
    public int initRootViewLayoutID() {
        AuxUdpUnicast.getInstance().requestNetModelRelevanceType(this);
        return R.layout.activity_model_bind;
    }

    @Override
    public void onClick(View v) {
        resetSelector();
        switch (v.getId()){
            case R.id.tv_model_bind_type_defautl:
                mModelBindType = 1;
                break;
            case R.id.tv_model_bind_type_async:
                mModelBindType = 2;
                break;
            case R.id.tv_model_bind_type_coustom:
                mModelBindType = 3;
                break;
            default:
                this.finish();
                return;
        }
        v.setSelected(true);
        resetSelector(mModelBindType);
        DataUitls.newInstance().setModelBindType(mModelBindType);
        AuxUdpUnicast.getInstance().setNetModelRelevanceType(mModelBindType);
    }
    private void resetSelector() {
        mTv_typeDefault.setSelected(false);
        mTv_typeCustom.setSelected(false);
        mTv_typeSync.setSelected(false);
    }
    private void resetSelector(int position) {
        if (position == 1){
            mTv_typeDefault.setSelected(true);
        }
        else if (position == 2) {
            mTv_typeSync.setSelected(true);
        }
        else if (position == 3) {
            mTv_typeCustom.setSelected(true);
        }
    }



    @Override
    public void onItemClick(Object dataBean, int position) {
        LogUtils.i("MRMNetModelBindActivity","position:"+position+"    "+dataBean.toString());

        if (dataBean instanceof MRMRoomBean && mModelBindType == 3){
            onClickRoomPosition = position;
            mListDialog = new ListDialog();
            mListDialog.setListDialogTitle(((MRMRoomBean) dataBean).getRoomName()+" Netmodel Change");
            mListDialog.setList(DataUitls.newInstance().getAuxNetModelEntities());
            mListDialog.show(getSupportFragmentManager(),"modelDialog");
            DataUitls.newInstance().setEventOprationCallback(this);
        }else if (dataBean instanceof AuxNetModelEntity){
            Object o = getData().get(onClickRoomPosition);
            AuxUdpUnicast.getInstance().setBindRoomforNetModel((AuxRoomEntity) o,(AuxNetModelEntity)dataBean);
            mListDialog.dismiss();
            mModelBindAdpater.notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        LogUtils.i("MRMNetModelBindActivity","getAdapter:"+getData().size());
        mModelBindAdpater = new ModelBindAdpater(this, getData(), R.layout.item_rc_model_bind);
        mModelBindAdpater.setOnItemClickEventCallback(this);
        return mModelBindAdpater;
    }

    @Override
    protected List<? extends Object> getData() {
        return DataUitls.newInstance().getAuxRoomEntities();
    }

    @Override
    public void onRelevanceType(int i) {
        mModelBindType = i;
        DataUitls.newInstance().setModelBindType(mModelBindType);
    }
}
