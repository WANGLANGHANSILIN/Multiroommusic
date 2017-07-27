package cn.com.multiroommusic.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import cn.com.auxdio.protocol.net.AuxUdpUnicast;
import cn.com.multiroommusic.R;
import cn.com.multiroommusic.utils.LogUtils;

/**
 * Created by wang l on 2017/6/1.
 * 高低音对话框
 */

public class HighLowDialog extends DialogFragment implements SeekBar.OnSeekBarChangeListener {

    private TextView mTv_hight;
    private TextView mTv_low;
    private SeekBar mHightSeekBar,mLowSeekBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View inflate = inflater.inflate(R.layout.dialog_higt_low_setting, null);
        mTv_hight = (TextView) inflate.findViewById(R.id.tv_show_higt_sound);
        mTv_low = (TextView) inflate.findViewById(R.id.tv_show_low_sound);
        mHightSeekBar = (SeekBar) inflate.findViewById(R.id.sb_hight_set);
        mLowSeekBar = (SeekBar) inflate.findViewById(R.id.sb_low_set);
        mLowSeekBar.setOnSeekBarChangeListener(this);
        mHightSeekBar.setOnSeekBarChangeListener(this);
        return inflate;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int lowPitch = AuxUdpUnicast.getInstance().getControlRoomEntities()[0].getLowPitch();
        int highPitch = AuxUdpUnicast.getInstance().getControlRoomEntities()[0].getHighPitch();
        LogUtils.i("HighLowDialog","onActivityCreated: lowPitch"+lowPitch+"   highPitch:"+highPitch);
        mLowSeekBar.setProgress(lowPitch+10);
        mHightSeekBar.setProgress(highPitch+10);
        mTv_low.setText(getString(R.string.Bass_text)+"："+getValue(mLowSeekBar.getProgress()));
        mTv_hight.setText(getString(R.string.Treble_text)+"："+getValue(mHightSeekBar.getProgress()));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager windowManager = getDialog().getWindow().getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;
        Display defaultDisplay = getDialog().getWindow().getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams attributes = getDialog().getWindow().getAttributes();
        attributes.gravity = Gravity.CENTER_VERTICAL;
        getDialog().getWindow().setLayout(width*3/4, height*3/6);
    }

    private String getValue(int progress){
        String vaule = "";
        int i = progress - 10;
        if (i > 0)
            vaule = "+"+i;
        else
            vaule = ""+i;
        return vaule;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        LogUtils.i("HighLowDialog","onProgressChanged:"+seekBar.getId()+"   progress:"+progress);
        if (seekBar.getId() == R.id.sb_hight_set){
            mTv_hight.setText("高音："+getValue(progress));
            AuxUdpUnicast.getInstance().setHighLowPitch(progress - 10,mLowSeekBar.getProgress()-10);
        }else if (seekBar.getId() == R.id.sb_low_set){
            mTv_low.setText("低音："+getValue(progress));
            AuxUdpUnicast.getInstance().setHighLowPitch(mHightSeekBar.getProgress()-10,progress - 10);
        }
        seekBar.setProgress(progress);
    }
}
