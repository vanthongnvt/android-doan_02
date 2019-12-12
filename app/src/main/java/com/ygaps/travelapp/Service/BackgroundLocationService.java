package com.ygaps.travelapp.Service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.ygaps.travelapp.ApiService.APIRetrofitCreator;
import com.ygaps.travelapp.ApiService.APITour;
import com.ygaps.travelapp.AppHelper.TokenStorage;
import com.ygaps.travelapp.Model.MessageResponse;
import com.ygaps.travelapp.R;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BackgroundLocationService extends Service implements LocationListener {

    private Intent intent;
    private Integer tourId;
    private static final String TAG = "MAP_DIRECTION_SERVICE";
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    long notify_interval = 5000;
    private LocationManager locationManager;
    private Location location;
    private APITour apiTour;

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
        mTimer = new Timer();

        apiTour = new APIRetrofitCreator().getAPIService();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        intent = new Intent(getString(R.string.receiver_action_send_coordinate));

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,this);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
        Bundle extras = intent.getExtras();
        if(extras!=null){
            tourId = extras.getInt("tourId");
//            (new RequestLocationTask(getApplicationContext(),tourId)).execute();
            mTimer.schedule(new TimerTaskToGetLocation(),5,notify_interval);
        }
        return Service.START_STICKY;
    }

    private class TimerTaskToGetLocation extends TimerTask{

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    requestHttp();
                }
            });
        }

    }

    private void requestHttp(){
//        Log.e("latitude",location.getLatitude()+"");
//        Log.e("longitude",location.getLongitude()+"");
        Log.d(TAG, "requestHttp: HTTP");
        if(location==null){
            return;
        }
        apiTour.currentCoordinate(TokenStorage.getInstance().getAccessToken(),TokenStorage.getInstance().getUserId(),tourId,location.getLatitude(),location.getLongitude()).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if(response.isSuccessful()){
                    Bundle bundle = new Bundle();
                    bundle.putInt("memberCoordinate",1);
                    intent.putExtras(bundle);
                    sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Log.d(TAG, "requestHttp: Send location failed");
            }
        });
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "Service Stopped");
        if(mTimer!=null){
            mTimer.cancel();
        }
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
//        Log.d(TAG, "onLocationChanged: "+location.getLatitude()+" , " + location.getLongitude());
        this.location = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
