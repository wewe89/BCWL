package com.wewe.android.util;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wewe.android.BaseApplication;
import com.wewe.android.R;

/**
 * User:wewecn on 2015/6/26 10:53
 * Email:wewecn@qq.com
 */
public class ToastUtil {
    private static CharSequence lastToast = "";
    private static long lastToastTime;
    private ToastUtil(){

    }
    public static void showToast(int message) {
        showToast(message, Toast.LENGTH_LONG, 0);
    }

    public static void showToast(CharSequence message) {
        showToast(message, Toast.LENGTH_LONG, 0, GRAVITY);
    }

    public static void showToast(int message, int icon) {
        showToast(message, Toast.LENGTH_LONG, icon);
    }

    public static void showToast(CharSequence message, int icon) {
        showToast(message, Toast.LENGTH_LONG, icon, GRAVITY);
    }

    public static void showToastShort(int message) {
        showToast(message, Toast.LENGTH_SHORT, Toast.LENGTH_SHORT);
    }

    public static void showToastShort(CharSequence message) {
        showToast(message, Toast.LENGTH_SHORT, 0,
                GRAVITY);
    }

    private static final int GRAVITY = Gravity.CENTER;

    public static void showToastShort(int message, Object... args) {
        showToast(message, Toast.LENGTH_SHORT, 0, GRAVITY, args);
    }

    public static void showToast(int message, int duration, int icon) {
        showToast(message, duration, icon, GRAVITY);
    }

    public static void showToast(int message, int duration, int icon,
                                 int gravity) {
        showToast(BaseApplication.context().getString(message), duration, icon, gravity);
    }
    public static void showToast(int message, int duration, int icon,
                                 int gravity, Object... args) {
        showToast(BaseApplication.context().getString(message, args), duration, icon, gravity);
    }

    public static void showToast(CharSequence message, int duration, int icon,
                                 int gravity) {
        if (message != null && !message.toString().equalsIgnoreCase("")) {
            long time = System.currentTimeMillis();
            if (!message.toString().equalsIgnoreCase(lastToast.toString())
                    || Math.abs(time - lastToastTime) > 2000) {
                View view = LayoutInflater.from(BaseApplication.context()).inflate(
                        R.layout.view_toast, null);
                TextView tv = ((TextView) view.findViewById(R.id.toast_tv));
                tv.setText(message);
                if (icon != 0) {
                    tv.setCompoundDrawables(BaseApplication.context().getResources().getDrawable(icon), null, null, null);
                }
                tv.setHeight(LocalDisplay.dp2px(40));
                tv.setWidth(LocalDisplay.SCREEN_WIDTH_PIXELS - 60);
                Toast toast = new Toast(BaseApplication.context());
                toast.setView(view);
                toast.setGravity(gravity, 0, 0);
                toast.setDuration(duration);
                toast.show();

                lastToast = message;
                lastToastTime = System.currentTimeMillis();
            }
        }
    }
}
