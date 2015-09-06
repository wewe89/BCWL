package cn.appem.bcwl.ui.fragemnt;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wewe.android.util.ToastUtil;
import com.wewe.android.widget.SlidingTabLayout;

import butterknife.InjectView;
import cn.appem.bcwl.AppContext;
import cn.appem.bcwl.R;
import cn.appem.bcwl.adapter.WaybillTabAdapter;
import cn.appem.bcwl.ui.LoginActivity;
import cn.appem.bcwl.ui.base.BaseFragment;
import cn.appem.bcwl.ui.waybill.RecordSearchActivity;
import cn.appem.bcwl.ui.waybill.WaybillRecordFragment;

/**
 */
public class WaybillTabFragment extends BaseFragment {
    @InjectView(R.id.waybill_viewpager)
    ViewPager mViewPager;
    @InjectView(R.id.waybill_slidingtab)
    SlidingTabLayout mSlidingTabLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_waybill, container, false);
        return root;
    }

    private WaybillTabAdapter mAdapter;
    boolean isLogin() {
        boolean res = AppContext.isLogin();
        if (res) {
            return true;
        } else {
            ToastUtil.showToastShort("请先登录");
            startActivity(new Intent(mContext, LoginActivity.class));
            return false;
        }
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle("我的运单");
        getNavLeft().setVisibility(View.GONE);

        Drawable drawable = getResources().getDrawable(R.mipmap.ic_action_search);
        // 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        getNavRight().setCompoundDrawables(null, null, drawable, null);
        getNavRight().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLogin())
                    startActivity(new Intent(mContext, RecordSearchActivity.class));
            }
        });

        mAdapter = new WaybillTabAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(mAdapter.getCount());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                WaybillRecordFragment.SELECT_TYPE = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.actionbar_bg));
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

}
