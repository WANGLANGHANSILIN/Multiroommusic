package cn.com.multiroommusic.ui;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import cn.com.multiroommusic.R;
import cn.com.multiroommusic.utils.SPUtil;

/**
 * Created by wang l on 2017/5/19.
 */

public class MRMInquiryActivity extends BaseMRMActivity {

    @Override
    public int initRootViewLayoutID() {
        return R.layout.activity_inquiry;
    }

    @Override
    public int getTitleViewlayoutID() {

        Switch aSwitch = (Switch) findViewById(R.id.switch_layout_inquiry);
        aSwitch.setChecked(SPUtil.getSpUtil().getBooleanValue("isMultiControl"));
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtil.getSpUtil().putValue("isMultiControl",isChecked);
            }
        });

        return R.id.include_layout_inquiry;
    }

    @Override
    public void onClick(View v) {
        this.finish();
    }
}
