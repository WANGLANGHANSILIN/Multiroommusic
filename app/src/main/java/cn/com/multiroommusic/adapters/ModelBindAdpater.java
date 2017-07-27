package cn.com.multiroommusic.adapters;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import java.util.List;

import cn.com.multiroommusic.R;
import cn.com.multiroommusic.bean.MRMRoomBean;
import cn.com.multiroommusic.callback.NetModelChangedCallBack;
import cn.com.multiroommusic.utils.DataUitls;
import cn.com.multiroommusic.utils.LogUtils;
import cn.com.multiroommusic.utils.RoomUtil;

/**
 * Created by wang l on 2017/5/31.
 */

public class ModelBindAdpater extends BaseRecycleAdapter implements NetModelChangedCallBack {

    public ModelBindAdpater(Context context, List baseDataBeenList, int layoutID) {
        super(context, baseDataBeenList, layoutID);
        DataUitls.newInstance().setNetModelChangedCallBack(this);
    }

    @Override
    public void onBind(BaseViewHodle hodle, Object baseDataBean, int position) {
        TextView tvRoomName = hodle.getView(R.id.tv_item_rc_model_room_name);
        TextView tvModelName = hodle.getView(R.id.tv_item_rc_model_model_name);
        LogUtils.i("ModelBindAdpater","onBind。。。"+getBaseDataBeenList().size());
        MRMRoomBean mrmRoomBean = (MRMRoomBean) baseDataBean;

        if (DataUitls.newInstance().getModelBindType() == 3){
            hodle.itemView.setBackgroundResource(position%2 == 0? R.drawable.list_item_bg1:R.drawable.list_item_bg2);
        }else{
            hodle.itemView.setBackgroundColor(getContext().getResources().getColor(R.color.color_gray_sw));
        }
        tvModelName.setText(RoomUtil.findNetModelByroomID(mrmRoomBean.getRoomID()).getModelName());
        tvRoomName.setText(mrmRoomBean.getRoomName());
    }

    @Override
    public void onNetModelChanged() {
        ((Activity)getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ModelBindAdpater.this.notifyDataSetChanged();
            }
        });

    }
}
