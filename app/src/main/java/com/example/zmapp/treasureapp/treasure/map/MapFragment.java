package com.example.zmapp.treasureapp.treasure.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.zmapp.treasureapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by gqq on 2017/3/31.
 */
// 地图和宝藏的展示
public class MapFragment extends Fragment {

    private static final int LOCATION_REQUEST_CODE = 100;
    @BindView(R.id.iv_located)
    ImageView mIvLocated;
    @BindView(R.id.btn_HideHere)
    Button mBtnHideHere;
    @BindView(R.id.centerLayout)
    RelativeLayout mCenterLayout;
    @BindView(R.id.iv_scaleUp)
    ImageView mIvScaleUp;
    @BindView(R.id.iv_scaleDown)
    ImageView mIvScaleDown;
    @BindView(R.id.tv_located)
    TextView mTvLocated;
    @BindView(R.id.tv_satellite)
    TextView mTvSatellite;
    @BindView(R.id.tv_compass)
    TextView mTvCompass;
    @BindView(R.id.ll_locationBar)
    LinearLayout mLlLocationBar;
    @BindView(R.id.tv_currentLocation)
    TextView mTvCurrentLocation;
    @BindView(R.id.iv_toTreasureInfo)
    ImageView mIvToTreasureInfo;
    @BindView(R.id.et_treasureTitle)
    EditText mEtTreasureTitle;
    @BindView(R.id.cardView)
    CardView mCardView;
    @BindView(R.id.layout_bottom)
    FrameLayout mLayoutBottom;
    @BindView(R.id.map_frame)
    FrameLayout mMapFrame;
    private BaiduMap mBaiduMap;
    private MapView mMapView;
    private LocationClient mLocationClient;
    private boolean isFirst = true;
    private LatLng mCurrentLocation;
    private String mCurrentAddr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map,container);

        // 1. 检测权限有没有授权成功
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // 没有成功，需要向用户申请
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        //视图处理工作

        //初始化百度地图
        initMapView();

        //初始化地图相关
        initLocation();
    }

    //初始化百度地图的操作
    private void initMapView(){

        //地图的状态
        MapStatus mapStatus =  new MapStatus.Builder()
                .rotate(0)// 地图旋转角度
                .zoom(15)// 地图缩放级别 3~21
                .overlook(0)// 地图俯仰角度
                .build();

        //设置地图信息
        BaiduMapOptions options = new BaiduMapOptions()
                .mapStatus(mapStatus)
                .compassEnabled(true)// 是否显示指南针
                .zoomGesturesEnabled(true)// 是否允许缩放手势
                .scaleControlEnabled(false)// 不显示比例尺
                .zoomControlsEnabled(false)// 不显示缩放控件
                ;

        //创建地图控件
        mMapView = new MapView(getContext(),options);

        //在布局中添加地图控件：0，放置在第一位
        mMapFrame.addView(mMapView,0);

        //拿到地图的操作类(设置地图的视图、地图状态变化、添加覆盖物等)
        mBaiduMap = mMapView.getMap();
    }

    // 初始化定位相关
    private void initLocation() {

        // 前置：激活定位图层
        mBaiduMap.setMyLocationEnabled(true);

        // 1. 第一步，初始化LocationClient类
        mLocationClient = new LocationClient(getContext().getApplicationContext());

        // 2. 第二步，配置定位SDK参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开GPS
        option.setCoorType("bd09ll");// 设置坐标类型，默认gcj02，会有偏差，设置返回的定位结果坐标系
        option.setIsNeedAddress(true);// 需要地址信息
        // 设置参数给LocationClient
        mLocationClient.setLocOption(option);

        // 3. 第三步，实现BDLocationListener接口
        mLocationClient.registerLocationListener(mBDLocationListener);

        // 4. 第四步，开始定位
        mLocationClient.start();

    }

    // 定位监听
    private BDLocationListener mBDLocationListener = new BDLocationListener() {

        // 当获取到定位数据的时候会触发
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            if (bdLocation==null){
                // 没有拿到数据，可以重新进行请求
                mLocationClient.requestLocation();
                return;
            }

            // 拿到定位的经纬度
            double latitude = bdLocation.getLatitude();
            double longitude = bdLocation.getLongitude();

            // 定位的位置和地址
            mCurrentLocation = new LatLng(latitude,longitude);
            mCurrentAddr = bdLocation.getAddrStr();

            Log.i("TAG","定位的位置："+ mCurrentAddr +"经纬度："+latitude+","+longitude);

            // 地图上设置定位数据
            MyLocationData locationData = new MyLocationData.Builder()
                    // 设置定位的经纬度
                    .latitude(latitude)
                    .longitude(longitude)
                    .accuracy(100f)// 定位精度的大小
                    .build();

            mBaiduMap.setMyLocationData(locationData);

            // 第一次进入将地图自动移动到定位的位置
            if (isFirst){
                // 自动移动到定位处
                moveToLocation();
                isFirst = false;
            }
        }
    };

    @OnClick(R.id.tv_satellite)
    public void switchMapType(){

        // 先拿到当前的地图的类型
        int mapType = mBaiduMap.getMapType();
        // 切换类型
        mapType = (mapType==BaiduMap.MAP_TYPE_NORMAL)?BaiduMap.MAP_TYPE_SATELLITE:BaiduMap.MAP_TYPE_NORMAL;
        // 文字的变化
        String msg = (mapType==BaiduMap.MAP_TYPE_NORMAL)?"卫星":"普通";
        mBaiduMap.setMapType(mapType);
        mTvSatellite.setText(msg);
    }

    @OnClick(R.id.tv_compass)
    public void switchCompass(){
        // 当前地图指南针有没有在显示
        boolean enabled = mBaiduMap.getUiSettings().isCompassEnabled();
        mBaiduMap.getUiSettings().setCompassEnabled(!enabled);
    }

    @OnClick({R.id.iv_scaleUp,R.id.iv_scaleDown})
    public void scaleMap(View view){
        switch(view.getId()){
            case R.id.iv_scaleUp:
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
                break;
            case R.id.iv_scaleDown:
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
                break;
        }
    }

    // 定位按钮：移动到定位的位置
    @OnClick(R.id.tv_located)
    public void moveToLocation(){
        // 更新的是地图的状态
        MapStatus mapStatus = new MapStatus.Builder()
                .target(mCurrentLocation)// 定位的位置
                .rotate(0)
                .overlook(0)
                .zoom(19)
                .build();
        // 更新的状态
        MapStatusUpdate update = MapStatusUpdateFactory.newMapStatus(mapStatus);
        // 利用地图操作类更新地图的状态
        mBaiduMap.animateMapStatus(update);
    }

    // 处理权限的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case LOCATION_REQUEST_CODE:

                // 用户授权成功了
                if (grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    // 定位了
                    mLocationClient.requestLocation();
                }else {
                    // 显示个吐司、提示框
                }
                break;
        }
    }
}
