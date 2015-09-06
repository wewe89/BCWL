package cn.appem.bcwl.ui.waybill;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.wewe.android.dialog.DialogHelper;
import com.wewe.android.dialog.WaitDialog;
import com.wewe.android.string.StringUtil;
import com.wewe.android.util.ToastUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.TimeZone;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.appem.bcwl.R;
import cn.appem.bcwl.adapter.WaybillRecordAdapter;
import cn.appem.bcwl.domain.RecordModel;
import cn.appem.bcwl.net.Api;
import cn.appem.bcwl.net.JsonHandler;
import cn.appem.bcwl.ui.base.BaseActivity;
import hirondelle.date4j.DateTime;

/**
 * User:wewecn on 2015/7/21 15:34
 * Email:wewecn@qq.com
 */
public class RecordSearchActivity extends BaseActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    @InjectView(R.id.search_starttime)
    TextView etStartTime;
    @InjectView(R.id.search_endtime)
    TextView etEndTime;

    @InjectView(R.id.search_info)
    EditText etSearch;
    @InjectView(R.id.search_do)
    Button btnSearch;
    DateTime nowTime;
    InputMethodManager imm;
    void hideKeybord() {
        /**隐藏软键盘**/
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_search);

        nowTime = DateTime.now(TimeZone.getDefault());
        datePickerDialog = new DatePickerDialog(mContext, this, nowTime.getYear(), nowTime.getMonth(), nowTime.getDay());

        DatePicker datePicker = datePickerDialog.getDatePicker();
        datePicker.setMaxDate(Calendar.getInstance().getTimeInMillis());

        etStartTime.setInputType(InputType.TYPE_NULL);
        etEndTime.setInputType(InputType.TYPE_NULL);
        etStartTime.setOnClickListener(this);
        etEndTime.setOnClickListener(this);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        String title = null;
        String hint = null;
        if (WaybillRecordFragment.SELECT_TYPE == WaybillRecordFragment.TYPE_POST) {
            title = "搜索历史发货记录";
            hint = "发货人信息 姓名或电话";
        }
        if (WaybillRecordFragment.SELECT_TYPE == WaybillRecordFragment.TYPE_RECEIVE) {
            title = "搜索历史收货记录";
            hint = "收货人信息 姓名或电话";
        }

        etSearch.setHint(hint);
        setTitle(title);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSearch();
            }
        });
    }

    void doSearch() {
        if (imm.isActive())
            imm.hideSoftInputFromWindow(etSearch.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        String startTime = etStartTime.getText().toString();
        if (StringUtil.isEmpty(startTime)) {
            ToastUtil.showToastShort("请选择开始时间");
            return;
        }
        String endTime = etEndTime.getText().toString();
        if (StringUtil.isEmpty(startTime)) {
            ToastUtil.showToastShort("请选择结束时间");
            return;
        }
        if (dtStart.gt(dtEnd)) {
            ToastUtil.showToastShort("开始时间不能大于结束时间");
            return;
        }
        RecordResultActivity.launchThis(mContext, etSearch.getText().toString().trim(), startTime.trim(), endTime.trim());
    }

    DateTime dtStart, dtEnd;

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear++;
        if (currentId == R.id.search_starttime) {
            etStartTime.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
            dtStart = DateTime.forDateOnly(year, monthOfYear, dayOfMonth);
        } else if (currentId == R.id.search_endtime) {
            etEndTime.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
            dtEnd = DateTime.forDateOnly(year, monthOfYear, dayOfMonth);
        }
    }

    int currentId;
    DatePickerDialog datePickerDialog;

    @Override
    public void onClick(View v) {
        hideKeybord();
        currentId = v.getId();
        datePickerDialog.show();
    }
}
