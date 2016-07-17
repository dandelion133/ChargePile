package com.kunpeng.ev.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kunpeng.ev.R;


public class MoreInfoView extends RelativeLayout {

	private TextView tv_title;
	private ImageView icon;

	private String desc_on;

	private String title;

	//自定义布局类  需要继承一个三个构造方法  一个都不能少
	/**
	 * 我们自定义的组合控件，它里面有两个TextView ，还有一个CheckBox,还有一个View
	 * @param context
	 * @return
	 */
	public void init(Context context) {
		//把一个布局文件setting_item 并且加载在MoreInfoView
		View.inflate(context,R.layout.item_more_info,this);

		//View.inflate(context, R.layout.setting_click_item, this);
		tv_title = (TextView) this.findViewById(R.id.more_info_title);
		icon = (ImageView) this.findViewById(R.id.more_info_icon);
		
	}
	public MoreInfoView(Context context) {
		super(context);
		init(context);
	}

	public MoreInfoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	public MoreInfoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.posView);

		title = a.getString(R.styleable.posView_myTitle);
		int imageSrc = a.getResourceId(R.styleable.posView_myIcon,R.drawable.ic_logo);

		//title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.pos", "myTitle");

		//int imageSrc = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/com.pos", "myIcon",R.drawable.icon_logo );

		tv_title.setText(title);
		icon.setImageResource(imageSrc);
		a.recycle();
		
	}

}
