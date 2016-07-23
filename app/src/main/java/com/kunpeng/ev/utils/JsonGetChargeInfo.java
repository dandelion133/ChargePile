package com.kunpeng.ev.utils;

import android.util.Log;

import com.kunpeng.ev.entity.InfoCharge;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/*
 *以JSON的格式与服务器通信，读取充电桩相关信息，并返回
 */
public class JsonGetChargeInfo {

    public static List<InfoCharge> listinfo= new ArrayList<InfoCharge>();
    final private String UrlPath="http://139.129.201.20/NewChargePile/getCabinetInfo";
    public List<InfoCharge> getchargeinfo() throws Exception
    {
        List<InfoCharge> listinfo=new ArrayList<InfoCharge>();
        byte[] data=readParse(UrlPath);
        JSONArray array = new JSONArray(new String(data));
        for (int i = 0; i < array.length(); i++)
        {
            JSONObject item = array.getJSONObject(i);
            //String num = item.getString("Num");
            //String sectionid = item.getString("SectionID");
            String deviceid = item.getString("cabinetId");
            double longitude = item.getDouble("longitude");
            double latitude  = item.getDouble("latitude");
            String address = item.getString("address");
            int idlenumber = item.getInt("idleNumber");

            Log.e("JsonTest",deviceid);
            Log.e("JsonTest",address);
            Log.e("JsonTest","rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr"+i);

            listinfo.add(new InfoCharge(deviceid,latitude,longitude,address,idlenumber));
        }
        return listinfo;
    }

    public byte[] readParse(String UrlPath) throws Exception
    {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        URL url = new URL(UrlPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.connect();
        // conn.setConnectTimeout(3000);
        //conn.setReadTimeout(3000);
        //判断url连接是否正确
        InputStream inStream = conn.getInputStream();
        while ((len = inStream.read(data)) != -1) {
            outStream.write(data, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }

}
