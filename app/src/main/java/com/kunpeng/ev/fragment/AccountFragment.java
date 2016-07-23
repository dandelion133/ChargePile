package com.kunpeng.ev.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kunpeng.ev.R;
import com.kunpeng.ev.activity.MyInfoActivity;
import com.kunpeng.ev.activity.OrderActivity;

/**
 * Created by QHF on 2016/7/11.
 */
public class AccountFragment extends Fragment {

    private View mView;
    private LinearLayout mAppointment;
    private LinearLayout mOrder;
    private LinearLayout mInfo;
    private LinearLayout mAccount;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
      //  ((HomeActivity)getActivity()).setTabSelect(false);
        mView = View.inflate(getActivity(), R.layout.fragment_account,null);

        initViews();

        init();

        return mView;
    }



    private void initViews() {
        mAppointment = (LinearLayout) mView.findViewById(R.id.my_appointment);
        mOrder = (LinearLayout) mView.findViewById(R.id.my_order);
        mInfo = (LinearLayout) mView.findViewById(R.id.my_info);
        mAccount = (LinearLayout) mView.findViewById(R.id.my_account);


    }
    private void init() {
        //我的预约
        mAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),AppointmentActivity.class));
            }
        });
        //我的订单
        mOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),OrderActivity.class));
            }
        });
        mInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),MyInfoActivity.class));
            }
        });
        mAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}
