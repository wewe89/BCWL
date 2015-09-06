package cn.appem.bcwl.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wewe.android.adapter.ExtBaseAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.appem.bcwl.R;
import cn.appem.bcwl.domain.RecordModel;

/**
 * User:wewecn on 2015/7/20 09:55
 * Email:wewecn@qq.com
 */
public class WaybillRecordAdapter extends ExtBaseAdapter<RecordModel> {
    private Context mContext;
    public WaybillRecordAdapter(Context context) {
        super(context);
         mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_record, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final RecordModel item = getItem(position);

        holder.mobile.setText(Html.fromHtml("<font color='#6A6AFF'><u>" + item.getMobile() + "</u></font>"));
        holder.mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //用intent启动拨打电话
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + item.getMobile()));
                mContext.startActivity(intent);
            }
        });

        holder.orderid.setText("流水号：" + item.getOrderId());
        holder.postAddress.setText("发货网点："+item.getPostBranch());
        holder.receiveAddress.setText(item.getReceiveBranch());
        holder.username.setText(item.getUserName());
        return convertView;
    }

    static class ViewHolder {
        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

        @InjectView(R.id.record_orderid)
        TextView orderid;
        @InjectView(R.id.record_phone)
        TextView mobile;
        @InjectView(R.id.record_username)
        TextView username;
        @InjectView(R.id.record_postaddress)
        TextView postAddress;
        @InjectView(R.id.record_receiveaddress)
        TextView receiveAddress;
    }
}
