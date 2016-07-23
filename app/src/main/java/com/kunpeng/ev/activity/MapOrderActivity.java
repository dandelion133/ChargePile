package com.kunpeng.ev.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kunpeng.ev.R;
import com.kunpeng.ev.utils.DateTimePickDialogUtil;

import java.util.Calendar;


public class MapOrderActivity extends Activity {
	private Button button;
	private Button buttonpay;
	private AlertDialog pay;
	private AlertDialog listdialog;
	private EditText startDateTime;
	private TextView myTextView;
	private TextView feeTextView;
	private TextView timeTextView;
	private TextView orderlenth;
	//区域id和地址
	private TextView sectionidTextView;
	private TextView adrressTextView;

	private RelativeLayout relala;
	private String[] times = new String[] { "1小时", "2小时","3小时" , "4小时","5小时" , "6小时" };

	Calendar c = Calendar.getInstance();
	int curYear = c.get(Calendar.YEAR);
	int curMonth = c.get(Calendar.MONTH)+1;
	int curday = c.get(Calendar.DAY_OF_MONTH);
	int curHours = c.get(Calendar.HOUR_OF_DAY);
	int curMinutes = c.get(Calendar.MINUTE);

	private String curYear1 = String.valueOf(curYear);
	private String curMonth1= String.valueOf(curMonth);
	private String curday1 = String.valueOf(curday);
	private String curHours1 = String.valueOf(curHours);
	private String curMinutes1;
	{
		if(curMinutes<10)
			curMinutes1= "0"+String.valueOf(curMinutes);
		else
			curMinutes1= String.valueOf(curMinutes);}
	private String initStartDateTime=curYear1+ "年"
			+ curMonth1 + "月"
			+ curday1 + "日 "
			+ curHours1 + ":"
			+ curMinutes1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.map_order1);

		relala = (RelativeLayout)findViewById(R.id.timechoose);
		timeTextView = (TextView)findViewById(R.id.tv);
		orderlenth = (TextView)findViewById(R.id.textview22);
		button = (Button)findViewById(R.id.choose1);
		buttonpay = (Button)findViewById(R.id.ordernow);

		startDateTime = (EditText) findViewById(R.id.inputDate);
		startDateTime.setText(initStartDateTime);
		myTextView = (TextView)findViewById(R.id.spinnertime);
		feeTextView = (TextView)findViewById(R.id.textview33);

		sectionidTextView= (TextView)findViewById(R.id.textview_sectionid);
		adrressTextView=(TextView)findViewById(R.id.textview_address);

		Intent intent = getIntent();
		String deviceid = intent.getStringExtra("id");
		String address = intent.getStringExtra("address");

		sectionidTextView.setText("电桩编号："+deviceid);
		adrressTextView.setText("电桩地址："+address);

		relala.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				listdialog = new AlertDialog.Builder(MapOrderActivity.this)
						.setTitle("请选择时间")
						.setIcon(R.drawable.time)
						.setItems(times, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {

								timeTextView.setText( times[which]);
								orderlenth.setText(times[which]);
								switch(which)
								{
									case 0:
										feeTextView.setText("6元");
										break;
									case 1:
										feeTextView.setText("12元");
										break;
									case 2:
										feeTextView.setText("18元");
										break;
									case 3:
										feeTextView.setText("24元");
										break;
									case 4:
										feeTextView.setText("30元");
										break;
									case 5:
										feeTextView.setText("36元");
										break;
									default:
										System.out.println("default");
										break;
								}
							}})
						.show();
			}});
		buttonpay.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				pay = new AlertDialog.Builder(MapOrderActivity.this)
						.setTitle("支付确认")
						//              .setIcon(R.drawable.money)
						.setMessage("您的预约费用为"+feeTextView.getText()+",确定支付？")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {

							}
						})
						.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {

							}
						}).show();
			}});

		//开始时间选择
		button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {

				DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
						MapOrderActivity.this, initStartDateTime);
				dateTimePicKDialog.dateTimePicKDialog(startDateTime);

			}});


	}
	//结束当前Activity
	public void back (View view)
	{
		//二维码扫描界面结束
		MapOrderActivity.this.finish();
	}


}