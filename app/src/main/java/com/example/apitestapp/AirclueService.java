package com.example.apitestapp;

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
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class AirclueService extends Service {

    private static final String NOTIFICATION_CHANNEL_ID = "channel1_ID";
    private static final String NOTIFICATION_CHANNEL_NAME = "channel1";


    private Location mLocation;

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

            case 0:
                setLocationProvider();
                break;

            case 1:
                break;
        }

        return START_STICKY;//service restarts if it is destroyed
    }

    @Override
    public void onDestroy(){
        Toast.makeText(getApplicationContext(),"Airclue Service destroyed", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    public void setLocationProvider(){


        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED )
            ;//no permission
        else{
            final LocationManager mLoca_mgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

            Location location = mLoca_mgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            mLocation = location;

            mLoca_mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, gpsLocationListener);
            //mLoca_mgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1, gpsLocationListener);
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
}
