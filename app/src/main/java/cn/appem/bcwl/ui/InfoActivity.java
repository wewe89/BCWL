package cn.appem.bcwl.ui;

import android.os.Bundle;

import cn.appem.bcwl.R;
import cn.appem.bcwl.ui.base.BaseActivity;

/**
 * User:wewecn on 2015/8/3 14:15
 * Email:wewecn@qq.com
 */
public class InfoActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setTitle("产品介绍");
    }
}
