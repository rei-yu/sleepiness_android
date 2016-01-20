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
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    String date;
    int flower_num;
    ArrayList<String> memberList;
    int current_score;
    String current_username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        if (!(sp.getBoolean("@string/signed_in", false))) {
            Log.e("Main Activity", "user null");
            startActivity(new Intent(MainActivity.this, SignInFragment.class));
        } else {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            date = year + "/" + (month + 1) + "/" + day;

            // TODO: should consider user change (fundamentally should judge from get response)
            if ((sp.getString("@string/record_updated", null) == null) || (!(sp.getString("@string/record_updated", null).equals(date)))) {
                Log.e("RECORD_UPDATED", String.valueOf(date) + ":" +
                        " data not yet recorded");
                startActivity(new Intent(MainActivity.this, WakeUpFragment.class));
            } else {
                setContentView(R.layout.activity_main);

                String msg = sp.getString("@string/username", null) + "'s Flower";
                setTitle(msg);

                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        String msg = ReflectFragment.getSession();
                        int session_num = ReflectFragment.getSessionNum();

                        if (session_num == 0) {
                            Toast.makeText(MainActivity.this, "Session is 9:00 ~ 21:00\nPlease wait until 10:30 for reflection", Toast.LENGTH_LONG).show();
                        } else if ((sp.getString("@string/sleepiness_updated", null) == null) || (!(sp.getString("@string/sleepiness_updated", null).equals(date + session_num)))) {
                            startActivity(new Intent(MainActivity.this, ReflectFragment.class));
                        } else {
                            Toast.makeText(MainActivity.this, "You have already reflected\nsession " + msg + ".", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                showMainFlower();

                if (sp.getStringSet("@string/member_set", null) != null) {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
                    memberList = new ArrayList<>(sp.getStringSet("@string/member_set", null));
                    flower_num = 0;
                    getFlowerState();
                    Log.e("MainActivity onCreate", sp.getStringSet("@string/member_set", null).toString());
//                        }
//                    }).start();

                } else {
                    Log.e("MainActivity onCreate", "member is null");
                }
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

    @Override
    public void onBackPressed() {
        System.exit(0);
    }

    private void signOut() {
        ParseUser.logOut();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putBoolean("@string/signed_in", false).commit();
        sp.edit().putString("@string/username", null).commit();
        sp.edit().putString("@string/email", null).commit();
        startActivity(new Intent(MainActivity.this, SignInFragment.class));
    }

    public void getFlowerState() {
        ParseQuery<ParseObject> query_score = ParseQuery.getQuery("SleepinessRecord");

        if (memberList.size() > 0) {
            current_username = memberList.get(0);
            memberList.remove(0);
            query_score.whereEqualTo("username", current_username);
            query_score.orderByDescending("createdAt");

            query_score.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject sleepinessRecord, ParseException e) {
                    if (sleepinessRecord != null) {
                        int score = sleepinessRecord.getInt("score");
                        current_score = score;

                        Log.e("FlowerRecordScore " + current_username, String.valueOf(score));

                        ParseQuery<ParseObject> query = ParseQuery.getQuery("FlowerRecord");
                        query.whereEqualTo("username", current_username);
                        query.orderByDescending("createdAt");

                        query.getFirstInBackground(new GetCallback<ParseObject>() {
                            public void done(ParseObject flowerRecord, ParseException e) {
                                if (e == null) {
                                    boolean hasClover2 = flowerRecord.getBoolean("hasClover2");
                                    boolean hasButterfly2 = flowerRecord.getBoolean("hasButterfly2");
                                    boolean hasClover = flowerRecord.getBoolean("hasClover");
                                    boolean hasLadybug = flowerRecord.getBoolean("hasLadybug");
                                    boolean hasButterfly = flowerRecord.getBoolean("hasButterfly");
                                    boolean hasLeaf = flowerRecord.getBoolean("hasLeaf");
                                    boolean hasPot = flowerRecord.getBoolean("hasPot");

                                    Log.e("FlowerRecordState " + current_username, "success");
                                    ImageView flower;
                                    switch (flower_num) {
                                        case 3:
                                            flower = (ImageView) findViewById(R.id.flower4);
                                            showFlower(flower, current_score, hasClover2, hasButterfly2, hasClover, hasLadybug, hasButterfly, hasLeaf, hasPot);
                                            flower_num += 1;
                                            break;
                                        case 2:
                                            flower = (ImageView) findViewById(R.id.flower3);
                                            showFlower(flower, current_score, hasClover2, hasButterfly2, hasClover, hasLadybug, hasButterfly, hasLeaf, hasPot);
                                            flower_num += 1;
                                            break;
                                        case 1:
                                            flower = (ImageView) findViewById(R.id.flower2);
                                            showFlower(flower, current_score, hasClover2, hasButterfly2, hasClover, hasLadybug, hasButterfly, hasLeaf, hasPot);
                                            flower_num += 1;
                                            break;
                                        case 0:
                                            flower = (ImageView) findViewById(R.id.flower1);
                                            showFlower(flower, current_score, hasClover2, hasButterfly2, hasClover, hasLadybug, hasButterfly, hasLeaf, hasPot);
                                            flower_num += 1;
                                            break;
                                    }
                                } else {
                                    Log.e("FlowerRecordState " + current_username, "Error: " + e.getMessage());
                                }
                            }
                        });
                    } else {
                        Log.e("FlowerRecordScore " + current_username, "Error: " + e.getMessage());
                    }
                    getFlowerState();
                }
            });
        } else {
            Log.e("FlowerRecord", "no more member");
        }
    }

    private void showFlower(ImageView flower, int score, Boolean hasClover2, boolean hasButterfly2, boolean hasClover, boolean hasLadybug, boolean hasButterfly, boolean hasLeaf, boolean hasPot) {
        Log.e("showFlower", "called");

        if (score < 0) {
            Toast.makeText(MainActivity.this, "Could not load score", Toast.LENGTH_LONG).show();
        } else {
            if (score > 60) {
                if (hasClover2) {
                    flower.setImageResource(R.drawable.happy_u_l_b_t_c_a_y);
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
            } else if (score > 30) {
                if (hasButterfly) {
                    flower.setImageResource(R.drawable.nogood_u_l_b);
                } else if (hasLeaf) {
                    flower.setImageResource(R.drawable.nogood_u_l);
                } else if (hasPot) {
                    flower.setImageResource(R.drawable.nogood_u);
                } else {
                    flower.setImageResource(R.drawable.nogood);
                }
            } else {
                if (hasPot) {
                    flower.setImageResource(R.drawable.bad_u);
                } else {
                    flower.setImageResource(R.drawable.bad);
                }
            }
        }
    }

    private void showMainFlower() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        int score = sp.getInt("@string/healthy_score", -1);
        if (score < 0) {
            Toast.makeText(MainActivity.this, "Could not load score", Toast.LENGTH_LONG).show();
        } else {
            boolean hasClover2 = sp.getBoolean("@string/clover2", false);
            boolean hasButterfly2 = sp.getBoolean("@string/butterfly2", false);
            boolean hasClover = sp.getBoolean("@string/clover", false);
            boolean hasLadybug = sp.getBoolean("@string/ladybug", false);
            boolean hasButterfly = sp.getBoolean("@string/clover2", false);
            boolean hasLeaf = sp.getBoolean("@string/leaf", false);
            boolean hasPot = sp.getBoolean("@string/pot", false);

            ImageView flower = (ImageView) findViewById(R.id.flower);

            if (score > 60) {
                if (hasClover2) {
                    flower.setImageResource(R.drawable.happy_u_l_b_t_c_a_y);
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
            } else if (score > 30) {
                if (hasButterfly) {
                    flower.setImageResource(R.drawable.nogood_u_l_b);
                } else if (hasLeaf) {
                    flower.setImageResource(R.drawable.nogood_u_l);
                } else if (hasPot) {
                    flower.setImageResource(R.drawable.nogood_u);
                } else {
                    flower.setImageResource(R.drawable.nogood);
                }
            } else {
                if (hasPot) {
                    flower.setImageResource(R.drawable.bad_u);
                } else {
                    flower.setImageResource(R.drawable.bad);
                }
            }
        }
    }
}
