package com.ygaps.travelapp.Service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

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
    public static String str_receiver = "tours.Service.BroadcastLocationReceiver";
    private Intent intent;
    private Integer tourId;

    public RequestLocationTask(Context context, Integer tourId) {
        this.context= context;
        this.tourId =tourId;
        apiTour = new APIRetrofitCreator().getAPIService();
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        intent = new Intent(str_receiver);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                fn_getlocation();
            }
        }, 5000);
        return null;
    }

    private void requestHttp(){
        Log.e("latitude",location.getLatitude()+"");
        Log.e("longitude",location.getLongitude()+"");
//        intent.putExtra("latutide",latitude+"");
//        intent.putExtra("longitude",longitude+"");
        apiTour.currentCoordinate(TokenStorage.getInstance().getAccessToken(),TokenStorage.getInstance().getUserId(),tourId,location.getLatitude(),location.getLongitude()).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if(response.isSuccessful()){
                    intent.putExtra("info",1);
                    context.sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {

            }
        });
    }

    @SuppressLint("MissingPermission")
    private void fn_getlocation(){
        boolean isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnable && !isNetworkEnable){

        }else {
            boolean hasRequest=false;
            if (isNetworkEnable){
                location = null;
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,0,this);
                if (locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    requestHttp();
                    hasRequest=true;
                }

            }


            if (isGPSEnable&&!hasRequest){
                location = null;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,this);
                if (locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    requestHttp();
                }
            }


        }

    }

    @Override
    public void onLocationChanged(Location location) {
//        Log.e("latitude",location.getLatitude()+"");
//        Log.e("longitude",location.getLongitude()+"");
//        requestHttp();
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
