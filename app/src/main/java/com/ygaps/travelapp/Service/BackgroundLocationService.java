package com.ygaps.travelapp.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class BackgroundLocationService extends Service {

    private final String TAG = "LocationService";
    private final String TAG_LOCATION = "TAG_LOCATION";
    private Integer tourId;

    public BackgroundLocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        (new RequestLocationTask(getApplicationContext(),tourId)).execute();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
        Bundle extras = intent.getExtras();
        if(extras!=null){
            tourId = extras.getInt("tourId");
        }
        return Service.START_STICKY;
    }


    @Override
    public void onDestroy() {
        Log.e(TAG, "Service Stopped");
        super.onDestroy();
    }
}
