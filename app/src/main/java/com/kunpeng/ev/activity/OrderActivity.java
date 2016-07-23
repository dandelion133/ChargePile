package com.kunpeng.ev.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kunpeng.ev.R;
import com.kunpeng.ev.ui.TitleView;
import com.kunpeng.ev.utils.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by QHF on 2016/7/21.
 */
public class OrderActivity extends Activity {

    private static final String TAG = "OrderActivity";
    private ListView mListOrder;
    private SharedPreferences mSpUser;
    private String mUserAccount;
    private JSONArray mHistroyOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        initView();

        init();

    }



    private void initView() {
        TitleView title = (TitleView) findViewById(R.id.order_title);
        title.setBackIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                OrderActivity.this.finish();
            }
        });
        mListOrder = (ListView) findViewById(R.id.list_order);


        mSpUser = getSharedPreferences("User",MODE_PRIVATE);
        mUserAccount = mSpUser.getString("userAccount", "");
    }
    private void init() {


        try {
            mHistroyOrder = getOrder(mUserAccount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mListOrder.setAdapter(new OrderAdapter());




    }
    //请求历史订单
    private JSONArray getOrder(String phoneNumber)  throws Exception  {
        //使用Map封装请求参数 采用post方法
        Map<String,String> map=new HashMap<>();
        map.put("phoneNumber", phoneNumber);
        String url= HttpUtil.OLD_URL +"getOrderList";
        Log.e(TAG,url);
        return new JSONArray(HttpUtil.postRequest(url, map));
    }

    private class ViewHoler {
        public TextView orderDate;
        public TextView orderNumber;
        public TextView orderStatus;
        //location  price  amount
        public TextView location;
        public TextView price;
        public TextView amount;
    }

    private class OrderAdapter extends BaseAdapter {

        @Override
        public int getCount() {
          //  Log.e(TAG,mHistroyOrder.length()+"");
            return mHistroyOrder.length();
        }

        @Override
        public Object getItem(int position) {

            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

           // View view =
            ViewHoler holder = null;
            if(convertView == null) {
                holder = new ViewHoler();
                convertView = View.inflate(OrderActivity.this,R.layout.item_order,null);
                holder.orderDate = (TextView) convertView.findViewById(R.id.order_date);
                holder.orderNumber = (TextView) convertView.findViewById(R.id.ordernumber);
                holder.orderStatus = (TextView) convertView.findViewById(R.id.status);
                holder.location = (TextView) convertView.findViewById(R.id.location);
                holder.price = (TextView) convertView.findViewById(R.id.price);
                holder.amount = (TextView) convertView.findViewById(R.id.amount);
                convertView.setTag(holder);
            } else {
                holder = (ViewHoler) convertView.getTag();
            }

            try {
                JSONObject jsonObject = mHistroyOrder.getJSONObject(position);
                holder.orderDate.setText("充电日期:"+jsonObject.getString("start_time"));
                holder.orderNumber.setText("订单编号:"+jsonObject.getString("orderid"));
                holder.orderStatus.setText("订单状态:"+jsonObject.getString("state"));
                holder.location.setText("充电时长:"+jsonObject.getString("duration"));
                holder.price.setText("订单金额:"+jsonObject.getString("ecost") + "(易充币)");
                holder.amount.setText("充电电量:"+jsonObject.getString("amount") + "Kw.h");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }


}
