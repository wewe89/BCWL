package cn.appem.bcwl.adapter.address;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wewe.android.adapter.ExtBaseAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.appem.bcwl.R;
import cn.appem.bcwl.domain.AddressInfo;

/**
 * User:wewecn on 2015/7/13 10:01
 * Email:wewecn@qq.com
 */
public class AddressListAdapter extends ExtBaseAdapter<AddressInfo> {
    public AddressListAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AddressInfo info=getItem(position);
        ViewHolder holder=null;
        if(convertView==null){
            convertView=mInflater.inflate(R.layout.item_address_delivery,null);
            holder=new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder)convertView.getTag();
        }

        holder.address.setText(info.getRegion()+info.getAddress());
        holder.branch.setText(info.getBranch());
        holder.name.setText(info.getName());
        holder.phone.setText(info.getPhone());

        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.address_del_name)
        TextView name;
        @InjectView(R.id.address_del_phone)
        TextView phone;
        @InjectView(R.id.address_del_address)
        TextView address;
        @InjectView(R.id.address_del_branch)
        TextView branch;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
