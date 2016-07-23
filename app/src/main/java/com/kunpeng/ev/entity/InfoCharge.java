package com.kunpeng.ev.entity;

import java.io.Serializable;

//充电桩的数据类型
public class InfoCharge implements Serializable
{
	private static final long serialVersionUID = 001;

	private String deviceid;
	private double latitude;
	private double longitude;
	private String address;
	private int idlenumber;

	//定义该类的对象 区域id, 设备id, 设备纬度, 设备精度, 设备地址, 设备状态
	public InfoCharge(String deviceid,double latitude,
					  double longitude, String address,int idlenumber)
	{

		this.deviceid=deviceid;
		this.latitude = latitude;
		this.longitude = longitude;
		this.address=address;
		this.idlenumber=idlenumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}




	public String getDeviceid() {
		return deviceid;
	}

	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public int getIdlenumber() {
		return idlenumber;
	}

	public void setIdlenumber(int idlenumber) {
		this.idlenumber = idlenumber;
	}

}
