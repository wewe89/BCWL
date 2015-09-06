package cn.appem.bcwl.ui.fragemnt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chatuidemo.DemoApplication;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.wewe.android.dialog.DialogAdapter;
import com.wewe.android.dialog.DialogHelper;
import com.wewe.android.dialog.WaitDialog;
import com.wewe.android.util.ToastUtil;

import java.io.File;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.appem.bcwl.AppContext;
import cn.appem.bcwl.Constant;
import cn.appem.bcwl.R;
import cn.appem.bcwl.ui.AboutActivity;
import cn.appem.bcwl.ui.LoginActivity;
import cn.appem.bcwl.ui.ModifyPwdActivity;
import cn.appem.bcwl.ui.address.AddressLisActivity;
import cn.appem.bcwl.ui.base.BaseFragment;
import cn.appem.bcwl.util.UserPref;

import static com.wewe.android.util.LogUtils.makeLogTag;

/**
 * User:wewecn on 2015/4/30 16:08
 * Email:wewecn@qq.com
 */
public class SettingFragment extends BaseFragment {
    private final static String TAG = makeLogTag(SettingFragment.class);
    //    private FeedbackAgent mFeedback;
    @InjectView(R.id.setting_modifypwd)
    View modify;
    @InjectView(R.id.setting_modifypwd_divider)
    View modifyDivider;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        mFeedback = new FeedbackAgent(mContext);
//        mFeedback.sync();
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @OnClick({R.id.setting_share, R.id.setting_about, R.id.setting_modifypwd, R.id.setting_address_rec, R.id.setting_address_del, R.id.setting_user_phone_layout, R.id.set_feedback})
    public void handleClick(View view) {
        switch (view.getId()) {
            case R.id.setting_share:
                umengShare();
                break;
            case R.id.setting_about:
                startActivity(new Intent(mContext, AboutActivity.class));
                break;
            case R.id.setting_address_rec:
                if (isLogin())
                    AddressLisActivity.launchThis(mContext, AddressLisActivity.TYPE_RECEIVE);
                break;
            case R.id.setting_address_del:
                if (isLogin())
                    AddressLisActivity.launchThis(mContext, AddressLisActivity.TYPE_POST);
                break;
            case R.id.setting_user_phone_layout:
                if (!UserPref.getInstance(mContext).isLogined()) {
                    startActivity(new Intent(mContext, LoginActivity.class));
                }
                break;
            case R.id.setting_modifypwd:
                if (isLogin())
                    startActivity(new Intent(mContext, ModifyPwdActivity.class));
                break;
        }
    }

    boolean isLogin() {
        boolean res = AppContext.isLogin();
        if (res) {
            return true;
        } else {
            ToastUtil.showToastShort("请先登录");
            startActivity(new Intent(mContext, LoginActivity.class));
            return false;
        }
    }

    UMSocialService mController;

    void initUmengShare() {
        // 首先在您的Activity中添加如下成员变量
        mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        // 设置分享内容
        mController.setShareContent("友盟社会化组件（SDK）让移动应用快速整合社交分享功能，http://www.umeng.com/social");
        // 设置分享图片, 参数2为图片的url地址
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getAssets().open("chatim.png"));
            mController.setShareMedia(new UMImage(getActivity(), bitmap));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(getActivity(), Constant.WEIXIN_APP_KEY, Constant.WEIXIN_APPSECRET);
        wxHandler.addToSocialSDK();
        // 添加微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(getActivity(), Constant.WEIXIN_APP_KEY, Constant.WEIXIN_APPSECRET);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();

        //参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(getActivity(), Constant.QQ_APP_KEY,
                Constant.QQ_APPSECRET);
        qqSsoHandler.addToSocialSDK();

        //参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(getActivity(), Constant.QZone_APP_KEY,
                Constant.QZone_APPSECRET);
        qZoneSsoHandler.addToSocialSDK();

        //设置新浪SSO handler
        mController.getConfig().setSsoHandler(new SinaSsoHandler());

        //设置腾讯微博SSO handler
        mController.getConfig().setSsoHandler(new TencentWBSsoHandler());

        // 添加短信
        SmsHandler smsHandler = new SmsHandler();
        smsHandler.addToSocialSDK();
    }

    void umengShare() {
        // 是否只有已登录用户才能打开分享选择页
        mController.openShare(getActivity(), false);
    }

    @InjectView(R.id.setting_address_del)
    View addressDel;
    @InjectView(R.id.setting_address_rec)
    View addressRec;
    @InjectView(R.id.setting_user_phone)
    TextView phone;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle("设置");
        getNavLeft().setVisibility(View.GONE);
        initView();

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserPref.getInstance(mContext).clear();
                initView();
                logout();
            }
        });
    }

    void logout() {
//        final WaitDialog pd = DialogHelper.getWaitDialog(mContext, R.string.Are_logged_out);
//        pd.show();
        DemoApplication.getInstance().logout(new EMCallBack() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(int code, String message) {
            }
        });
    }

    void initView() {
        if (UserPref.getInstance(mContext).isLogined()) {
            phone.setText(UserPref.getInstance(mContext).getUsername());
            log.setVisibility(View.VISIBLE);

            modify.setVisibility(View.VISIBLE);
            modifyDivider.setVisibility(View.VISIBLE);
        } else {
            phone.setText("请先登录");
            log.setVisibility(View.GONE);

            modify.setVisibility(View.GONE);
            modifyDivider.setVisibility(View.GONE);
        }
    }

    @InjectView(R.id.setting_log)
    Button log;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        imagePath = new File(PathUtil.getInstance().getTempPath(null) + "/chatim.png");
//        LOGI(TAG, imagePath.toString());
//        if (!imagePath.exists()) {
//            FileUtil.copyAssets2SDCard(mContext, "chatim.png", PathUtil.getInstance().getTempPath(null).toString());
//        }
        initUmengShare();
    }

    private File imagePath;
//    private void showShare() {
//        String url="http://shouji.baidu.com/software/item?docid=7659644";
//        ShareSDK.initSDK(mContext);
//        OnekeyShare oks = new OnekeyShare();
//        //关闭sso授权
//        oks.disableSSOWhenAuthorize();
////        oks.setDialogMode();
////        oks.setEditPageBackground();
//        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//        oks.setTitle(getResources().getString(R.string.app_name));
//        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl(url);
//        // text是分享文本，所有平台都需要这个字段
//        oks.setText(getString(R.string.share_txt));
//        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
////        oks.setImageUrl("http://a.hiphotos.bdimg.com/wisegame/pic/item/3043fbf2b2119313289fad4d61380cd790238d6b.jpg");
//        oks.setImagePath(imagePath.toString());
//        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl(url);
//        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("加入集信通，享受畅聊乐趣！");
//        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(getResources().getString(R.string.app_name));
//        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
////        oks.setSiteUrl(url);
//
////        oks.addHiddenPlatform(.NAME);
////        oks.addHiddenPlatform(WechatFavorite.NAME);
//
//        // 启动分享GUI
//        oks.show(mContext);
//    }
}
