package cn.appem.bcwl.ui.address;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Html;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.wewe.android.dialog.DialogHelper;
import com.wewe.android.dialog.WaitDialog;
import com.wewe.android.string.StringUtil;
import com.wewe.android.util.ToastUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.appem.bcwl.R;
import cn.appem.bcwl.adapter.MyBrinchAdapter;
import cn.appem.bcwl.domain.AddressInfo;
import cn.appem.bcwl.domain.ProvinceModel;
import cn.appem.bcwl.domain.StationModel;
import cn.appem.bcwl.net.Api;
import cn.appem.bcwl.net.JsonHandler;
import cn.appem.bcwl.ui.base.BaseActivity;
import cn.appem.bcwl.util.CommonUtil;
import cn.appem.bcwl.util.UserPref;
import cn.appem.bcwl.view.DataPickWheelDialog;

import static com.wewe.android.util.LogUtils.LOGD;
import static com.wewe.android.util.LogUtils.makeLogTag;

/**
 * 新增地址
 * User:wewecn on 2015/7/13 09:46
 * Email:wewecn@qq.com
 */
public class AddressOprActivity extends BaseActivity implements DataPickWheelDialog.OnChangedListener, AdapterView.OnItemSelectedListener {
    @InjectView(R.id.address_etPosterName)
    EditText address_etPosterName;
    @InjectView(R.id.address_etPosterPhone)
    EditText address_etPosterPhone;
    @InjectView(R.id.address_tvPosterAreaName)
    TextView address_tvPosterAreaName;
    @InjectView(R.id.address_etPosterAddress)
    EditText address_etPosterAddress;
    @InjectView(R.id.address_spPosterBranch)
    Spinner address_spPosterBranch;
    private MyBrinchAdapter mBrinchAdapter;

    @InjectView(R.id.order_tvName)
    TextView orderName;
    @InjectView(R.id.order_tvAddress)
    TextView orderAddress;
    @InjectView(R.id.order_tvBranch)
    TextView orderBranch;

    void initView() {
        mProvinceDialog = new DataPickWheelDialog(mContext);
        mProvinceDialog.setOnChangedListener(this);

        mBrinchAdapter = new MyBrinchAdapter(mContext);
        address_spPosterBranch.setAdapter(mBrinchAdapter);
        address_spPosterBranch.setOnItemSelectedListener(this);
    }

    private StationModel mSelcetBranch;
    private static final String TAG = makeLogTag(AddressOprActivity.class);

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSelcetBranch = mBrinchAdapter.getItem(position);
        LOGD(TAG, mSelcetBranch.getText());
    }

    void loadData() {
        Api.loadRegion(mContext, new JsonHandler() {
            @Override
            public void onFailure(int statusCode, String msg) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                mProvinceData = ProvinceModel.parse(response);
                mProvinceDialog.setData(mProvinceData);
            }
        });
    }

    private List<ProvinceModel> mProvinceData;

    @Override
    public void onChanged(int flag, int city, int county) {
        ProvinceModel model = mProvinceData.get(city);
        int areaId = model.getId();
        String cityName = model.getText();
        if (model.getmCounties() != null && model.getmCounties().size() != 0) {
            cityName += "-" + model.getmCounties().get(county).getText();
            areaId = model.getmCounties().get(county).getId();
        }
        address_tvPosterAreaName.setText(cityName);
        mAreaId=areaId;
        loadBranch(areaId);
    }
private  int mAreaId;
    void loadBranch(int areaId) {
        mBrinchAdapter.clear();
        mBrinchAdapter.notifyDataSetChanged();
        Api.getBranchByCounty(mContext, areaId, new JsonHandler() {
            @Override
            public void onFailure(int statusCode, String msg) {
                ToastUtil.showToastShort(Html.fromHtml("获取网点信息失败<br>请重试"));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                List<StationModel> mBranchData = StationModel.parse(response);
                if (mBranchData.size() == 0) {
                    ToastUtil.showToastShort(Html.fromHtml("当前地区没有网点<br>请选择其它地区"));
                    return;
                }
                mBrinchAdapter.setData(mBranchData);
                mBrinchAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        LOGD(TAG, "onNothingSelected");
        mSelcetBranch = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_edit);
        type = getIntent().getIntExtra(TYPE_STR, 0);
        getNavRight().setText("提交");
        getNavRight().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSubmit();
            }
        });
        initView();
        loadData();
        mAddressInfo = (AddressInfo) getIntent().getSerializableExtra(DATA_STR);
        address_tvPosterAreaName.setInputType(InputType.TYPE_NULL);

        String title = null;
        if (mAddressInfo != null) {
            initData();
            title = "修改";
        } else {
            title = "新增";
        }
        if (type == TYPE_POST) {
            typeName = "发货";
            title += "发货人地址";

            orderName.setText("收获人：");
            orderAddress.setText("收获地区：");
            orderBranch.setText("收获网点：");
        } else {
            title += "收货人地址";
            typeName = "收货";
        }
        setTitle(title);
    }

    String typeName;

    void initData() {
        try {
            address_etPosterName.setText(mAddressInfo.getName());
            address_etPosterPhone.setText(mAddressInfo.getPhone());
            address_tvPosterAreaName.setText(mAddressInfo.getRegion());
            address_etPosterAddress.setText(mAddressInfo.getAddress());
            loadBranch(Integer.valueOf(mAddressInfo.getRegion_id()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    AddressInfo mAddressInfo;

    void doSubmit() {
        String order_etPosterNameStr = address_etPosterName.getText().toString();
        if (StringUtil.isEmpty(order_etPosterNameStr)) {
            ToastUtil.showToastShort("请输入" + typeName + "人姓名");
            return;
        }
        String order_etPosterPhoneStr = address_etPosterPhone.getText().toString();
        if (StringUtil.isEmpty(order_etPosterPhoneStr)) {
            ToastUtil.showToastShort(typeName + "人手机号码不能为空");
            return;
        }
        order_etPosterPhoneStr = StringUtil.removeSpace(order_etPosterPhoneStr);
        if (!StringUtil.isMobileNO(order_etPosterPhoneStr)) {
            ToastUtil.showToastShort("请输入" + typeName + "人手机号码");
            return;
        }
        String order_tvPosterAreaNameStr = address_tvPosterAreaName.getText().toString();
        if (StringUtil.isEmpty(order_tvPosterAreaNameStr)) {
            ToastUtil.showToastShort("请输选择地区");
            return;
        }
        String order_etPosterAddressStr = address_etPosterAddress.getText().toString();
        if (StringUtil.isEmpty(order_etPosterAddressStr)) {
            ToastUtil.showToastShort("请输入详细地址");
            return;
        }
        if (mSelcetBranch == null) {
            ToastUtil.showToastShort(typeName + "地区没有网点");
            return;
        }

        JSONObject object = new JSONObject();
        try {
            if (mAddressInfo != null) {
                object.put("address_id", mAddressInfo.getAddress_id());
            } else
                object.put("address_id", 0);
            object.put("linkman", order_etPosterNameStr);
            object.put("mobile", order_etPosterPhoneStr);
            object.put("region", order_tvPosterAreaNameStr);
            object.put("address", order_etPosterAddressStr);
            object.put("type", type);
            object.put("user_id", UserPref.getInstance(mContext).getUserId());
            object.put("stroe", mSelcetBranch.getText());

            object.put("region_id", mAreaId);

            LOGD(TAG, object.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String str = "修改";
        if (mAddressInfo == null) {
            str = "添加";
        }
        final String val = str;
        final WaitDialog dialog = DialogHelper.getWaitDialog(mContext, "正在" + str + "..");
        dialog.show();
        Api.editAddress(mContext, object, new JsonHandler() {
            @Override
            public void onFailure(int statusCode, String msg) {
                ToastUtil.showToastShort(val + "失败");
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
                    ToastUtil.showToastShort(val + "成功");
                    finish();
                } else onFailure(0, null);
            }
        });
    }

    void hideKeybord() {
        /**隐藏软键盘**/
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static final int PICK_CONTACT = 100;
    private DataPickWheelDialog mProvinceDialog;

    @OnClick({R.id.address_btnAddPoster, R.id.address_btnAddPosterArea, R.id.address_tvPosterAreaName})
    public void handleClick(View view) {
        switch (view.getId()) {
            case R.id.address_btnAddPoster:
                startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), PICK_CONTACT);
                break;
            case R.id.address_btnAddPosterArea:
            case R.id.address_tvPosterAreaName:
                hideKeybord();
                mProvinceDialog.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case PICK_CONTACT:
                Uri contactData = data.getData();
                Cursor cursor = managedQuery(contactData, null, null, null,
                        null);
                cursor.moveToFirst();
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                String num = CommonUtil.getContactPhone(mContext, cursor);
                address_etPosterName.setText(name);
                address_etPosterPhone.setText(num);
                break;
            default:
                break;
        }
    }

    public static void launchThis(Context context, int type, AddressInfo info) {
        Intent intent = new Intent(context, AddressOprActivity.class);
        intent.putExtra(TYPE_STR, type);
        intent.putExtra(DATA_STR, info);
        context.startActivity(intent);
    }

    private int type = 0;
    private static final String TYPE_STR = "type";
    private static final String DATA_STR = "data";
    public static final int TYPE_POST = 0;
    public static final int TYPE_RECEIVE = 1;
}
