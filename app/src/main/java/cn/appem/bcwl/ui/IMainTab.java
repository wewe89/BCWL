package cn.appem.bcwl.ui;

import android.support.v4.widget.SlidingPaneLayout;

import com.wewe.android.widget.SlidingTabLayout;

/**
 * User:wewecn on 2015/5/4 10:52
 * Email:wewecn@qq.com
 */
public interface IMainTab {
    public SlidingTabLayout getSlidingTabLayout();

    public int getCurrentTab();
}
