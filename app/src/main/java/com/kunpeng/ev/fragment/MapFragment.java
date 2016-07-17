package com.kunpeng.ev.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.kunpeng.ev.R;
import com.kunpeng.ev.activity.HomeActivity;

/**
 * Created by QHF on 2016/7/11.
 */
public class MapFragment extends Fragment {
    public static final int HIDE = 0;
    private MapView mMapView;
    public LocationClient mLocClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private BaiduMap mBaiduMap;
    boolean isFirstLoc = true; // 是否首次定位
    private FrameLayout mMapHide;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {


            super.handleMessage(msg);
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        ((HomeActivity)getActivity()).setTabSelect(true);
        final View view = View.inflate(getActivity(), R.layout.fragment_map,null);
        ImageView location = (ImageView) view.findViewById(R.id.location);
        mMapView = (MapView) view.findViewById(R.id.bmapView);
        isFirstLoc = true;
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(getActivity());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();


        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFirstLoc = true;

                // mMapView = (MapView) findViewById(R.id.bmapView);
                mBaiduMap = mMapView.getMap();
                // 开启定位图层
                mBaiduMap.setMyLocationEnabled(true);
                // 定位初始化
                mLocClient = new LocationClient(getActivity());
                mLocClient.registerLocationListener(myListener);
                LocationClientOption option = new LocationClientOption();
                option.setOpenGps(true); // 打开gps
                option.setCoorType("bd09ll"); // 设置坐标类型
                option.setScanSpan(1000);
                mLocClient.setLocOption(option);
                mLocClient.start();
            }
        });
        /*Message message = Message.obtain();
        message.what = HIDE;
        mHandler.sendMessage(message);*/

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // mMapHide.setVisibility(View.VISIBLE);
      //  mMapView.setVisibility(View.GONE);

        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();

        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
      //  mMapView.setVisibility(View.VISIBLE);
     //   mMapHide.setVisibility(View.INVISIBLE);
    }
    @Override
    public void onPause() {
     //   mMapHide.setVisibility(View.VISIBLE);
     //   mMapView.setVisibility(View.GONE);
      //  mMapHide.setVisibility(View.VISIBLE);
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }










    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {









            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                Log.e("定位",location.getLongitude() + "");
                Log.e("定位",location.getLatitude() + "");

                Log.e("定位",location.getLocType() + "");
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }


        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }
}


