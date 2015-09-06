package cn.appem.bcwl.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.wewe.android.dialog.DialogHelper;
import com.wewe.android.dialog.WaitDialog;
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
 * User:wewecn on 2015/8/7 14:22
 * Email:wewecn@qq.com
 */
public class ModifyPwdActivity extends BaseActivity {
    @InjectView(R.id.et_oldpwd)
    EditText etOld;
    @InjectView(R.id.et_oldpwd)
    EditText etNew;
    @InjectView(R.id.et_oldpwd)
    EditText etNewre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifypwd);
        setTitle("修改密码");
    }

    public void doModify(View view) {
        String oldPwd=etOld.getText().toString();
        if(StringUtil.isEmpty(oldPwd)){
            ToastUtil.showToastShort("请输入旧密码");
            return;
        }
        final String newPwd=etNew.getText().toString();
        String renewPwd=etNewre.getText().toString();
        if(!newPwd.equals(renewPwd)){
            ToastUtil.showToastShort("输入的新密码不一致");
            return;
        }
        if(newPwd.length()<6){
            ToastUtil.showToastShort("新密码长度不能少于6位");
            return;
        }

        final WaitDialog dialog= DialogHelper.getWaitDialog(mContext,"修改中");
        dialog.show();
        Api.modifyPwd(mContext, oldPwd, newPwd, new JsonHandler() {
            @Override
            public void onFailure(int statusCode, String msg) {
                ToastUtil.showToastShort("修改密码失败");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                if(response.optBoolean("success")){
                    ToastUtil.showToastShort("修改成功");
                    UserPref pref=UserPref.getInstance(mContext);
                    pref.setPassword(newPwd);
                    finish();
                }else {
                    ToastUtil.showToastShort(response.optString("message"));
                }
            }
        });
    }
}
