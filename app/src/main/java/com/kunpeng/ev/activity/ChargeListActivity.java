package com.kunpeng.ev.activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.kunpeng.ev.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChargeListActivity extends Activity {
	private ListView listview;
	private String pileId;
	private String state;
	private List<Map<String, Object>> jsonreturn;
	private String sta;
	//地图传过来的值
	private String deviceid;
	private String address;
	private ProgressDialog progressDialog = null;
	private boolean netflag=false;
	//		private ProgressBar payProgress;
	//		private int a;
	private JsonGetpaiedGoodsInfo json=new JsonGetpaiedGoodsInfo();
	private ImageView ic_back;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.charge);
		Intent intent = getIntent();
		deviceid=intent.getStringExtra("deviceid");
		address = intent.getStringExtra("address");

		listview = (ListView) findViewById(R.id.list);
		netflag=isNetworkConnected(ChargeListActivity.this);
		if(netflag==false){
			Toast.makeText(ChargeListActivity.this,"网络异常，请连接网络", Toast.LENGTH_SHORT).show();
		}
		else{
			progressDialog = ProgressDialog.show(ChargeListActivity.this, "请稍等...", "获取数据中...", true);
			GetChargeThread();
		}
		ic_back=(ImageView) findViewById(R.id.imageView_back);
		ic_back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				ChargeListActivity.this.finish();
			}

		});
	}
	//离网判断
	public boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return true;
			}
		}
		return false;
	}

	private void GetChargeThread()
	{
		new Thread()
		{
			@Override
			public void run()
			{
				//在子线程里面进行通信登录链接
				//				http://139.129.201.20:8080/SSHDemo2/loginAction?
				//				username=panda&password=123456
				//				String Url="http://139.129.201.20:8080/SSHDemo2/getCpo?userId="+user_name;
				//String Url="http://139.129.201.20/SSHDemo2/getReal?userId=13788905678";
				Message message= chargehandler.obtainMessage();
				try
				{
					//得到返回的结果
					jsonreturn=json.getpaiedgoodsinfo();
					chargehandler.sendEmptyMessage(0);

					//Log.e("log_tag",location);
				} catch (SocketTimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("-------------------------------->");
					message.what=1;
					System.out.println("-------------------------------->"+message.what);
					chargehandler.sendMessage(message);
				}
				catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("-------------------------------->");
					message.what=1;
					System.out.println("-------------------------------->"+message.what);
					chargehandler.sendMessage(message);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					Log.e("log_tag", "Error parsing data "+e.toString());
				}

				//执行完毕后给handler发送一个空消息
				//pswhandler.sendEmptyMessage(0);

			}
		}.start();
	}


	//开启一个handler处理线程
	private Handler chargehandler =new Handler(){
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			if(msg.what==1){
				Toast.makeText(ChargeListActivity.this,"服务器异常，请稍后再试", Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();
			}
			else{
				//执行到这里  对界面进行更新  如果账号密码相同则进入主界面
				SimpleAdapter mSimpleAdapter = new SimpleAdapter(ChargeListActivity.this,jsonreturn,
						R.layout.chargelist_item,
						new String[] {"pileId","state","pileIdnumber"},
						new int[] {R.id.pileidtv,R.id.statetv,R.id.pileidnumber}
				);
				listview.setAdapter(mSimpleAdapter);
				listview.setOnItemClickListener(new OnItemClickListener(){
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
											int arg2, long arg3) {
						// TODO Auto-generated method stub
						View v=arg1;
						TextView pilenum=(TextView) v.findViewById(R.id.pileidnumber);
						String id = (String) pilenum.getText();
						TextView st=(TextView) v.findViewById(R.id.statetv);
						//							Toast.makeText(MyGoodsPayActivity.this,st.getText(), Toast.LENGTH_LONG).show();
						sta=(String) st.getText();
						//Toast.makeText(ChargeListActivity.this,sta, Toast.LENGTH_SHORT).show();
						if(sta.equals("当前状态：空闲（可预约）")){
							Intent intent=new Intent();
							intent.putExtra("id", id);
							intent.putExtra("address", address);
							intent.setClass(ChargeListActivity.this, MapOrderActivity.class);
							startActivity(intent);
						}
						else if(sta.equals("当前状态：故障（不可预约）")){
							Toast.makeText(ChargeListActivity.this,"当前电桩有故障，无法使用，请重新选择", Toast.LENGTH_SHORT).show();
						}
						else{
							Toast.makeText(ChargeListActivity.this,"当前电桩正在使用或被预约，请重新选择", Toast.LENGTH_SHORT).show();
						}
					}
				});

			}}

	};
	public class JsonGetpaiedGoodsInfo {


		public List<Map<String, Object>> getpaiedgoodsinfo() throws Exception
		{

			//String UrlPath="http://139.129.201.20/SSHDemo2/getCpo?userId="+user_name;
			String UrlPath="http://139.129.201.20/NewChargePile/getPileInfo?cabinetId="+deviceid;
			List<Map<String, Object>> paiedgoodsinfo=new ArrayList<Map<String, Object>>();
			byte[] data=readParse(UrlPath);
			JSONArray array = new JSONArray(new String(data));

			for (int i = 0; i < array.length(); i++)
			{

				Map<String, Object> map=new HashMap<String, Object>();
				JSONObject item = array.getJSONObject(i);
				pileId = item.getString("qr_codes");
				map.put("pileId", "电桩编号："+pileId);
				map.put("pileIdnumber",pileId);
				if(item.getString("state").equals("enlock"))
				{
					map.put("state","当前状态：空闲（可预约）");
				}
				else if(item.getString("state").equals("fault"))
				{
					map.put("state","当前状态：故障（不可预约）");
				}
				else{
					map.put("state","当前状态：繁忙（不可预约）");
				}

				//if(status.equals("paid")){
				//	map.put("status", "订单状态：已完成");}
				//else{
				//	map.put("status", "订单状态：未完成");}
				//Log.e("JTest",location);
				paiedgoodsinfo.add(map);
				if(i==array.length()-1){
					progressDialog.dismiss();
				}
			}


			return paiedgoodsinfo;
		}

		public byte[] readParse(String UrlPath) throws Exception
		{
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] data = new byte[1024];
			int len = 0;
			URL url = new URL(UrlPath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			//int responsecode=1;
			//responsecode=conn.getResponseCode();
			//System.out.println("------------------>"+responsecode);
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(3000);
			InputStream inStream = conn.getInputStream();
			while ((len = inStream.read(data)) != -1) {
				outStream.write(data, 0, len);
			}
			inStream.close();
			return outStream.toByteArray();
		}


	}
	//结束当前Activity
	public void back (View view)
	{
		//二维码扫描界面结束
		ChargeListActivity.this.finish();
	}
}


