package cn.appem.bcwl.ui.waybill;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.wewe.android.util.ToastUtil;
import com.wewe.android.widget.EmptyLayout;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.TimeZone;

import butterknife.InjectView;
import cn.appem.bcwl.AppConfig;
import cn.appem.bcwl.R;
import cn.appem.bcwl.adapter.WaybillRecordAdapter;
import cn.appem.bcwl.domain.RecordModel;
import cn.appem.bcwl.net.Api;
import cn.appem.bcwl.net.JsonHandler;
import cn.appem.bcwl.ui.base.BaseFragment;
import cn.appem.bcwl.ui.base.BaseListFragment;
import hirondelle.date4j.DateTime;

/**
 * User:wewecn on 2015/7/14 14:04
 * Email:wewecn@qq.com
 */
public class WaybillRecordFragment extends BaseListFragment {
    public static final String TYPE = "record_type";
    public static final int TYPE_POST = 0;
    public static final int TYPE_RECEIVE = 1;
    private int currentType;
    @InjectView(R.id.emptyview)
    EmptyLayout emptyLayout;
    public static int SELECT_TYPE = TYPE_POST;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentType = getArguments().getInt(TYPE);
        return inflater.inflate(R.layout.fragment_waybill_record, container, false);
    }

    private WaybillRecordAdapter mAdapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new WaybillRecordAdapter(mContext);
        initListView();
        mListView.setAdapter(mAdapter);
        emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(1);
            }
        });
        isLoading = false;
        loadData(1);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    protected void loadData(final int pageIndex) {
        if (isLoading)
            return;
        if (pageIndex == 1) {
            mAdapter.clear();
            emptyLayout.setErrorType(EmptyLayout.ErrorType.NETWORK_LOADING);
        }
        isLoading = true;
        Api.getMyOrder(mContext, currentType, null, null, null, pageIndex, new JsonHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                JSONArray jsonArray = response.optJSONArray("rows");
                List<RecordModel> data = RecordModel.parse(currentType, jsonArray);

                mPageIndex = pageIndex;
                if (jsonArray.length() < AppConfig.PAGE_SIZE)
                    loadFinished();
                else
                    loadMoreDateDone();

                if (pageIndex == 1)
                    if (data.size() == 0)
                        emptyLayout.setErrorType(EmptyLayout.ErrorType.NODATA_ENABLE_CLICK);
                    else
                        emptyLayout.dismiss();

                mAdapter.addAll(data);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                isLoading = false;
            }

            @Override
            public void onFailure(int statusCode, String msg) {
                if (pageIndex == 1)
                    emptyLayout.setErrorType(EmptyLayout.ErrorType.NETWORK_ERROR);
                else ToastUtil.showToastShort("加载失败");
            }
        });
    }

    public static Fragment newInstance(int t) {
        WaybillRecordFragment fragment = new WaybillRecordFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE, t);
        fragment.setArguments(bundle);
        return fragment;
    }
}
