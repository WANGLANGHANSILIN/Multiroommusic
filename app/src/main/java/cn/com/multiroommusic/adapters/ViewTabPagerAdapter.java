package cn.com.multiroommusic.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by wang l on 2017/5/19.
 */

public class ViewTabPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragments;
    private String[] mStrings = new String[]{"房间","控制","设置"};
    public ViewTabPagerAdapter(FragmentManager fm,List<Fragment> fragments) {
        super(fm);
        this.mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mStrings[position];
    }
}
