package com.example.apitestapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class CoreActivity extends AppCompatActivity implements View.OnClickListener {

    private Button button;
    AirclueService mService;
    boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core);

        button = findViewById(R.id.core_bt);
        // Button listeners
        findViewById(R.id.right_bt).setOnClickListener(this);
        findViewById(R.id.left_bt).setOnClickListener(this);
    }

    public void onButtonClicked(View view){
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( CoreActivity.this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    0 );
        }

        Intent intent = new Intent(this, AirclueService.class);
        intent.putExtra("command", 0);
        //startService(intent);

        if(!mBound)
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.right_bt){

            if(mBound) {
                Toast.makeText(getApplicationContext(), "unbindService", Toast.LENGTH_SHORT).show();
                unbindService(mConnection);
                mBound = false;
            }
            else
                Toast.makeText(this, "nothing", Toast.LENGTH_SHORT).show();
        }
        else if(i == R.id.left_bt){
            if(mBound) {
                //int num = mService.getRandomNumber();//unbind 해도 사용 가능하네
                //Toast.makeText(getApplicationContext(), "number:" + num, Toast.LENGTH_SHORT).show();
                mService.setLocationProvider();
                Location location = mService.getLocation();
                String string = "Lat:"+location.getLatitude() + ", Lng:" + location.getLongitude();
                Toast.makeText(this, string, Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(this, "nothing", Toast.LENGTH_SHORT).show();
        }
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            AirclueService.AirclueBinder binder = (AirclueService.AirclueBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
