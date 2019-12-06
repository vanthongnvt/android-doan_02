package com.ygaps.travelapp.AppHelper;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class TokenStorage extends Application {
    private static TokenStorage sInstance;
    private SharedPreferences sharedPreferences;
    private String key = "TOKEN";

    String accessToken=null;
    Integer userId;

    @Override
    public void onCreate(){
        super.onCreate();

        sInstance = this;
        sharedPreferences = getSharedPreferences("TOKEN_STORAGE", Context.MODE_PRIVATE);
        accessToken = retrieveTokenFromSharedPrefs();
        userId = getUserIdFromSharedPrefs();
    }

    public static TokenStorage getInstance() {
        return sInstance;
    }

    private String retrieveTokenFromSharedPrefs() {

        return sharedPreferences.getString(key,null);
    }

    private Integer getUserIdFromSharedPrefs(){
        return sharedPreferences.getInt("Id",-1);
    }

    public void setToken(String token, Integer id) {
        accessToken = token;
        userId= id;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Id",userId);
        editor.putString(key, token);

        editor.apply();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Integer getUserId(){
        return userId;
    }

    public boolean hasLoggedIn() {
        return getAccessToken() != null && getUserId()!=null;
    }

    public boolean hasTokenExpired() {
       return false;
    }

    public void removeToken(){
        sInstance.accessToken=null;
        sInstance.userId=null;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.remove("Id");
        editor.apply();
    }
}
