package cn.appem.bcwl.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;

import cn.appem.bcwl.R;
import cn.appem.bcwl.domain.BranchInfo;
import cn.appem.bcwl.ui.RoutePlanActivity;
import cn.appem.bcwl.ui.waybill.DraftsEditActivity;

/**
 * User:wewecn on 2015/6/25 16:11
 * Email:wewecn@qq.com
 */
public class MapPopupWindow extends PopupWindow {
    TextView name;
    TextView phone;
    TextView address;
    Button order;
    Button go;
    private Context mContext;

    public MapPopupWindow(Context context) {
        super(context);
        mContext = context;
        initViews();
    }

    public void setData(final BranchInfo info, final LatLng latLng) {
        name.setText(info.getStation_name());
        phone.setText(info.getMobile());
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + info.getMobile());
                intent.setData(data);
                mContext.startActivity(intent);
            }
        });
        address.setText(info.getAddress());
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoutePlanActivity.launchThis(mContext,latLng,new LatLng(info.getLat(),info.getLng()));
            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DraftsEditActivity.launchThis(mContext,info);
            }
        });
    }

    void initViews() {
        View root = LayoutInflater.from(mContext).inflate(R.layout.dialog_branch_map, null);
        setContentView(root);
        name=(TextView)root.findViewById(R.id.map_name);
        address=(TextView)root.findViewById(R.id.map_address);
        phone=(TextView)root.findViewById(R.id.map_phone);
        go=(Button)root.findViewById(R.id.map_go);
        order=(Button)root.findViewById(R.id.map_order);

        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(R.style.home_popupmenu_animation);
        setFocusable(true);
        setBackgroundDrawable(new PaintDrawable());
        setOutsideTouchable(true);
    }
}
