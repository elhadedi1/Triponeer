package com.app.triponeer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 1500;
    SharedPreferences saving;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser users = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                saving = getSharedPreferences(Login.LOGIN_DATA, 0);
                boolean isLogin = saving.getBoolean(Login.IS_LOGIN, false);
                boolean isFacebookLogin = saving.getBoolean(Login.IS_FACEBOOK_LOGIN, false);
                boolean isGoogleLogin = saving.getBoolean(Login.IS_GOOGLE_LOGIN, false);
                if (isLogin || isFacebookLogin || isGoogleLogin) {
                    Intent login = new Intent(Splash.this, MainActivity.class);
                    startActivity(login);
                    finish();
                } else {
                    Intent login = new Intent(Splash.this, Login.class);
                    startActivity(login);
                    finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}