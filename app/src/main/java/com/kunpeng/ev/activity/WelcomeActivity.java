package com.kunpeng.ev.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.kunpeng.ev.R;
import com.kunpeng.ev.manage.ThreadManager;
import com.kunpeng.ev.utils.HttpUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WelcomeActivity extends Activity {
    private SharedPreferences sp;
    private SharedPreferences spUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        SDKInitializer.initialize(getApplicationContext());
        // 推送设置
        /*PushReceiver receiver = new PushReceiver();
        IntentFilter filter = new IntentFilter();
        String action = "com.igexin.sdk.action.4vpjq6XBG1AidyVf6t1xK1";
        filter.addAction(action);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        registerReceiver(receiver, filter);*/


        ThreadManager.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    sp = getSharedPreferences("config",Activity.MODE_PRIVATE);
                    spUser = getSharedPreferences("User",Activity.MODE_PRIVATE);
                    Boolean isAutoLogin = sp.getBoolean("cbAutoLogin", false);
                   if (isAutoLogin) {
                        String userAccount = spUser.getString("userAccount", "");
                        String userPwd =  spUser.getString("userPassword","");
                        if(login(userAccount,userPwd)) { //Toast.makeText(WelcomeActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(WelcomeActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                }
                            });
                            startActivity(new Intent(WelcomeActivity.this,HomeActivity.class));
                            finish();
                        } else {
                           runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   Toast.makeText(WelcomeActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                               }
                           });

                            Intent intent = new Intent(WelcomeActivity.this,HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }


                    } else {
                        Intent intent = new Intent(WelcomeActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                  /*  Intent intent = new Intent(WelcomeActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();*/

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }






            }
        });

    }
    public boolean login(String user,String password) {

        try {
            JSONObject jsonObject = loginquery(user, password);

            if(jsonObject.getString("info").equals("loginSuccess")) {
                System.out.println("登录成功");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return  false;
    }

    //定义发送请求方法 登录
    private JSONObject loginquery(String username,String password) throws Exception {
        //使用Map封装请求参数 采用post方法
        Map<String,String> map=new HashMap<>();
        map.put("phoneNumber", username);
        map.put("password", password);
        Log.e("LoginActivity", "名称  密码" + username + password);

        String url= HttpUtil.TEST_BASE_URL+"loginAction";
        Log.e("LoginActivity", url);

        return new JSONObject(HttpUtil.postRequest(url, map));

    }


}
