package cn.appem.bcwl.ui.address;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wewe.android.dialog.CommonDialog;
import com.wewe.android.dialog.DialogHelper;
import com.wewe.android.dialog.WaitDialog;
import com.wewe.android.util.ToastUtil;
import com.wewe.android.widget.EmptyLayout;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import butterknife.InjectView;
import cn.appem.bcwl.AppConfig;
import cn.appem.bcwl.R;
import cn.appem.bcwl.adapter.address.AddressListAdapter;
import cn.appem.bcwl.domain.AddressInfo;
import cn.appem.bcwl.net.Api;
import cn.appem.bcwl.net.JsonHandler;
import cn.appem.bcwl.ui.base.BaseActivity;
import cn.appem.bcwl.ui.base.BaseListActivity;

/**
 * 地址
 * User:wewecn on 2015/7/13 09:45
 * Email:wewecn@qq.com
 */
public class AddressLisActivity extends BaseListActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    private AddressListAdapter mAdapter;
    @InjectView(R.id.emptyview)
    EmptyLayout mEmptyView;

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        AddressOprActivity.launchThis(mContext, type, mAdapter.getItem(position));
//    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final CommonDialog dialog = DialogHelper.getAlertDialogCancelable(mContext);
        String[] val = {"修改", "删除"};
        dialog.setItems(val, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 0)
                    AddressOprActivity.launchThis(mContext, type, mAdapter.getItem(position));
                if (pos == 1)
                    delAddress(mAdapter.getItem(position).getAddress_id(), position);
                dialog.dismiss();
            }
        });
        dialog.show();
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AddressInfo info = mAdapter.getItem(position);
        Intent intent = new Intent();
        intent.putExtra("data", info);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void delAddress(String id, final int position) {
        final WaitDialog dialog = DialogHelper.getWaitDialog(mContext, "正在删除...");
        dialog.show();
        Api.delAddress(mContext, id, new JsonHandler() {
            @Override
            public void onFailure(int statusCode, String msg) {
                ToastUtil.showToastShort("删除失败");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if (response.optBoolean("success")) {
                    ToastUtil.showToastShort("删除成功");
                    mAdapter.remove(position);
                    mAdapter.notifyDataSetChanged();
                    if(mAdapter.isEmpty()){
                        mEmptyView.setErrorType(EmptyLayout.ErrorType.NODATA_ENABLE_CLICK);
                    }
                } else onFailure(0, null);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
        initListView();
        mAdapter = new AddressListAdapter(mContext);
        mListView.setAdapter(mAdapter);

        type = getIntent().getIntExtra(TYPE_STR, 0);
        forResult = getIntent().getBooleanExtra(RESULT_STR, false);


        if (forResult)
            mListView.setOnItemClickListener(this);
        else
            mListView.setOnItemLongClickListener(this);

        if (type == TYPE_POST) {
            setTitle("历史发货人地址");
        } else {
            setTitle("历史收货人地址");
        }
        if (!forResult) {
            getNavRight().setText("新增");
            getNavRight().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddressOprActivity.launchThis(mContext, type, null);
                }
            });
        }
        mEmptyView.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(1);
            }
        });
    }

    boolean forResult = false;

    public static void launchThis(Context context, int type) {
        launchThis(context, type, false);
    }

    public static void launchThis(Context context, int type, boolean forResult) {
        Intent intent = new Intent(context, AddressLisActivity.class);
        intent.putExtra(TYPE_STR, type);
        intent.putExtra(RESULT_STR, forResult);
        context.startActivity(intent);
    }

    public static final String RESULT_STR = "result";
    private int type = 0;
    public static final String TYPE_STR = "type";

    public static final int TYPE_POST = 0;
    public static final int TYPE_RECEIVE = 1;

    protected void loadData(final int pageIndex) {
        if (isLoading) return;
        mEmptyView.setErrorType(EmptyLayout.ErrorType.NETWORK_LOADING);
        isLoading = true;
        if (pageIndex == 1) {
            mAdapter.clear();
        }
        Api.getAddressList(mContext, null, type, pageIndex, new JsonHandler() {
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
                List<AddressInfo> data = AddressInfo.parse(jsonArray);
                mAdapter.addAll(data);
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
