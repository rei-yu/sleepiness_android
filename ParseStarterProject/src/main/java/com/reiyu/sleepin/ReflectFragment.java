package com.reiyu.sleepin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Satomi on 1/3/16.
 */
public class ReflectFragment extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_reflect);

        TextView textView = (TextView) findViewById(R.id.session);
        if (!(textView == null)) {
            setSession(textView);
        } else {
            Log.e("setSession", "textView is null");
        }
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
                break;
            case 10:
                if (min < 30) {
                    session = "9:00 ~ 10:30";
                } else {
                    session = "10:30 ~ 12:00";
                }
                break;
            case 11:
                session = "10:30 ~ 12:00";
                break;
            case 12:
                session = "12:00 ~ 13:30";
                break;
            case 13:
                if (min < 30) {
                    session = "12:00 ~ 13:30";
                } else {
                    session = "13:30 ~ 15:00";
                }
                break;
            case 14:
                session = "13:30 ~ 15:00";
                break;
            case 15:
                session = "15:00 ~ 16:30";
                break;
            case 16:
                if (min < 30) {
                    session = "15:00 ~ 16:30";
                } else {
                    session = "16:30 ~ 18:00";
                }
                break;
            case 17:
                session = "16:30 ~ 18:00";
                break;
            case 18:
                session = "18:00 ~ 19:30";
                break;
            case 19:
                if (min < 30) {
                    session = "18:00 ~ 19:30";
                } else {
                    session = "19:30 ~ 21:00";
                }
                break;
            case 20:
                session = "19:30 ~ 21:00";
                break;
            default:
                session = "Session is not held now";
                Toast.makeText(ReflectFragment.this, "Sessionは9:00~21:00です", Toast.LENGTH_SHORT);
                break;
        }
        return session;
    }
}
