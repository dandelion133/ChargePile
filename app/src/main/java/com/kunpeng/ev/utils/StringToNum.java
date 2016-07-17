package com.kunpeng.ev.utils;

import java.text.DecimalFormat;

public class StringToNum {
	
	private DecimalFormat mFormat;

	public StringToNum(int decimalDigit){
		String digitStr = "";
		for(int i=0;i<decimalDigit;i++){
			digitStr = digitStr+"0";
		}
		mFormat = new DecimalFormat("###,###,##0."+digitStr);
	
	}
	public static boolean isFloat(String num) {
		try {
			Float.valueOf(num);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	//看名字
	public String StringToString(String inputString){
		float balanceNum = Float.parseFloat(inputString);
		String twoFloatStr = null;
		twoFloatStr = mFormat.format(balanceNum);
		return twoFloatStr;
	}
	
	//
	public String FloatToString(float inputFloat){
		String twoFloatStr = null;
		twoFloatStr = mFormat.format(inputFloat);
		return twoFloatStr;
	}
	
	//
	public float FloatToFloat(float inputFloat){
		String twoFloatStr = null;
		twoFloatStr = mFormat.format(inputFloat);
		return Float.parseFloat(twoFloatStr);
		
	}
	
	//看名字
	public float StringToFloat(String inputString){
		float balanceNum = Float.parseFloat(inputString);
		String twoFloatStr = null;
		twoFloatStr = mFormat.format(balanceNum);
		return Float.parseFloat(twoFloatStr);
	}
}
