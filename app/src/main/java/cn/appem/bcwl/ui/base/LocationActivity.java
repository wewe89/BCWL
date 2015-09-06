package cn.appem.bcwl.ui.base;

import android.os.Bundle;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import static com.wewe.android.util.LogUtils.LOGD;
import static com.wewe.android.util.LogUtils.makeLogTag;

/**
 * 定位
 * Created by Administrator on 2015/1/28.
 */
abstract public class LocationActivity extends BaseActivity {
    private static final String TAG = makeLogTag(LocationActivity.class);
    /**
     * 监听函数，有新位置的时候，格式化成字符串，输出到屏幕中
     */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
            if (isFirstLoc) {
                isFirstLoc = false;
                mLatlng = location;
                locationFinished(mLatlng);
                LOGD(TAG, "On location change received:" + location.getLatitude() + "-" + location.getLongitude() + "-" +
                        location.getStreet()+ "-" + location.getStreetNumber()+"==="+location.getAddrStr());
            }
        }
    }

    protected void locationFinished(BDLocation mLatlng) {

    }

    // 定位相关
    LocationClient mLocClient;
    protected BDLocation mLatlng;
    private MyLocationListenner myListener = new MyLocationListenner();
    boolean isFirstLoc = true;// 是否首次定位

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mLocClient = new LocationClient(getApplicationContext());
        mLocClient.registerLocationListener(myListener);

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
         option.setCoorType("bd09ll"); //设置坐标类型
        // Johnson change to use gcj02 coordination. chinese national standard
        // so need to conver to bd09 everytime when draw on baidu map
        option.setScanSpan(30000);
        option.setIsNeedAddress(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setAddrType("all");
        mLocClient.setLocOption(option);
    }

    @Override
    protected void onResume() {
        if (mLocClient != null) {
            mLocClient.start();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLocClient != null) {
            mLocClient.stop();
        }
    }
}
