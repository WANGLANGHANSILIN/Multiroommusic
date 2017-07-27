package cn.com.multiroommusic.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import cn.com.auxdio.protocol.bean.AuxRoomEntity;
import cn.com.auxdio.protocol.net.AuxUdpUnicast;
import cn.com.multiroommusic.R;
import cn.com.multiroommusic.adapters.MRMCommonAdapter;

/**
 * Created by wang l on 2017/6/2.
 * 重命名对话框
 */

public class RenameDialog extends DialogFragment implements View.OnClickListener{
    private AuxRoomEntity mAuxRoomEntity;
    private EditText mETInput;
    private MRMCommonAdapter mCommonAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.dialog_rename_room, container);
        mETInput = (EditText) inflate.findViewById(R.id.et_dialog_rename_edit);
        inflate.findViewById(R.id.bt_dialog_rename_cancle).setOnClickListener(this);
        inflate.findViewById(R.id.bt_dialog_rename_comfirm).setOnClickListener(this);
        return inflate;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_dialog_rename_comfirm){
            if (mAuxRoomEntity != null)
                AuxUdpUnicast.getInstance().setRoomName(mAuxRoomEntity,mETInput.getText().toString());
        }
        mCommonAdapter.notifyDataSetChanged();
        dismiss();
    }

    public void setAuxRoomEntity(AuxRoomEntity auxRoomEntity, MRMCommonAdapter mrmCommonAdapter) {
        mAuxRoomEntity = auxRoomEntity;
        mCommonAdapter = mrmCommonAdapter;
    }
}
