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

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

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
                        } else {
                            Log.e(TAG, "Error", e);
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
                        } else {
                            Log.e(TAG, "Error Sign in");
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
}
