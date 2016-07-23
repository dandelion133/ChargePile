package com.kunpeng.ev.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.kunpeng.ev.R;
import com.kunpeng.ev.utils.PayResult;
import com.kunpeng.ev.utils.SignUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class PayDemoActivity extends FragmentActivity {

	// 商户PID
	public static final String PARTNER = "2088121050878332";
	// 商户收款账号
	public static final String SELLER = "xxshuai@companiontek.com";
	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBANzvchQvBigKKliIRD2wr049RtjfnQlhajqm5zSbuov3D5KYmXbQg5MKtW+DGJRFZGPlnN4zUooIAy/OCVeLW6ZPzAF3aZ+rHI/BBIfQivZAOKqj3GxNNl1iQRunsH/ivCfSShRTLQ8i+wud+MeTKEQZrtr9bgvte9Op4a8XP0khAgMBAAECgYEAqGEIxV8tRviwWzbHI2jzwiIrS3/os8x9ZGPtU4fX/XH2eVg5pXa2wCrqKil0YxQbtZK6IhJlNnNy0k0pi4HW/xcprYqt9cnnhksPLlozdll/+pnyT9g9pU6g9ehLY10V7qQUkOFb0NuEx8jc05pVlsNNl1DLt4F6fp3qjtHjaEECQQD8n3Dw9bwo9t945ah9ti4mgcy5buRIKaUf5tqQmYg6YkpkrWDU0qXvw9Opx0qwTlrkZ4/utqQNPRKVq9+d5xppAkEA3+OPNMVuk9QPWGLjzm1D/MuBpzfvjPahp5VndTd+wviUnG/f0uYT79Afwi+7iJIjl6y7ixr7K24bi71RaBSx+QJBALpGw1wahy1ZpL0b81sHLA6e5ZvShigj9SuJ6Z54Z7hNY/fK1Lz5uvwKVRg66PsKPAFxz/NA38UXRI3y14gysgkCQEjDGQCKhaRvj8km4mhtJGJc9TcWO7kxUlo3eRckQQM2bH3m8fQFqEcQ+bFPeAArlnutvIm57BUyRKuGjJVp3GkCQQDe47i3CF6vE4NtFFj7qLpc8Fhjvo8Ub28AOUKr8nnzJeuqKkdUJvq3dZ2wb9nZkJiIFlkOXwq0m5E5f+Y//zmH";
	// 支付宝公钥
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
	private static final int SDK_PAY_FLAG = 1;

	private static final int SDK_CHECK_FLAG = 2;
	
	private EditText moneynumber;
    private String money;
    private Handler handler;
    private boolean netflag=false;
	
	
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);

				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();

				String resultStatus = payResult.getResultStatus();

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					Toast.makeText(PayDemoActivity.this, "支付成功",
							Toast.LENGTH_SHORT).show();
					//支付完成后，通知服务器
					afterpayThread at = new afterpayThread();
					at.start();
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(PayDemoActivity.this, "支付结果确认中",
								Toast.LENGTH_SHORT).show();

					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(PayDemoActivity.this, "支付失败",
								Toast.LENGTH_SHORT).show();

					}
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				Toast.makeText(PayDemoActivity.this, "检查结果为：" + msg.obj,
						Toast.LENGTH_SHORT).show();
				break;
			}
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_main);
		  moneynumber = (EditText)findViewById(R.id.writemoney);
		  
		 /*
		 intent.putExtra("cost",tvcost.getText());
			intent.putExtra("chargeamount",tvcdamount.getText());
			intent.putExtra("lengthoftieme",tvcdtime.getText());
			intent.putExtra("deviceid",tvcdnumber.getText());
			*/
		 
		 
		 ;
	
	}
	 class handler extends Handler                                                                                                                                                          
	    { 
			 @Override  
		        public void handleMessage(Message msg) {  
		            // TODO Auto-generated method stub  
		            super.handleMessage(msg);  
		            String result = (String)msg.obj;
		            if(msg.what==1){
		            	Toast.makeText(PayDemoActivity.this,"服务器异常", Toast.LENGTH_SHORT).show();
		            	
		            }
		            else{
		            	if(result.equals("updata Success")){
		            		Toast.makeText(PayDemoActivity.this,"充值成功", Toast.LENGTH_SHORT).show();
		            	}
		            	else{
		            		Toast.makeText(PayDemoActivity.this,"充值失败", Toast.LENGTH_SHORT).show();
		            	}
		            }
			 }
	    }
//支付完成后向服务器发送数据
	class afterpayThread  extends Thread{
    	@Override
    	public void run(){
    		SharedPreferences share=getSharedPreferences("username",Activity.MODE_PRIVATE);
    		String mynumber = share.getString("USER_NAME", "");
    		String returninfo = "";
    		 Message message= handler.obtainMessage(); 
    		//创建HttpClient
    		HttpClient httpClient = new DefaultHttpClient();
    		//创建代表请求的对象,参数是访问的服务器地址
    		//http://www.baidu.com
    		//HttpGet httpGet = new HttpGet("http://139.129.201.20/SSHDemo2/SetUserPassword?number="+mynumber+"&password="+mtext1);
   		HttpGet httpGet = new HttpGet("http://139.129.201.20/NewChargePile/setUserAccount?phoneNumber="+mynumber+"&balance="+money+"&chargeMethod=支付宝支付");
    		//执行请求，获取服务器发还的相应对象
    		try {
				HttpResponse resp = httpClient.execute(httpGet);
				//检查相应的状态是否正常。检查状态码的值是否等于200
				int code = resp.getStatusLine().getStatusCode();
				if(code == 200){
					//从相应对象当中取出数据
					HttpEntity entity = resp.getEntity();
					InputStream in = entity.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					String line = reader.readLine();
					String json=line;    
				      //  cost=jsonObject2.getString("consumeMoney");
				        JSONObject jsonObject =new JSONObject(json); 
				        returninfo=jsonObject.getString("info");
				        message.obj=returninfo;
				        handler.sendMessage(message);
					Log.d("HTTP", "从服务器取得的数据位：" + line);
				}
				else{
					message.what=1;
					handler.sendMessage(message);
				}
			} catch (Exception e) {
				e.printStackTrace();
				message.what=1;
				handler.sendMessage(message);
			}
    	}
    } 
	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void pay(View v) {
		    money = moneynumber.getText().toString();
		    handler = new handler();
	        netflag=isNetworkConnected(PayDemoActivity.this);
			if(netflag==false){
				Toast.makeText(getApplication(),"网络异常，请连接网络", Toast.LENGTH_SHORT).show();
			}else{
				if(money.equals("")||money.equals("0")){
					Toast.makeText(getApplication(),"金额输入不正确", Toast.LENGTH_SHORT).show();
				}
				
				else{
					// 订单
					String orderInfo = getOrderInfo("电动汽车充值费用", "充值费用"+money+"元",money);

					// 对订单做RSA 签名
					String sign = sign(orderInfo);
					try {
						// 仅需对sign 做URL编码
						sign = URLEncoder.encode(sign, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}

					// 完整的符合支付宝参数规范的订单信息
					final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
							+ getSignType();

					Runnable payRunnable = new Runnable() {

						@Override
						public void run() {
							// 构造PayTask 对象
							PayTask alipay = new PayTask(PayDemoActivity.this);
							// 调用支付接口，获取支付结果
							String result = alipay.pay(payInfo);

							Message msg = new Message();
							msg.what = SDK_PAY_FLAG;
							msg.obj = result;
							mHandler.sendMessage(msg);
						}
					};

					// 必须异步调用
					Thread payThread = new Thread(payRunnable);
					payThread.start();
				}
			}
		
		
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
	/**
	 * check whether the device has authentication alipay account.
	 * 查询终端设备是否存在支付宝认证账户
	 * 
	 */
	/*
	public void check(View v) {
		Runnable checkRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask payTask = new PayTask(PayDemoActivity.this);
				// 调用查询接口，获取查询结果
				boolean isExist = payTask.checkAccountIfExist();

				Message msg = new Message();
				msg.what = SDK_CHECK_FLAG;
				msg.obj = isExist;
				mHandler.sendMessage(msg);
			}
		};

		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();

	}
*/
	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(this);
		String version = payTask.getVersion();
		Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
	}
	//返回
	public void payback(View view) {
		PayDemoActivity.this.finish();
	}

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	public String getOrderInfo(String subject, String body, String price) {

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm"
				+ "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	public String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}

}
