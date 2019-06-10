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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Timer;
import java.util.TimerTask;

//import java.lang.Object.androidx.navigation.NavController;

public class CoreActivity extends AppCompatActivity implements View.OnClickListener {

    //private Button button;
    AirclueService mService;
    boolean mBound = false;
    ImageView circle;
    Timer location_timer;
    Timer elapsedtime_timer;
    long cnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core);

/*
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph()).build();
  */
        //툴바
        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //button = findViewById(R.id.core_bt);
        // Button listeners
        findViewById(R.id.core_bt).setOnClickListener(this);
        findViewById(R.id.right_bt).setOnClickListener(this);
        findViewById(R.id.left_bt).setOnClickListener(this);
        findViewById(R.id.api_bt).setOnClickListener(this);

        circle = findViewById(R.id.circle_img);

    }
/*
    public void onButtonClicked(View view){
    }
*/
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.core_bt) {

            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION )
                            != PackageManager.PERMISSION_GRANTED ) {

                ActivityCompat.requestPermissions( CoreActivity.this,
                        new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  }, 0 );
            }

            //start service
            Intent intent = new Intent(this, AirclueService.class);
            intent.putExtra("command", 0);
            startService(intent);

            if(!mBound)
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

            //애니메이션
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_1);
            circle.setAnimation(animation);
            circle.setVisibility(View.INVISIBLE);//안해주면 안보임
            circle.setVisibility(View.VISIBLE);

            //위치 타이머
            location_timer = new Timer();
            TimerTask tt = new TimerTask() {
                @Override
                public void run() {
                    Location location = mService.getLocation();
                    String lat = String.format("%.6f",location.getLatitude());
                    String lng = String.format("%.6f",location.getLongitude());
                    final String string = "위도 "+lat + ",  경도 " + lng;
                    final TextView textView = findViewById(R.id.position_text);

                    //ui thread가 아닌 thread에서 ui변경을 위한 메소드 호출
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            textView.setText(string);
                        }
                    });
                }
            };

            location_timer.schedule(tt, 500, 5000);


            //경과시간 타이머
            cnt = 0;
            elapsedtime_timer = new Timer();
            TimerTask tt2 = new TimerTask() {
                @Override
                public void run() {

                    cnt+=1000;
                    final String string = GetTime(cnt);
                    final TextView textView = findViewById(R.id.time_text);

                    //ui thread가 아닌 thread에서 ui변경을 위한 메소드 호출
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            textView.setText(string);
                        }
                    });
                }
            };

            elapsedtime_timer.schedule(tt2, 0, 1000);

        }
        else if (i == R.id.right_bt){

            if(mBound) {
                Toast.makeText(getApplicationContext(), "unbindService", Toast.LENGTH_SHORT).show();
                unbindService(mConnection);
                mBound = false;
            }
            else
                Toast.makeText(this, "nothing", Toast.LENGTH_SHORT).show();

            //stop service
            Intent intent = new Intent(this, AirclueService.class);
            intent.putExtra("command", 1);
            startService(intent);

            //애니메이션 종료
            circle.clearAnimation();

            //타이머 종료
            location_timer.cancel();
            elapsedtime_timer.cancel();
        }
        else if(i == R.id.left_bt){
            if(mBound) {
                //int num = mService.getRandomNumber();//unbind 해도 사용 가능하네
                //Toast.makeText(getApplicationContext(), "number:" + num, Toast.LENGTH_SHORT).show();
                //mService.setLocationProvider();
                Location location = mService.getLocation();
                String string = "Lat:"+location.getLatitude() + ", Lng:" + location.getLongitude();
                Toast.makeText(this, string, Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(this, "nothing", Toast.LENGTH_SHORT).show();
        }
        else if(i == R.id.api_bt){
            startActivity(new Intent(this, ApiActivity.class));
        }
    }

    //경과 시간 변환
    private String GetTime(long msec){

        final long sec_ms = 1000;
        final long minute_ms = sec_ms*60;
        final long hour_ms = minute_ms*60;
        final long day_ms = hour_ms*24;

        long day = msec / day_ms;
        msec -= day*day_ms;

        long hour = msec / hour_ms;
        msec -= hour*hour_ms;

        long minute = msec / minute_ms;
        msec -= minute*minute_ms;

        long sec = msec / sec_ms;

        String date_str = String.format("%02d일 %02d시간 %02d분 %02d초", day, hour, minute, sec);

        return date_str;
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
