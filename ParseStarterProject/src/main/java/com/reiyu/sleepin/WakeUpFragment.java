package com.reiyu.sleepin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Satomi on 1/3/16.
 */
public class WakeUpFragment extends AppCompatActivity {
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_wake_up);
        Button button = (Button) findViewById(R.id.save_record);

        DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        date = year + "/" + (month + 1) + "/" + day;

        TimePicker tp1 = (TimePicker) findViewById(R.id.go_to_bed);
        tp1.setCurrentHour(23);
        tp1.setCurrentMinute(00);
        tp1.setIs24HourView(true);

        TimePicker tp2 = (TimePicker) findViewById(R.id.wake_up);
        tp2.setIs24HourView(true);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePicker tp1 = (TimePicker) findViewById(R.id.go_to_bed);
                int hour = tp1.getCurrentHour();
                int minute = tp1.getCurrentMinute();
                String go_to_bed_time = hour + ":" + minute;

                TimePicker tp2 = (TimePicker) findViewById(R.id.wake_up);
                hour = tp2.getCurrentHour();
                minute = tp2.getCurrentMinute();
                String wake_up_time = hour + ":" + minute;

                EditText memoText = (EditText) findViewById(R.id.memo);
                String memo = memoText.getText().toString();

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WakeUpFragment.this);
                String username = sp.getString("@string/username", null);


                if (username != null) {
                    ParseObject testObject = new ParseObject("SleepRecord");
                    testObject.put("date", date);
                    testObject.put("go_to_bed", go_to_bed_time);
                    testObject.put("wake_up", wake_up_time);
                    testObject.put("memo", memo);
                    testObject.put("username", username);
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
                    Log.e("Sleep Record", "username is null");
                    Toast.makeText(WakeUpFragment.this, "User info was empty. Please Sign in again.", Toast.LENGTH_SHORT);
                    startActivity(new Intent(WakeUpFragment.this, SignInFragment.class));
                }
            }
        });
    }

    private void wakeUp(String date) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putString("@string/record_updated", date).commit();
        sp.edit().putInt("@string/healthy_score", 100).commit();

        Toast.makeText(WakeUpFragment.this, "Sleep Record is successfully saved.", Toast.LENGTH_SHORT);

        startActivity(new Intent(WakeUpFragment.this, MainActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sign_out) {
            signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        ParseUser.logOut();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putBoolean("@string/signed_in", false).commit();
        sp.edit().putString("@string/username", null).commit();
        sp.edit().putString("@string/email", null).commit();
        startActivity(new Intent(WakeUpFragment.this, SignInFragment.class));
    }

    private void getAveScore() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("SleepinessRecord");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WakeUpFragment.this);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String yesterday = year + "/" + (month + 1) + "/" + day;

        query.whereEqualTo("username", sp.getString("@string/username", null));
        query.whereEqualTo("date", yesterday);

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    if (scoreList.size() > 0) {
                        int sum = 0;
                        for (ParseObject score : scoreList) {
                            sum += score.getInt("score");
                        }
                        int ave = sum / scoreList.size();
                        storeAveScore(ave);
                    } else {
                        Log.e("Average Score", "data was empty");
                    }
                } else {
                    Log.d("Average Score", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void storeAveScore(int ave) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WakeUpFragment.this);
        int count = sp.getInt("@string/count", 0);
        int count_neg = sp.getInt("@string/count_neg", 0);

        if (ave > 60) {
            count_neg = 0;
            count += 1;
        } else if (ave > 30) {
            count = 0;
            count_neg += 1;
        } else {
            count = 0;
            count_neg += 2;
        }
        updateFlower(count, count_neg);
        sp.edit().putInt("@string/count", count).commit();
        sp.edit().putInt("@string/count_neg", count_neg).commit();
    }

    private void updateFlower(int count, int count_neg) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WakeUpFragment.this);

        boolean hasClover2 = sp.getBoolean("@string/clover2", false);
        boolean hasButterfly2 = sp.getBoolean("@string/butterfly2", false);
        boolean hasClover = sp.getBoolean("@string/clover", false);
        boolean hasLadybug = sp.getBoolean("@string/ladybug", false);
        boolean hasButterfly = sp.getBoolean("@string/clover2", false);
        boolean hasLeaf = sp.getBoolean("@string/leaf", false);
        boolean hasPot = sp.getBoolean("@string/pot", false);

        if (hasClover2) {

        } else if (hasButterfly2) {
            flower.setImageResource(R.drawable.happy_u_l_b_t_c_a);
        } else if (hasClover) {
            flower.setImageResource(R.drawable.happy_u_l_b_t_c);
        } else if (hasLadybug) {
            flower.setImageResource(R.drawable.happy_u_l_b_t);
        } else if (hasButterfly) {
            flower.setImageResource(R.drawable.happy_u_l_b);
        } else if (hasLeaf) {
            flower.setImageResource(R.drawable.happy_u_l);
        } else if (hasPot) {
            flower.setImageResource(R.drawable.happy_u);
        } else {
            flower.setImageResource(R.drawable.happy);
        }
    }
}