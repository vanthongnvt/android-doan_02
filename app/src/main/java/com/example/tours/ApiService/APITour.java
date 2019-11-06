package com.example.tours.ApiService;

import com.example.tours.Model.Auth;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APITour {


    //dinh nghia endpoint va cac field o day

    @POST("/user/login")
    @FormUrlEncoded
    Call<Auth> normalLogin(@Field("emailPhone") String emailPhone,
                           @Field("password") String password);
}