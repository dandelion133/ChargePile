package com.kunpeng.ev.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.kunpeng.ev.R;
import com.kunpeng.ev.ui.TitleView;

/**
 * Created by QHF on 2016/7/21.
 *
 * 我的预约
 */
public class AppointmentActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        TitleView title = (TitleView) findViewById(R.id.appointment_title);
        title.setBackIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppointmentActivity.this.finish();
            }
        });
    }
}
