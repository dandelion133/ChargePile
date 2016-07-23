package com.kunpeng.ev.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.kunpeng.ev.R;
import com.kunpeng.ev.activity.BNDemoGuideActivity;
import com.kunpeng.ev.activity.ChargeListActivity;
import com.kunpeng.ev.activity.MipcaActivityCapture;
import com.kunpeng.ev.entity.InfoCharge;
import com.kunpeng.ev.utils.JsonGetChargeInfo;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by QHF on 2016/7/11.
 */
public class MapFragment extends Fragment {
    public static final int HIDE = 0;
    private static final String TAG = "MapFragment";
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

    //得到充电桩的数据信息  以list返回
    private JsonGetChargeInfo jsongetchargerinfo=new JsonGetChargeInfo();
    private ProgressDialog progressDialog = null;
    //IOException
    //建立一个总的info信息
    public static List<InfoCharge> ChargerInfo= new ArrayList<InfoCharge>();
    //根据不同的状态给info分类 第一个是空闲状态 第二个是繁忙状态
    public static List<InfoCharge> info1= new ArrayList<InfoCharge>();
    public static List<InfoCharge> info2= new ArrayList<InfoCharge>();
    public static List<InfoCharge> info3= new ArrayList<InfoCharge>();
    public static List<InfoCharge> info4= new ArrayList<InfoCharge>();
    public static List<InfoCharge> info5= new ArrayList<InfoCharge>();
    public static List<InfoCharge> info6= new ArrayList<InfoCharge>();
    public static List<InfoCharge> info7= new ArrayList<InfoCharge>();
    public static List<InfoCharge> info8= new ArrayList<InfoCharge>();
    public static List<InfoCharge> info9= new ArrayList<InfoCharge>();
    public static List<InfoCharge> info10= new ArrayList<InfoCharge>();
    public static List<InfoCharge> info11= new ArrayList<InfoCharge>();
    public static List<InfoCharge> info12= new ArrayList<InfoCharge>();
    public static List<InfoCharge> info= new ArrayList<InfoCharge>();
    //百度地图覆盖物
    private BitmapDescriptor mMarker;
    private BitmapDescriptor mMarker1;
    private BitmapDescriptor mMarker2;
    private BitmapDescriptor mMarker3;
    private BitmapDescriptor mMarker4;
    private BitmapDescriptor mMarker5;
    private BitmapDescriptor mMarker6;
    private BitmapDescriptor mMarker7;
    private BitmapDescriptor mMarker8;
    private BitmapDescriptor mMarker9;
    private BitmapDescriptor mMarker10;
    private BitmapDescriptor mMarker11;
    private BitmapDescriptor mMarker12;
    private RelativeLayout mMarkerLy;
    private Handler markerhandler;
    //导航和预约功能
    private static final String APP_FOLDER_NAME = "ev";
    private String mSDCardPath = null;
    private Button navbutton;
    private Button chargelistbutton;
    private View mView;
    public static List<Activity> activityList = new LinkedList<Activity>();
    public static final String ROUTE_PLAN_NODE = "routePlanNode";
    public static final String SHOW_CUSTOM_ITEM = "showCustomItem";
    public static final String RESET_END_NODE = "resetEndNode";
    public static final String VOID_MODE = "voidMode";

    private ImageView erweimaimag;
    //定位
    //BitmapDescriptor mCurrentMarker;
    private double mLatitude;
    private double mLongtitude;
    private double CabinetLatitude;
    private double CabinetLongtitude;
    private String Cabinetadress;
    //搜索相关
    private PoiSearch mPoiSearch = null;
    private TextView searchimag;
    private EditText city;
    /**
     * 搜索关键字输入窗口
     */
    private ArrayAdapter<String> sugAdapter = null;
    private int load_Index = 0;
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
      //  ((HomeActivity)getActivity()).setTabSelect(true);
        mView = View.inflate(getActivity(), R.layout.fragment_map,null);
        ImageView location = (ImageView) mView.findViewById(R.id.location);
        mMapView = (MapView) mView.findViewById(R.id.bmapView);
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

        //初始化导航
        activityList.add(getActivity());
        if (initDirs()) {
            initNavi();
        }

        initMarker();

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

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //通过Bundle得到地图的信息info
                Bundle extraInfo = marker.getExtraInfo();
                final InfoCharge info = (InfoCharge) extraInfo.getSerializable("info");
                //将图标移动到地图中心  方便客户查看充电桩状态
                LatLng mkLatLng = new LatLng(info.getLatitude(), info.getLongitude());
                MapStatusUpdate ctmsu = MapStatusUpdateFactory.newLatLng(mkLatLng);
                mBaiduMap.animateMapStatus(ctmsu);
                //mmarkerLy的弹出窗口设置
                TextView title = (TextView) mMarkerLy
                        .findViewById(R.id.title);
                TextView address = (TextView) mMarkerLy
                        .findViewById(R.id.textView_address);
                TextView status = (TextView) mMarkerLy
                        .findViewById(R.id.textView_status);

                title.setText("充电站编号:" + info.getDeviceid());
                //section.setText("区域位置:"+info.getSectionid());
                address.setText("充电站地址:" + info.getAddress());
                status.setText("空闲桩数:" + info.getIdlenumber());
                mMarkerLy.setVisibility(View.VISIBLE);
                System.out.println(View.VISIBLE + "mark status");
                //导航按钮功能
                navbutton = (Button) mView.findViewById(R.id.button_navigation);
                navbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //导航功能的实现
                        //我的位置 mLatitude、mLongtitude
                        //目标充电桩的位置info.getLatitude()、info.getLongitude()
                        CabinetLatitude = info.getLatitude();
                        CabinetLongtitude = info.getLongitude();
                        //System.out.println("........................>"+CabinetLatitude+".........."+mLatitude);
                        Cabinetadress = info.getAddress();
                        // 导航
                        routeplanToNavi(BNRoutePlanNode.CoordinateType.BD09LL);

                    }
                });

                //预约按钮功能
                chargelistbutton = (Button) mView.findViewById(R.id.button_order);
                chargelistbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //预约界面
                        Intent intent1 = new Intent();
                        intent1.putExtra("deviceid", info.getDeviceid());
                        intent1.putExtra("address", info.getAddress());
                        intent1.setClass(MapFragment.this.getActivity(), ChargeListActivity.class);
                        startActivity(intent1);
                    }
                });

                return true;
            }
        });
        //点击地图时,图标对应的显示框消失
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener()
        {

            @Override
            public boolean onMapPoiClick(MapPoi arg0)
            {
                return false;
            }

            @Override
            public void onMapClick(LatLng arg0)
            {
                mMarkerLy.setVisibility(View.GONE);

                System.out.println(View.GONE+"mark status");
            }
        });

        //调用二维码
        erweimaimag = (ImageView)mView.findViewById(R.id.erweima);
        erweimaimag.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent (MapFragment.this.getActivity(),MipcaActivityCapture.class);
                startActivity(intent);
            }
        });

        //搜索相关
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult result) {
                if (result == null
                        || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                    Toast.makeText(MapFragment.this.getActivity(), "未找到结果", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                    mBaiduMap.clear();
                    PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
                    mBaiduMap.setOnMarkerClickListener(overlay);
                    overlay.setData(result);
                    overlay.addToMap();
                    overlay.zoomToSpan();
                    mBaiduMap.clear();
                    initMarker();
                    return;
                }
                if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

                    // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
                    String strInfo = "在";
                    for (CityInfo cityInfo : result.getSuggestCityList()) {
                        strInfo += cityInfo.city;
                        strInfo += ",";
                    }
                    strInfo += "找到结果";
                    Toast.makeText(MapFragment.this.getActivity(), strInfo, Toast.LENGTH_LONG)
                            .show();
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult result) {
                if (result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(MapFragment.this.getActivity(), "抱歉，未找到结果", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(MapFragment.this.getActivity(), result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        });
        city = (EditText) mView.findViewById(R.id.searchcity);
        //city.clearFocus();
        searchimag = (TextView)mView.findViewById(R.id.searchimg);
        searchimag.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                mPoiSearch.searchInCity((new PoiCitySearchOption())
                        .city(city.getText().toString())
                        .keyword("餐厅")
                        .pageNum(load_Index));
            }
        });

        /*Message message = Message.obtain();
        message.what = HIDE;
        mHandler.sendMessage(message);*/

        return mView;
    }
    private class MyPoiOverlay extends PoiOverlay {

        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }
        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);
            // if (poi.hasCaterDetails) {
            mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                    .poiUid(poi.uid));
            // }
            return true;
        }
    }
    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }
    //导航的相关函数
    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    String authinfo = null;
    private void initNavi() {
        // BaiduNaviManager.getInstance().setNativeLibraryPath(mSDCardPath +
        // "/BaiduNaviSDK_SO");


      //  BNOuterTTSPlayerCallback ttsCallback = null;
     //   BaiduNaviManager.getInstance().init();
        BaiduNaviManager.getInstance().init(getActivity(), mSDCardPath, APP_FOLDER_NAME, new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                if (0 == status) {
                    authinfo = "key校验成功!";
                } else {
                    authinfo = "key校验失败, " + msg;
                }
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(MapFragment.this.getActivity(), authinfo, Toast.LENGTH_LONG).show();
                    }
                });
            }

            public void initSuccess() {
                //Toast.makeText(MapActivity.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                //progressDialog.dismiss();
            }

            public void initStart() {
                //Toast.makeText(MapActivity.this, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
            }

            public void initFailed() {
                Toast.makeText(getActivity(), "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
            }

        },  null/* null mTTSCallback */);
    }

    private void routeplanToNavi(BNRoutePlanNode.CoordinateType coType) {
        BNRoutePlanNode sNode = null;
        BNRoutePlanNode eNode = null;
        switch (coType) {
            case WGS84: {
                sNode = new BNRoutePlanNode(mLongtitude, mLatitude, "我的位置", null, coType);
                eNode = new BNRoutePlanNode(CabinetLongtitude, CabinetLatitude, Cabinetadress, null, coType);
                break;
            }
            case BD09LL: {
                sNode = new BNRoutePlanNode(mLongtitude, mLatitude, "我的位置", null, coType);
                eNode = new BNRoutePlanNode(CabinetLongtitude, CabinetLatitude, Cabinetadress, null, coType);
                break;
            }
            default:
                ;
        }
        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<>();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManager.getInstance().launchNavigator(getActivity(), list, 1, true, new DemoRoutePlanListener(sNode));
        }
    }
    public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode mBNRoutePlanNode = null;

        public DemoRoutePlanListener(BNRoutePlanNode node) {
            mBNRoutePlanNode = node;
        }

        @Override
        public void onJumpToNavigator() {
			/*
			 * 设置途径点以及resetEndNode会回调该接口
			 */

           /* for (Activity ac : activityList) {

                if (ac.getClass().getName().endsWith("BNDemoGuideActivity")) {

                    return;
                }
            }*/
            Intent intent = new Intent(getActivity(), BNDemoGuideActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ROUTE_PLAN_NODE,  mBNRoutePlanNode);
            intent.putExtras(bundle);
            startActivity(intent);

        }


        @Override
        public void onRoutePlanFailed() {
            // TODO Auto-generated method stub
            Toast.makeText(getActivity(), "算路失败", Toast.LENGTH_SHORT).show();
        }
    }
    private void initMarker() {
        new Thread(new Runnable()
        {
            public void run()
            {
//                Message message= markerhandler.obtainMessage();
                try
                {
                    ChargerInfo=jsongetchargerinfo.getchargeinfo();
                   /* message.what=2;
                    markerhandler.sendMessage(message);*/
                } catch (SocketTimeoutException e) {
                    // TODO Auto-generated catch block IOException SocketTimeoutException
                    e.printStackTrace();
                   /* message.what=1;
                    markerhandler.sendMessage(message);*/
                }
                catch (IOException e) {
                    // TODO Auto-generated catch block IOException SocketTimeoutException
                    e.printStackTrace();
                   /* message.what=1;
                    markerhandler.sendMessage(message);*/
                }
                catch (Exception e) {
                    // TODO Auto-generated catch block IOException
                   /* message.what=1;
                    markerhandler.sendMessage(message);*/
                    e.printStackTrace();
                }
                info1.clear();
                info2.clear();
                info3.clear();
                info4.clear();
                info5.clear();
                info6.clear();
                info7.clear();
                info8.clear();
                info9.clear();
                info10.clear();
                info11.clear();
                info12.clear();
                info.clear();
                for(int i=0;i<ChargerInfo.size();i++)
                {
                    Log.e("mapTest","yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy"+i);
                    String deviceid = ChargerInfo.get(i).getDeviceid();
                    double longitude = ChargerInfo.get(i).getLongitude();
                    double latitude = ChargerInfo.get(i).getLatitude();
                    String address=ChargerInfo.get(i).getAddress();
                    int idlenumber=ChargerInfo.get(i).getIdlenumber();
                    //根据柜子的空闲数量生成数组
                    if(idlenumber==1)
                    {
                        info1.add(new InfoCharge(deviceid, latitude, longitude,address,idlenumber));
                    }
                    else if(idlenumber==2)
                    {
                        info2.add(new InfoCharge(deviceid, latitude, longitude, address,idlenumber));
                    }
                    else if(idlenumber==3)
                    {
                        info3.add(new InfoCharge(deviceid, latitude, longitude, address,idlenumber));
                    }
                    else if(idlenumber==4)
                    {
                        info4.add(new InfoCharge(deviceid, latitude, longitude, address,idlenumber));
                    }
                    else if(idlenumber==5)
                    {
                        info5.add(new InfoCharge(deviceid, latitude, longitude, address,idlenumber));
                    }
                    else if(idlenumber==6)
                    {
                        info6.add(new InfoCharge(deviceid, latitude, longitude, address,idlenumber));
                    }
                    else if(idlenumber==7)
                    {
                        info7.add(new InfoCharge(deviceid, latitude, longitude, address,idlenumber));
                    }
                    else if(idlenumber==8)
                    {
                        info8.add(new InfoCharge(deviceid, latitude, longitude, address,idlenumber));
                    }
                    else if(idlenumber==9)
                    {
                        info9.add(new InfoCharge(deviceid, latitude, longitude, address,idlenumber));
                    }
                    else if(idlenumber==10)
                    {
                        info10.add(new InfoCharge(deviceid, latitude, longitude, address,idlenumber));
                    }
                    else if(idlenumber==11)
                    {
                        info11.add(new InfoCharge(deviceid, latitude, longitude, address,idlenumber));
                    }
                    else if(idlenumber==12)
                    {
                        info12.add(new InfoCharge(deviceid, latitude, longitude, address,idlenumber));
                    }
                    else
                    {
                        info.add(new InfoCharge(deviceid, latitude, longitude,address,idlenumber));
                    }
                }
                addOverlays(info1,info2,info3,info4,info5,info6,info7,info8,info9,info10,info11,info12,info);
                Log.e("TAG", "初始化覆盖物");
            }
        }).start();
    }
    private void addOverlays(List<InfoCharge> info1,List<InfoCharge> info2,List<InfoCharge> info3,List<InfoCharge> info4,List<InfoCharge> info5,List<InfoCharge> info6,List<InfoCharge> info7,List<InfoCharge> info8,List<InfoCharge> info9,List<InfoCharge> info10,List<InfoCharge> info11,List<InfoCharge> info12,List<InfoCharge> info)
    {
        //自定义显示图标
        mMarker1 = BitmapDescriptorFactory.fromResource(R.drawable.evfree1);
        mMarker2 = BitmapDescriptorFactory.fromResource(R.drawable.evfree2);
        mMarker3 = BitmapDescriptorFactory.fromResource(R.drawable.evfree3);
        mMarker4 = BitmapDescriptorFactory.fromResource(R.drawable.evfree4);
        mMarker5 = BitmapDescriptorFactory.fromResource(R.drawable.evfree5);
        mMarker6 = BitmapDescriptorFactory.fromResource(R.drawable.evfree6);
        mMarker7 = BitmapDescriptorFactory.fromResource(R.drawable.evfree7);
        mMarker8 = BitmapDescriptorFactory.fromResource(R.drawable.evfree8);
        mMarker9= BitmapDescriptorFactory.fromResource(R.drawable.evfree9);
        mMarker10 = BitmapDescriptorFactory.fromResource(R.drawable.evfree10);
        mMarker11 = BitmapDescriptorFactory.fromResource(R.drawable.evfree11);
        mMarker12 = BitmapDescriptorFactory.fromResource(R.drawable.evfree12);
        mMarker = BitmapDescriptorFactory.fromResource(R.drawable.evbusy);
        mMarkerLy = (RelativeLayout) mView.findViewById(R.id.marklayout);

        //清空百度地图的图层
        mBaiduMap.clear();
        LatLng latLng = null;
        Marker marker = null;
        OverlayOptions options;

        for(int i=0;i<info.size();i++)
        {
            latLng = new LatLng( info.get(i).getLatitude(), info.get(i).getLongitude());
            //定位图标
            options = new MarkerOptions().position(latLng)
                    .icon(mMarker)//指定覆盖物的图标
                    .zIndex(5);
            marker = (Marker) mBaiduMap.addOverlay(options);
            //通过bundle将数据信息加载到对应marker里面
            Bundle arg0 = new Bundle();
            arg0.putSerializable("info", info.get(i));
            marker.setExtraInfo(arg0);
        }

        for (int i=0;i<info1.size();i++)
        {
            // 经纬度
            latLng = new LatLng(info1.get(i).getLatitude(), info1.get(i).getLongitude());
            // 图标
            //定位图标
            options = new MarkerOptions().position(latLng).icon(mMarker1).zIndex(5);  //指定覆盖物的图标
            marker = (Marker) mBaiduMap.addOverlay(options);
            Bundle arg0 = new Bundle();
            arg0.putSerializable("info", info1.get(i));
            marker.setExtraInfo(arg0);
        }

        for (int i=0;i<info2.size();i++)
        {
            latLng = new LatLng(info2.get(i).getLatitude(), info2.get(i).getLongitude());
            Log.e(TAG,"经纬度");
            options = new MarkerOptions().position(latLng)
                    .icon(mMarker2)//指定覆盖物的图标
                    .zIndex(5);
            marker = (Marker) mBaiduMap.addOverlay(options);
            Bundle arg0 = new Bundle();
            arg0.putSerializable("info", info2.get(i));
            marker.setExtraInfo(arg0);
        }
        for (int i=0;i<info3.size();i++)
        {
            latLng = new LatLng(info3.get(i).getLatitude(), info3.get(i).getLongitude());
            Log.e(TAG,"经纬度");
            options = new MarkerOptions().position(latLng)
                    .icon(mMarker3)//指定覆盖物的图标
                    .zIndex(5);
            marker = (Marker) mBaiduMap.addOverlay(options);
            Bundle arg0 = new Bundle();
            arg0.putSerializable("info", info3.get(i));
            marker.setExtraInfo(arg0);
        }
        for (int i=0;i<info4.size();i++)
        {
            // 经纬度
            latLng = new LatLng(info4.get(i).getLatitude(), info4.get(i).getLongitude());
            // 图标
            //定位图标
            options = new MarkerOptions().position(latLng).icon(mMarker4).zIndex(5);  //指定覆盖物的图标
            marker = (Marker) mBaiduMap.addOverlay(options);
            Bundle arg0 = new Bundle();
            arg0.putSerializable("info", info4.get(i));
            marker.setExtraInfo(arg0);
        }

        for (int i=0;i<info5.size();i++)
        {
            latLng = new LatLng(info5.get(i).getLatitude(), info5.get(i).getLongitude());
            Log.e(TAG,"经纬度");
            options = new MarkerOptions().position(latLng)
                    .icon(mMarker5)//指定覆盖物的图标
                    .zIndex(5);
            marker = (Marker) mBaiduMap.addOverlay(options);
            Bundle arg0 = new Bundle();
            arg0.putSerializable("info", info5.get(i));
            marker.setExtraInfo(arg0);
        }
        for (int i=0;i<info6.size();i++)
        {
            latLng = new LatLng(info6.get(i).getLatitude(), info6.get(i).getLongitude());
            Log.e(TAG,"经纬度");
            options = new MarkerOptions().position(latLng)
                    .icon(mMarker6)//指定覆盖物的图标
                    .zIndex(5);
            marker = (Marker) mBaiduMap.addOverlay(options);
            Bundle arg0 = new Bundle();
            arg0.putSerializable("info", info6.get(i));
            marker.setExtraInfo(arg0);
        }
        for (int i=0;i<info7.size();i++)
        {
            // 经纬度
            latLng = new LatLng(info7.get(i).getLatitude(), info7.get(i).getLongitude());
            // 图标
            //定位图标
            options = new MarkerOptions().position(latLng).icon(mMarker7).zIndex(5);  //指定覆盖物的图标
            marker = (Marker) mBaiduMap.addOverlay(options);
            Bundle arg0 = new Bundle();
            arg0.putSerializable("info", info7.get(i));
            marker.setExtraInfo(arg0);
        }

        for (int i=0;i<info8.size();i++)
        {
            latLng = new LatLng(info8.get(i).getLatitude(), info8.get(i).getLongitude());
            Log.e(TAG,"经纬度");
            options = new MarkerOptions().position(latLng)
                    .icon(mMarker8)//指定覆盖物的图标
                    .zIndex(5);
            marker = (Marker) mBaiduMap.addOverlay(options);
            Bundle arg0 = new Bundle();
            arg0.putSerializable("info", info8.get(i));
            marker.setExtraInfo(arg0);
        }
        for (int i=0;i<info9.size();i++)
        {
            latLng = new LatLng(info9.get(i).getLatitude(), info9.get(i).getLongitude());
            Log.e(TAG,"经纬度");
            options = new MarkerOptions().position(latLng)
                    .icon(mMarker9)//指定覆盖物的图标
                    .zIndex(5);
            marker = (Marker) mBaiduMap.addOverlay(options);
            Bundle arg0 = new Bundle();
            arg0.putSerializable("info", info9.get(i));
            marker.setExtraInfo(arg0);
        }
        for (int i=0;i<info10.size();i++)
        {
            // 经纬度
            latLng = new LatLng(info10.get(i).getLatitude(), info10.get(i).getLongitude());
            // 图标
            //定位图标
            options = new MarkerOptions().position(latLng).icon(mMarker10).zIndex(5);  //指定覆盖物的图标
            marker = (Marker) mBaiduMap.addOverlay(options);
            Bundle arg0 = new Bundle();
            arg0.putSerializable("info", info10.get(i));
            marker.setExtraInfo(arg0);
        }

        for (int i=0;i<info11.size();i++)
        {
            latLng = new LatLng(info11.get(i).getLatitude(), info11.get(i).getLongitude());
            Log.e(TAG,"经纬度");
            options = new MarkerOptions().position(latLng)
                    .icon(mMarker11)//指定覆盖物的图标
                    .zIndex(5);
            marker = (Marker) mBaiduMap.addOverlay(options);
            Bundle arg0 = new Bundle();
            arg0.putSerializable("info", info11.get(i));
            marker.setExtraInfo(arg0);
        }
        for (int i=0;i<info12.size();i++)
        {
            Log.e("infoTest","ffffffffffffffffffffffffffffffffffff"+i);
            latLng = new LatLng(info12.get(i).getLatitude(), info12.get(i).getLongitude());
            Log.e(TAG,"经纬度");
            options = new MarkerOptions().position(latLng)
                    .icon(mMarker12)//指定覆盖物的图标
                    .zIndex(5);
            marker = (Marker) mBaiduMap.addOverlay(options);
            Bundle arg0 = new Bundle();
            arg0.putSerializable("info", info12.get(i));
            marker.setExtraInfo(arg0);
        }

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
            mLatitude = location.getLatitude();
            mLongtitude = location.getLongitude();
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


