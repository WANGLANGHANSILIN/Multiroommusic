package cn.com.multiroommusic.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.com.multiroommusic.R;

/**
 * Created by wang l on 2017/5/19.
 */

public abstract class BaseFragment extends Fragment{
    protected Context mContext;
    protected String mTitleTag = "";
    /**
     * 初始化上下文
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    /**
     * Fragment视图创建
     * 子类必须实现该抽象方法 用于加载自己的视图
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = initView();
        view.setBackgroundResource(R.mipmap.theme_bg);
        return view;
    }

    /**
     * 用于强制子类实现加载自己的视图
     *
     * @return
     */
    protected abstract View initView();

    /**
     * 当Activity的onCreate方法返回时调用
     * 用于初始化数据 相当于Activity的OnCreate方法调用
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    /**
     * 子类用于初始化数据
     */
    protected void initData(){}


    public String getTitleTag() {
        return mTitleTag;
    }
}
