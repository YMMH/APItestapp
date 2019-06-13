package com.example.apitestapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class AirclueService extends Service {

    private static final String NOTIFICATION_CHANNEL_ID = "channel1_ID";
    private static final String NOTIFICATION_CHANNEL_NAME = "channel1";


    private Location mLocation;

    private int mStatus = 0;

    public AirclueService() {
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Toast.makeText(getApplicationContext(),"Airclue Service created", Toast.LENGTH_SHORT).show();
    }

    //----------Bind

    // Binder given to clients
    private final IBinder mBinder = new AirclueBinder();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class AirclueBinder extends Binder {
        AirclueService getService() {
            // Return this instance of LocalService so clients can call public methods
            return AirclueService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(),"onBind", Toast.LENGTH_SHORT).show();
        return mBinder;
    }
    //-------------------

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Toast.makeText(getApplicationContext(),"onStartCommand", Toast.LENGTH_SHORT).show();

        int cmd = intent.getIntExtra("command", -1);

        switch(cmd){

            case 0://시작
                mStatus = 1;
                setLocationProvider();
                makeForgroundService();//forground service
                break;

            case 1://종료
                mStatus = 0;
                stopForeground(true);
                Toast.makeText(getApplicationContext(),"stopForeground", Toast.LENGTH_SHORT).show();
                stopSelf();
                break;
        }

        //return START_STICKY;//service restarts if it is destroyed
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        Toast.makeText(getApplicationContext(),"Airclue Service destroyed", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    public int getStatus(){
        return mStatus;
    }

    public void setLocationProvider(){


        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED )
            ;//no permission
        else{
            final LocationManager mLoca_mgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

            //Location location = mLoca_mgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location location = mLoca_mgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            mLocation = location;

            //mLoca_mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, gpsLocationListener);
            mLoca_mgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1, gpsLocationListener);
        }
    }

    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            mLocation = location;
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    public Location getLocation(){
        return mLocation;
    }

    private void makeForgroundService(){
        NotificationManager notificationManager
                = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel
                    = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        int notificationId = 1010;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.airclue_icon);
        builder.setContentTitle("AirClue 서비스");
        builder.setContentText("AirClue 작동 중");

        //this leads user to activity when user touches noti,
        Intent intent = new Intent(getApplicationContext(), CoreActivity.class);
        PendingIntent contentIntent =
                PendingIntent.getActivity(getApplicationContext(), 1010, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        startForeground(notificationId, builder.build());
        Toast.makeText(getApplicationContext(),"startForeground", Toast.LENGTH_SHORT).show();
    }
}
