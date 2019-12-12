package com.ygaps.travelapp.Service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.ygaps.travelapp.ApiService.APIRetrofitCreator;
import com.ygaps.travelapp.ApiService.APITour;
import com.ygaps.travelapp.AppHelper.TokenStorage;
import com.ygaps.travelapp.Model.MessageResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestLocationTask extends AsyncTask<Void, Void, Void> implements LocationListener {

    private LocationManager locationManager;
    private Location location;
    private Context context;
    private APITour apiTour;
    public static String str_location_receiver = "tours.Service.BroadcastLocationReceiver";
    private Intent intent;
    private Integer tourId;
    private static final String TAG = "MAP_DIRECTION_SERVICE";


    public RequestLocationTask(Context context, Integer tourId) {
        this.context = context;
        this.tourId = tourId;
        apiTour = new APIRetrofitCreator().getAPIService();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        intent = new Intent(str_location_receiver);

        if (ContextCompat.checkSelfPermission(context.getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context.getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,this);
        }

    }

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        new Handler().postDelayed(this::requestHttp, 5000);
        super.onPostExecute(aVoid);
    }


    private void requestHttp(){
//        Log.e("latitude",location.getLatitude()+"");
//        Log.e("longitude",location.getLongitude()+"");
        Log.d(TAG, "requestHttp: HTTP");
        apiTour.currentCoordinate(TokenStorage.getInstance().getAccessToken(),TokenStorage.getInstance().getUserId(),tourId,location.getLatitude(),location.getLongitude()).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if(response.isSuccessful()){
                    Bundle bundle = new Bundle();
                    bundle.putInt("memberCoordinate",1);
                    intent.putExtras(bundle);
                    context.sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Log.d(TAG, "requestHttp: Send location failed");
            }
        });
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
