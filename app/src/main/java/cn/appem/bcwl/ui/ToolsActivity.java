package cn.appem.bcwl.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import cn.appem.bcwl.R;
import cn.appem.bcwl.ui.base.BaseActivity;

/**
 * User:wewecn on 2015/7/1 10:43
 * Email:wewecn@qq.com
 */
public class ToolsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);
        setTitle("工具");
    }

    public void launchActivity(View view) {
        switch (view.getId()) {
            case R.id.tool_info:
                startActivity(new Intent(mContext,InfoActivity.class));
                break;
            case R.id.tool_time:
                break;
            case R.id.tool_demand:
                startActivity(new Intent(mContext,DemandActivity.class));
                break;
        }
    }
}
