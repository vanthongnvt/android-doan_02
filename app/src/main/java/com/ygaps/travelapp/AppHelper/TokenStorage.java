package com.ygaps.travelapp.AppHelper;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.ygaps.travelapp.Model.UserInfo;

public class TokenStorage extends Application {
    private static TokenStorage sInstance;
    private SharedPreferences sharedPreferences;
    private String key = "TOKEN";

    String accessToken=null;
    Integer userId;
    String name;
    String avatar;

    @Override
    public void onCreate(){
        super.onCreate();

        sInstance = this;
        sharedPreferences = getSharedPreferences("TOKEN_STORAGE", Context.MODE_PRIVATE);
        accessToken = retrieveTokenFromSharedPrefs();
        userId = getUserIdFromSharedPrefs();
        name = getNameFromSharedPrefs();
        avatar = getAvatarFromSharedPrefs();
    }

    private String getAvatarFromSharedPrefs() {
        return sharedPreferences.getString("avatar",null);
    }

    private String getNameFromSharedPrefs() {
        return sharedPreferences.getString("name",null);
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
    public void setUserInfo(String name,String avatar){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(avatar!=null) {
            this.avatar =avatar;
            editor.putString("avatar", avatar);
        }
        editor.putString("name",name);
        this.name=name;
        editor.apply();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Integer getUserId(){
        return userId;
    }
    public String getName(){
        if(name==null){
            return "<ID: "+ userId+" >";
        }
        return name;
    }

    public String getAvatar(){
        return avatar;
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
        editor.remove("Id").remove("avatar").remove("name");
        editor.apply();
    }
}
