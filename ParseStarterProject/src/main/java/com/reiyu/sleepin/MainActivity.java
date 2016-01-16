package com.reiyu.sleepin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

//        SessionReceiver.scheduleAlarms(this);
//
//        Toast.makeText(this, "alarm has set", Toast.LENGTH_LONG)
//                .show();
//        finish();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        if (!(sp.getBoolean("@string/signed_in", false))) {
            Log.e("Main Activity", "user null");
            startActivity(new Intent(MainActivity.this, SignInFragment.class));
        } else {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            String date = year + "/" + (month + 1) + "/" + day;

            // TODO: should consider user change (fundamentally should judge from get response)
            if ((sp.getString("@string/record_updated", null) == null) || (!(sp.getString("@string/record_updated", null).equals(date)))) {
                Log.e("RECORD_UPDATED", String.valueOf(date) + ":" +
                        " data not yet recorded");
                startActivity(new Intent(MainActivity.this, WakeUpFragment.class));
            } else {
                setContentView(R.layout.activity_main);

                TextView textView = (TextView) findViewById(R.id.welcome_message);
                String msg = sp.getString("@string/username", null) + "'s Flower";
                textView.setText(msg);

                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Log.e("Floating Action Button", "clicked");
                        startActivity(new Intent(MainActivity.this, ReflectFragment.class));
                    }
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
        startActivity(new Intent(MainActivity.this, SignInFragment.class));
    }
}
