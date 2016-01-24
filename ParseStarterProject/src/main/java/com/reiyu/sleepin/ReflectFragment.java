package com.reiyu.sleepin;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseACL;
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
public class ReflectFragment extends AppCompatActivity {
    String date = "0000/00/00";
    static private int session_num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(R.string.app_name);

        setContentView(R.layout.fragment_reflect);

        TextView textView = (TextView) findViewById(R.id.session);
        setSession(textView);

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        date = year + "/" + (month + 1) + "/" + day;

        getSession();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String sleepiness_updated = sp.getString("@string/sleepiness_updated", "0000/00/000");
        if (sleepiness_updated.equals(date + session_num)) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        Button bt1 = (Button) findViewById(R.id.sleepiness1);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("SleepinessRecord");
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                getSession();

                query.whereEqualTo("username", sp.getString("@string/username", null));
                query.whereEqualTo("session", session_num);
                query.whereEqualTo("date", date);

                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> sleepinessRecordList, ParseException e) {
                        if (e == null) {
                            if (sleepinessRecordList.size() < 1) {
                                setScore(35);
                                sendSleepinessRecord(1);
                            } else {
                                Log.e("SleepinessRecord", "already registered");
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                            startActivity(new Intent(getApplicationContext(), ReflectFragment.class));
                        }
                    }
                });
            }
        });
        Button bt2 = (Button) findViewById(R.id.sleepiness2);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("SleepinessRecord");
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                getSession();

                query.whereEqualTo("username", sp.getString("@string/username", null));
                query.whereEqualTo("session", session_num);
                query.whereEqualTo("date", date);

                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> sleepinessRecordList, ParseException e) {
                        if (e == null) {
                            if (sleepinessRecordList.size() < 1) {
                                setScore(5);
                                sendSleepinessRecord(2);
                            } else {
                                Log.e("SleepinessRecord", "already registered");
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                            startActivity(new Intent(getApplicationContext(), ReflectFragment.class));
                        }
                    }
                });
            }
        });
        Button bt3 = (Button) findViewById(R.id.sleepiness3);
        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("SleepinessRecord");
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                getSession();

                query.whereEqualTo("username", sp.getString("@string/username", null));
                query.whereEqualTo("session", session_num);
                query.whereEqualTo("date", date);

                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> sleepinessRecordList, ParseException e) {
                        if (e == null) {
                            if (sleepinessRecordList.size() < 1) {
                                setScore(-20);
                                sendSleepinessRecord(3);
                            } else {
                                Log.e("SleepinessRecord", "already registered");
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                            startActivity(new Intent(getApplicationContext(), ReflectFragment.class));
                        }
                    }
                });
            }
        });
        Button bt4 = (Button) findViewById(R.id.sleepiness4);
        bt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("SleepinessRecord");
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                getSession();

                query.whereEqualTo("username", sp.getString("@string/username", null));
                query.whereEqualTo("session", session_num);
                query.whereEqualTo("date", date);

                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> sleepinessRecordList, ParseException e) {
                        if (e == null) {
                            if (sleepinessRecordList.size() < 1) {
                                setScore(-30);
                                sendSleepinessRecord(4);
                            } else {
                                Log.e("SleepinessRecord", "already registered");
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                            startActivity(new Intent(getApplicationContext(), ReflectFragment.class));
                        }
                    }
                });
            }
        });
        Button bt5 = (Button) findViewById(R.id.sleepiness5);
        bt5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("SleepinessRecord");
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                getSession();

                query.whereEqualTo("username", sp.getString("@string/username", null));
                query.whereEqualTo("session", session_num);
                query.whereEqualTo("date", date);

                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> sleepinessRecordList, ParseException e) {
                        if (e == null) {
                            if (sleepinessRecordList.size() < 1) {
                                setScore(-45);
                                sendSleepinessRecord(5);
                            } else {
                                Log.e("SleepinessRecord", "already registered");
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                            startActivity(new Intent(getApplicationContext(), ReflectFragment.class));
                        }
                    }
                });
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
        sp.edit().putInt("@string/group_id", -1).commit();
        sp.edit().putString("@string/email", null).commit();
        startActivity(new Intent(getApplicationContext(), SignInFragment.class));
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

    public static String getSession() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -90);
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
                session_num = 0;
                break;
        }
        return session;
    }

    public static int getSessionNum() {
        return session_num;
    }

    private void sendSleepinessRecord(int sleepiness) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = sp.getString("@string/username", null);
        int score = sp.getInt("@string/healthy_score", 0);

        if (username != null) {
            ParseObject testObject = new ParseObject("SleepinessRecord");
            ParseACL postACL = new ParseACL(ParseUser.getCurrentUser());
            postACL.setPublicReadAccess(true);
            testObject.setACL(postACL);

            testObject.put("date", date);
            testObject.put("session", session_num);
            testObject.put("sleepiness", sleepiness);
            testObject.put("score", score);
            testObject.put("username", username);
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
        Toast.makeText(getApplicationContext(), "Successfully Saved.", Toast.LENGTH_SHORT);

        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    private void invalidUserInfo() {
        Log.e("Sleep Record", "username is null");
        Toast.makeText(getApplicationContext(), "User info was empty. Please Sign in again.", Toast.LENGTH_SHORT);

        startActivity(new Intent(getApplicationContext(), SignInFragment.class));
    }

    private void setScore(int diff) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int score = sp.getInt("@string/healthy_score", 0);
        score = score + diff;
        if (score < 0) {
            score = 0;
        } else if (score > 100) {
            score = 100;
        }
        sp.edit().putInt("@string/healthy_score", score).commit();
    }
}
