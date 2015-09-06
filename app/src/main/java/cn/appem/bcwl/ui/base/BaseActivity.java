package cn.appem.bcwl.ui.base;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;

import static com.wewe.android.util.LogUtils.*;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import cn.appem.bcwl.R;
import cn.appem.bcwl.net.RequestManager;

public abstract class BaseActivity extends FragmentActivity {
    @Optional
    @InjectView(R.id.navbar_left)
    ImageView mNavLeft;
    @Optional
    @InjectView(R.id.navbar_middle)
    TextView mNavMiddle;
    @Optional
    @InjectView(R.id.navbar_right)
    Button mNavRight;

    public ImageView getNavLeft() {
        return mNavLeft;
    }
    public TextView getNavMiddle() {
        return mNavMiddle;
    }
    public Button getNavRight() {
        return mNavRight;
    }

    protected FragmentActivity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        LOGD(getClass().getSimpleName(), "onCreate");
        initSystemBar();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setTitle(CharSequence title) {
        mNavMiddle.setText(title);
    }

    @Override
    public void setTitle(int title) {
        mNavMiddle.setText(title);
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    protected int mSataeBarBackground = R.color.style_color_primary;

    protected void initSystemBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);

            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(mSataeBarBackground);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LOGD(getClass().getSimpleName(), "onPause");
        MobclickAgent.onPause(this);
        RequestManager.getHttpClient().cancelRequests(this, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LOGD(getClass().getSimpleName(), "onResume");
        MobclickAgent.onResume(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackClick(View view) {
        onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(R.anim.anim_none, R.anim.trans_center_2_right);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.inject(this);
        setupToolbar();
    }

    protected void setupToolbar() {
        if (mNavLeft != null) {
            mNavLeft.setImageResource(R.mipmap.ic_action_back);
        }
    }
}
