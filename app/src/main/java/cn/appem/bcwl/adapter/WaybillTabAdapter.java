package cn.appem.bcwl.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.appem.bcwl.ui.waybill.WaybillRecordFragment;

/**
 * User:wewecn on 2015/7/14 14:21
 * Email:wewecn@qq.com
 */
public class WaybillTabAdapter extends FragmentPagerAdapter {
    private List<Fragment> mData;

    public WaybillTabAdapter(FragmentManager fm) {
        super(fm);
        mData = new ArrayList<>();
        mData.add(WaybillRecordFragment.newInstance(WaybillRecordFragment.TYPE_POST));
        mData.add(WaybillRecordFragment.newInstance(WaybillRecordFragment.TYPE_RECEIVE));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0)
            title = "发货记录";
        else if (position == 1)
            title = "收货记录";
        return title;
    }

    @Override
    public Fragment getItem(int position) {
        return mData.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }
}
