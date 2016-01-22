package com.reiyu.sleepin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.HashSet;
import java.util.List;

/**
 * Created by Satomi on 1/3/16.
 */
public class SignInFragment extends AppCompatActivity {
    final static String TAG = "Sign in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sign_in);

        Button button = (Button) findViewById(R.id.register);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameText = (EditText) findViewById(R.id.name_register);
                EditText emailText = (EditText) findViewById(R.id.email_register);
                EditText passText = (EditText) findViewById(R.id.password_register);
                EditText groupIdText = (EditText) findViewById(R.id.group_id_register);

                String username = nameText.getText().toString();
                String email = emailText.getText().toString();
                int group_id = Integer.parseInt(groupIdText.getText().toString());

                ParseUser user = new ParseUser();
                user.setUsername(username);
                user.setEmail(email);
                user.setPassword(passText.getText().toString());
                user.put("group_id", group_id);

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SignInFragment.this);
                sp.edit().putString("@string/username", username).commit();
                sp.edit().putString("@string/email", email).commit();
                sp.edit().putInt("@string/group_id", group_id).commit();

                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.e(TAG, "Success");
                            signIn();
                            getGroup();
                        } else {
                            Log.e(TAG, "Error", e);
                            Toast.makeText(SignInFragment.this, "Could not Register", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        Button buttonSignIn = (Button) findViewById(R.id.sign_in);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameText = (EditText) findViewById(R.id.name_sign_in);
                EditText passText = (EditText) findViewById(R.id.password_sign_in);

                String username = nameText.getText().toString();
                String pass = passText.getText().toString();
                ParseUser user = new ParseUser();
                user.setUsername(username);
                user.setPassword(passText.getText().toString());

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SignInFragment.this);
                sp.edit().putString("@string/username", username).commit();

                ParseUser.logInInBackground(username, pass, new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            signIn();
                            getGroupID();
                        } else {
                            Log.e(TAG, "Error Sign in");
                            Toast.makeText(SignInFragment.this, "Could not Sign in", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    protected void signIn() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putBoolean("@string/signed_in", true).commit();

        Log.d(TAG, "Successfully Sign in");
        startActivity(new Intent(SignInFragment.this, MainActivity.class));
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
    }

    private void getGroupID() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SignInFragment.this);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", sp.getString("@string/username", null));

        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> memberObject, ParseException e) {
                if (e == null) {
                    if (memberObject.size() > 0) {
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SignInFragment.this);
                        for (ParseUser member : memberObject) {
                            String name = member.getString("username");
                            String username = sp.getString("@string/username", null);
                            if (name.equals(username)) {
                                int group_id = member.getInt("group_id");
                                if (group_id > 0) {
                                    sp.edit().putInt("@string/group_id", group_id).commit();
                                    Log.e("getGroupID", String.valueOf(group_id));
                                    getGroup();
                                }
                            } else {
                                Log.e("getGroupID", "group_id was null");
                            }
                        }
                    } else {
                        Log.e("getGroupID", "no user matches username : " + memberObject.toString());
                    }
                } else {
                    Log.e("getGroupID", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void getGroup() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SignInFragment.this);
        int group_id = sp.getInt("@string/group_id", -1);

        Log.e("WakeUp stored group_id", String.valueOf(group_id));
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("group_id", group_id);

        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> memberObject, ParseException e) {
                if (e == null) {
                    if (memberObject.size() > 0) {
                        HashSet<String> usernameSet = new HashSet<>();
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SignInFragment.this);
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
}
