package com.reiyu.sleepin;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    Bitmap flowerBmpSmall;
    Bitmap flowerBmpLarge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(R.string.app_name);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (!(sp.getBoolean("@string/signed_in", false))) {
            Log.e("Main Activity", "user null");
            startActivity(new Intent(getApplicationContext(), SignInFragment.class));
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
                startActivity(new Intent(getApplicationContext(), WakeUpFragment.class));
            } else {
                String msg = sp.getString("@string/username", null) + "'s Flower";
                setTitle(msg);

                setView();
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
        } else if (id == R.id.action_get_member) {
            getGroup();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        setView();
        super.onResume();
    }

    @Override
    protected void onStop() {
        if (flowerBmpSmall != null) {
            flowerBmpSmall.recycle();
            flowerBmpSmall = null;
        }

        if (flowerBmpLarge != null) {
            flowerBmpLarge.recycle();
            flowerBmpLarge = null;
        }
        super.onStop();
//        ImageView imageView = (ImageView) findViewById(R.id.flower);
//        imageView.setImageDrawable(null);
//        imageView.destroyDrawingCache();
//
//        ImageView imageView1 = (ImageView) findViewById(R.id.flower1);
//        imageView1.setImageDrawable(null);
//        imageView1.destroyDrawingCache();
//
//        ImageView imageView2 = (ImageView) findViewById(R.id.flower2);
//        imageView2.setImageDrawable(null);
//        imageView.destroyDrawingCache();
//
//        ImageView imageView3 = (ImageView) findViewById(R.id.flower3);
//        imageView3.setImageDrawable(null);
//        imageView3.destroyDrawingCache();
//
//        ImageView imageView4 = (ImageView) findViewById(R.id.flower4);
//        imageView4.setImageDrawable(null);
//        imageView4.destroyDrawingCache();
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
    }

    private void signOut() {
        ParseUser.logOut();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sp.edit().putBoolean("@string/signed_in", false).commit();
        sp.edit().putString("@string/username", null).commit();
        sp.edit().putInt("@string/group_id", -1).commit();
        sp.edit().putString("@string/email", null).commit();
        sp.edit().putStringSet("@string/member_set", null).commit();
        startActivity(new Intent(getApplicationContext(), SignInFragment.class));
    }

    public void getFlowerScore() {
        Log.e("getFlowerScore", "called");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (sp.getStringSet("@string/member_set", null) != null) {
            ParseQuery<ParseObject> query_score = ParseQuery.getQuery("SleepinessRecord");
            query_score.whereContainedIn("username", sp.getStringSet("@string/member_set", null));
            query_score.orderByAscending("createdAt");

            query_score.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> sleepinessRecordList, ParseException e) {
                    if (e == null) {
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
                        Log.e("FlowerRecordScore", sleepinessRecordList.toString());
                    } else {
                        Log.e("FlowerRecordScore", "Error: " + e.getMessage());
                        Toast.makeText(getApplicationContext(), "他の人のスコアが読み込めません", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Log.e("getFlowerScore", "no member");
        }
    }

    private void showFlower(ImageView flower, int score, String name) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

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
                            if (setAry[1].equals("true")){ hasClover2 = true; }
                            break;
                        case 2:
                            if (setAry[1].equals("true")){ hasButterfly2 = true; }
                            break;
                        case 3:
                            if (setAry[1].equals("true")){ hasClover = true; }
                            break;
                        case 4:
                            if (setAry[1].equals("true")){ hasLadybug = true; }
                            break;
                        case 5:
                            if (setAry[1].equals("true")){ hasButterfly = true; }
                            break;
                        case 6:
                            if (setAry[1].equals("true")){ hasLeaf = true; }
                            break;
                        case 7:
                            if (setAry[1].equals("true")){ hasPot = true; }
                            break;
                    }
                }

                if (score < 0) {
                    Toast.makeText(getApplicationContext(), "Could not load score", Toast.LENGTH_LONG).show();
                } else {
                    if (score > 60) {
                        if (hasClover2) {
                            flowerBmpSmall = decodeSampledBitmapFromResource(getResources(), R.drawable.happy7, 350, 350);
                        } else if (hasButterfly2) {
                            flowerBmpSmall = decodeSampledBitmapFromResource(getResources(), R.drawable.happy6, 350, 350);
                        } else if (hasClover) {
                            flowerBmpSmall = decodeSampledBitmapFromResource(getResources(), R.drawable.happy5, 350, 350);
                        } else if (hasLadybug) {
                            flowerBmpSmall = decodeSampledBitmapFromResource(getResources(), R.drawable.happy4, 350, 350);
                        } else if (hasButterfly) {
                            flowerBmpSmall = decodeSampledBitmapFromResource(getResources(), R.drawable.happy3, 350, 350);
                        } else if (hasLeaf) {
                            flowerBmpSmall = decodeSampledBitmapFromResource(getResources(), R.drawable.happy2, 350, 350);
                        } else if (hasPot) {
                            flowerBmpSmall = decodeSampledBitmapFromResource(getResources(), R.drawable.happy1, 350, 350);
                        } else {
                            flowerBmpSmall = decodeSampledBitmapFromResource(getResources(), R.drawable.happy, 350, 350);
                        }
                    } else if (score > 30) {
                        if (hasClover2) {
                            flowerBmpSmall = decodeSampledBitmapFromResource(getResources(), R.drawable.sad7, 350, 350);
                        } else if (hasButterfly2) {
                            flowerBmpSmall = decodeSampledBitmapFromResource(getResources(), R.drawable.sad6, 350, 350);
                        } else if (hasClover) {
                            flowerBmpSmall = decodeSampledBitmapFromResource(getResources(), R.drawable.sad5, 350, 350);
                        } else if (hasLadybug) {
                            flowerBmpSmall = decodeSampledBitmapFromResource(getResources(), R.drawable.sad4, 350, 350);
                        } else if (hasButterfly) {
                            flowerBmpSmall = decodeSampledBitmapFromResource(getResources(), R.drawable.sad3, 350, 350);
                        } else if (hasLeaf) {
                            flowerBmpSmall = decodeSampledBitmapFromResource(getResources(), R.drawable.sad2, 350, 350);
                        } else if (hasPot) {
                            flowerBmpSmall = decodeSampledBitmapFromResource(getResources(), R.drawable.sad1, 350, 350);
                        } else {
                            flowerBmpSmall = decodeSampledBitmapFromResource(getResources(), R.drawable.sad, 350, 350);
                        }
                    } else {
                        if (hasClover2) {
                            flowerBmpSmall = decodeSampledBitmapFromResource(getResources(), R.drawable.cry7, 350, 350);
                        } else if (hasButterfly2) {
                            flowerBmpSmall = decodeSampledBitmapFromResource(getResources(), R.drawable.cry6, 350, 350);
                        } else if (hasClover) {
                            flowerBmpSmall = decodeSampledBitmapFromResource(getResources(), R.drawable.cry5, 350, 350);
                        } else if (hasLadybug) {
                            flowerBmpSmall = decodeSampledBitmapFromResource(getResources(), R.drawable.cry4, 350, 350);
                        } else if (hasButterfly) {
                            flowerBmpSmall = decodeSampledBitmapFromResource(getResources(), R.drawable.cry3, 350, 350);
                        } else if (hasLeaf) {
                            flowerBmpSmall = decodeSampledBitmapFromResource(getResources(), R.drawable.cry2, 350, 350);
                        } else if (hasPot) {
                            flowerBmpSmall = decodeSampledBitmapFromResource(getResources(), R.drawable.cry1, 350, 350);
                        } else {
                            flowerBmpSmall = decodeSampledBitmapFromResource(getResources(), R.drawable.cry, 350, 350);
                        }
                    }
                    flower.setImageBitmap(flowerBmpSmall);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Please Sync Again", Toast.LENGTH_SHORT).show();
                Log.e("showFlower", "state not enough");
            }
        }
    }

    private void showMainFlower() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int score = sp.getInt("@string/healthy_score", -1);

        if (score < 0) {
            Toast.makeText(getApplicationContext(), "Could not load score", Toast.LENGTH_LONG).show();
        } else {
            boolean hasClover2 = sp.getBoolean("@string/clover2", false);
            boolean hasButterfly2 = sp.getBoolean("@string/butterfly2", false);
            boolean hasClover = sp.getBoolean("@string/clover", false);
            boolean hasLadybug = sp.getBoolean("@string/ladybug", false);
            boolean hasButterfly = sp.getBoolean("@string/clover2", false);
            boolean hasLeaf = sp.getBoolean("@string/leaf", false);
            boolean hasPot = sp.getBoolean("@string/pot", false);

            Log.e("showMainFlower HAS_POT", String.valueOf(hasPot));

            ImageView flower = (ImageView) findViewById(R.id.flower);
            if (score > 60) {
                if (hasClover2) {
                    flowerBmpLarge = decodeSampledBitmapFromResource(getResources(), R.drawable.happy7, 1000, 1000);
                } else if (hasButterfly2) {
                    flowerBmpLarge = decodeSampledBitmapFromResource(getResources(), R.drawable.happy6, 1000, 1000);
                } else if (hasClover) {
                    flowerBmpLarge = decodeSampledBitmapFromResource(getResources(), R.drawable.happy5, 1000, 1000);
                } else if (hasLadybug) {
                    flowerBmpLarge = decodeSampledBitmapFromResource(getResources(), R.drawable.happy4, 1000, 1000);
                } else if (hasButterfly) {
                    flowerBmpLarge = decodeSampledBitmapFromResource(getResources(), R.drawable.happy3, 1000, 1000);
                } else if (hasLeaf) {
                    flowerBmpLarge = decodeSampledBitmapFromResource(getResources(), R.drawable.happy2, 1000, 1000);
                } else if (hasPot) {
                    flowerBmpLarge = decodeSampledBitmapFromResource(getResources(), R.drawable.happy1, 1000, 1000);
                } else {
                    flowerBmpLarge = decodeSampledBitmapFromResource(getResources(), R.drawable.happy, 1000, 1000);
                }
            } else if (score > 30) {
                if (hasClover2) {
                    flowerBmpLarge = decodeSampledBitmapFromResource(getResources(), R.drawable.sad7, 1000, 1000);
                } else if (hasButterfly2) {
                    flowerBmpLarge = decodeSampledBitmapFromResource(getResources(), R.drawable.sad6, 1000, 1000);
                } else if (hasClover) {
                    flowerBmpLarge = decodeSampledBitmapFromResource(getResources(), R.drawable.sad5, 1000, 1000);
                } else if (hasLadybug) {
                    flowerBmpLarge = decodeSampledBitmapFromResource(getResources(), R.drawable.sad4, 1000, 1000);
                } else if (hasButterfly) {
                    flowerBmpLarge = decodeSampledBitmapFromResource(getResources(), R.drawable.sad3, 1000, 1000);
                } else if (hasLeaf) {
                    flowerBmpLarge = decodeSampledBitmapFromResource(getResources(), R.drawable.sad2, 1000, 1000);
                } else if (hasPot) {
                    flowerBmpLarge = decodeSampledBitmapFromResource(getResources(), R.drawable.sad1, 1000, 1000);
                } else {
                    flowerBmpLarge = decodeSampledBitmapFromResource(getResources(), R.drawable.sad, 1000, 1000);
                }
            } else {
                if (hasClover2) {
                    flowerBmpLarge = decodeSampledBitmapFromResource(getResources(), R.drawable.cry7, 1000, 1000);
                } else if (hasButterfly2) {
                    flowerBmpLarge = decodeSampledBitmapFromResource(getResources(), R.drawable.cry6, 1000, 1000);
                } else if (hasClover) {
                    flowerBmpLarge = decodeSampledBitmapFromResource(getResources(), R.drawable.cry5, 1000, 1000);
                } else if (hasLadybug) {
                    flowerBmpLarge = decodeSampledBitmapFromResource(getResources(), R.drawable.cry4, 1000, 1000);
                } else if (hasButterfly) {
                    flowerBmpLarge = decodeSampledBitmapFromResource(getResources(), R.drawable.cry3, 1000, 1000);
                } else if (hasLeaf) {
                    flowerBmpLarge = decodeSampledBitmapFromResource(getResources(), R.drawable.cry2, 1000, 1000);
                } else if (hasPot) {
                    flowerBmpLarge = decodeSampledBitmapFromResource(getResources(), R.drawable.cry1, 1000, 1000);
                } else {
                    flowerBmpLarge = decodeSampledBitmapFromResource(getResources(), R.drawable.cry, 1000, 1000);
                }
            }
            flower.setImageBitmap(flowerBmpLarge);
        }
    }

    private void showDetail() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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
        Log.e("groupSync", "called");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (sp.getStringSet("@string/member_set", null) != null) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FlowerRecord");
            query.whereContainedIn("username", sp.getStringSet("@string/member_set", null));
            query.orderByAscending("createdAt");

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> flowerRecordList, ParseException e) {
                    if (e == null) {
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                        String name;
                        boolean hasClover2;
                        boolean hasButterfly2;
                        boolean hasClover;
                        boolean hasLadybug;
                        boolean hasButterfly;
                        boolean hasLeaf;
                        boolean hasPot;

                        for (ParseObject flowerRecord : flowerRecordList) {
                            hasClover2 = flowerRecord.getBoolean("clover2");
                            hasButterfly2 = flowerRecord.getBoolean("butterfly2");
                            hasClover = flowerRecord.getBoolean("clover");
                            hasLadybug = flowerRecord.getBoolean("ladybug");
                            hasButterfly = flowerRecord.getBoolean("butterfly");
                            hasLeaf = flowerRecord.getBoolean("leaf");
                            hasPot = flowerRecord.getBoolean("pot");

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
                        getFlowerScore();
                    } else {
                        Log.e("GroupSync", "Error: " + e.getMessage());
                    }
                }
            });
        } else {
            Log.e("GroupSync", "member_set is null");
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private void getGroup() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int group_id = sp.getInt("@string/group_id", -1);

        Log.e("WakeUp stored group_id", String.valueOf(group_id));
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("group_id", group_id);
        Log.e("getMember group_id", String.valueOf(group_id));

        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> memberObject, ParseException e) {
                if (e == null) {
                    if (memberObject.size() > 0) {
                        HashSet<String> usernameSet = new HashSet<>();
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        for (ParseUser member : memberObject) {
                            String name = member.getString("username");

                            if (!name.equals(sp.getString("@string/username", null))) {
                                usernameSet.add(name);
                                Log.e("getMember", member.getString("username"));
                            }
                        }
                        sp.edit().putStringSet("@string/member_set", usernameSet).commit();
                    } else {
                        Log.e("getGroup", "invalid group_id : " + memberObject.toString());
                    }
                } else {
                    Log.e("getGroup", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void setFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.e("FAB", "clicked");
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String msg = ReflectFragment.getSession();
                int session_num = ReflectFragment.getSessionNum();

                if (session_num == 0) {
                    Toast.makeText(getApplicationContext(), "Session is 9:00 ~ 21:00\nPlease wait until 10:30 for reflection", Toast.LENGTH_LONG).show();
                } else if ((sp.getString("@string/sleepiness_updated", null) == null) || (!(sp.getString("@string/sleepiness_updated", null).equals(date + session_num)))) {
                    Log.e("FAB", "start ReflectFragment");
                    startActivity(new Intent(getApplicationContext(), ReflectFragment.class));
                } else {
                    Toast.makeText(getApplicationContext(), "You have already reflected\nsession " + msg + ".", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setView() {
        setContentView(R.layout.activity_main);
        showMainFlower();
        showDetail();
        setFab();

        new Thread(new Runnable() {
            @Override
            public void run() {
                getFlowerScore();
            }
        }).start();
    }
}
