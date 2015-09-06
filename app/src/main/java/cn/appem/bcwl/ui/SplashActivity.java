package cn.appem.bcwl.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.InjectView;
import cn.appem.bcwl.R;
import cn.appem.bcwl.ui.base.BaseActivity;
import cn.appem.bcwl.util.PrefUtils;

public class SplashActivity extends BaseActivity {
    @InjectView(R.id.splash_root)
    LinearLayout rootLayout;
    @InjectView(R.id.tv_version)
    TextView versionText;

    private static final int sleepTime = 2000;

    @Override
    protected void onCreate(Bundle arg0) {
        mSataeBarBackground = R.color.transparent;
        super.onCreate(arg0);
        setContentView(R.layout.activity_splash);
        versionText.setText(getVersion());
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(1500);
        rootLayout.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if (PrefUtils.getInstance(mContext).hasOpenEd()) {
                    intent.setClass(mContext, MainActivity.class);
                } else {
                    intent.setClass(mContext, GuideActivity.class);
                    PrefUtils.getInstance(mContext).setOpenEd();
                }
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    private String getVersion() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);
            String version = packinfo.versionName;
            return version;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "error";
        }
    }
}
