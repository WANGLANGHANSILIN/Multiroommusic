package cn.com.multiroommusic.adapters;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.com.auxdio.protocol.bean.AuxNetRadioTypeEntity;
import cn.com.multiroommusic.R;
import cn.com.multiroommusic.bean.MRMRadioBean;
import cn.com.multiroommusic.bean.MRMSourceBean;

/**
 * Created by wang l on 2017/5/27.
 */

public class SourceAdapter extends BaseRecycleAdapter {
    private int[] mresIcons = {R.mipmap.icon_source_usb,R.mipmap.icon_source_sd,R.mipmap.icon_source_aux,R.mipmap.icon_source_dvd,
            R.mipmap.icon_source_radio,R.mipmap.icon_source_dlna};
    public SourceAdapter(Context context, List baseDataBeenList, int layoutID) {
        super(context, baseDataBeenList, layoutID);
    }

    @Override
    public void onBind(BaseViewHodle hodle, Object baseDataBean, int position) {
        ImageView iconView = hodle.getView(R.id.iv_item_source_icon);
        TextView tvNameView = hodle.getView(R.id.tv_item_source_name);
        final ImageView checkView = hodle.getView(R.id.cb_item_source_check);
        boolean isCheck = false;
        int color_red = getContext().getResources().getColor(R.color.color_checked);
        if (baseDataBean instanceof MRMSourceBean){
            isCheck = ((MRMSourceBean) baseDataBean).isCheck();
            tvNameView.setText(((MRMSourceBean) baseDataBean).getSourceName());
            iconView.setImageResource(getResourceID((MRMSourceBean) baseDataBean));
            checkView.setBackgroundColor(color_red);
            checkView.setVisibility(View.VISIBLE);
        }else if (baseDataBean instanceof MRMRadioBean){
            isCheck = ((MRMRadioBean) baseDataBean).isCheck();
            iconView.setImageResource(mresIcons[4]);
            tvNameView.setText(((MRMRadioBean) baseDataBean).getRadioName());
            checkView.setVisibility(View.VISIBLE);
        }else if (baseDataBean instanceof AuxNetRadioTypeEntity){
            iconView.setImageResource(R.mipmap.icon_folder);
            tvNameView.setText(((AuxNetRadioTypeEntity) baseDataBean).getRadioType());
            checkView.setVisibility(View.INVISIBLE);
        }
        checkView.setSelected(isCheck);
    }

    private int getResourceID(MRMSourceBean baseDataBean) {
        int positon = -1;
        int sourceID = baseDataBean.getSourceID();
        switch (sourceID)
        {
            case 0x41:
                positon = 3;
                break;
            case 0x51:
            case 0x52:
                positon = 2;
                break;
            case 0x01:
            case 0x81:
                positon = 1;
            case 0x91:
            case 0xA1:
                positon = 0;
        }
        if (sourceID > 0xD0){
            positon = 0;
        }else if (sourceID > 0xC0){
            positon = 4;
        }else if(sourceID > 0xB0)
            positon = 5;
        return mresIcons[positon];
    }
}
