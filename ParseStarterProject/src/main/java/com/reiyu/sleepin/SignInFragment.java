package com.reiyu.sleepin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by Satomi on 1/3/16.
 */
public class SignInFragment extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sign_in);

        Button button = (Button) findViewById(R.id.register);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameText = (EditText) findViewById(R.id.name);
                EditText emailText = (EditText) findViewById(R.id.email);
                EditText passText = (EditText) findViewById(R.id.password);

                String name = nameText.getText().toString();
                String email = emailText.getText().toString();
                ParseUser user = new ParseUser();
                user.setUsername(name);
                user.setEmail(email);
                user.setPassword(passText.getText().toString());

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SignInFragment.this);
                sp.edit().putString("@string/name", name).commit();
                sp.edit().putString("@string/email", email).commit();

                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.e("Create User", "Success");
                            signIn();
                        } else {
                            // Sign up didn't succeed. Look at the ParseException
                            // to figure out what went wrong
                            Log.e("Create User", "Error", e);
                        }
                    }
                });
            }
        });
    }

    protected void signIn() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putBoolean("@string/signed_in", true).commit();
        startActivity(new Intent(SignInFragment.this, MainActivity.class));
    }
}
