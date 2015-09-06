package cn.appem.bcwl.ui;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import cn.appem.bcwl.R;
import cn.appem.bcwl.ui.base.BaseActivity;

/**
 * User:wewecn on 2015/8/3 14:02
 * Email:wewecn@qq.com
 */
public class DemandActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demand);
        setTitle("运输要求");

        TextView tv=(TextView) findViewById(R.id.demand_tv);
        tv.setText(Html.fromHtml(getResources().getString(R.string.demand)));
    }
}
