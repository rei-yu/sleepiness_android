package com.reiyu.sleepin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.Calendar;

/**
 * Created by Satomi on 1/3/16.
 */
public class ReflectFragment extends FragmentActivity {
    String date = "0000/00/00";
    int session_num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_reflect);

        TextView textView = (TextView) findViewById(R.id.session);
        setSession(textView);

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        date = year + "/" + (month + 1) + "/" + day;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ReflectFragment.this);
        String sleepiness_updated = sp.getString("@string/sleepiness_updated", "0000/00/00");
        if (sleepiness_updated.equals(date + session_num)) {
            Log.e("Sleepiness Record", "already saved for this session");
            Toast.makeText(ReflectFragment.this, "already saved for this session", Toast.LENGTH_SHORT);

            startActivity(new Intent(ReflectFragment.this, MainActivity.class));
        }

        Button bt1 = (Button) findViewById(R.id.sleepiness1);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSleepinessRecord(1);
            }
        });

        Button bt2 = (Button) findViewById(R.id.sleepiness2);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSleepinessRecord(2);
            }
        });
        Button bt3 = (Button) findViewById(R.id.sleepiness3);
        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSleepinessRecord(3);
            }
        });
        Button bt4 = (Button) findViewById(R.id.sleepiness4);
        bt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSleepinessRecord(4);
            }
        });
        Button bt5 = (Button) findViewById(R.id.sleepiness5);
        bt5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSleepinessRecord(5);
            }
        });

    }

    private void setSession(TextView textView) {
        String session = getSession();
        if (session.equals("Session is not held now")) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            String msg = "Session" + session + "の振り返り";
            textView.setText(msg);
        }
    }

    private String getSession() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        String session;

        switch (hour) {
            case 9:
                session = "9:00 ~ 10:30";
                session_num = 1;
                break;
            case 10:
                if (min < 30) {
                    session = "9:00 ~ 10:30";
                    session_num = 1;
                } else {
                    session = "10:30 ~ 12:00";
                    session_num = 2;
                }
                break;
            case 11:
                session = "10:30 ~ 12:00";
                session_num = 2;
                break;
            case 12:
                session = "12:00 ~ 13:30";
                session_num = 3;
                break;
            case 13:
                if (min < 30) {
                    session = "12:00 ~ 13:30";
                    session_num = 3;
                } else {
                    session = "13:30 ~ 15:00";
                    session_num = 4;
                }
                break;
            case 14:
                session = "13:30 ~ 15:00";
                session_num = 4;
                break;
            case 15:
                session = "15:00 ~ 16:30";
                session_num = 5;
                break;
            case 16:
                if (min < 30) {
                    session = "15:00 ~ 16:30";
                    session_num = 5;
                } else {
                    session = "16:30 ~ 18:00";
                    session_num = 6;
                }
                break;
            case 17:
                session = "16:30 ~ 18:00";
                session_num = 6;
                break;
            case 18:
                session = "18:00 ~ 19:30";
                session_num = 7;
                break;
            case 19:
                if (min < 30) {
                    session = "18:00 ~ 19:30";
                    session_num = 7;
                } else {
                    session = "19:30 ~ 21:00";
                    session_num = 8;
                }
                break;
            case 20:
                session = "19:30 ~ 21:00";
                session_num = 8;
                break;
            default:
                session = "Session is not held now";
                Toast.makeText(ReflectFragment.this, "Sessionは9:00~21:00です", Toast.LENGTH_SHORT);
                break;
        }
        return session;
    }

    private void sendSleepinessRecord(int sleepiness) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ReflectFragment.this);
        String email = sp.getString("@string/email", null);

        if (email != null) {
            ParseObject testObject = new ParseObject("SleepinessRecord");
            testObject.put("date", date);
            testObject.put("session", session_num);
            testObject.put("sleepiness", sleepiness);
            testObject.put("email", email);
            testObject.saveInBackground(new SaveCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        Log.e("Sleepiness Record", "Successfully saved");
                        finishReflection();
                    } else {
                        // Sign up didn't succeed. Look at the ParseException
                        // to figure out what went wrong
                        Log.e("Sleepiness Record", "Error", e);
                    }
                }
            });
        } else {
            invalidUserInfo();
        }
    }

    private void finishReflection() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putString("@string/sleepiness_updated", date + session_num).commit();
        Toast.makeText(ReflectFragment.this, "Successfully Saved.", Toast.LENGTH_SHORT);

        startActivity(new Intent(ReflectFragment.this, MainActivity.class));
    }

    private void invalidUserInfo() {
        Log.e("Sleep Record", "email is null");
        Toast.makeText(ReflectFragment.this, "User info was empty. Please Sign in again.", Toast.LENGTH_SHORT);
        
        startActivity(new Intent(ReflectFragment.this, SignInFragment.class));
    }
}
