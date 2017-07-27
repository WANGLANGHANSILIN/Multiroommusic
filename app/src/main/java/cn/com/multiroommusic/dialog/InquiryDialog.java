package cn.com.multiroommusic.dialog;

import android.os.Bundle;
import android.view.View;

import cn.com.multiroommusic.R;
import cn.com.multiroommusic.utils.DataUitls;

/**
 * Created by wang l on 2017/5/27.
 * 询问对话框
 */

public class InquiryDialog extends UpDateDialog implements View.OnClickListener {

    private String mTitle;
    private String mContent;
    private String mConfirm,mCancle;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPb_progressBar.setVisibility(View.INVISIBLE);
        mTv_title.setText(mTitle);
        mTv_content.setText(mContent);
        mBt_cancle.setText(mCancle);
        mBv_confirm.setText(mConfirm);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_dialog_update_cigfim)
            DataUitls.newInstance().getFragmentChangedCallBack().onFragmentChanged(1);
        dismiss();
    }
    public InquiryDialog setTitle(String title){
        this.mTitle = title;
        return this;
    }

    public InquiryDialog setButtonText(String confirm,String cancle){
        this.mConfirm = confirm;
        this.mCancle = cancle;
        return this;
    }

    public InquiryDialog setContent(String content){
        this.mContent = content;
        return this;
    }

}
