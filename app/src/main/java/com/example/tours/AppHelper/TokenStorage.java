package com.example.tours.AppHelper;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class TokenStorage extends Application {
    private static TokenStorage sInstance;
    private SharedPreferences sharedPreferences;
    private String key = "TOKEN";

    String accessToken=null;

    @Override
    public void onCreate(){
        super.onCreate();

        sInstance = this;
        sharedPreferences = getSharedPreferences("TOKEN_STORAGE", Context.MODE_PRIVATE);
        accessToken = retrieveTokenFromSharedPrefs();

    }

    public static TokenStorage getInstance() {
        return sInstance;
    }

    private String retrieveTokenFromSharedPrefs() {

        return sharedPreferences.getString(key,null);
    }

    public void setToken(String token) {
        accessToken = token;
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(key, token);

        editor.apply();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public boolean hasLoggedIn() {
        return getAccessToken() != null;
    }

    public boolean hasTokenExpired() {
       return false;
    }
}
