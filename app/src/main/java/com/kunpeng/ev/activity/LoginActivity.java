package com.kunpeng.ev.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kunpeng.ev.R;
import com.kunpeng.ev.manage.ThreadManager;
import com.kunpeng.ev.utils.HttpUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity {

    private static final int LOGIN_SUCCESS = 0;
    private static final int LOGIN_FAIL = 1;
    private EditText userAcountEt;
    private EditText userPwdEt;
    private CheckBox cbRemenberPwd;
    private CheckBox cbAutoLogin;
    private SharedPreferences sp;
    private SharedPreferences spUser;
    private boolean isFirstPress = false;
    private FrameLayout mLoginProgress;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOGIN_SUCCESS:
                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                    mLoginProgress.setVisibility(FrameLayout.INVISIBLE);
                    break;
                case LOGIN_FAIL:
                    Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                    mLoginProgress.setVisibility(FrameLayout.INVISIBLE);
                    break;

            }
            super.handleMessage(msg);
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sp = getSharedPreferences("config",Activity.MODE_PRIVATE);
        spUser = getSharedPreferences("User",Activity.MODE_PRIVATE);
        TextView tvRegister = (TextView) findViewById(R.id.tv_register);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
        TextView mForgetPwd = (TextView) findViewById(R.id.tv_forget_pwd);
        mForgetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  startActivity(new Intent(LoginActivity.this,ForgetPwdActivity.class));
            }
        });
        userAcountEt = (EditText) findViewById(R.id.user_account);
        userPwdEt = (EditText) findViewById(R.id.user_password);
        // (Button)findViewById(R.id.btn_login);
        cbRemenberPwd = (CheckBox) findViewById(R.id.cb_auto_rmpwd);
        cbAutoLogin = (CheckBox) findViewById(R.id.cb_auto_login);
        Boolean isCheck = sp.getBoolean("cbRemenberPwd", false);
        if(isCheck) {
            cbRemenberPwd.setChecked(true);
            userAcountEt.setText(spUser.getString("userAccount", ""));
            userPwdEt.setText(spUser.getString("userPassword",""));



        } else {
            userAcountEt.setText(spUser.getString("userAccount", ""));
            cbRemenberPwd.setChecked(false);
        }
        Boolean isAutoLogin = sp.getBoolean("cbAutoLogin", false);
        if(isAutoLogin) {
            cbAutoLogin.setChecked(true);

        } else {
            cbAutoLogin.setChecked(false);
        }


        mLoginProgress = (FrameLayout) findViewById(R.id.login_progress);
        mLoginProgress.setVisibility(FrameLayout.INVISIBLE);






    }
    public void login(View view) {

        final String userAcount = userAcountEt.getText().toString().trim();
        final String userPassword = userPwdEt.getText().toString().trim();
        if(userAcount.isEmpty()) {

            Toast.makeText(LoginActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
            return;
        } else if(userPassword.isEmpty()) {

            Toast.makeText(LoginActivity.this,"请输入密码",Toast.LENGTH_SHORT).show();
            return;
        }
        //login(userAcount,userPassword);
        mLoginProgress.setVisibility(FrameLayout.VISIBLE);
        Runnable loginTask = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(login(userAcount,userPassword)) {//userAcount.equals("13438396169") && userPassword.equals("123456")) {
                    //Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                    Message message = Message.obtain();
                    message.what = LOGIN_SUCCESS;
                    handler.sendMessage(message);
                    startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                    finish();
                } else {
                    //Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                    Message message = Message.obtain();
                    message.what = LOGIN_FAIL;
                    handler.sendMessage(message);
                }

                if(cbRemenberPwd.isChecked()) {
                    SharedPreferences.Editor edit = sp.edit();
                    SharedPreferences.Editor edit1 = spUser.edit();
                    edit1.putString("userAccount", userAcount);
                    edit1.putString("userPassword", userPassword);
                    edit.putBoolean("cbRemenberPwd", true);
                    edit.apply();
                    edit1.apply();

                } else {
                    SharedPreferences.Editor edit = sp.edit();
                    SharedPreferences.Editor edit1 = spUser.edit();
                    edit1.putString("userAccount", userAcount);
                    edit1.apply();
                    edit.putBoolean("cbRemenberPwd", false);
                    edit.apply();
                }

                if(cbAutoLogin.isChecked()) {
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putBoolean("cbAutoLogin", true);
                    edit.apply();
                } else {
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putBoolean("cbAutoLogin", false);
                    edit.apply();
                }
            }
        };

        ThreadManager.THREAD_POOL_EXECUTOR.execute(loginTask);




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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        isFirstPress = false;
        return super.onTouchEvent(event);
    }

    @Override
    protected void onResume() {
        isFirstPress = false;
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if(!isFirstPress) {
            Toast.makeText(this,"再按一次退出",Toast.LENGTH_SHORT).show();
            isFirstPress = true;
        } else {
            Process.killProcess(Process.myPid());
        }
    }
}