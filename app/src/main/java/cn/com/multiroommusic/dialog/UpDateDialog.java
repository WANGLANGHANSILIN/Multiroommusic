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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.com.multiroommusic.R;
import cn.com.multiroommusic.callback.DownloadCompleteCallBack;
import cn.com.multiroommusic.upgrade.MRMDownFileThread;
import cn.com.multiroommusic.utils.DataUitls;

/**
 * Created by wang l on 2017/5/26.
 * 更新对话框
 */

public class UpDateDialog extends DialogFragment implements View.OnClickListener,DownloadCompleteCallBack {

    protected TextView mTv_title;
    protected TextView mTv_content;
    protected Button mBt_cancle;
    protected Button mBv_confirm;
    protected ProgressBar mPb_progressBar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_update,null);
        view.setBackgroundResource(R.mipmap.theme_bg);
        mTv_title = (TextView) view.findViewById(R.id.tv_dialog_update_title);
        mTv_content = (TextView) view.findViewById(R.id.tv_dialog_update_content);
        mPb_progressBar = (ProgressBar) view.findViewById(R.id.pb_dialog_progress);
        mBt_cancle = (Button) view.findViewById(R.id.bt_dialog_update_cancle);
        mBv_confirm = (Button) view.findViewById(R.id.bt_dialog_update_cigfim);
        mBt_cancle.setOnClickListener(this);
        mBv_confirm.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DataUitls.newInstance().setDownloadCompleteCallBack(this);
        mTv_title.setText(getActivity().getResources().getString(R.string.update_now));
        String format = String.format("%s %s, %s", getActivity().getResources().getString(R.string.new_version),
                DataUitls.newInstance().getAppUpdateVersion(),
                getActivity().getResources().getString(R.string.want_to_upgrade));
//        String text = String.format("%s %s, %s",);
        mTv_content.setText(format);
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
        getDialog().getWindow().setLayout(width*3/4, height*3/8);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_dialog_update_cigfim){
            new MRMDownFileThread(getActivity()).start();
        }else
            dismiss();
    }

    @Override
    public void onDownloadComplate(int progress) {
        mPb_progressBar.setProgress(progress);
        if (progress == 100)
            dismiss();
    }
}
