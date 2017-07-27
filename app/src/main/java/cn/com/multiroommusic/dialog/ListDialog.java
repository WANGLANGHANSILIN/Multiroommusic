package cn.com.multiroommusic.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.com.multiroommusic.R;
import cn.com.multiroommusic.adapters.MRMCommonAdapter;
import cn.com.multiroommusic.utils.DataUitls;
import cn.com.multiroommusic.utils.LogUtils;

/**
 * Created by wang l on 2017/5/24.
 * 列表对话框
 */

public class ListDialog extends DialogFragment{

    private RecyclerView mRecyclerView;
    private List<Object> mList;
    private MRMCommonAdapter mMrmCommonAdapter;
    private ImageView mIvBackView;
    private TextView mTvTitleView;
    private String mTitle = "";
    private boolean isVisibiliy;
    private String imageTag;
    private View.OnClickListener mListener;


    public ListDialog() {
    }

    public static final ListDialog newInstance()
    {
        ListDialog dialog = new ListDialog();
//        Bundle bundle = new Bundle();
//        bundle.putParcelableArrayList("list", (ArrayList<? extends Parcelable>) list);
//        dialog.setArguments(bundle);
        return dialog ;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ArrayList<Parcelable> list = getArguments().getParcelableArrayList("list");
//        this.mList = list;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View inflate = inflater.inflate(R.layout.activity_baseconmon, null);
        mRecyclerView = (RecyclerView) inflate.findViewById(R.id.rc_base_common);
        View viewById = inflate.findViewById(R.id.include_layout_common);
        mIvBackView = (ImageView) viewById.findViewById(R.id.bt_left_icon);
        mTvTitleView = (TextView) viewById.findViewById(R.id.tv_cent_title);
        inflate.setBackgroundResource(R.mipmap.theme_bg);
        mIvBackView.setImageResource(R.drawable.button_back);
        LogUtils.i("ListDialog","onCreateView...");
        return inflate;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMrmCommonAdapter = new MRMCommonAdapter(getActivity(),mList, R.layout.item_rc_device_common);
        mMrmCommonAdapter.setOnItemClickEventCallback(DataUitls.newInstance().getEventOprationCallback());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mMrmCommonAdapter);
        LogUtils.i("ListDialog","onActivityCreated...");
        mTvTitleView.setText(mTitle);
        mIvBackView.setVisibility(isVisibiliy?View.VISIBLE:View.INVISIBLE);

        if (mListener != null)
            mIvBackView.setOnClickListener(mListener);


        mIvBackView.setImageResource(R.drawable.button_back);
        if (imageTag != null){
            mIvBackView.setTag(imageTag);
            if (imageTag.equals("radioTypeList"))
                mIvBackView.setImageResource(R.mipmap.radio_custom_add);
        }
        LogUtils.i("ListDialog","mTitle:"+mTitle+"   mListener != null:"+(mListener != null) +"   imageTag != null:"+(imageTag != null));
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

    public List getList() {
        return mList;
    }

    public ListDialog setList(List list) {
        mList = list;
        if (mMrmCommonAdapter != null)
            mMrmCommonAdapter.setBaseDataBeenList(mList);
        return this;
    }

    public ListDialog setOnBackViewVisibility(boolean isVisibiliy){
        this.isVisibiliy = isVisibiliy;
        if (mIvBackView != null) {
            LogUtils.i("ListDialog","setOnBackViewVisibility..."+isVisibiliy);
            mIvBackView.setVisibility(isVisibiliy? View.VISIBLE:View.INVISIBLE);
        }
        return this;
    }

    public ListDialog setListDialogTitle(String title){
        this.mTitle = title;
        if (mTvTitleView != null) {
            LogUtils.i("ListDialog","setListDialogTitle..."+mTitle);
            mTvTitleView.setText(mTitle);
        }
        return this;
    }

    public ListDialog setImageViewTagListener(String tag, View.OnClickListener listener){
        this.imageTag = tag;
        this.mListener = listener;
        if (mIvBackView != null){
            LogUtils.i("ListDialog","setImageViewTagListener..."+tag);
            mIvBackView.setTag(imageTag);
            mIvBackView.setOnClickListener(listener);

            if (imageTag.equals("radioTypeList"))
                mIvBackView.setImageResource(R.mipmap.radio_custom_add);
            else
                mIvBackView.setImageResource(R.drawable.button_back);
        }
        return this;
    }


    public String getImageTag() {
        return imageTag;
    }
}
