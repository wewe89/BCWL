package cn.appem.bcwl.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * User:wewecn on 2015/7/9 10:26
 * Email:wewecn@qq.com
 */
public class UserPref {
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String MOBILE = "mobile";
    private static final String IS_LOGINED = "is_logined";
    private static final String USER_ID = "user_id";
    private static UserPref mPreferenceUtils;

    private UserPref(Context context) {
        mSharedPreferences = context.getSharedPreferences("pref_user",Context.MODE_PRIVATE);
    }

    private SharedPreferences mSharedPreferences;

    public void clear() {
        mSharedPreferences.edit().clear().apply();
    }

    public synchronized static UserPref getInstance(Context cxt) {
        if (mPreferenceUtils == null) {
            mPreferenceUtils = new UserPref(cxt);
        }
        return mPreferenceUtils;
    }

    public void setUsername(String username) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(USERNAME, username);
        editor.apply();
    }
    public void setPassword(String username) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PASSWORD, username);
        editor.apply();
    }

    public void setMobile(String mobile) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(MOBILE, mobile);
        editor.apply();
    }

    public String getMobile() {
        return mSharedPreferences.getString(MOBILE, "");
    }

    public void setLogined(boolean flag) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(IS_LOGINED, flag);
        editor.apply();
    }

    public boolean isLogined() {
        return mSharedPreferences.getBoolean(IS_LOGINED, false);
    }

    public void setUserId(int userId) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(USER_ID, userId);
        editor.apply();
    }

    public int getUserId() {
        return mSharedPreferences.getInt(USER_ID, 0);
    }

    public String getUsername() {
        return mSharedPreferences.getString(USERNAME, "");
    }

    public String getPassword() {
        return mSharedPreferences.getString(PASSWORD, "");
    }

}
