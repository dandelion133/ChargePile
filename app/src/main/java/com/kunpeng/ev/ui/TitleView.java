package com.kunpeng.ev.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kunpeng.ev.R;


public class TitleView extends RelativeLayout {

	private TextView title;
	private RelativeLayout iconBack;
	private RelativeLayout iconMore;
	private ImageView ivMore;
	public void setBackIconOnClickListener(OnClickListener clickListener) {
		if(iconBack != null)
		 	iconBack.setOnClickListener(clickListener);
	}


	public void setMoreIconOnClickListener(OnClickListener clickListener) {
		if(iconMore != null)
			iconMore.setOnClickListener(clickListener);
	}
	public void setTitle(String text) {
		title.setText(text);
	}

	//自定义布局类  需要继承一个三个构造方法  一个都不能少
	/**
	 * 我们自定义的组合控件，它里面有两个TextView ，还有一个CheckBox,还有一个View
	 * @param context
	 * @return
	 */
	public void init(Context context) {
		//把一个布局文件setting_item 并且加载在MoreInfoView
		View.inflate(context, R.layout.item_title, this);
		iconMore = (RelativeLayout)findViewById(R.id.icon_more);
		ivMore = (ImageView)findViewById(R.id.img_more);
		//View.inflate(context, R.layout.setting_click_item, this);
		title = (TextView)findViewById(R.id.tv_title);

	}
	public TitleView(Context context) {
		super(context);
		init(context);
	}

	public TitleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public TitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.posView);

		String myTitle = a.getString(R.styleable.posView_myTitle);
		boolean backIconIsVisible = a.getBoolean(R.styleable.posView_backIconIsVisible, true);
		//Log.e("TitleView",backIconIsVisible+"");
		if (!backIconIsVisible) {

			ImageView img_back = (ImageView)findViewById(R.id.img_back);
			img_back.setImageResource(R.drawable.button_normal);
		} else {
			iconBack = (RelativeLayout)findViewById(R.id.icon_back);
		}


		int iconMoreId = a.getResourceId(R.styleable.posView_rightIcon, R.drawable.button_normal);
		ivMore.setImageResource(iconMoreId);
		title.setText(myTitle);

		a.recycle();
		
	}


}
