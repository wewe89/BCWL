package cn.appem.bcwl.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.wewe.android.util.ToastUtil;
import com.wewe.android.widget.EmptyLayout;
import com.wewe.android.widget.PullToRefreshListView;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.appem.bcwl.R;
import cn.appem.bcwl.adapter.BranchAdapter;
import cn.appem.bcwl.domain.BranchInfo;
import cn.appem.bcwl.net.Api;
import cn.appem.bcwl.net.JsonHandler;
import cn.appem.bcwl.ui.base.BaseActivity;
import cn.appem.bcwl.ui.base.LocationActivity;
import cn.appem.bcwl.util.PrefUtils;
import cn.appem.bcwl.util.UserPref;
import cn.appem.bcwl.view.MapPopupWindow;

import static com.wewe.android.util.LogUtils.*;

/**
 * User:wewecn on 2015/6/24 14:17
 * Email:wewecn@qq.com
 */
public class BranchActivity extends BaseActivity implements BaiduMap.OnMarkerClickListener{
    private static final String TAG = makeLogTag(BranchActivity.class);
    @InjectView(R.id.branch_list)
    ListView mListview;
    private BranchAdapter mAdapter;
    @InjectView(R.id.emptyview)
    EmptyLayout mEmptyView;

    @OnClick(R.id.navbar_right)
    public void switchBranckList(TextView button) {
        if (mapIsShow) {
            button.setText("地图");
            mListview.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.home_pop_menu_enter);
            mListview.setAnimation(animation);
        } else {
            button.setText("列表");
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.home_pop_menu_exit);
            mListview.setAnimation(animation);
            mListview.setVisibility(View.GONE);
        }
        mapIsShow = !mapIsShow;
    }

    private boolean mapIsShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch);
        mAdapter = new BranchAdapter(mContext);
        mListview.setAdapter(mAdapter);
        setTitle("网点查询");
        getData();
        getNavRight().setText("地图");
        initLocation();
        mEmptyView.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
    }

    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                mMyLocation = new LatLng(location.getLatitude(),
                        location.getLongitude());
                mAdapter.setLocation(mMyLocation);
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(mMyLocation);
                mBaiduMap.animateMapStatus(u);
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    private LatLng mMyLocation;
    void initLocation() {
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.mapview);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();

        mBaiduMap.setOnMarkerClickListener(this);
        mData = new ArrayList<>();
    }

    private List<BranchInfo> mData;

    private void getData() {
        mEmptyView.setErrorType(EmptyLayout.ErrorType.NETWORK_LOADING);
        Api.getCityBranchInfo(mContext, PrefUtils.getInstance(mContext).getCityId(), new JsonHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                mData = BranchInfo.parse(response);
                if (mData.size() == 0) {
                    mEmptyView.setErrorType(EmptyLayout.ErrorType.NODATA_ENABLE_CLICK);
                } else {
                    mEmptyView.dismiss();
                    mAdapter.setData(mData);
                    mAdapter.notifyDataSetChanged();
                    loadMarker();
                }
            }

            @Override
            public void onFailure(int statusCode, String msg) {
                mEmptyView.setErrorType(EmptyLayout.ErrorType.NETWORK_ERROR);
            }
        });
    }

    MapView mMapView;
    BaiduMap mBaiduMap;
    boolean isFirstLoc = true;// 是否首次定位

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }


    void loadMarker() {
        for (BranchInfo item : mData) {
            if (item.getLat()==0||item.getLng()==0) continue;
            LatLng latLng = new LatLng(item.getLat(),item.getLng());
            Marker marker = (Marker) mBaiduMap.addOverlay(new MarkerOptions().position(latLng)
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.mipmap.ic_map_marker)));
            Bundle bundle = new Bundle();
            bundle.putSerializable("info", item);
            marker.setExtraInfo(bundle);
            // 将地图移到到最后一个经纬度位置
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
            mBaiduMap.setMapStatus(u);
        }
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(mMyLocation));
    }

    InfoWindow mInfoWindow;

    @Override
    public boolean onMarkerClick(Marker marker) {
        final BranchInfo item = (BranchInfo) marker.getExtraInfo().getSerializable("info");
        Button button = new Button(getApplicationContext());
        button.setText(item.getStation_name() + ">>");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showPopup(item);
            }
        });
        LatLng ll = marker.getPosition();
        mInfoWindow = new InfoWindow(button, ll, -47);
        mBaiduMap.showInfoWindow(mInfoWindow);
        return true;
    }

    private MapPopupWindow popWindow;
    @InjectView(R.id.branch_keyword)
    EditText etKeywords;

    private void showPopup(BranchInfo item) {
        if (this.popWindow == null)
            this.popWindow = new MapPopupWindow(this);
        popWindow.setData(item, mMyLocation);
        popWindow.showAtLocation(etKeywords, Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void onBackPressed() {
        if (mapIsShow) {
            switchBranckList(getNavRight());
            return;
        }
        super.onBackPressed();
    }
}
