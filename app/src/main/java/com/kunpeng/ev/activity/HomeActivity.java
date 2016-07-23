package com.kunpeng.ev.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import com.kunpeng.ev.R;
import com.kunpeng.ev.fragment.AccountFragment;
import com.kunpeng.ev.fragment.ChargeFragment;
import com.kunpeng.ev.fragment.MapFragment;
import com.kunpeng.ev.fragment.StateFragment;
import com.kunpeng.ev.manage.ThreadManager;
import com.kunpeng.ev.ui.TabIndicatorView;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    //定义FragmentTabHost对象
    public FragmentTabHost mTabHost;
    public static final int HIDE = 0;
    //static Activity activity1;//只是为了取得HomeActivity的引用，后面可能会用
    //定义数组来存放Fragment界面
    private Class fragmentArray[] = {MapFragment.class,ChargeFragment.class,StateFragment.class, AccountFragment.class};

    //定义数组来存放按钮图片
   // private int mImageViewArray[] = {R.drawable.tab_income,R.drawable.tab_share,R.drawable.tab_myinfo};
    private int mImageViewArrayNomal[] = {R.drawable.tab_map_normal,R.drawable.tab_charge_normal,R.drawable.tab_state_normal,R.drawable.tab_account_normal};
    private int mImageViewArrayFocus[] = {R.drawable.tab_map_pressed,R.drawable.tab_charge_pressed,R.drawable.tab_state_pressed,R.drawable.tab_account_pressed};
    //设置文字的颜色

    //Tab选项卡的文字
    private String mTextviewArray[] = {"定位", "充电", "状态","账户"};
    private TabIndicatorView[] mTabIndicatorView = new TabIndicatorView[4];

    private boolean isFirstPress = false;
    public static Activity instance;
    private boolean isFirstTabSelected = true;
    private SharedPreferences sp;
    public FrameLayout mMapHide;
    private FrameLayout mRealtabcontent;
    private FrameLayout mIncludeCharge;
    private FrameLayout mIncludeState;
    private FrameLayout mIncludeAccount;
    private FrameLayout mIncludeMap;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            mMapHide.setVisibility(View.INVISIBLE);
            mIncludeCharge.setVisibility(View.INVISIBLE);
            mIncludeState.setVisibility(View.INVISIBLE);
            mIncludeAccount.setVisibility(View.INVISIBLE);
            mIncludeMap.setVisibility(View.INVISIBLE);
            mRealtabcontent.setVisibility(View.VISIBLE);
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG,"HomeActivity");
        setContentView(R.layout.activity_home);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        instance = this;
        initView();
    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {

        isFirstPress = false;
        return  super.onTouchEvent(event);
    }

    /**
     * 初始化组件
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    private void initView() {



       /* Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);

        // toolbar.setNavigationIcon(R.mipmap.ic_launcher);//设置导航栏图标
        toolbar.setLogo(R.drawable.qwe);//设置app logo
        toolbar.setTitle("佳易充");//设置主标题
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        //  toolbar.setSubtitle("Subtitle");//设置子标题
        // toolbar.inflateMenu(R.menu.menu_home);//设置右上角的填充菜单
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.exit:
                        Toast.makeText(HomeActivity.this, "退出", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.search:
                        Toast.makeText(HomeActivity.this, "搜索", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });*/
        mRealtabcontent = (FrameLayout) findViewById(R.id.realtabcontent);
        mMapHide = (FrameLayout) findViewById(R.id.map_hide);

        mIncludeCharge = (FrameLayout) findViewById(R.id.include_charge);
        mIncludeState = (FrameLayout) findViewById(R.id.include_state);
        mIncludeAccount = (FrameLayout) findViewById(R.id.include_account);
        mIncludeMap = (FrameLayout) findViewById(R.id.include_map);



        mMapHide.setVisibility(View.INVISIBLE);

        //实例化TabHost对象，得到TabHost
        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        //得到fragment的个数
        int count = fragmentArray.length;

        for(int i = 0; i < count; i++) {

            mTabIndicatorView[i] = new TabIndicatorView(this);
            mTabIndicatorView[i].setDesc(mTextviewArray[i]);
            //mTabIndicatorView[i].setTabTitle(mTextviewArray[i]);
            mTabIndicatorView[i].setIconId(mImageViewArrayFocus[i], mImageViewArrayNomal[i]);
            // mTabIndicatorView[i].setTabIcon(mImageViewArrayFocus[i], mImageViewArrayNomal[i]);
            //为每一个Tab按钮设置图标、文字和内容
            TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(mTabIndicatorView[i]);
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);

        }
        mTabIndicatorView[3].setTabSelected(false);
        mTabIndicatorView[2].setTabSelected(false);
        mTabIndicatorView[1].setTabSelected(false);
        mTabIndicatorView[0].setTabSelected(true);
        mTabHost.setCurrentTab(0);


        mTabHost.getTabWidget().setDividerDrawable(android.R.color.white);//设置分割线
        //mTabIndicatorView[0].setTabSelected(true);

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {



            @Override
            public void onTabChanged(String tabId) {

                // Toast.makeText(HomeActivity.this, "Frgment", Toast.LENGTH_SHORT).show();

                isFirstPress = false;
                mTabIndicatorView[0].setTabSelected(false);
                mTabIndicatorView[1].setTabSelected(false);
                mTabIndicatorView[2].setTabSelected(false);
                mTabIndicatorView[3].setTabSelected(false);

                mRealtabcontent.setVisibility(View.INVISIBLE);
                mMapHide.setVisibility(View.VISIBLE);

                mIncludeCharge.setVisibility(View.INVISIBLE);
                mIncludeState.setVisibility(View.INVISIBLE);
                mIncludeAccount.setVisibility(View.INVISIBLE);
                mIncludeMap.setVisibility(View.INVISIBLE);

                if (tabId.equals(mTextviewArray[0])) {
                    mRealtabcontent.setVisibility(View.VISIBLE);
                    mTabIndicatorView[0].setTabSelected(true);
                    mIncludeMap.setVisibility(View.VISIBLE);
                    isFirstTabSelected = true;
                    setTabSelect(true);
                } else {
                    //百度地图BUG 掩盖掉黑边
                    if(isFirstTabSelected) {

                        Log.e(TAG,"HIDE");
                        isFirstTabSelected = false;
                    }


                    if (tabId.equals(mTextviewArray[1])) {
                       // mMapHide.setBackground();
                        mIncludeCharge.setVisibility(View.VISIBLE);
                        mTabIndicatorView[1].setTabSelected(true);
                    } else if (tabId.equals(mTextviewArray[2])) {
                        mIncludeState.setVisibility(View.VISIBLE);
                        mTabIndicatorView[2].setTabSelected(true);
                    } else if (tabId.equals(mTextviewArray[3])) {
                        mIncludeAccount.setVisibility(View.VISIBLE);
                        mTabIndicatorView[3].setTabSelected(true);
                    }
                    setTabSelect(false);
                }
            }
        });

    }


    public void setTabSelect(boolean isMap) {

            if(isMap) {
                ThreadManager.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            Thread.sleep(600);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Message message = Message.obtain();
                        message.what = HIDE;
                        mHandler.sendMessage(message);
                    }
                });
            } else {
                ThreadManager.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Message message = Message.obtain();
                        message.what = HIDE;
                        mHandler.sendMessage(message);
                    }
                });
            }


    }

    @Override
    public void onBackPressed() {
        if(!isFirstPress) {
            Toast.makeText(this,"再按一次退出",Toast.LENGTH_SHORT).show();
            isFirstPress = true;
        } else {
            //Process.killProcess(Process.myPid());
            Process.killProcess(Process.myPid());
            //ActivityManager am = (ActivityManager)getSystemService (Context.ACTIVITY_SERVICE);
            //am.restartPackage(getPackageName());

        }

        //super.onBackPressed();
    }

    @Override
    protected void onResume() {
        isFirstPress = false;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        instance = null;
        super.onDestroy();
    }
}
