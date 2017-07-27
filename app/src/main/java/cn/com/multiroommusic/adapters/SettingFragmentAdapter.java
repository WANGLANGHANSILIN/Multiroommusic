package cn.com.multiroommusic.adapters;

import android.content.Context;
import android.widget.TextView;

import java.util.List;

import cn.com.multiroommusic.R;
import cn.com.multiroommusic.bean.MRMSettingBean;

/**
 * Created by wang l on 2017/5/19.
 */

public class SettingFragmentAdapter extends BaseRecycleAdapter<MRMSettingBean> {

    public SettingFragmentAdapter(Context context, List<MRMSettingBean> baseDataBeen, int layoutID) {
        super(context, baseDataBeen, layoutID);
    }

    @Override
    public int getItemViewType(int position) {
        return getBaseDataBeenList().size()-2;
    }


    @Override
    public void onBind(BaseViewHodle hodle, Object baseDataBean, final int position) {
        MRMSettingBean MRMSettingBean = (MRMSettingBean) baseDataBean;
        TextView view = (hodle.getView(android.R.id.text1));
        view.setTextColor(getContext().getResources().getColor(R.color.font_color));
        view.setText(MRMSettingBean.getSettingName());
    }
}
