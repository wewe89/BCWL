package cn.appem.bcwl.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.easemob.chatuidemo.activity.BaseChatActivity;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import cn.appem.bcwl.R;

/**
 * User:wewecn on 2015/7/30 10:45
 * Email:wewecn@qq.com
 */
public class ChatActivity extends BaseChatActivity {
    @Override
    public int getContentViewId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initView() {
        initSystemBar();

        super.initView();
        findViewById(R.id.navbar_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        Button navRight = (Button) findViewById(R.id.navbar_right);
        navRight.setBackgroundResource(R.drawable.mm_title_remove);
        navRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyHistory();
            }
        });
        navMiddle = (TextView) findViewById(R.id.navbar_middle);
    }

    TextView navMiddle;

    @Override
    public void setName(String name) {
        navMiddle.setText(name);
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
}
