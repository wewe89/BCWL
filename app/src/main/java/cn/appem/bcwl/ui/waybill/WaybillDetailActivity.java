package cn.appem.bcwl.ui.waybill;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.List;

import butterknife.InjectView;
import cn.appem.bcwl.R;
import cn.appem.bcwl.domain.WaybillDetail;
import cn.appem.bcwl.domain.WaybillState;
import cn.appem.bcwl.net.Api;
import cn.appem.bcwl.net.JsonHandler;
import cn.appem.bcwl.ui.base.BaseActivity;

/**
 * User:wewecn on 2015/6/26 16:09
 * Email:wewecn@qq.com
 */
public class WaybillDetailActivity extends BaseActivity {
    @InjectView(R.id.waybilldetail_state)
    TextView state;
    @InjectView(R.id.waybilldetail_consignor)
    TextView consignor;
    @InjectView(R.id.waybilldetail_consignee)
    TextView consignee;
    @InjectView(R.id.waybilldetail_station)
    TextView station;
    @InjectView(R.id.waybilldetail_cargo)
    TextView cargo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waybill_detail);
        setTitle("运单详情");
        WaybillState item = (WaybillState) getIntent().getSerializableExtra("waybill_state");
        state.setText("状态：" + item.getState());
        consignor.setText("发货人：" + item.getConsignor());
        consignee.setText("收货人：" + item.getConsignee());
        station.setText("发货站：" + item.getStation());
        cargo.setText("类型：" + item.getCargo());
        loadDetail(item.getWaybill_id());
    }

    void loadDetail(String id) {
        Api.searchDetail(mContext, id, new JsonHandler() {
            @Override
            public void onFailure(int statusCode, String msg) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                List<WaybillDetail> data = WaybillDetail.parse(response);
                initDetail(data);
            }
        });
    }

    @InjectView(R.id.waybilldetail_container)
    LinearLayout mContainer;

    void initDetail(List<WaybillDetail> data) {
        for (int index = 0; index < data.size(); index++) {
            WaybillDetail item = data.get(index);
            View root = LayoutInflater.from(mContext).inflate(R.layout.item_waybill, null);
            mContainer.addView(root);
            if (index == 0) {
//                View markerView = root.findViewById(R.id.waybill_remarker_view);
//                markerView.setBackgroundColor(getResources().getColor(R.color.orange));
                ImageView markerImg = (ImageView) root.findViewById(R.id.waybill_remarker_img);
                markerImg.setImageResource(R.mipmap.waybill_follow_orange);
            }
            TextView time = (TextView) root.findViewById(R.id.waybill_time);
            TextView content = (TextView) root.findViewById(R.id.waybill_content);

            time.setText(item.getCreate_date());
            content.setText(item.getState());
        }
    }
}
