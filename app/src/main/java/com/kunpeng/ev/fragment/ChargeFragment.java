package com.kunpeng.ev.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.kunpeng.ev.R;
import com.kunpeng.ev.activity.MipcaActivityCapture;
import com.kunpeng.ev.manage.ThreadManager;
import com.kunpeng.ev.ui.SelectPicPopupWindow;
import com.kunpeng.ev.utils.HttpUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by QHF on 2016/7/11.
 */
public class ChargeFragment extends Fragment {

    private static final String TAG = "ChargeFragment";
    private static final int BIND_SUCCESS = 0;//绑定成功
    private static final int NETWORK_ERROR = 1;//绑定成功
    private String[] items = new String[] { "电桩显示屏上设置", "手机上设置" };
    private ImageView mScanCode;
    private Button mInputPileNum;
    private EditText mEtNumPile;
    private Button mOk;
    private SharedPreferences spUser;
    private ProgressDialog progressDialog = null;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case BIND_SUCCESS:
                    Toast.makeText(ChargeFragment.this.getActivity(),"桩体绑定成功",Toast.LENGTH_SHORT).show();

                    showDialog();
                    break;
                case NETWORK_ERROR:
                    Toast.makeText(ChargeFragment.this.getActivity(),"网络错误",Toast.LENGTH_SHORT).show();
                    break;

            }
            super.handleMessage(msg);
        }
    };
    private String mUserAccount;
    private String mDeviceId;
    private ImageView mIvMode;
    //自定义的弹出框类
    private SelectPicPopupWindow menuWindow;

    private AlertDialog  inputdialog;
    private boolean isPhoneMode = false;
    private View mView;
    private SharedPreferences mSpData;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
     //   ((HomeActivity)getActivity()).setTabSelect(false);
        mView = View.inflate(getActivity(), R.layout.fragment_charge,null);
        initView();
        init();
        Log.e(TAG,"CANCEL HIDE");
        return mView;
    }





    private void initView() {
        spUser = getActivity().getSharedPreferences("User", Activity.MODE_PRIVATE);

        mSpData = getActivity().getSharedPreferences("data", Context.MODE_PRIVATE);

        mScanCode = (ImageView) mView.findViewById(R.id.scan_code);
        mInputPileNum = (Button) mView.findViewById(R.id.input_pile_num);
        mEtNumPile = (EditText) mView.findViewById(R.id.et_num_pile);
        mOk = (Button) mView.findViewById(R.id.ok);
        mIvMode = (ImageView) mView.findViewById(R.id.iv_mode);
    }

    private void init() {
        mScanCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChargeFragment.this.getActivity(), MipcaActivityCapture.class);
                startActivity(intent);
            }

        });

        mInputPileNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputPileNum.setVisibility(View.GONE);
                mEtNumPile.setVisibility(View.VISIBLE);
                mOk.setVisibility(View.VISIBLE);
            }
        });
        mUserAccount = spUser.getString("userAccount", "");
        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDeviceId = mEtNumPile.getText().toString().trim();
                //System.out.println("111111111111111yy"+deviceId.length()+deviceId.charAt(0));
                if(mDeviceId.length()==7) {
                    SharedPreferences.Editor edit = mSpData.edit();
                    edit.putString("deviceid", mDeviceId);
                    edit.apply();
                    Log.e(TAG,"111111111111111yy"+ mDeviceId.length()+ mDeviceId.charAt(0));
                    ThreadManager.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = scanQRCode(mUserAccount, mDeviceId);
                                if (jsonObject.getString("info").equals("delockSuccess")) {
                                    // identifycode=jsonObject.getString("identify_Code");
                                    Message message = Message.obtain();
                                    message.what = BIND_SUCCESS;
                                    mHandler.sendMessage(message);
                                } else {

                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    //NumberThread nt=new NumberThread();
                    // Thread thread=new Thread(nt);
                    //thread.start();
                } else if(mDeviceId.equals("")) {
                    Toast.makeText(ChargeFragment.this.getActivity(),"请输入电桩编号！！！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChargeFragment.this.getActivity(),"电桩编号错误，请重新输入", Toast.LENGTH_SHORT).show();
                }
            }
        });


        mIvMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPhoneMode) {
                    //实例化SelectPicPopupWindow
                    menuWindow = new SelectPicPopupWindow(ChargeFragment.this.getActivity(), itemsOnClick);
                    //显示窗口
                    menuWindow.showAtLocation(mView.findViewById(R.id.main), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位

                } else {
                    Toast.makeText(ChargeFragment.this.getActivity(), "当前不能选择模式", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    //为弹出窗口实现监听类
    private View.OnClickListener  itemsOnClick = new View.OnClickListener(){

        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                case R.id.popofull:
                    /*SharedPreferences settingdata = getActivity().getSharedPreferences("data",Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor=settingdata.edit();
                    editor.putString("three", "0");
                    editor.putString("two", "自动充满");
                    editor.putString("one", "phone");
                    editor.commit();*/
                    /*SharedPreferences settingimageflag=getSharedPreferences("flag",Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 =settingimageflag.edit();
                    editor1.putInt("flag", 0);
                    editor1.commit();*/
                   /* String setupmethod = settingdata.getString("one", "");
                    String chargemethod = settingdata.getString("two", "");
                    String value = settingdata.getString("three", "");*/
                    isPhoneMode = false;
                    mIvMode.setImageDrawable(getResources().getDrawable(R.drawable.mode));
                    //timehandler.removeCallbacks(runnable);
                    progressDialog = ProgressDialog.show(getActivity(), "请稍等...", "数据传输中...", true);
                    /*SettingInfoThread1 st = new SettingInfoThread1();
                    st.start();*/

                    ThreadManager.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = modeSetting(mUserAccount, mDeviceId, "phone", "自动充满", "0");
                                if (jsonObject.equals("setSuccess")) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ChargeFragment.this.getActivity(), "设置成功", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    });

                                } else {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.dismiss();
                                        }
                                    });

                                }
                            } catch (Exception e) {
                                Message message = Message.obtain();
                                message.what = NETWORK_ERROR;
                                mHandler.sendMessage(message);
                                e.printStackTrace();
                            }
                        }
                    });


                    break;
                case R.id.popotime:
                    final EditText input1 = new EditText(getActivity());   //定义一个EditText
                    input1.setInputType(InputType.TYPE_CLASS_NUMBER);
                    inputdialog = new AlertDialog.Builder(getActivity())
                            .setTitle("请输入充电时间（分钟）")
                            .setView(input1)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //Toast.makeText(ChargesettingActivity.this,input.getText().toString(), Toast.LENGTH_SHORT).show();
                                    if(input1.getText().toString().equals("")){
                                        Toast.makeText(getActivity(),"请输入时间", Toast.LENGTH_SHORT).show();
                                    } else {
                                       /* SharedPreferences settingdata=getActivity().getSharedPreferences("data",Activity.MODE_PRIVATE);
                                        SharedPreferences.Editor editor=settingdata.edit();
                                        editor.putString("three", input1.getText().toString());
                                        editor.putString("two", "定时间充电");
                                        editor.putString("one", "phone");
                                        editor.commit();*/
                                        /*SharedPreferences settingimageflag=getActivity().getSharedPreferences("flag",Activity.MODE_PRIVATE);
                                        SharedPreferences.Editor editor2 =settingimageflag.edit();
                                        editor2.putInt("flag", 0);
                                        editor2.commit();*/
                                        isPhoneMode = false;
                                        mIvMode.setImageDrawable(getResources().getDrawable(R.drawable.mode));
                                        //  timehandler.removeCallbacks(runnable);
                                        progressDialog = ProgressDialog.show(getActivity(), "请稍等...", "数据传输中...", true);
                                       /* SettingInfoThread1 st = new SettingInfoThread1();
                                        st.start();*/
                                        ThreadManager.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    JSONObject jsonObject = modeSetting(mUserAccount, mDeviceId, "phone", "定时间充电", input1.getText().toString());
                                                    if (jsonObject.equals("setSuccess")) {
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(ChargeFragment.this.getActivity(), "设置成功", Toast.LENGTH_SHORT).show();
                                                                progressDialog.dismiss();
                                                            }
                                                        });

                                                    } else {
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                progressDialog.dismiss();
                                                            }
                                                        });

                                                    }
                                                } catch (Exception e) {
                                                    Message message = Message.obtain();
                                                    message.what = NETWORK_ERROR;
                                                    mHandler.sendMessage(message);
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    }
                                }
                            }) .setNegativeButton("取消",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //Toast.makeText(ChargesettingActivity.this,input.getText().toString(), Toast.LENGTH_SHORT).show();
                                    inputdialog.dismiss();
                                }
                            }).show();
                    break;
                case R.id.popokwh:
                    final EditText input2 = new EditText(getActivity());    //定义一个EditText
                    input2.setInputType(InputType.TYPE_CLASS_NUMBER);
                    inputdialog = new AlertDialog.Builder(getActivity())
                            .setTitle("请输入充电电量（Kw/h）")
                            .setView(input2)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //Toast.makeText(ChargesettingActivity.this,input.getText().toString(), Toast.LENGTH_SHORT).show();
                                    if(input2.getText().toString().equals("")){
                                        Toast.makeText(getActivity(),"请输入充电电量", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        /*SharedPreferences settingdata=getSharedPreferences("data",Activity.MODE_PRIVATE);
                                        Editor editor=settingdata.edit();
                                        editor.putString("three", input2.getText().toString());
                                        editor.putString("two", "定电量充电");
                                        editor.putString("one", "phone");
                                        editor.commit();
                                        SharedPreferences settingimageflag=getSharedPreferences("flag",Activity.MODE_PRIVATE);
                                        Editor editor3 =settingimageflag.edit();
                                        editor3.putInt("flag", 0);
                                        editor3.commit();
                                        setimage.setImageDrawable(getResources().getDrawable(R.drawable.mode));
                                        //timehandler.removeCallbacks(runnable);
                                        progressDialog = ProgressDialog.show(MyActivity.this, "请稍等...", "数据传输中...", true);
                                        SettingInfoThread1 st = new SettingInfoThread1();
                                        st.start();*/
                                        isPhoneMode = false;

                                        mIvMode.setImageDrawable(getResources().getDrawable(R.drawable.mode));
                                        //  timehandler.removeCallbacks(runnable);
                                        progressDialog = ProgressDialog.show(getActivity(), "请稍等...", "数据传输中...", true);
                                       /* SettingInfoThread1 st = new SettingInfoThread1();
                                        st.start();*/
                                        ThreadManager.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    JSONObject jsonObject = modeSetting(mUserAccount, mDeviceId, "phone", "定电量充电", input2.getText().toString());
                                                    if (jsonObject.equals("setSuccess")) {
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(ChargeFragment.this.getActivity(), "设置成功", Toast.LENGTH_SHORT).show();
                                                                progressDialog.dismiss();
                                                            }
                                                        });

                                                    } else {
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                progressDialog.dismiss();
                                                            }
                                                        });

                                                    }
                                                } catch (Exception e) {
                                                    Message message = Message.obtain();
                                                    message.what = NETWORK_ERROR;
                                                    mHandler.sendMessage(message);
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .setNegativeButton("取消",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //Toast.makeText(ChargesettingActivity.this,input.getText().toString(), Toast.LENGTH_SHORT).show();
                                    inputdialog.dismiss();
                                }
                            }).show();
                    break;
                case R.id.popomoney:

                    final EditText input = new EditText(getActivity());    //定义一个EditText
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    inputdialog = new AlertDialog.Builder(getActivity())
                            .setTitle("请输入充电金额（元）")
                            .setView(input)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //Toast.makeText(ChargesettingActivity.this,input.getText().toString(), Toast.LENGTH_SHORT).show();
                                    if(input.getText().toString().equals("")){
                                        Toast.makeText(getActivity(),"请输入充电金额", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        /*SharedPreferences settingdata=getSharedPreferences("data",Activity.MODE_PRIVATE);
                                        Editor editor=settingdata.edit();
                                        editor.putString("three", input.getText().toString());
                                        editor.putString("two", "定金额充电");
                                        editor.putString("one", "phone");
                                        editor.commit();
                                        SharedPreferences settingimageflag=getSharedPreferences("flag",Activity.MODE_PRIVATE);
                                        Editor editor4 =settingimageflag.edit();
                                        editor4.putInt("flag", 0);
                                        editor4.commit();
                                        setimage.setImageDrawable(getResources().getDrawable(R.drawable.mode));
                                        // timehandler.removeCallbacks(runnable);
                                        progressDialog = ProgressDialog.show(MyActivity.this, "请稍等...", "数据传输中...", true);
                                        SettingInfoThread1 st = new SettingInfoThread1();
                                        st.start();*/
                                        isPhoneMode = false;

                                        mIvMode.setImageDrawable(getResources().getDrawable(R.drawable.mode));
                                        //  timehandler.removeCallbacks(runnable);
                                        progressDialog = ProgressDialog.show(getActivity(), "请稍等...", "数据传输中...", true);
                                       /* SettingInfoThread1 st = new SettingInfoThread1();
                                        st.start();*/
                                        ThreadManager.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    JSONObject jsonObject = modeSetting(mUserAccount, mDeviceId, "phone", "定金额充电", input.getText().toString());
                                                    if (jsonObject.equals("setSuccess")) {
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(ChargeFragment.this.getActivity(), "设置成功", Toast.LENGTH_SHORT).show();
                                                                progressDialog.dismiss();
                                                            }
                                                        });

                                                    } else {
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                progressDialog.dismiss();
                                                            }
                                                        });

                                                    }
                                                } catch (Exception e) {
                                                    Message message = Message.obtain();
                                                    message.what = NETWORK_ERROR;
                                                    mHandler.sendMessage(message);
                                                    e.printStackTrace();
                                                }
                                            }
                                        });





                                    }
                                }
                            })
                            .setNegativeButton("取消",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //Toast.makeText(ChargesettingActivity.this,input.getText().toString(), Toast.LENGTH_SHORT).show();
                                    inputdialog.dismiss();
                                }
                            }).show();
                    break;
                default:
                    break;
            }


        }

    };

    //定义发送请求方法
    private JSONObject scanQRCode(String username, String pileId)  throws Exception  {
        //使用Map封装请求参数 采用post方法
        Map<String,String> map=new HashMap<>();
        map.put("phoneNumber", username);
        map.put("pileId",pileId);
        String url= HttpUtil.OLD_URL +"ScanQRCode";

        return new JSONObject(HttpUtil.postRequest(url, map));
    }
    //定义发送请求方法
    private JSONObject modeSetting(String username, String pileId,String setupMethod,String chargeMethod,String setValue)  throws Exception  {
        //使用Map封装请求参数 采用post方法
        Map<String,String> map=new HashMap<>();
        map.put("phoneNumber", username);
        map.put("pileId",pileId);
        map.put("SetupMethod",setupMethod);
        map.put("ChargeMethod",chargeMethod);
        map.put("SetValue",setValue);
        String url= HttpUtil.OLD_URL +"modelSelection";
        //"&SetupMethod=andriodScreen&ChargeMethod=0&SetValue=0"
        return new JSONObject(HttpUtil.postRequest(url, map));
    }
    private void showDialog() {

        new AlertDialog.Builder(getActivity())
                .setTitle("充电设置方式选择")
                .setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                progressDialog = ProgressDialog.show(ChargeFragment.this.getActivity(), "请稍等...", "数据传输中...", true);


                                ThreadManager.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            JSONObject jsonObject = modeSetting(mUserAccount, mDeviceId,"andriodScreen","0","0");
                                            if(jsonObject.getString("info").equals("settingForScreen")) {
                                                //
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(ChargeFragment.this.getActivity(),"请到电桩显示屏上进行充电设置", Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();
                                                    }
                                                });
                                            } else {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                      //  Toast.makeText(ChargeFragment.this.getActivity(),"网络错误", Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();
                                                    }
                                                });
                                            }


                                        } catch (Exception e) {

                                            Message message = Message.obtain();
                                            message.what = NETWORK_ERROR;
                                            mHandler.sendMessage(message);
                                            e.printStackTrace();
                                        }

                                    }
                                });

                                /* SettingInfoThread st = new SettingInfoThread();
                                st.start();*/

                                break;
                            case 1:
                                isPhoneMode = true;
                                /*SharedPreferences settingimageflag=getSharedPreferences("flag",Activity.MODE_PRIVATE);
                                Editor editor =settingimageflag.edit();
                                editor.putInt("flag", 1);
                                editor.commit();
                                setimage.setImageDrawable(getResources().getDrawable(R.drawable.modechange));*/
                                break;
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }

}
