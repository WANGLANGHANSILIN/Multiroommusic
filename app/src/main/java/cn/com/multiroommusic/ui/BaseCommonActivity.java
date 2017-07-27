package cn.com.multiroommusic.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import cn.com.multiroommusic.R;
import cn.com.multiroommusic.adapters.MRMCommonAdapter;
import cn.com.multiroommusic.callback.OnItemClickEventCallback;

/**
 * Created by wang l on 2017/5/19.
 */

public abstract class BaseCommonActivity extends BaseMRMActivity implements OnItemClickEventCallback {


    protected MRMCommonAdapter mMrmCommonAdapter;
    protected RecyclerView mMRecycleView;

    @Override
    public int initRootViewLayoutID() {
        return R.layout.activity_baseconmon;
    }

    @Override
    public int getTitleViewlayoutID() {
        initView();
        return R.id.include_layout_common;
    }
    private void initView() {
        mMRecycleView = (RecyclerView)findViewById(R.id.rc_base_common);
        mMRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mMRecycleView.setAdapter(getAdapter());
    }

    public RecyclerView.Adapter getAdapter(){
        mMrmCommonAdapter = new MRMCommonAdapter(this, getData(), R.layout.item_rc_device_common);
        mMrmCommonAdapter.setOnItemClickEventCallback(this);
        return mMrmCommonAdapter;
    }

    protected abstract List<? extends Object> getData();



}
