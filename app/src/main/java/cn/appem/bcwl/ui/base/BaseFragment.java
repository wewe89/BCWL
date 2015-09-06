package cn.appem.bcwl.ui.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import cn.appem.bcwl.R;
import cn.appem.bcwl.net.RequestManager;

import static com.wewe.android.util.LogUtils.LOGD;

/**
 */
public abstract class BaseFragment extends Fragment {
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

    public void setTitle(CharSequence title) {
        mNavMiddle.setText(title);
    }

    public void setTitle(int title) {
        mNavMiddle.setText(title);
    }

    protected Activity mContext;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        LOGD(getClass().getSimpleName(), "onViewCreated");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LOGD(getClass().getSimpleName(), "onActivityCreated");
    }

    @Override
    public void onPause() {
        super.onPause();
        LOGD(getClass().getSimpleName(), "onPause");
        MobclickAgent.onPageEnd(getClass().getSimpleName());
    }

    @Override
    public void onStart() {
        super.onStart();
        LOGD(getClass().getSimpleName(), "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        LOGD(getClass().getSimpleName(), "onResume");
        MobclickAgent.onPageStart(getClass().getSimpleName()); //统计页面
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LOGD(getClass().getSimpleName(), "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
