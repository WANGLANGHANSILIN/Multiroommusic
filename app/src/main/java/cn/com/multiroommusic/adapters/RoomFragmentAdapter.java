package cn.com.multiroommusic.adapters;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.auxdio.protocol.bean.AuxRoomEntity;
import cn.com.auxdio.protocol.bean.AuxSourceEntity;
import cn.com.auxdio.protocol.net.AuxUdpUnicast;
import cn.com.multiroommusic.R;
import cn.com.multiroommusic.bean.MRMRoomBean;
import cn.com.multiroommusic.callback.OnItemClickEventCallback;
import cn.com.multiroommusic.utils.DataUitls;
import cn.com.multiroommusic.utils.LogUtils;
import cn.com.multiroommusic.utils.RoomUtil;
import cn.com.multiroommusic.utils.SPUtil;
import cn.com.multiroommusic.utils.TT;

/**
 * Created by wang l on 2017/5/22.
 * 房间适配器...
 */

public class RoomFragmentAdapter extends BaseRecycleAdapter implements OnItemClickEventCallback {
    public RoomFragmentAdapter(Context context, List baseDataBeen, int layoutID) {
        super(context, baseDataBeen, layoutID);
        setOnItemClickEventCallback(this);
    }

    @Override
    public void onBind(BaseViewHodle hodle, Object baseDataBean, int position) {
        final MRMRoomBean mrmRoomBean = (MRMRoomBean) baseDataBean;
        LogUtils.i("onBind","mrmRoomBean"+mrmRoomBean.toString()+"position:"+position);
        CheckBox checkBox = hodle.getView(R.id.cb_item_rc_room_check);
        TextView tvRoomName = hodle.getView(R.id.tv_item_rc_room_name);
        TextView tvSrcName = hodle.getView(R.id.tv_item_rc_room_source_name);
        TextView tvSrcVolume = hodle.getView(R.id.tv_item_rc_room_volume);
        final Switch aSwitch = hodle.getView(R.id.switch_layout_inquiry);

        checkBox.setVisibility(mrmRoomBean.isSelector()? View.VISIBLE:View.INVISIBLE);
        int color_red = getContext().getResources().getColor(R.color.color_checked);
        int color_yell= getContext().getResources().getColor(R.color.color_checked_yell);
        checkBox.setBackgroundColor(position%2 == 0? color_red:color_yell);

        String roomSrcName = mrmRoomBean.getRoomSrcName();
        SPUtil.getSpUtil().getIntValue("");
        tvRoomName.setText(mrmRoomBean.getRoomName());
        tvSrcName.setText(roomSrcName);//roomSrcName.equals("")?getSourceName(mrmRoomBean.getSrcID()):
        tvSrcVolume.setText(mrmRoomBean.getVolumeID()+"%");
        aSwitch.setChecked(mrmRoomBean.getoNOffState() == 1);

        if (mrmRoomBean.getoNOffState() != 1)
            mrmRoomBean.setSelector(false);
        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.i("onBind","OnClickListener  "+aSwitch.isChecked());
                if (!aSwitch.isChecked())
                    mrmRoomBean.setSelector(false);
                AuxUdpUnicast.getInstance().setRoomOnOffState(mrmRoomBean,aSwitch.isChecked());
            }
        });
    }

    private AuxRoomEntity[] getSelectorRoomList() {
        List baseDataBeenList = getBaseDataBeenList();
        List<MRMRoomBean> mrmRoomList = new ArrayList<>();
        for (Object o : baseDataBeenList) {
            MRMRoomBean roomBean = (MRMRoomBean) o;
            if(roomBean.isSelector()){
                mrmRoomList.add((MRMRoomBean) o);
            }
        }
        return RoomUtil.convertArray(mrmRoomList);
    }

    private String getSourceName(int srcID) {
        List<? extends AuxSourceEntity> currentRoomSourceList = DataUitls.newInstance().getCurrentRoomSourceList();
        if (currentRoomSourceList != null){
            for (AuxSourceEntity auxSourceEntity : currentRoomSourceList) {
                if (auxSourceEntity.getSourceID() == srcID)
                    return auxSourceEntity.getSourceName();
            }
        }
        return "";
    }

    //房间选择处理操作
    private void checkHandle(MRMRoomBean mrmRoomBean) {
        if (mrmRoomBean.getoNOffState() == 0x00) {
            TT.showToast(getContext(),getContext().getResources().getString(R.string.channel_open));
            return;
        }
        synchronized (RoomFragmentAdapter.class){
            if (mrmRoomBean.isSelector())
                mrmRoomBean.setSelector(false);
            else
                mrmRoomBean.setSelector(true);
        }

    }

    private boolean isExistRoom(AuxRoomEntity mrmRoomBean, List<AuxRoomEntity> clone) {
        for (AuxRoomEntity auxRoomEntity : clone) {
            LogUtils.i("checkHandle","equals:"+(auxRoomEntity.equals(mrmRoomBean)));
            if (auxRoomEntity.getRoomIP().equals(mrmRoomBean.getRoomIP()) && auxRoomEntity.getRoomID() == mrmRoomBean.getRoomID()){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onItemClick(Object dataBean, int position) {
        checkHandle((MRMRoomBean) dataBean);
        AuxUdpUnicast.getInstance().setControlRoomEntities(getSelectorRoomList());
        notifyDataSetChanged();
    }
}
