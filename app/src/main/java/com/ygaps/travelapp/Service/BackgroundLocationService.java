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
import com.ygaps.travelapp.Model.MemberLocation;
import com.ygaps.travelapp.Model.MessageResponse;
import com.ygaps.travelapp.Model.NotificationOnRoadList;
import com.ygaps.travelapp.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BackgroundLocationService extends Service implements LocationListener {

//    private Intent intent;
    private Integer tourId;
    private static final String TAG = "MAP_DIRECTION_SERVICE";
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    long notify_interval = 5000;
    private LocationManager locationManager;
    private Location location;
    private APITour apiTour;
    private Intent intent;

    public BackgroundLocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTimer = new Timer();
        intent = new Intent(getString(R.string.receiver_action_send_notification_on_road));
        apiTour = new APIRetrofitCreator().getAPIService();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        intent = new Intent(getString(R.string.receiver_action_send_coordinate));

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
                    requestLocation();
                    RequestNotificationOnRoad();
                }
            });
        }

    }

    private void requestLocation(){
//        Log.e("latitude",location.getLatitude()+"");
//        Log.e("longitude",location.getLongitude()+"");
//        Log.d(TAG, "requestHttp: HTTP");
        if(location==null){
            return;
        }
        apiTour.currentCoordinate(TokenStorage.getInstance().getAccessToken(),TokenStorage.getInstance().getUserId(),tourId,location.getLatitude(),location.getLongitude()).enqueue(new Callback<List<MemberLocation>>() {
            @Override
            public void onResponse(Call<List<MemberLocation>> call, Response<List<MemberLocation>> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "requestLocation: Send location failed " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<MemberLocation>> call, Throwable t) {
                Log.d(TAG, "requestLocation: Send location failed");
            }
        });
    }

    private void RequestNotificationOnRoad(){
        if(location==null){
            return;
        }
        apiTour.getNotificationOnRoadByCoordinate(TokenStorage.getInstance().getAccessToken(),3,location.getLatitude(),location.getLongitude()).enqueue(new Callback<NotificationOnRoadList>() {
            @Override
            public void onResponse(Call<NotificationOnRoadList> call, Response<NotificationOnRoadList> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "RequestNotificationOnRoad: failed " + response.code());
                }
                else{
                    NotificationOnRoadList notificationOnRoadList = response.body();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("notificationOnRoad",notificationOnRoadList);
                    intent.putExtras(bundle);
                    sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<NotificationOnRoadList> call, Throwable t) {
                Log.d(TAG, "RequestNotificationOnRoad: Send location failed");
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
