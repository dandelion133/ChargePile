package com.kunpeng.ev.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kunpeng.ev.R;
import com.kunpeng.ev.activity.PayDemoActivity;
import com.kunpeng.ev.manage.ThreadManager;
import com.kunpeng.ev.utils.CheckStringUtil;
import com.kunpeng.ev.utils.HttpUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
/**
 * Created by QHF on 2016/7/11.
 */
public class StateFragment extends Fragment {
    private static final int NETWORK_ERROR = 0;//网络错误
    private static final String TAG = "StateFragment";
    private View mView;
    private TextView mStatus;
    private TextView mTvcdnumber;
    private TextView mTvcdcost;
    private TextView mTvcdtime;
    private TextView mTvcdamount;
    private ImageView mStopimage;
    private TextView mStoptv;
    private SharedPreferences mSpUser;
    private String mUserAccount;
    private AlertDialog mDialog;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case NETWORK_ERROR:
                    Toast.makeText(StateFragment.this.getActivity(),"网络错误",Toast.LENGTH_SHORT).show();
                    break;

            }
            super.handleMessage(msg);
        }
    };
    private SharedPreferences mSpData;
    private String mDeviceid;

    private ProgressDialog progressDialog = null;
    //是否充电标志位
    private boolean isCharge = false;
    private AlertDialog mAlertDialog;
    private int mRemianMoney;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
       // ((HomeActivity)getActivity()).setTabSelect(false);
        mView = View.inflate(getActivity(), R.layout.fragment_state,null);
        initViews();
        init();
        return mView;
    }



    private void initViews() {

        mSpUser = getActivity().getSharedPreferences("User", Activity.MODE_PRIVATE);

        mSpData = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);
        mStatus= (TextView) mView.findViewById(R.id.statusinfo);//状态
        mTvcdnumber = (TextView) mView.findViewById(R.id.tvcdnumber);//充电桩编号
        mTvcdcost = (TextView) mView.findViewById(R.id.tvcdcost);//充电金额
        mTvcdtime = (TextView) mView.findViewById(R.id.tvcdtime);//充电时间
        mTvcdamount = (TextView) mView.findViewById(R.id.tvcdamount);//充电电量
        mStopimage = (ImageView) mView.findViewById(R.id.stopimage);
        mStoptv = (TextView) mView.findViewById(R.id.stoptv);
    }
    private void init() {

        mDeviceid = mSpData.getString("deviceid", "");
        //拉取网络获取余额
        mUserAccount = mSpUser.getString("userAccount", "");
        ThreadManager.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = getMoney(mUserAccount);
                    String balanceStr = jsonObject.getString("balance");
                    if(CheckStringUtil.isNumber(balanceStr)) {
                        mRemianMoney = Integer.parseInt(balanceStr);
                        //将余额缓存本地
                        SharedPreferences.Editor edit = mSpData.edit();
                        edit.putInt("remianMoney", mRemianMoney);
                        edit.apply();

                        if (mRemianMoney < 1000 & mRemianMoney > 0) {
                            //	                      .setIcon(R.drawable.money)
                            mDialog = new AlertDialog.Builder(getActivity())
                                    .setTitle("温馨提示")
                                    //	                      .setIcon(R.drawable.money)
                                    .setMessage("您当前易充币为" + mRemianMoney + ",余额可能不足，请及时充值？")
                                    .setPositiveButton("充值", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            Intent intent = new Intent(StateFragment.this.getActivity(), PayDemoActivity.class);
                                            startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {

                                        }
                                    }).show();
                        } else if (mRemianMoney < 0) {
                            mDialog = new AlertDialog.Builder(StateFragment.this.getActivity())
                                    .setTitle("温馨提示")
                                    //	                      .setIcon(R.drawable.money)
                                    .setMessage("您当前易充币为" + mRemianMoney + ",请及时充值？")
                                    .setPositiveButton("充值", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            Intent intent = new Intent(StateFragment.this.getActivity(), PayDemoActivity.class);
                                            startActivity(intent);
                                        }
                                    }).show();

                        }
                    }


                } catch (Exception e) {
                    Message message = Message.obtain();
                    message.what = NETWORK_ERROR;
                    mHandler.sendMessage(message);
                    e.printStackTrace();
                }
            }
        });


        //实时数据
        ThreadManager.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject realData = getRealData(mDeviceid);

                    if(!realData.getString("info").equals("No realInfo")) {
                        isCharge = false;
                        String  cost=realData.getString("money");
                        String  starttime=realData.getString("startTime");
                        String status=realData.getString("chargeState");
                        String lengthoftime=realData.getString("chargeTime");
                        String chargeamout=realData.getString("Equantity");

                        if(status.equals("setting")) {
                            isCharge = false;
                            mStatus.setText("设置中");
                            mStatus.setTextColor(Color.rgb(0, 245, 255));
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("充电提示")
                                    .setMessage("请接好插头到电桩显示屏上进行充电设置并开始充电")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).show();


                        } else if(status.equals("fault")) {
                            isCharge = false;
                            Toast.makeText(getActivity(),"电桩故障，您依旧可以充电，但无法看到实时数据", Toast.LENGTH_SHORT).show();

                            mTvcdcost.setText("0");
                            mTvcdtime.setText("没有数据");
                            mTvcdamount.setText("没有数据");
                            mTvcdnumber.setText("没有数据");
                        } else if (status.equals("charging")) {


                            if (Integer.valueOf(cost) > mRemianMoney) {
                                isCharge = false;
                                // bat.setBackgroundResource(R.drawable.q0);
                               /* deviceideditor.putString("deviceid", "");
                                deviceideditor.commit();
                                timehandler.removeCallbacks(runnable);*/
                                mStatus.setText("充电结束");
                                mStatus.setTextColor(Color.rgb(0, 0, 0));
                               /* mymoney = 0;
                                Editor editor = preferences.edit();
                                editor.putInt("mymoney", mymoney);
                                editor.commit();
                                SharedPreferences settingimageflag = getSharedPreferences("flag", Activity.MODE_PRIVATE);
                                Editor editor1 = settingimageflag.edit();
                                editor.putInt("flag", 0);
                                editor.commit();*/
                                mAlertDialog = new AlertDialog.Builder(getActivity())
                                        .setTitle("温馨提示")
                                        .setMessage("当前充电费用已大于您的余额，您是一般用户，已停止充电，请及时充值。")
                                        .setPositiveButton("充值", new DialogInterface.OnClickListener() {

                                                    public void onClick(  DialogInterface dialog, int whichButton) {

                                                        Intent intent = new Intent(getActivity(), PayDemoActivity.class);
                                                        startActivity(intent);
                                                    }
                                                })
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                                    public void onClick(  DialogInterface dialog, int whichButton) {
                                                        mAlertDialog.dismiss();
                                                    }
                                                }).show();
                            }


                            isCharge = false;
                            mStatus.setText("充电中...");
                            mStatus.setTextColor(Color.rgb(67, 205, 168));
                            mTvcdcost.setText(cost + "(易充币)");
                            mTvcdtime.setText(lengthoftime);
                            mTvcdamount.setText(chargeamout + "Kw.h");
                            mTvcdnumber.setText(mDeviceid);
                        }

                    } else {
                        Log.e(TAG,"没有实时数据");
                    }




                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //每隔5秒刷新数据






        mStopimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCharge) {
                    /* SharedPreferences settingimageflag = getSharedPreferences(
                             "flag", Activity.MODE_PRIVATE);
                     Editor editor = settingimageflag.edit();
                     editor.putInt("flag", 0);
                     editor.commit();*//* stopThread st = new stopThread();
                      st.start();*/
                    mAlertDialog = new AlertDialog.Builder(getActivity())
                            .setTitle("温馨提示")
                            .setMessage("确定停止充电吗？")
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int whichButton) {
                                           /* SharedPreferences settingimageflag = getSharedPreferences(
                                                    "flag", Activity.MODE_PRIVATE);
                                            Editor editor = settingimageflag.edit();
                                            editor.putInt("flag", 0);
                                            editor.commit();*/
                                            progressDialog = ProgressDialog.show(
                                                    getActivity(), "请稍等...",
                                                    "获取数据中...", true);
                                           /* stopThread st = new stopThread();
                                            st.start();*/

                                            ThreadManager.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        JSONObject jsonObject = stopCharge(mUserAccount, mDeviceid);
                                                        String duration = jsonObject.getString("duration");
                                                        String money  = jsonObject.getString("money");


                                                        mStatus.setText("充电结束");
                                                        mStatus.setTextColor(Color.rgb(0, 0, 0));
                                                      //  String duration = msg.getData().getString("duration");
                                                      //  String money = msg.getData().getString("money");
                                                        mAlertDialog = new AlertDialog.Builder(getActivity())
                                                                .setTitle("扣费提示")
                                                                .setMessage(
                                                                        "您充电时长" + duration + "分钟,系统自动扣除" + money
                                                                                + "易充币。")
                                                                .setPositiveButton("确定",
                                                                        new DialogInterface.OnClickListener() {
                                                                            public void onClick(DialogInterface dialog,
                                                                                                int whichButton) {
                                                                                mStatus.setText("未充电");
                                                                                mTvcdcost.setText("0");
                                                                                mTvcdtime.setText("没有数据");
                                                                                mTvcdamount.setText("没有数据");
                                                                                mTvcdnumber.setText("没有数据");
                                                                                // 清除缓存
                                                                             //   finishmoney = null;
                                                                              //  finishchargeamount = null;
                                                                               // chargeflag = 0;
                                                                                isCharge = false;
                                                                            }
                                                                        }).show();


                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });



                                        }
                                    })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            mAlertDialog.dismiss();
                                        }

                               }).show();
                } else {
                    Toast.makeText(StateFragment.this.getActivity(), "当前没有充电", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    //停止充电
    private JSONObject stopCharge(String phoneNumber,String pileId)  throws Exception  {
        //使用Map封装请求参数 采用post方法
        Map<String,String> map=new HashMap<>();
        map.put("phoneNumber", phoneNumber);
        map.put("pileId", pileId);//stopSign=stopCharging
        map.put("stopSign", "stopCharging");
        String url= HttpUtil.OLD_URL +"stopCharging";

        return new JSONObject(HttpUtil.postRequest(url, map));
    }


    //获得实时数据
    private JSONObject getRealData(String pileId)  throws Exception  {
        //使用Map封装请求参数 采用post方法
        Map<String,String> map=new HashMap<>();
        map.put("pileId", pileId);

        String url= HttpUtil.OLD_URL +"getRealInfo";

        return new JSONObject(HttpUtil.postRequest(url, map));
    }

    //获得余额
    private JSONObject getMoney(String username)  throws Exception  {
        //使用Map封装请求参数 采用post方法
        Map<String,String> map=new HashMap<>();
        map.put("phoneNumber", username);

        String url= HttpUtil.OLD_URL +"getUserAccount";

        return new JSONObject(HttpUtil.postRequest(url, map));
    }

}
