package cn.appem.bcwl.ui.fragemnt;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wewe.android.util.ToastUtil;
import com.wewe.android.widget.DotView;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.appem.bcwl.R;
import cn.appem.bcwl.ui.BranchActivity;
import cn.appem.bcwl.ui.GuideActivity;
import cn.appem.bcwl.ui.LoginActivity;
import cn.appem.bcwl.ui.ToolsActivity;
import cn.appem.bcwl.ui.base.BaseFragment;
import cn.appem.bcwl.ui.waybill.DraftsEditActivity;
import cn.appem.bcwl.ui.waybill.WaybillSerchActivity;
import cn.appem.bcwl.util.UserPref;

/**
 * User:wewecn on 2015/6/24 10:02
 * Email:wewecn@qq.com
 */
public class IndexFragment extends BaseFragment {
    @InjectView(R.id.index_viewpager)
    ViewPager mViewPager;
    @InjectView(R.id.index_dotview)
    DotView mDotView;
    private ImageView[] mImageViews;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_index, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setTitle("首页");
        getNavLeft().setVisibility(View.GONE);

//        getNavRight().setText("引导页");
//        getNavRight().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(mContext, GuideActivity.class));
//            }
//        });

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().showImageOnLoading(R.mipmap.default_image).cacheOnDisk(true).cacheInMemory(true).showImageOnFail(R.mipmap.default_image).build();

        String[] imgIdArray = new String[]{"http://www.scbcwl.com/areas/web/images/web_dg2.jpg", "http://www.scbcwl.com/areas/web/images/web_dg1.jpg"};

        mImageViews = new ImageView[imgIdArray.length];
        for (int i = 0; i < mImageViews.length; i++) {
            mImageViews[i] = new ImageView(mContext);
            mImageViews[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageLoader.displayImage(imgIdArray[i], mImageViews[i], options);
        }
        ImageAdapter adapter = new ImageAdapter();
        mViewPager.setAdapter(adapter);
        mDotView.setNum(imgIdArray.length);
        mDotView.setSelected(0);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mDotView.setSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private static final long ANIM_VIEWPAGER_DELAY = 3000;

    private Runnable animateViewPager = new Runnable() {
        public void run() {
            if (isLoop && mImageViews.length > 0) {
                mViewPager.setCurrentItem((mViewPager.getCurrentItem() + 1)
                        % mImageViews.length, true);
                autoPlay(mViewPager);
            }
        }
    };

    private void autoPlay(ViewPager viewPager) {
        viewPager.postDelayed(animateViewPager, ANIM_VIEWPAGER_DELAY);
    }

    @Override
    public void onPause() {
        super.onPause();
        isLoop = false;
    }

    private boolean isLoop = false;

    @Override
    public void onResume() {
        super.onResume();
        isLoop = true;
        autoPlay(mViewPager);
    }

    public class ImageAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mImageViews.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mImageViews[position]);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mImageViews[position], 0);
            return mImageViews[position];
        }
    }

    @OnClick({R.id.index_branch, R.id.index_order, R.id.index_waybill, R.id.index_tools})
    public void handleClick(View view) {
        switch (view.getId()) {
            case R.id.index_waybill:
                startActivity(new Intent(mContext, WaybillSerchActivity.class));
                break;
            case R.id.index_branch:
                startActivity(new Intent(mContext, BranchActivity.class));
                break;
            case R.id.index_order:
                if (UserPref.getInstance(mContext).isLogined())
                    startActivity(new Intent(mContext, DraftsEditActivity.class));
                else {
                    ToastUtil.showToastShort("请先登录");
                    startActivity(new Intent(mContext, LoginActivity.class));
                }
                break;
            case R.id.index_tools:
                startActivity(new Intent(mContext, ToolsActivity.class));
                break;
        }
    }
}
