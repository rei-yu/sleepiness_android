package com.reiyu.sleepin;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

/**
 * Created by Satomi on 1/3/16.
 */
public class WakeUpActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_wake_up);
        Button button = (Button) findViewById(R.id.save_record);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Save Button", "clicked");

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                String date = year + "/" + (month + 1) + "/" + day;
            }
        });
    }
}