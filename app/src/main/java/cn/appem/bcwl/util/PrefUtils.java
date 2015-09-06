package cn.appem.bcwl.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefUtils {

    private static PrefUtils mPreferenceUtils;

    private PrefUtils(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private SharedPreferences mSharedPreferences;

    public void clear() {
        mSharedPreferences.edit().clear().apply();
    }

    public synchronized static PrefUtils getInstance(Context cxt) {
        if (mPreferenceUtils == null) {
            mPreferenceUtils = new PrefUtils(cxt);
        }
        return mPreferenceUtils;
    }

    private static final String IS_FIRST = "is_first";

    public void setOpenEd() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(IS_FIRST, true);
        editor.apply();
    }

    public boolean hasOpenEd() {
        return mSharedPreferences.getBoolean(IS_FIRST, false);
    }

    public void setCityName(String name) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(CITY_NAME, name);
        editor.apply();
    }

    public void setCityId(int name) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(CITY_ID, name);
        editor.apply();
    }

    public String getCity() {
        return mSharedPreferences.getString(CITY_NAME, "成都市");
    }

    public int getCityId() {
        return mSharedPreferences.getInt(CITY_ID, 2);
    }

    private static final String CITY_NAME = "city_name";
    private static final String CITY_ID = "city_id";
}
