package cn.appem.bcwl.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.DemoApplication;
import com.wewe.android.dialog.DialogHelper;
import com.wewe.android.dialog.WaitDialog;
import com.wewe.android.string.Md5Util;
import com.wewe.android.string.StringUtil;
import com.wewe.android.util.ToastUtil;

import org.apache.http.Header;
import org.json.JSONObject;

import butterknife.InjectView;
import cn.appem.bcwl.R;
import cn.appem.bcwl.net.Api;
import cn.appem.bcwl.net.JsonHandler;
import cn.appem.bcwl.ui.base.BaseActivity;
import cn.appem.bcwl.util.UserPref;
import static com.wewe.android.util.LogUtils.*;
/**
 * User：wewecn on 2015/4/29 11:56
 * Email：wewecn@qq.com
 */
public class LoginActivity extends BaseActivity {
    @InjectView(R.id.login_reg)
    Button mRegBtn;
    @InjectView(R.id.login_name)
    EditText usernameEt;
    @InjectView(R.id.login_pwd)
    EditText pwdEt;
private static  final String TAG=makeLogTag(LoginActivity.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("登陆");
        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, RegisterActivity.class));
            }
        });
    }

    public void doLogin(View view) {
        currentUsername = usernameEt.getText().toString();
        if (StringUtil.isEmpty(currentUsername)) {
            ToastUtil.showToastShort("用户名不能为空");
            return;
        }
//        if (username.length() < 6) {
//            ToastUtil.showToastShort("登录名格式不正确");
//            return;
//        }
        currentPassword = pwdEt.getText().toString();
        if (currentPassword.length() < 6) {
            ToastUtil.showToastShort("密码不能少于6位");
            return;
        }
        final UserPref pref = UserPref.getInstance(mContext);
        pref.clear();
        currentPassword = Md5Util.strToMd5_32(currentPassword);
        final WaitDialog dialog=DialogHelper.getWaitDialog(mContext,"正在登陆");
        dialog.show();
        Api.login(mContext, currentUsername, currentPassword, new JsonHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if (response.optBoolean("success")) {
                    pref.setUsername(currentUsername);
                    pref.setUserId(response.optInt("data"));
                    pref.setLogined(true);
                    pref.setPassword(currentPassword);

                    login2easemob();
                } else onFailure(200, response.optString("message"));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, String msg) {
                ToastUtil.showToastShort(msg);
            }
        });
    }
    String currentUsername,currentPassword;

    void login2easemob() {
        final String currentUsername = UserPref.getInstance(mContext).getUsername();
        final String currentPassword = UserPref.getInstance(mContext).getPassword();
        // 调用sdk登陆方法登陆聊天服务器
        EMChatManager.getInstance().login(currentUsername, currentPassword, new EMCallBack() {
            @Override
            public void onSuccess() {
                // 登陆成功，保存用户名密码
                DemoApplication.getInstance().setUserName(currentUsername);
                DemoApplication.getInstance().setPassword(currentPassword);

                try {
                    // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                    // ** manually load all local groups and
                    EMChatManager.getInstance().loadAllConversations();
                    // 处理好友和群组
//                    initializeContacts();
                } catch (Exception e) {
                    e.printStackTrace();
                    // 取好友或者群聊失败，不让进入主页面
                    runOnUiThread(new Runnable() {
                        public void run() {
                            DemoApplication.getInstance().logout(null);
                            MainActivity.launchThis(mContext);
                        }
                    });
                    return;
                }
                // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(
                        DemoApplication.currentUserNick.trim());
                if (!updatenick) {
                    Log.e("LoginActivity", "update current user nick fail");
                }
                // 进入主页面
                LOGD(TAG,"登录成功");
                MainActivity.launchThis(mContext);
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(final int code, final String message) {
                runOnUiThread(new Runnable() {
                    public void run() {

                        LOGD(TAG,getString(R.string.Login_failed) + message);

                        MainActivity.launchThis(mContext);
                    }
                });
            }
        });
    }
}
