package com.kunpeng.ev.ui;


import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapFragment;
import com.baidu.mapapi.map.MapView;

public class MapFragMent1 extends Fragment {
    private static final String a = MapFragment.class.getSimpleName();
    private MapView b;
    private BaiduMapOptions c;

   public MapFragMent1() {

   }


   /* private MapFragMent1(BaiduMapOptions var1) {
        this.c = var1;
    }
*/



    public static MapFragment newInstance() {
        return new MapFragment();
    }

    /*public static MapFragment newInstance(BaiduMapOptions var0) {
        return new MapFragment(var0);
    }*/

    public BaiduMap getBaiduMap() {
        return this.b == null?null:this.b.getMap();
    }

    public MapView getMapView() {
        return this.b;
    }

    public void onAttach(Activity var1) {
        super.onAttach(var1);
    }

    public void onCreate(Bundle var1) {
        super.onCreate(var1);
    }

    public View onCreateView(LayoutInflater var1, ViewGroup var2, Bundle var3) {
        this.b = new MapView(this.getActivity(), this.c);
        return this.b;
    }

    public void onViewCreated(View var1, Bundle var2) {
        super.onViewCreated(var1, var2);
    }

    public void onActivityCreated(Bundle var1) {
        super.onActivityCreated(var1);
    }

    public void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
        this.b.onResume();
    }

    public void onSaveInstanceState(Bundle var1) {
        super.onSaveInstanceState(var1);
    }

    public void onPause() {
        super.onPause();
        this.b.onPause();
    }

    public void onStop() {
        super.onStop();
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.b.onDestroy();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onDetach() {
        super.onDetach();
    }

    public void onConfigurationChanged(Configuration var1) {
        super.onConfigurationChanged(var1);
    }
}