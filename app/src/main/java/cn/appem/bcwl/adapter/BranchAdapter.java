package cn.appem.bcwl.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.wewe.android.adapter.ExtBaseAdapter;
import com.wewe.android.util.ToastUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.appem.bcwl.R;
import cn.appem.bcwl.domain.BranchInfo;
import cn.appem.bcwl.ui.RoutePlanActivity;
import cn.appem.bcwl.ui.waybill.DraftsEditActivity;
import cn.appem.bcwl.util.PrefUtils;
import cn.appem.bcwl.util.UserPref;

/**
 * User:wewecn on 2015/6/24 14:33
 * Email:wewecn@qq.com
 */
public class BranchAdapter extends ExtBaseAdapter<BranchInfo> {
    public BranchAdapter(Context context) {
        super(context);
        mContext = context;
    }

    private Context mContext;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_branch, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final BranchInfo item = getItem(position);
        holder.address.setText(item.getAddress());
        holder.station_name.setText(item.getStation_name());
        holder.mobile.setText(Html.fromHtml("<font color='#6A6AFF'><u>" + item.getMobile() + "</u></font>"));
        holder.mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //用intent启动拨打电话
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + item.getMobile()));
                mContext.startActivity(intent);
            }
        });

        holder.go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latLng = new LatLng(item.getLat(), item.getLng());
                RoutePlanActivity.launchThis(mContext, startPoint, latLng);
            }
        });

        holder.order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DraftsEditActivity.launchThis(mContext, item);
            }
        });
        return convertView;
    }

    public void setLocation(LatLng location) {
        startPoint = location;
    }

    private LatLng startPoint;

    static class ViewHolder {
        @InjectView(R.id.item_branch_station_name)
        TextView station_name;
        @InjectView(R.id.item_branch_address)
        TextView address;
        @InjectView(R.id.item_branch_mobile)
        TextView mobile;
        @InjectView(R.id.item_branch_go)
        View go;
        @InjectView(R.id.item_branch_order)
        View order;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
