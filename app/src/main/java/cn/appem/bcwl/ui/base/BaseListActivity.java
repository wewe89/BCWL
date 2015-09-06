package cn.appem.bcwl.ui.base;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.InjectView;
import cn.appem.bcwl.R;

/**
 * User:wewecn on 2015/7/20 13:52
 * Email:wewecn@qq.com
 */
public abstract class BaseListActivity extends BaseActivity implements AbsListView.OnScrollListener {
    protected ListView mListView;

    protected View mFootView, mLoadMore, mLoadFinish, mLoadMorePb;
    protected TextView mLoadMoreTv, mLoadFinishTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initListView() {
        mListView = (ListView) findViewById(R.id.listView);
        mListView.setOnScrollListener(this);
        mListView.addFooterView(getFootView());
    }

    protected View getFootView() {
        mFootView = getLayoutInflater().inflate(R.layout.listview_foot, null);
        mLoadMore = mFootView.findViewById(R.id.listview_footview_loadmore);
        mLoadFinish = mFootView.findViewById(R.id.listview_footview_loadfinish);
        mLoadFinishTv = (TextView) mFootView.findViewById(R.id.listview_footview_loadfinish_text);
        mLoadMorePb = mFootView.findViewById(R.id.listview_footview_loadmore_pb);
        mLoadMoreTv = (TextView) mFootView.findViewById(R.id.listview_footview_loadmore_tv);
        return mFootView;
    }

    /**
     * 正在加载
     */
    protected void isLoadMoreDate() {
        mLoadFinish.setVisibility(View.GONE);
        mLoadMorePb.setVisibility(View.VISIBLE);
        mLoadMoreTv.setText("正在加载...");
    }

    /**
     * 分页加载完成
     */
    protected void loadMoreDateDone() {
        mLoadMorePb.setVisibility(View.INVISIBLE);
        mLoadMoreTv.setText("加载更多");
        hasMoreItems = true;
    }

    /**
     * 所有数据加载完成
     */
    protected void loadFinished() {
        mLoadMore.setVisibility(View.GONE);
        mLoadFinish.setVisibility(View.VISIBLE);
        hasMoreItems = false;
    }

    protected void loadFinished(String message) {
        mLoadMore.setVisibility(View.GONE);
        mLoadFinish.setVisibility(View.VISIBLE);
        mLoadFinishTv.setText(message);
        hasMoreItems = false;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    protected int mPageIndex = 1;
    protected boolean isLoading = false;
    protected boolean hasMoreItems = false;

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if ((totalItemCount == (firstVisibleItem + visibleItemCount)) && !isLoading && hasMoreItems)
            loadData(mPageIndex + 1);
    }

    abstract protected void loadData(int pageIndex);
}
