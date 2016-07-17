package com.kunpeng.ev.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kunpeng.ev.R;
import com.kunpeng.ev.activity.HomeActivity;

/**
 * Created by QHF on 2016/7/11.
 */
public class ChargeFragment extends Fragment {

    private static final String TAG = "ChargeFragment";

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        ((HomeActivity)getActivity()).setTabSelect(false);
        View view = View.inflate(getActivity(), R.layout.fragment_charge,null);

        Log.e(TAG,"CANCEL HIDE");
        return view;
    }


}
