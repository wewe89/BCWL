package cn.appem.bcwl.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wewe.android.adapter.ExtBaseAdapter;

import cn.appem.bcwl.R;
import cn.appem.bcwl.domain.StationModel;

/**
 * User:wewecn on 2015/7/13 15:45
 * Email:wewecn@qq.com
 */
public class MyBrinchAdapter extends ExtBaseAdapter<StationModel> {
    public MyBrinchAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StationModel info = (StationModel) getItem(position);
        View root = mInflater.inflate(R.layout.item_spinner, null);
        TextView tv = (TextView) root.findViewById(R.id.text);
        tv.setText(info.getStation_full_name());
        return root;
    }
}
