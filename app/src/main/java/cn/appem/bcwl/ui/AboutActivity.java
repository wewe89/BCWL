package cn.appem.bcwl.ui;

import android.os.Bundle;
import android.widget.TextView;

import butterknife.InjectView;
import cn.appem.bcwl.AppContext;
import cn.appem.bcwl.R;
import cn.appem.bcwl.ui.base.BaseActivity;

/**
 * User:wewecn on 2015/7/1 10:16
 * Email:wewecn@qq.com
 */
public class AboutActivity extends BaseActivity {
    @InjectView(R.id.about_version)
    TextView mVersion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setTitle("关于我们");
        mVersion.setText("版本号："+AppContext.getInstance().getPackageInfo().versionName);
    }
}
