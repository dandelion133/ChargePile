package com.kunpeng.ev.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

/*
 *扫描电桩信息之后 给服务器发送请求
 */
public class SetChargeStatus {

    //http://139.129.201.20:8080/SSHDemo2/setCPS?SectionID=11&DeviceID=CD201404241112&Status=busy
    //final private String UrlPath="http://139.129.201.20:8080/SSHDemo2/setCPS?SectionID=11&DeviceID=CD201404241112&Status=unlock";

    public  void setstatus(String UrlPath) throws Exception
    {
        byte[] data=readParse(UrlPath);
    }

    public byte[] readParse(String UrlPath) throws Exception
    {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        URL url = new URL(UrlPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        InputStream inStream = conn.getInputStream();
        while ((len = inStream.read(data)) != -1) {
            outStream.write(data, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }
}
