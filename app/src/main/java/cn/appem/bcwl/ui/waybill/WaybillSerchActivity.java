package cn.appem.bcwl.ui.waybill;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wewe.android.dialog.DialogHelper;
import com.wewe.android.dialog.WaitDialog;
import com.wewe.android.string.StringUtil;
import com.wewe.android.util.LocalDisplay;
import com.wewe.android.util.ToastUtil;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.appem.bcwl.R;
import cn.appem.bcwl.domain.WaybillState;
import cn.appem.bcwl.net.Api;
import cn.appem.bcwl.net.JsonHandler;
import cn.appem.bcwl.ui.base.BaseActivity;
import cn.appem.bcwl.ui.ScanActivity;

import static com.wewe.android.util.LogUtils.*;

/**
 * 运单查询
 * User:wewecn on 2015/6/26 14:33
 * Email:wewecn@qq.com
 */
public class WaybillSerchActivity extends BaseActivity {
    private static final String TAG = makeLogTag(WaybillSerchActivity.class);
    private final static int SCANNIN_GREQUEST_CODE = 1;
    @InjectView(R.id.waybillsearch_text)
    EditText mBillNum;

    @OnClick(R.id.waybillsearch_search)
    public void goSearch(View view) {
        String code = mBillNum.getText().toString();
        if (code.length() != 10) {
            ToastUtil.showToastShort("请输入10位运单号码！！！");
            return;
        }
        searchState(code);
    }

    @OnClick(R.id.waybillsearch_scan)
    public void goScan(View view) {
        startActivityForResult(new Intent(mContext, ScanActivity.class), SCANNIN_GREQUEST_CODE);
    }

    @OnClick(R.id.waybillsearch_clear)
    public void clear(View view) {
        clearHistory();
        mHistoryValues.clear();
        mHistory.removeAllViews();
    }

    private LinkedList<String> mHistoryValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waybill_search);
        setTitle("运单查询");
        mHistoryValues = readHistory();
        mHistory.removeAllViews();
        for (String code : mHistoryValues) {
            loadHistory(code);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    mBillNum.setText("");
                    Bundle bundle = data.getExtras();
                    //显示扫描到的内容
                    String code = bundle.getString("result");
                    if(code==null)
                        return;
                    LOGD(TAG, code);
                    Pattern p = Pattern.compile("^[0-9]{10}$");
                    Matcher m = p.matcher(code);
                    if(!m.matches()){
                        ToastUtil.showToastShort("不是运单号");
                        return;
                    }

                    searchState(code);
                }
                break;
        }
    }

    LinkedList<String> readHistory() {
        LinkedList<String> list = new LinkedList<>();
        SharedPreferences SAVE = mContext.getSharedPreferences("waybill_search", MODE_PRIVATE);
        list.addAll(SAVE.getStringSet("waybill_id", new HashSet<String>()));
        return list;
    }

    private static final int HISTORY_MAX_VALUE = 10;

    void writeHistory(List<String> path) {
        SharedPreferences SAVE = mContext.getSharedPreferences("waybill_search", MODE_PRIVATE);
        SharedPreferences.Editor editor = SAVE.edit();
        editor.putStringSet("waybill_id", new LinkedHashSet<String>(path));
        editor.apply();
    }

    void clearHistory() {
        SharedPreferences SAVE = mContext.getSharedPreferences("waybill_search", MODE_PRIVATE);
        SAVE.edit().clear().apply();
    }

    @InjectView(R.id.waybillsearch_history)
    LinearLayout mHistory;

    public void loadHistory(final String code) {
        TextView tv = new TextView(mContext);
        tv.setText(Html.fromHtml("单号：<u>" + code + "</u>"));
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchState(code);
            }
        });
        tv.setTextColor(getResources().getColor(R.color.black));
        tv.setHeight(LocalDisplay.dp2px(30));
        tv.setTextSize(16);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        mHistory.addView(tv, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearHistory();
        for (String str : mHistoryValues)
            LOGI(TAG, str);
        writeHistory(mHistoryValues);
    }

    boolean isLoading = false;

    void searchState(final String id) {
        if (isLoading) {
            return;
        }
        isLoading = true;
        final WaitDialog dialog = DialogHelper.getWaitDialog(mContext, "正在查找");
        dialog.show();
        Api.searchState(mContext, id, new JsonHandler() {
            @Override
            public void onFailure(int statusCode, String responseString) {
                ToastUtil.showToastShort("查询失败");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dialog.dismiss();
                isLoading = false;
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseString) {
                super.onSuccess(statusCode, headers, responseString);
                WaybillState state = WaybillState.parse(responseString);
                if (state == null) {
                    ToastUtil.showToastShort("未查询到运单信息");
                    return;
                }
                Intent intent = new Intent(mContext, WaybillDetailActivity.class);
                intent.putExtra("waybill_state", state);
                startActivity(intent);
                if (mHistoryValues.size() == HISTORY_MAX_VALUE) {
                    mHistoryValues.removeLast();
//                    mHistory.removeViewAt(mhi);
                }
                if (mHistoryValues.contains(id)) {
                    mHistory.removeViewAt(mHistoryValues.size() - 1 - mHistoryValues.indexOf(id));
                    mHistoryValues.remove(id);
                }
                mHistoryValues.add(id);
                loadHistory(id);
            }
        });
    }
}
