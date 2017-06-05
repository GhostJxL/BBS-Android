package com.twtstudio.bbs.bdpqchen.bbs.main;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BaseFragment;
import com.twtstudio.bbs.bdpqchen.bbs.main.hot.HotFragment;
import com.twtstudio.bbs.bdpqchen.bbs.main.latest.LatestFragment;

import javax.inject.Inject;

/**
 * Created by bdpqchen on 17-6-5.
 */

public class MainTabAdapter extends FragmentPagerAdapter {
    //    private static final int PAGE_COUNT = 2;
    private Context mContext;
    private static String[] titles = {"最新动态", "全站十大"};
    private BaseFragment[] fragments = new BaseFragment[2];


    @Inject
    public MainTabAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;

    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return LatestFragment.newInstance();
            case 1:
                return HotFragment.newInstance();
        }
        return LatestFragment.newInstance();
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
