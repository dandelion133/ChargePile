package com.kunpeng.ev.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.kunpeng.ev.utils.SetChargeStatus;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.kunpeng.ev.R;
import com.mining.app.zxing.camera.CameraManager;
import com.mining.app.zxing.decoding.CaptureActivityHandler;
import com.mining.app.zxing.decoding.InactivityTimer;
import com.mining.app.zxing.view.ViewfinderView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Initial the camera
 *
 * @author Ryan.Tang
 */
public class MipcaActivityCapture extends Activity implements Callback {

    private static final String TAG = "MipcaActivityCapture";
    private SetChargeStatus setchargestatus = new SetChargeStatus();
    //String UrlPath="http://139.129.201.20/SSHDemo2/setCPS?SectionID=11&DeviceID=CD201404241112&Status=unlock";
    //String UrlPath1="http://139.129.201.20/SSHDemo2/setCPS?SectionID=11&DeviceID=CD201404241112&Status=enlock";
    //String UrlPath2="http://139.129.201.20/SSHDemo2/setCPS?SectionID=11&DeviceID=CD201404241112&Status=pay";

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private ProgressDialog progressDialog = null;
    static Activity MipcaActivityCapture;
    private Handler settinghandler;
    private boolean vibrate;
    private String deviceId;
    private AlertDialog hint;
    private Handler handler1;
    private Handler handler2;
    private int bindcode = 0;
    private String[] items = new String[]{"电桩显示屏上设置", "手机上设置"};
    //解析绑定接口返回数据的判断值
    private int jsonflag = 0;
    //变换充电设置图片的判断值
    private int flag = 0;
    private SharedPreferences mSpData;
    private SharedPreferences mSpUser;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);


        mSpData = getSharedPreferences("data", Context.MODE_PRIVATE);
        mSpUser = getSharedPreferences("User", Context.MODE_PRIVATE);

        //ViewUtil.addTopView(getApplicationContext(), this, R.string.scan_card);
        CameraManager.init(getApplication());
        MipcaActivityCapture = this;
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        hasSurface = false;
        handler1 = new MyHandler1();
        handler2 = new MyHandler2();
        settinghandler = new settinghandler();
        inactivityTimer = new InactivityTimer(this);

    }

    //结束当前Activity
    public void scanningback(View view) {
        //二维码扫描界面结束
        MipcaActivityCapture.this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * 处理扫描结果
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        Toast.makeText(MipcaActivityCapture.this, resultString, Toast.LENGTH_SHORT).show();
        deviceId = resultString;
        //String deviceId=extractDeviceId(resultString);
        System.out.println(resultString);
        System.out.println("feigege" + deviceId);
        if (resultString.equals("")) {
            Toast.makeText(MipcaActivityCapture.this, "Scan failed!", Toast.LENGTH_SHORT).show();
        } else {

            //扫描完成后结束MipcaActivityCapture 跳转到另一界面
            //Intent resultIntent = new Intent(MipcaActivityCapture.this, ScanResultActivity.class);;
            ///Bundle bundle = new Bundle();
            //bundle.putString("result", resultString);
            //resultIntent.putExtras(bundle);
            //this.setResult(RESULT_OK, resultIntent);
            //startActivity(resultIntent);
            NumberThread nt = new NumberThread();
            Thread thread = new Thread(nt);
            thread.start();
            //Toast.makeText(MipcaActivityCapture.this,deviceId, Toast.LENGTH_SHORT).show();
            //progressdialog = ProgressDialog.show(MipcaActivityCapture.this, "请稍等...", "获取数据中...", true);
        }


    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    class MyHandler1 extends Handler {
        //接受message的信息
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if (msg.what == 1) {
                Toast.makeText(MipcaActivityCapture.this, "网络异常，请连接网络", Toast.LENGTH_SHORT).show();

            } else if (msg.what == 2) {
                Toast.makeText(MipcaActivityCapture.this, "服务器异常，请稍后再试", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 4) {
                int flag = 1;
                String identifycode = (String) msg.obj;

                Editor edit = mSpData.edit();
                edit.putString("identifycode", identifycode);
                edit.putString("deviceid", deviceId);
                edit.apply();

                Log.e(TAG,"identifycode"+ identifycode);
                Log.e(TAG,"deviceid"+deviceId);
               /* SharedPreferences identify_code = getSharedPreferences("identifycode", Activity.MODE_PRIVATE);
                Editor editor = identify_code.edit();
                editor.putString("identifycode", identifycode);
                editor.commit();*/
              //  Toast.makeText(MipcaActivityCapture.this, identifycode, Toast.LENGTH_SHORT).show();
               /* SharedPreferences deviceid = getSharedPreferences("deviceid", Activity.MODE_PRIVATE);
                Editor editor1 = deviceid.edit();
                //	Toast.makeText(MipcaActivityCapture.this,"电桩解锁成功", Toast.LENGTH_SHORT).show();
                editor1.putString("deviceid", deviceId);*/
              //  editor1.commit();


                //弹出设置方式对话框
                showDialog();
                //progressDialog = ProgressDialog.show(MipcaActivityCapture.this, "请稍等...", "数据传输中...", true);
                //SettingInfoThread st = new SettingInfoThread();
                //st.start();
                // MipcaActivityCapture.this.finish();
            } else {
                String result = (String) msg.obj;
                if (result.equals("pileBinded")) {
                    Toast.makeText(MipcaActivityCapture.this, "解锁成功", Toast.LENGTH_SHORT).show();
                    MipcaActivityCapture.this.finish();
                } else if (result.equals("Binded another Pile")) {
                    Toast.makeText(MipcaActivityCapture.this, "解锁失败，不能重复绑定", Toast.LENGTH_SHORT).show();
                    MipcaActivityCapture.this.finish();
                } else if (result.equals("user Locked")) {
                    Toast.makeText(MipcaActivityCapture.this, "解锁失败，由于之前有不当的操作，您已被锁定", Toast.LENGTH_SHORT).show();
                    MipcaActivityCapture.this.finish();
                } else if (result.equals("user non-existent")) {
                    Toast.makeText(MipcaActivityCapture.this, "用户丢失，请重新注册", Toast.LENGTH_SHORT).show();
                    MipcaActivityCapture.this.finish();
                } else if (result.equals("pileUsing")) {
                    Toast.makeText(MipcaActivityCapture.this, "解锁失败，当前桩体正在被其他用户使用", Toast.LENGTH_SHORT).show();
                    MipcaActivityCapture.this.finish();
                } else if (result.equals("pileIdError")) {
                    Toast.makeText(MipcaActivityCapture.this, "桩体编号错误", Toast.LENGTH_SHORT).show();
                    MipcaActivityCapture.this.finish();
                } else if (result.equals("pile have not connect")) {
                    Toast.makeText(MipcaActivityCapture.this, "电桩与服务器没有连接，请稍后再试", Toast.LENGTH_SHORT).show();
                    //MipcaActivityCapture.this.finish();
                    showDialog();
                } else {
                    Toast.makeText(MipcaActivityCapture.this, "解锁失败请重新扫描二维码", Toast.LENGTH_SHORT).show();
                    MipcaActivityCapture.this.finish();
                }
            }
        }
    }

    class MyHandler2 extends Handler {
        //接受message的信息
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if (msg.what == 1) {
                Toast.makeText(MipcaActivityCapture.this, "网络异常，请链接网络", Toast.LENGTH_SHORT).show();

            } else if (msg.what == 2) {
                Toast.makeText(MipcaActivityCapture.this, "服务器异常，请稍后再试", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 3) {
                jsonflag = 1;
                NumberThread nt = new NumberThread();
                Thread thread = new Thread(nt);
                thread.start();
            } else {
                //订单数据的判断
                String status = msg.getData().getString("status");
                final String cost = msg.getData().getString("cost");
                final String chargeamount = msg.getData().getString("chargeamount");
                final String lengthoftime = msg.getData().getString("lengthoftime");
                final String deviceID = msg.getData().getString("deviceid");
                if (status.equals("paying")) {
                    hint = new AlertDialog.Builder(MipcaActivityCapture.this)
                            .setTitle("付费提示")
                            .setMessage("您有订单未支付，请先支付")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Intent intent = new Intent(MipcaActivityCapture.this, PayDemoActivity.class);
                                    intent.putExtra("cost", cost);
                                    intent.putExtra("chargeamount", chargeamount);
                                    intent.putExtra("lengthoftime", lengthoftime);
                                    intent.putExtra("deviceid", deviceID);
                                    startActivity(intent);
                                }
                            }).show();


                }
            }
        }
    }

    //向服务器发送电桩编号和电话号码
    class NumberThread implements Runnable {
        @Override
        public void run() {
            String result = "";
            String identifycode = "";
           // SharedPreferences share = getSharedPreferences("username", Activity.MODE_PRIVATE);
            String mynumber =  mSpUser.getString("userAccount","");
          //  String mynumber = share.getString("USER_NAME", "");
            Message message = handler1.obtainMessage();
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>" + mynumber + deviceId);
            //创建HttpClient
            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setIntParameter(
                    HttpConnectionParams.SO_TIMEOUT, 5000); // 超时设置
            httpClient.getParams().setIntParameter(
                    HttpConnectionParams.CONNECTION_TIMEOUT, 5000);// 连接超时
            //创建代表请求的对象,参数是访问的服务器地址
            //http://www.baidu.com
            //HttpGet httpGet = new HttpGet("http://139.129.201.20/SSHDemo2/BindAction?username="+mynumber+"&DeviceID="+deviceId);
            HttpGet httpGet = new HttpGet("http://139.129.201.20/NewChargePile/ScanQRCode?phoneNumber=" + mynumber + "&pileId=" + deviceId);
            //执行请求，获取服务器发还的相应对象
            try {
                HttpResponse resp = httpClient.execute(httpGet);
                //检查相应的状态是否正常。检查状态码的值是否等于200
                int code = resp.getStatusLine().getStatusCode();
                if (code == 200) {
                    //从相应对象当中取出数据
                    HttpEntity entity = resp.getEntity();
                    InputStream in = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line = reader.readLine();
                    String json = line;
                    JSONObject jsonObject2 = new JSONObject(json);
                    if (jsonflag == 0) {
                        result = jsonObject2.getString("info");
                        if (result.equals("delockSuccess")) {
                            identifycode = jsonObject2.getString("identify_Code");
                            message.what = 4;
                            message.obj = identifycode;
                            handler1.sendMessage(message);
                        } else {
                            Log.d("HTTP", "从服务器取得的数据位：" + line);
                            message.obj = result;
                            handler1.sendMessage(message);
                        }
                    } else {//订单数据
                        Log.d("HTTP", "从服务器取得的数据位：" + line);
                        String paystatus = jsonObject2.getString("payStatus");
                        String cost = jsonObject2.getString("consumeMoney");
                        String lengthoftime = jsonObject2.getString("chargeTime");
                        String chargeamount = jsonObject2.getString("chargeQuantity");
                        String deviceid = jsonObject2.getString("deviceID");

                        //Message message= handler.obtainMessage();
                        // message.what=1;
                        Bundle bundle = new Bundle();
                        bundle.putString("cost", cost);
                        bundle.putString("status", paystatus);
                        bundle.putString("lengthoftime", lengthoftime);
                        bundle.putString("chargeamount", chargeamount);
                        bundle.putString("deviceid", deviceid);
                        message.setData(bundle);
                        jsonflag = 0;
                        //message.obj = sex;
                        handler2.sendMessage(message);
                    }

                } else {
                    message.what = 2;
                    handler1.sendMessage(message);
                }
            } catch (ConnectTimeoutException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                message.what = 2;
                handler1.sendMessage(message);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                message.what = 1;
                handler1.sendMessage(message);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                message.what = 3;
                handler2.sendMessage(message);
            }
        }
    }

    /**
     * 显示选择对话框
     */
    private void showDialog() {

        new AlertDialog.Builder(this)
                .setTitle("充电设置方式选择")
                .setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                progressDialog = ProgressDialog.show(MipcaActivityCapture.this, "请稍等...", "数据传输中...", true);
                                SettingInfoThread st = new SettingInfoThread();
                                st.start();

                                break;
                            case 1:
                                SharedPreferences settingimageflag = getSharedPreferences("flag", Activity.MODE_PRIVATE);
                                Editor editor1 = settingimageflag.edit();
                                editor1.putInt("flag", 1);
                                editor1.commit();
                                MipcaActivityCapture.this.finish();
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

    //设置方式
    class settinghandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            String result = (String) msg.obj;
            System.out.println(result);
            super.handleMessage(msg);
            if (msg.what == 1) {
                Toast.makeText(MipcaActivityCapture.this, "服务器异常，请连接网络", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            } else {
                if (result.equals("settingForScreen")) {
                    flag = 1;
                    Toast.makeText(MipcaActivityCapture.this, "请到电桩显示屏上进行充电设置", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                    MipcaActivityCapture.this.finish();
                } else {
                    //Toast.makeText(MipcaActivityCapture.this,"修改失败", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        }
    }

    class SettingInfoThread extends Thread {

        public void run() {

            //创建HttpClient
            HttpClient httpClient = new DefaultHttpClient();
        //    SharedPreferences share = getSharedPreferences("username", Activity.MODE_PRIVATE);
           // SharedPreferences settingdata = getSharedPreferences("data", Activity.MODE_PRIVATE);
          //  SharedPreferences deviceid = getSharedPreferences("deviceid", Activity.MODE_PRIVATE);
            Message message = settinghandler.obtainMessage();
            String returninfo = "";
          //  String mynumber = share.getString("USER_NAME", "");
         //   String pileid = deviceid.getString("deviceid", "");


            String mynumber = mSpUser.getString("userAccount","");
            String pileid =  mSpData.getString("deviceid","");

            //System.out.println(mynumber+mypassword+myname+mysignature+sex+mycar);
            //创建代表请求的对象,参数是访问的服务器地址
            //HttpGet httpGet = new HttpGet("http://139.129.201.20/SSHDemo2/SetUserInfo?number=13980753184&password=82676667&name=养肥&gender=男&sign=helloworld&cartype=长安");
            // HttpGet httpGet = new HttpGet("http://139.129.201.20/SSHDemo2/SetUserInfo?number="+mynumber+"&password="+mypassword+"&name="+myname+"&gender="+sex+"&sign="+mysignature+"&cartype="+mycar
            //http://139.129.201.20/NewChargePile/setUserInfo?phoneNumber=13808193674&nickName=中值顶&gender=男&carModel=宝马&signature=哈哈哈&email=6905@163.com
            //);
            HttpGet httpGet = new HttpGet("http://139.129.201.20/NewChargePile/modelSelection?phoneNumber=" + mynumber + "&pileId=" + pileid + "&SetupMethod=andriodScreen&ChargeMethod=0&SetValue=0");
            //执行请求，获取服务器发还的相应对象
            try {
                HttpResponse resp = httpClient.execute(httpGet);
                //检查相应的状态是否正常。检查状态码的值是否等于200
                int code = resp.getStatusLine().getStatusCode();
                if (code == 200) {
                    //从相应对象当中取出数据
                    HttpEntity entity = resp.getEntity();
                    InputStream in = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line = reader.readLine();
                    String json = line;
                    //  cost=jsonObject2.getString("consumeMoney");
                    JSONObject jsonObject2 = new JSONObject(json);
                    returninfo = jsonObject2.getString("info");
                    message.obj = returninfo;
                    settinghandler.sendMessage(message);
                    System.out.println(line);
                    Log.d("HTTP", "从服务器取得的数据位：" + line);
                } else {
                    System.out.println("错了");
                }
            } catch (Exception e) {
                e.printStackTrace();
                message.what = 1;
                settinghandler.sendMessage(message);
            }
        }
    }

    //获得电桩编号
    private String extractDeviceId(String str) {
        String deviceidstr = "";
        Pattern pattern = Pattern.compile("\\w(\\d\\d)(\\w+)");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            String srcStr = matcher.group(0);

            deviceidstr = deviceidstr + srcStr;

        }

        return deviceidstr;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

}