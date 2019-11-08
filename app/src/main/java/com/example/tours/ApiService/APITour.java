package com.example.tours.ApiService;

import com.example.tours.Model.Auth;
import com.example.tours.Model.AuthRegister;

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

    @POST("/user/register")
    @FormUrlEncoded
    Call<AuthRegister> Register(@Field("password") String password,
                                @Field("fullName") String fullName,
                                @Field("email") String email,
                                @Field("phone") String phone,
                                @Field("address") String address,
                                @Field("dob") String dob,
                                @Field("gender") Number gender);
}