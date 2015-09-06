package cn.appem.bcwl.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wewe.android.widget.DotView;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import cn.appem.bcwl.R;
import cn.appem.bcwl.ui.base.BaseActivity;

/**
 * User：wewecn on 2015/4/29 11:59
 * Email：wewecn@qq.com
 */
public class GuideActivity extends BaseActivity {

    @InjectView(R.id.guide_go)
    Button mGoBtn;
    @InjectView(R.id.guide_viewpager)
    ViewPager mGuideView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSataeBarBackground = R.color.transparent;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initData();
        initWithPageGuideMode();
        mGoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, MainActivity.class));
                finish();
            }
        });
    }

    class DataHolder {
        public int bg;
        public String bgColor;

        public DataHolder(int bg, String bgColor) {
            this.bg = bg;
            this.bgColor=bgColor;
        }
    }

    private List<DataHolder> mData;

    private void initData() {
        mData = new ArrayList<>();
        mData.add(new DataHolder(R.mipmap.welcome1, "#F5F4F2"));
        mData.add(new DataHolder(R.mipmap.welcome2, "#D7EEFE"));
        mData.add(new DataHolder(R.mipmap.welcome3, "#F5F4F2"));
    }

    /**
     * 程序导航页效果
     */
    public void initWithPageGuideMode() {

        List<View> mList = new ArrayList<>();
        LayoutInflater inflat = LayoutInflater.from(this);
        for (DataHolder holder : mData) {
            View item = inflat.inflate(R.layout.item_guide, null);
            ImageView imageView=(ImageView)item.findViewById(R.id.item_guide_img);
            imageView.setImageResource(holder.bg);
            imageView.setBackgroundColor(Color.parseColor(holder.bgColor));
            mList.add(item);
        }
        //ViewPager最重要的设置Adapter，这和ListView一样的原理
        ViewPageAdapter adapter = new ViewPageAdapter(mList);
        mGuideView.setAdapter(adapter);
        mGuideView.setOnPageChangeListener(adapter);
        mGuideView.setCurrentItem(0);
    }

    /**
     * 内部类，继承PagerAdapter，当然你也可以直接 new PageAdapter
     *
     * @author yangxiaolong
     */
    class ViewPageAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {

        private List<View> mViewList;

        public ViewPageAdapter(List<View> views) {
            mViewList = views;
        }

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position), 0);
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int position) {
            if(position==getCount()-1){
                mGoBtn.setVisibility(View.VISIBLE);
            }
        }
    }
}
