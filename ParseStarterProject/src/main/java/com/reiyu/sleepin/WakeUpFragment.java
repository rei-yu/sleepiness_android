package com.reiyu.sleepin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

/**
 * Created by Satomi on 1/3/16.
 */
public class WakeUpFragment extends FragmentActivity {
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_wake_up);
        Button button = (Button) findViewById(R.id.save_record);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();
                date = year + "/" + (month + 1) + "/" + day;

                TimePicker tp1 = (TimePicker) findViewById(R.id.go_to_bed);
                TimePicker tp2 = (TimePicker) findViewById(R.id.wake_up);
                tp1.setIs24HourView(true);
                tp1.setIs24HourView(true);
                EditText memoText = (EditText) findViewById(R.id.memo);

                int hour = tp1.getCurrentHour();
                int minute = tp1.getCurrentMinute();
                String go_to_bed_time = hour + ":" + minute;
                hour = tp2.getCurrentHour();
                minute = tp2.getCurrentMinute();
                String wake_up_time = hour + ":" + minute;
                String memo = memoText.getText().toString();

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WakeUpFragment.this);
                String email = sp.getString("@string/email", null);

                if (email != null) {
                    ParseObject testObject = new ParseObject("SleepRecord");
                    testObject.put("date", date);
                    testObject.put("go_to_bed", go_to_bed_time);
                    testObject.put("wake_up", wake_up_time);
                    testObject.put("memo", memo);
                    testObject.put("email", email);
                    testObject.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.e("Sleep Record", "Successfully saved");
                                wakeUp(date);
                            } else {
                                // Sign up didn't succeed. Look at the ParseException
                                // to figure out what went wrong
                                Log.e("Sleep Record", "Error", e);
                            }
                        }
                    });
                } else {
                    Log.e("Sleep Record", "email is null");
                    Toast.makeText(WakeUpFragment.this, "User info was empty. Please Sign in again.", Toast.LENGTH_SHORT);
                    startActivity(new Intent(WakeUpFragment.this, SignInFragment.class));
                }
            }
        });
    }

    private void wakeUp(String date) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putString("@string/record_updated", date).commit();
        Toast.makeText(WakeUpFragment.this, "Sleep Record is successfully saved.", Toast.LENGTH_SHORT);

        startActivity(new Intent(WakeUpFragment.this, MainActivity.class));
    }
}