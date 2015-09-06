package cn.appem.bcwl.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

/**
 * User：wewecn on 2015/4/29 11:56
 * Email：wewecn@qq.com
 */
public class RegisterActivity extends BaseActivity {
    @InjectView(R.id.register_mobile)
    EditText mobileEt;
    @InjectView(R.id.register_pwd)
    EditText pwdEt;
    @InjectView(R.id.register_repwd)
    EditText repwdEt;
    @InjectView(R.id.register_username)
    EditText usernameEt;

    String currentUsername,currentPassword;
    void login2easemob(){
        final WaitDialog pd= DialogHelper.getWaitDialog(mContext, "正在登陆聊天服务器");
        pd.show();
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
                            pd.dismiss();
                            DemoApplication.getInstance().logout(null);
                            ToastUtil.showToastShort(R.string.login_failure_failed);
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
                if (!mContext.isFinishing() && pd.isShowing()) {
                    pd.dismiss();
                }
                // 进入主页面
                Intent intent = new Intent(mContext,
                        MainActivity.class);
                startActivity(intent);

                finish();
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(final int code, final String message) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        ToastUtil.showToast(getString(R.string.Login_failed) + message);
                    }
                });
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("注册");
    }

    public void doRegister(View view) {
        final String username = usernameEt.getText().toString().trim();
        if (StringUtil.isEmpty(username)) {
            ToastUtil.showToastShort("请输入用户名");
            return;
        }
        if(!username.matches("^[a-zA-Z0-9_]{6,12}$")){
            ToastUtil.showToastShort("用户名由6~12位的字母、数字或下划线组成");
            return;
        }
        final String mobile = mobileEt.getText().toString().trim();
        if (!StringUtil.isMobileNO(mobile)) {
            ToastUtil.showToastShort("手机号码格式不正确");
            return;
        }
        final String pwd = pwdEt.getText().toString();
        if (pwd.length() < 6) {
            ToastUtil.showToastShort("密码不能少于6位");
            return;
        }
        final String repwd = repwdEt.getText().toString();
        if(!repwd.equals(pwd)){
            ToastUtil.showToastShort("两次输入的密码不一样");
            return;
        }

        currentPassword=Md5Util.strToMd5_32(pwd);

        final UserPref pref = UserPref.getInstance(mContext);
        pref.clear();
        Api.register(mContext, username, currentPassword, mobile, new JsonHandler() {
            @Override
            public void onFailure(int statusCode, String msg) {
                if (statusCode != 200)
                    msg = "注册失败";
                ToastUtil.showToastShort(msg);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if (response.optBoolean("success")) {
                    UserPref pref = UserPref.getInstance(mContext);
                    pref.setMobile(mobile);
                    pref.setUserId(response.optInt("data"));
                    pref.setUsername(username);
                    currentUsername=username;
                    pref.setPassword(currentPassword);
                    pref.setLogined(true);

                    login2easemob();
                } else onFailure(200, response.optString("message"));
            }
        });
    }
}
