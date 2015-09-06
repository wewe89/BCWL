package cn.appem.bcwl.ui.waybill;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.wewe.android.dialog.DialogHelper;
import com.wewe.android.dialog.WaitDialog;
import com.wewe.android.util.ToastUtil;
import com.wewe.android.widget.EmptyLayout;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.InjectView;
import cn.appem.bcwl.AppConfig;
import cn.appem.bcwl.R;
import cn.appem.bcwl.adapter.WaybillRecordAdapter;
import cn.appem.bcwl.domain.RecordModel;
import cn.appem.bcwl.net.Api;
import cn.appem.bcwl.net.JsonHandler;
import cn.appem.bcwl.ui.base.BaseListActivity;

/**
 * User:wewecn on 2015/7/24 15:12
 * Email:wewecn@qq.com
 */
public class RecordResultActivity extends BaseListActivity {
    private WaybillRecordAdapter mAdapter;
    @InjectView(R.id.emptyview)
    EmptyLayout mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waybill_result);

        setTitle("搜索结果");
        initListView();
        mAdapter = new WaybillRecordAdapter(mContext);
        mListView.setAdapter(mAdapter);

        where = getIntent().getStringExtra("where");
        startTime = getIntent().getStringExtra("startTime");
        endTime = getIntent().getStringExtra("where");

        loadData(1);
    }

    String where, startTime, endTime;

    public static void launchThis(Context context, String where, String startTime, String endTime) {
        Intent intent = new Intent(context, RecordResultActivity.class);
        intent.putExtra("where", where);
        intent.putExtra("startTime", startTime);
        intent.putExtra("endTime", endTime);
        context.startActivity(intent);
    }

    @Override
    protected void loadData(final int pageIndex) {
        if (isLoading)
            return;
        if (pageIndex == 1)
            mAdapter.clear();

        mEmptyView.setErrorType(EmptyLayout.ErrorType.NETWORK_LOADING);

        Api.getMyOrder(mContext, WaybillRecordFragment.SELECT_TYPE, where, startTime, endTime, pageIndex, new JsonHandler() {
            @Override
            public void onFailure(int statusCode, String msg) {
                if (pageIndex == 1)
                    mEmptyView.setErrorType(EmptyLayout.ErrorType.NETWORK_ERROR);
                else ToastUtil.showToastShort("加载失败");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                isLoading = false;
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                mPageIndex = pageIndex;
                if (response.length() < AppConfig.PAGE_SIZE)
                    loadFinished();
                else
                    loadMoreDateDone();

                JSONArray jsonArray = response.optJSONArray("rows");

                if (pageIndex == 1)
                    if (jsonArray.length() == 0) {
                        mEmptyView.setErrorType(EmptyLayout.ErrorType.NODATA_ENABLE_CLICK);
                        return;
                    } else {
                        mEmptyView.dismiss();
                    }

                mAdapter.addAll(RecordModel.parse(WaybillRecordFragment.SELECT_TYPE, jsonArray));
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
