package com.example.apitestapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class CoreActivity extends AppCompatActivity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core);

         button = findViewById(R.id.core_bt);
    }

    public void onButtonClicked(View view){
        startActivity(new Intent(this, ApiActivity.class));
    }
}
