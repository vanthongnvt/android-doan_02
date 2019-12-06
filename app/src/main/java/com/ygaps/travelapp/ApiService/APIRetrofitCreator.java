package com.ygaps.travelapp.ApiService;

public class APIRetrofitCreator {

    public   APIRetrofitCreator(){}

    public static final String BASE_URL = "http://35.197.153.192:3000";

    public APITour getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(APITour.class);
    }
}
