package com.kunpeng.ev.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.kunpeng.ev.R;




public class SettingItem extends RelativeLayout {

	private TextView tv_title;
	private Switch mSwitch;





	//自定义布局类  需要继承一个三个构造方法  一个都不能少
	/**
	 * 我们自定义的组合控件，它里面有两个TextView ，还有一个CheckBox,还有一个View
	 * @param context
	 * @return
	 */
	public void init(Context context) {
		//把一个布局文件setting_item 并且加载在MoreInfoView
		View.inflate(context, R.layout.item_setting,this);

		//View.inflate(context, R.layout.setting_click_item, this);
		tv_title = (TextView) this.findViewById(R.id.setting_title);
		mSwitch = (Switch) this.findViewById(R.id.switch_setting);

	}
	public SettingItem(Context context) {
		super(context);
		init(context);
	}

	public SettingItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public SettingItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.posView);

		String title = a.getString(R.styleable.posView_myTitle);

		tv_title.setText(title);

		a.recycle();
		
	}
	/*public void setSwitch(boolean checked) {
		mSwitch.setChecked(checked);
	}*/
	public boolean isChecked() {
		return mSwitch.isChecked();
	}
	public void setSwitchChecked(boolean checked) {
		mSwitch.setChecked(checked);

	}

	public void  setSwitchOnClickListener(OnClickListener listener) {
		mSwitch.setOnClickListener(listener);
	}

}
