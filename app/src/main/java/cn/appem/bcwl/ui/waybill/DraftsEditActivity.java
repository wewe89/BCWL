package cn.appem.bcwl.ui.waybill;

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

import com.baidu.location.BDLocation;
import com.wewe.android.dialog.DialogHelper;
import com.wewe.android.dialog.WaitDialog;
import com.wewe.android.string.StringUtil;
import com.wewe.android.util.ToastUtil;
import com.wewe.android.widget.ActionSheet;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.appem.bcwl.R;
import cn.appem.bcwl.adapter.MyBrinchAdapter;
import cn.appem.bcwl.domain.AddressInfo;
import cn.appem.bcwl.domain.BranchInfo;
import cn.appem.bcwl.domain.ProvinceModel;
import cn.appem.bcwl.domain.StationModel;
import cn.appem.bcwl.net.Api;
import cn.appem.bcwl.net.JsonHandler;
import cn.appem.bcwl.ui.LoginActivity;
import cn.appem.bcwl.ui.address.AddressLisActivity;
import cn.appem.bcwl.ui.address.AddressOprActivity;
import cn.appem.bcwl.ui.base.LocationActivity;
import cn.appem.bcwl.util.CommonUtil;
import cn.appem.bcwl.util.UserPref;
import cn.appem.bcwl.view.DataPickWheelDialog;

import static com.wewe.android.util.LogUtils.*;

/**
 * 下单
 * User:wewecn on 2015/6/30 10:59
 * Email:wewecn@qq.com
 */
public class DraftsEditActivity extends LocationActivity implements DataPickWheelDialog.OnChangedListener, AdapterView.OnItemSelectedListener, ActionSheet.ActionSheetListener {
    private static final String TAG = makeLogTag(DraftsEditActivity.class);
    @InjectView(R.id.order_tvPosterAreaName)
    TextView order_tvPosterAreaName;
    @InjectView(R.id.order_etPosterAddress)
    EditText order_etPosterAddress;
    @InjectView(R.id.order_spPosterBranch)
    Spinner order_spPosterBranch;

    public static void launchThis(Context context, BranchInfo info) {
        if (UserPref.getInstance(context).isLogined()) {
            Intent intent = new Intent(context, DraftsEditActivity.class);
            intent.putExtra("data", info);
            context.startActivity(intent);
        } else context.startActivity(new Intent(context, LoginActivity.class));
    }

    @Override
    protected void locationFinished(BDLocation mLatlng) {
        super.locationFinished(mLatlng);
        if (mLatlng.getStreet() != null)
            order_etPosterAddress.setText(mLatlng.getStreet() + mLatlng.getStreetNumber());
        else ToastUtil.showToastShort("定位失败");
    }

    private ActionSheet.Builder mSheetBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drafts_edit);
        setTitle("我要下单");
        getNavRight().setText("下单");
        getNavRight().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSubmit();
            }
        });

        mSheetBuilder = ActionSheet.createBuilder(mContext, getSupportFragmentManager());
        mSheetBuilder.setListener(this);
        mSheetBuilder.setCancelButtonTitle("取消");

        initView();

        BranchInfo info = (BranchInfo) getIntent().getSerializableExtra("data");
        if (info != null) {
            initData(info);
        }

        loadData();
    }

    private void initData(BranchInfo info) {
        order_etPosterAddress.setText(info.getAddress());
        order_etPosterPhone.setText(info.getMobile());
        order_tvPosterAreaName.setText(info.getCity_name() + "-" + info.getCounty_name());
        loadBranch(info.getCountyId());
    }

    void doSubmit() {
        //1、发货人信息
        String order_etPosterNameStr = order_etPosterName.getText().toString().trim();
        if (StringUtil.isEmpty(order_etPosterNameStr)) {
            ToastUtil.showToastShort("请输入发货人姓名");
            return;
        }
        String order_etPosterPhoneStr = order_etPosterPhone.getText().toString().trim();
        LOGI(TAG, order_etPosterPhoneStr);
        if (StringUtil.isEmpty(order_etPosterNameStr)) {
            ToastUtil.showToastShort("发货人手机号码不能为空");
            return;
        }
        order_etPosterPhoneStr = StringUtil.removeSpace(order_etPosterPhoneStr);
        if (!StringUtil.isMobileNO(order_etPosterPhoneStr)) {
            ToastUtil.showToastShort("请输入发货人手机号码");
            return;
        }
        String order_tvPosterAreaNameStr = order_tvPosterAreaName.getText().toString().trim();
        if (StringUtil.isEmpty(order_tvPosterAreaNameStr)) {
            ToastUtil.showToastShort("请输入发货人地区");
            return;
        }
        String order_etPosterAddressStr = order_etPosterAddress.getText().toString().trim();
        if (StringUtil.isEmpty(order_etPosterAddressStr)) {
            ToastUtil.showToastShort("请输入发货人详细地址");
            return;
        }
        if (mSelcetBranch == null) {
            ToastUtil.showToastShort("发货地区没有网点");
            return;
        }
        //2、收货人信息
        String order_etReceiverNameStr = order_etReceiverName.getText().toString().trim();
        if (StringUtil.isEmpty(order_etReceiverNameStr)) {
            ToastUtil.showToastShort("请输入收货人姓名");
            return;
        }
        String order_etReceiverPhoneStr = order_etReceiverPhone.getText().toString().trim();
        if (StringUtil.isEmpty(order_etReceiverPhoneStr)) {
            ToastUtil.showToastShort("收货人手机号码不能为空");
            return;
        }
        order_etReceiverPhoneStr = StringUtil.removeSpace(order_etReceiverPhoneStr);
        if (!StringUtil.isMobileNO(order_etReceiverPhoneStr)) {
            ToastUtil.showToastShort("请输入收货人手机号码");
            return;
        }
        String order_tvReceiverAreaNameStr = order_tvReceiverAreaName.getText().toString().trim();
        if (StringUtil.isEmpty(order_tvReceiverAreaNameStr)) {
            ToastUtil.showToastShort("请输入收货人地区");
            return;
        }
        String order_etReceiverAddressStr = order_etReceiverAddress.getText().toString().trim();
        if (StringUtil.isEmpty(order_etReceiverAddressStr)) {
            ToastUtil.showToastShort("请输入收货人详细地址");
            return;
        }
        //3、货物信息
        String order_etCargoNameStr = order_etCargoName.getText().toString().trim();
        if (StringUtil.isEmpty(order_etCargoNameStr)) {
            ToastUtil.showToastShort("请输入物品名称");
            return;
        }
        String order_etCargoQuantityStr = order_etCargoQuantity.getText().toString().trim();
        if (StringUtil.isEmpty(order_etCargoQuantityStr)) {
            ToastUtil.showToastShort("请输入报价申明");
            return;
        }

        String order_etCargoWeightStr = order_etCargoWeight.getText().toString().trim();

        String order_etCargoCubageStr = order_etCargoCubage.getText().toString().trim();
        if (StringUtil.isEmpty(order_etCargoCubageStr) && StringUtil.isEmpty(order_etCargoWeightStr)) {
            ToastUtil.showToastShort("请输入物品重量或者物品体积");
            return;
        }

        String order_etCargoCountStr = order_etCargoCount.getText().toString().trim();
        if (StringUtil.isEmpty(order_etCargoCountStr)) {
            ToastUtil.showToastShort("请输入物品数量");
            return;
        }
        String order_etRemarkStr = order_etRemark.getText().toString().trim();
//        if (StringUtil.isEmpty(order_etRemarkStr)) {
//            ToastUtil.showToastShort("请输入备注");
//            return;
//        }
        //录入数据
        JSONObject params = new JSONObject();
        try {
            params.put("user_id", UserPref.getInstance(mContext).getUserId());
            params.put("f_store", mSelcetBranch.getText());//网点
            params.put("cargo_name", order_etCargoNameStr);
            params.put("cargo_bj", order_etCargoQuantityStr);

            params.put("cargo_weight", StringUtil.isEmpty(order_etCargoWeightStr) ? 0 : order_etCargoWeightStr);
            params.put("cargo_size", StringUtil.isEmpty(order_etCargoCubageStr) ? 0 : order_etCargoCubageStr);

            params.put("cargo_num", order_etCargoCountStr);

            params.put("f_linkman", order_etPosterNameStr);
            params.put("f_mobile", order_etPosterPhoneStr);
            params.put("f_region", order_tvPosterAreaNameStr);
            params.put("f_address", order_etPosterAddressStr);

            params.put("s_linkman", order_etReceiverNameStr);
            params.put("s_mobile", order_etReceiverPhoneStr);
            params.put("s_region", order_tvReceiverAreaNameStr);
            params.put("s_address", order_etReceiverAddressStr);

            params.put("remark", order_etRemarkStr);
            params.put("p_f_region_id", mSelcetBranch.getId());

            LOGD(TAG, params.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        LOGI(TAG, params.toString());
        final WaitDialog dialog = DialogHelper.getWaitDialog(mContext, "表单提交中...");
        dialog.show();
        Api.newOrder(mContext, params, new JsonHandler() {
            @Override
            public void onFinish() {
                super.onFinish();
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, String msg) {
                ToastUtil.showToastShort("下单失败");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if (response.optBoolean("success")) {
                    ToastUtil.showToastShort("下单成功");
                    finish();
                } else ToastUtil.showToastShort(response.optString("message"));
            }
        });
    }

    private int mPosterAreaId;

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
                    ToastUtil.showToastShort(Html.fromHtml("发货地区没有网点<br>请选择其它地区"));
                    return;
                }
                mBrinchAdapter.setData(mBranchData);
                mBrinchAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onChanged(int flag, int city, int county) {
        ProvinceModel model = mProvinceData.get(city);
        int areaId = model.getId();
        String cityName = model.getText();
        if (model.getmCounties() != null && model.getmCounties().size() != 0) {
            cityName += "-" + model.getmCounties().get(county).getText();
            areaId = model.getmCounties().get(county).getId();
        }
        if (flag == PICK_POSTERAREA) {
            mPosterAreaId = areaId;
            order_tvPosterAreaName.setText(cityName);
            loadBranch(areaId);
        } else {
            order_tvReceiverAreaName.setText(cityName);
        }
    }

    private MyBrinchAdapter mBrinchAdapter;

    void initView() {
        mProvinceDialog = new DataPickWheelDialog(mContext);
        mProvinceDialog.setOnChangedListener(this);

        mBrinchAdapter = new MyBrinchAdapter(mContext);

        order_spPosterBranch.setAdapter(mBrinchAdapter);
        order_spPosterBranch.setOnItemSelectedListener(this);

        order_tvPosterAreaName.setInputType(InputType.TYPE_NULL);
    }

    private StationModel mSelcetBranch;

    void hideKeybord() {
        /**隐藏软键盘**/
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSelcetBranch = mBrinchAdapter.getItem(position);
        LOGD(TAG, mSelcetBranch.getText());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        LOGD(TAG, "onNothingSelected");
        mSelcetBranch = null;
    }

    private List<ProvinceModel> mProvinceData;

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

    public static final int PICK_POSTER_MOBILE = 100;
    public static final int PICK_RECEIVER_MOBILE = 101;
    public static final int PICK_POSTER_ADDRESS = 103;
    public static final int PICK_RECEIVER_ADDRESS = 104;
    public static final int PICK_POSTERAREA = 100;
    public static final int PICK_RECEIVERAREA = 101;
    private DataPickWheelDialog mProvinceDialog;

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        if (actionSheet.getTag().equals("post") && index == 1) {
            startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), PICK_POSTER_MOBILE);
        }
        if (actionSheet.getTag().equals("receive") && index == 1) {
            startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), PICK_RECEIVER_MOBILE);
        }
        if (actionSheet.getTag().equals("post") && index == 0) {

            Intent intent = new Intent(mContext, AddressLisActivity.class);
            intent.putExtra(AddressLisActivity.TYPE_STR, AddressLisActivity.TYPE_POST);
            intent.putExtra(AddressLisActivity.RESULT_STR, true);
            startActivityForResult(intent, PICK_POSTER_ADDRESS);
        }
        if (actionSheet.getTag().equals("receive") && index == 0) {

            Intent intent = new Intent(mContext, AddressLisActivity.class);
            intent.putExtra(AddressLisActivity.TYPE_STR, AddressLisActivity.TYPE_RECEIVE);
            intent.putExtra(AddressLisActivity.RESULT_STR, true);
            startActivityForResult(intent, PICK_RECEIVER_ADDRESS);
        }
    }

    @OnClick({R.id.order_btnAddPoster, R.id.order_tvPosterAreaName, R.id.order_btnAddPosterArea, R.id.order_btnAddPosterAddress, R.id.order_btnAddReceiver, R.id.order_btnAddReceiverArea})
    public void handleClick(View view) {
        switch (view.getId()) {
            case R.id.order_btnAddPoster:

                mSheetBuilder.setOtherButtonTitles("历史发货人记录", "手机通讯录");
                mSheetBuilder.setTag("post");
                mSheetBuilder.setCancelableOnTouchOutside(true);
                mSheetBuilder.show();
                break;
            case R.id.order_btnAddReceiver:

                mSheetBuilder.setOtherButtonTitles("历史收货人记录", "手机通讯录");
                mSheetBuilder.setTag("receive");
                mSheetBuilder.setCancelableOnTouchOutside(true);
                mSheetBuilder.show();
                break;
            case R.id.order_btnAddPosterArea:
            case R.id.order_tvPosterAreaName:
                hideKeybord();

                mProvinceDialog.setFlag(PICK_POSTERAREA);
                mProvinceDialog.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.order_btnAddReceiverArea:
                mProvinceDialog.setFlag(PICK_RECEIVERAREA);
                mProvinceDialog.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.order_btnAddPosterAddress:
                if (mLatlng == null)
                    ToastUtil.showToastShort("正在定位");
                else
                    locationFinished(mLatlng);
                break;
        }
    }

    @InjectView(R.id.order_etReceiverName)
    EditText order_etReceiverName;
    @InjectView(R.id.order_etReceiverPhone)
    EditText order_etReceiverPhone;
    @InjectView(R.id.order_etReceiverAddress)
    EditText order_etReceiverAddress;
    @InjectView(R.id.order_etCargoName)
    EditText order_etCargoName;
    @InjectView(R.id.order_etCargoQuantity)
    EditText order_etCargoQuantity;
    @InjectView(R.id.order_etCargoWeight)
    EditText order_etCargoWeight;
    @InjectView(R.id.order_etCargoCubage)
    EditText order_etCargoCubage;
    @InjectView(R.id.order_etCargoCount)
    EditText order_etCargoCount;
    @InjectView(R.id.order_etRemark)
    EditText order_etRemark;
    @InjectView(R.id.order_tvReceiverAreaName)
    TextView order_tvReceiverAreaName;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case PICK_POSTER_MOBILE:
            case PICK_RECEIVER_MOBILE:
                Uri contactData = data.getData();
                Cursor cursor = managedQuery(contactData, null, null, null,
                        null);
                cursor.moveToFirst();
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                String num = CommonUtil.getContactPhone(mContext, cursor);
                if (requestCode == PICK_POSTER_MOBILE) {
                    order_etPosterPhone.setText(num);
                    order_etPosterName.setText(name);
                } else {
                    order_etReceiverPhone.setText(num);
                    order_etReceiverName.setText(name);
                }
                break;
            case PICK_POSTER_ADDRESS:
            case PICK_RECEIVER_ADDRESS:
                AddressInfo info = (AddressInfo) data.getSerializableExtra("data");
                if (requestCode == PICK_POSTER_ADDRESS) {
                    order_etPosterName.setText(info.getName());
                    order_etPosterPhone.setText(info.getPhone());
                    order_tvPosterAreaName.setText(info.getRegion());
                    order_etPosterAddress.setText(info.getAddress());
                    try {
                        loadBranch(Integer.valueOf(info.getRegion_id()));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    order_etReceiverName.setText(info.getName());
                    order_etReceiverPhone.setText(info.getPhone());
                    order_etReceiverAddress.setText(info.getAddress());
                    order_tvReceiverAreaName.setText(info.getRegion());
                }
                break;
            default:
                break;
        }
    }

    @InjectView(R.id.order_etPosterName)
    EditText order_etPosterName;
    @InjectView(R.id.order_etPosterPhone)
    EditText order_etPosterPhone;

}
