package com.example.apitestapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash);//theme가 있으면 중복x

        try{
            Thread.sleep(500);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }

        //startActivity(new Intent(this, GoogleSignInActivity.class));
        startActivity(new Intent(this, PhoneAuthActivity.class));
        finish();
    }
}
