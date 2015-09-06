package cn.appem.bcwl.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.wewe.android.dialog.CommonDialog;
import com.wewe.android.dialog.DialogHelper;
import com.wewe.android.util.ToastUtil;

import butterknife.InjectView;
import cn.appem.bcwl.R;
import cn.appem.bcwl.ui.base.BaseActivity;

import static com.wewe.android.util.LogUtils.makeLogTag;

/**
 * 线路规划
 * User:wewecn on 2015/6/26 09:57
 * Email:wewecn@qq.com
 */
public class RoutePlanActivity extends BaseActivity implements OnGetRoutePlanResultListener {
    private final static String TAG = makeLogTag(RoutePlanActivity.class);
    private LatLng mStartPoint, mEndPoint;

    //地图相关，使用继承MapView的MyRouteMapView目的是重写touch事件实现泡泡处理
    //如果不处理touch事件，则无需继承，直接使用MapView即可
    @InjectView(R.id.mapview)
    MapView mMapView = null;    // 地图View
    BaiduMap mBaidumap = null;
    //搜索相关
    RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(
                Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routeplan);
        setTitle("线路规划");
        getNavRight().setText("导航");
        getNavRight().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NaviParaOption para = new NaviParaOption();
                para.startName("从这里开始");
                para.startPoint(mStartPoint);
                para.endName("到这里结束");
                para.endPoint(mEndPoint);
                try {
                    BaiduMapNavigation.openBaiduMapNavi(para, mContext);
                } catch (BaiduMapAppNotSupportNaviException e) {
                    e.printStackTrace();
                    CommonDialog dialog = DialogHelper.getAlertDialog(mContext);
                    dialog.setMessage("您尚未安装百度地图app或app版本过低");
                    dialog.setTitle("提示");
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            }
        });
        initMap();
    }

    public void initMap() {
        double lat = getIntent().getDoubleExtra("start_lat", 0d);
        double lng = getIntent().getDoubleExtra("start_lng", 0d);
        mStartPoint = new LatLng(lat, lng);
        mBaidumap = mMapView.getMap();
        lat = getIntent().getDoubleExtra("end_lat", 0d);
        lng = getIntent().getDoubleExtra("end_lng", 0d);
        mEndPoint = new LatLng(lat, lng);

        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);


        //设置起终点信息，对于tranist search 来说，城市名无意义
        PlanNode stNode = PlanNode.withLocation(mStartPoint);
        PlanNode enNode = PlanNode.withLocation(mEndPoint);


        mSearch.drivingSearch((new DrivingRoutePlanOption())
                .from(stNode)
                .to(enNode));
    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            ToastUtil.showToastShort("抱歉，未找到结果");
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            ToastUtil.showToastShort("起终点或途经点地址有岐义");
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            //result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaidumap);
            routeOverlay = overlay;
            mBaidumap.setOnMarkerClickListener(overlay);
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    //定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
//            if (useDefaultIcon) {
//                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
//            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
//            if (useDefaultIcon) {
//                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
//            }
            return null;
        }
    }

    OverlayManager routeOverlay = null;

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    public static void launchThis(Context mContext, LatLng startPoint, LatLng endPoint) {
        if(startPoint==null){
            ToastUtil.showToastShort("获取位置失败，请稍后再试");
            return;
        }
        Intent intent = new Intent(mContext, RoutePlanActivity.class);
        intent.putExtra("end_lat", endPoint.latitude);
        intent.putExtra("end_lng", endPoint.longitude);
        intent.putExtra("start_lat", startPoint.latitude);
        intent.putExtra("start_lng", startPoint.longitude);
        mContext.startActivity(intent);
    }

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
        mSearch.destroy();
        mMapView.onDestroy();
        super.onDestroy();
    }
}
