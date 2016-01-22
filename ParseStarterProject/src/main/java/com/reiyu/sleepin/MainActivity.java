package com.reiyu.sleepin;

import android.app.NotificationManager;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(R.string.app_name);

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
                showDetail();

//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
                getFlowerScore();
//                    }).start();
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
        } else if (id == R.id.action_group_sync) {
            groupSync();
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
        sp.edit().putInt("@string/group_id", -1).commit();
        sp.edit().putString("@string/email", null).commit();
        sp.edit().putStringSet("@string/member_set", null).commit();
        startActivity(new Intent(MainActivity.this, SignInFragment.class));
    }

    public void getFlowerScore() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        if (sp.getStringSet("@string/member_set", null) != null) {
            ParseQuery<ParseObject> query_score = ParseQuery.getQuery("SleepinessRecord");
            query_score.whereContainedIn("username", sp.getStringSet("@string/member_set", null));
            query_score.orderByAscending("createdAt");

            query_score.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> sleepinessRecordList, ParseException e) {
                    if (sleepinessRecordList.size() > 0) {
                        int flower_num = 0;
                        for (ParseObject sleepinessRecord : sleepinessRecordList) {
                            int score = sleepinessRecord.getInt("score");
                            String name = sleepinessRecord.getString("username");
                            Log.e("FlowerScore", "successfully get:" + sleepinessRecord.getString("username"));
                            Log.e("FlowerScore", "score:" + score);
                            ImageView flower;
                            TextView tag;
                            switch (flower_num) {
                                case 3:
                                    flower = (ImageView) findViewById(R.id.flower4);
                                    showFlower(flower, score, name);
                                    tag = (TextView) findViewById(R.id.flower4text);
                                    tag.setText(name + "'s");
                                    flower_num += 1;
                                    break;
                                case 2:
                                    flower = (ImageView) findViewById(R.id.flower3);
                                    showFlower(flower, score, name);
                                    tag = (TextView) findViewById(R.id.flower3text);
                                    tag.setText(name + "'s");
                                    flower_num += 1;
                                    break;
                                case 1:
                                    flower = (ImageView) findViewById(R.id.flower2);
                                    showFlower(flower, score, name);
                                    tag = (TextView) findViewById(R.id.flower2text);
                                    tag.setText(name + "'s");
                                    flower_num += 1;
                                    break;
                                case 0:
                                    flower = (ImageView) findViewById(R.id.flower1);
                                    showFlower(flower, score, name);
                                    tag = (TextView) findViewById(R.id.flower1text);
                                    tag.setText(name + "'s");
                                    flower_num += 1;
                                    break;
                                default:
                                    flower_num = 0;
                                    break;
                            }
                        }
                    } else {
                        Log.e("FlowerRecordScore", "Error: " + e.getMessage());
                    }
                }

            });
        } else {
            Log.e("getFlowerScore", "no member");
        }
    }

    private void showFlower(ImageView flower, int score, String name) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        if (sp.getStringSet("@string/flower_state" + name, null) != null) {
            ArrayList<String> flowerStateList = new ArrayList<>(sp.getStringSet("@string/flower_state" + name, null));
            if (flowerStateList.size() == 7) {
                boolean hasClover2 = false;
                boolean hasButterfly2 = false;
                boolean hasClover = false;
                boolean hasLadybug = false;
                boolean hasButterfly = false;
                boolean hasLeaf = false;
                boolean hasPot = false;

                for (String setStr : flowerStateList) {
                    String[] setAry = setStr.split(",", 0);

                    switch (Integer.parseInt(setAry[0])) {
                        case 1:
                            hasClover2 = Boolean.getBoolean(setAry[1]);
                            break;
                        case 2:
                            hasButterfly2 = Boolean.getBoolean(setAry[1]);
                            break;
                        case 3:
                            hasClover = Boolean.getBoolean(setAry[1]);
                            break;
                        case 4:
                            hasLadybug = Boolean.getBoolean(setAry[1]);
                            break;
                        case 5:
                            hasButterfly = Boolean.getBoolean(setAry[1]);
                            break;
                        case 6:
                            hasLeaf = Boolean.getBoolean(setAry[1]);
                            break;
                        case 7:
                            hasPot = Boolean.getBoolean(setAry[1]);
                            break;
                    }
                }

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
            } else {
                Toast.makeText(MainActivity.this, "Please Sync Again", Toast.LENGTH_SHORT).show();
                Log.e("showFlower", "state not enough");
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

    private void showDetail() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        Log.e("Ave Score", String.valueOf(sp.getInt("@string/ave_score", -1)));
        int score = sp.getInt("@string/healthy_score", -100);
        int until_next = sp.getInt("@string/until_next", -100);
        Log.e("untilNext", String.valueOf(until_next));

        TextView scoreText = (TextView) findViewById(R.id.score);
        TextView untilNextText = (TextView) findViewById(R.id.until_next);

        if (score > -1) {
            scoreText.setText("今のスコア\n    " + score + "点");
        } else {
            scoreText.setText("スコアが読み込めません");
        }

        if (until_next > -20) {
            untilNextText.setText("報酬まで\nあと" + until_next + "日");
        }
    }

    private void groupSync() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        if (sp.getStringSet("@string/member_set", null) != null) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FlowerRecord");
            query.whereContainedIn("username", sp.getStringSet("@string/member_set", null));
            query.orderByAscending("createdAt");

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> flowerRecordList, ParseException e) {
                    if (e == null) {
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

                        String name;
                        boolean hasClover2;
                        boolean hasButterfly2;
                        boolean hasClover;
                        boolean hasLadybug;
                        boolean hasButterfly;
                        boolean hasLeaf;
                        boolean hasPot;

                        for (ParseObject flowerRecord : flowerRecordList) {
                            hasClover2 = flowerRecord.getBoolean("hasClover2");
                            hasButterfly2 = flowerRecord.getBoolean("hasButterfly2");
                            hasClover = flowerRecord.getBoolean("hasClover");
                            hasLadybug = flowerRecord.getBoolean("hasLadybug");
                            hasButterfly = flowerRecord.getBoolean("hasButterfly");
                            hasLeaf = flowerRecord.getBoolean("hasLeaf");
                            hasPot = flowerRecord.getBoolean("hasPot");

                            name = flowerRecord.getString("username");

                            HashSet<String> flowerState = new HashSet<>();
                            flowerState.add("1," + String.valueOf(hasClover2));
                            flowerState.add("2," + String.valueOf(hasButterfly2));
                            flowerState.add("3," + String.valueOf(hasClover));
                            flowerState.add("4," + String.valueOf(hasLadybug));
                            flowerState.add("5," + String.valueOf(hasButterfly));
                            flowerState.add("6," + String.valueOf(hasLeaf));
                            flowerState.add("7," + String.valueOf(hasPot));

                            sp.edit().putStringSet("@string/flower_state" + name, flowerState).commit();

                            if (flowerState.size() == 7) {
                                Log.e("GroupSync " + name, "successfully get" + flowerState.toString());
                            } else {
                                Log.e("GroupSync " + name, "failed" + flowerState.toString());
                            }
                        }
                    } else {
                        Log.e("GroupSync", "Error: " + e.getMessage());
                    }
                }
            });
        }
    }
//
//    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//
//        if (height > reqHeight || width > reqWidth) {
//
//            final int halfHeight = height / 2;
//            final int halfWidth = width / 2;
//
//            while ((halfHeight / inSampleSize) > reqHeight
//                    && (halfWidth / inSampleSize) > reqWidth) {
//                inSampleSize *= 2;
//            }
//        }
//
//        return inSampleSize;
//    }
}
