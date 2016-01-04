/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
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

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ParseAnalytics.trackAppOpenedInBackground(getIntent());

    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
    if (!(sp.getBoolean("@string/signed_in", false))) {
      startActivity(new Intent(MainActivity.this, SignInFragment.class));
    }

    Calendar cal = Calendar.getInstance();
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH);
    int day = cal.get(Calendar.DAY_OF_MONTH);
    String date = year + "/" + (month + 1) + "/" + day;

    if ((sp.getString("@string/record_updated", null) == null)||(!(sp.getString("@string/record_updated", null).equals(date)))) {
      Log.e("RECORD_UPDATED", String.valueOf(date) + ":" +
              " data not yet recorded");
      startActivity(new Intent(MainActivity.this, WakeUpFragment.class));
    }
    setContentView(R.layout.activity_main);

    TextView textView = (TextView) findViewById(R.id.welcome_message);
    String msg = sp.getString("@string/name", null) + "'s Flower";
    textView.setText(msg);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        Log.e("Floating Action Button", "clicked");
        startActivity(new Intent(MainActivity.this, ReflectFragment.class));
      }
    });
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
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
