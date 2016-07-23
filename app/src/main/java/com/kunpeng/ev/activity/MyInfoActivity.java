package com.kunpeng.ev.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.kunpeng.ev.R;
import com.kunpeng.ev.manage.ThreadManager;
import com.kunpeng.ev.ui.TitleView;
import com.kunpeng.ev.utils.HttpUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by QHF on 2016/7/22.
 */
public class MyInfoActivity extends Activity {

    private TitleView mTitle;
    private SharedPreferences mSpUser;
    private String mUserAccount;
    private TextView mName;
    private TextView mGender;
    private TextView mSignature;
    private TextView mMail;
    private TextView mCarType;
    private TextView mAddress;
    private String name;
    private String gender;
    private String signature;
    private String mail;
    private String carType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_info);

        initView();
        init();



    }
    private void initView() {

        mTitle = (TitleView) findViewById(R.id.my_detail_info_title);
        mSpUser = getSharedPreferences("User",MODE_PRIVATE);

        mName = (TextView) findViewById(R.id.name);
        mGender = (TextView) findViewById(R.id.sex);
        mSignature = (TextView) findViewById(R.id.signature);
        mMail = (TextView) findViewById(R.id.mail);
        mCarType = (TextView) findViewById(R.id.car_type);
        mAddress = (TextView) findViewById(R.id.address);

    }
    private void init() {
        mTitle.setBackIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyInfoActivity.this.finish();
            }
        });
        mTitle.setMoreIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //编辑
            }
        });

        mUserAccount = mSpUser.getString("userAccount", "");
        name = mSpUser.getString("name", "");
        gender = mSpUser.getString("sex", "");
        signature = mSpUser.getString("signature", "");
        mail = mSpUser.getString("mail", "");
        carType = mSpUser.getString("CarType", "");
        String address = mSpUser.getString("address", "");
        mName.setText(name);
        mGender.setText(gender);
        mSignature.setText(signature);
        mMail.setText(mail);
        mCarType.setText(carType);
        mAddress.setText(address);
        if(TextUtils.isEmpty(name) && TextUtils.isEmpty(gender) &&
           TextUtils.isEmpty(signature) && TextUtils.isEmpty(mail) &&
           TextUtils.isEmpty(carType) && TextUtils.isEmpty(carType) ) {
            //请求服务器  并且缓存到本地


            ThreadManager.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject userInfo = getUserInfo(mUserAccount);
                        name = userInfo.getString("nickname");
                        gender = userInfo.getString("gender");//signature
                        signature = userInfo.getString("signature");
                        carType = userInfo.getString("car_models");
                        mail = userInfo.getString("email");
                        //  carType = userInfo.getString("");//数据库中没有地址
                        SharedPreferences.Editor edit = mSpUser.edit();
                        /*name = mSpUser.getString("name", "");
                        gender = mSpUser.getString("sex", "");
                        signature = mSpUser.getString("signature", "");
                        mail = mSpUser.getString("mail", "");
                        carType = mSpUser.getString("CarType", "");*/
                        edit.putString("name",name);
                        edit.putString("sex",gender);
                        edit.putString("signature",signature);
                        edit.putString("mail",mail);
                        edit.putString("CarType",carType);
                        edit.apply();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mName.setText(name);
                                mGender.setText(gender);
                                mSignature.setText(signature);
                                mMail.setText(mail);
                                mCarType.setText(carType);
                                //     mAddress.setText(address);
                            }
                        });


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });



        }




    }

    //获得余额
    private JSONObject getUserInfo(String username)  throws Exception  {
        //使用Map封装请求参数 采用post方法
        Map<String,String> map=new HashMap<>();
        map.put("phoneNumber", username);

        String url= HttpUtil.OLD_URL +"getUserInfo";

        return new JSONObject(HttpUtil.postRequest(url, map));
    }


    /**
     * 退出登录
     * @param view
     */
    public void exitLogin(View view) {

    }


}
