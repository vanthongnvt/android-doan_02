package com.example.tours;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.tours.ApiService.APIRetrofitCreator;
import com.example.tours.ApiService.APITour;
import com.example.tours.Model.Auth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private  APITour apiTour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //khoi tao doi tuong de goi api
        APITour apiTour =  new APIRetrofitCreator().getAPIService();

        String emailPhone = "someemail";
        String password ="1234";

        //goi api, su dung queue de thuc hien tac vu chay nen
        apiTour.normalLogin(emailPhone,password).enqueue(new Callback<Auth>() {
            @Override
            public void onResponse(Call<Auth> call, Response<Auth> response) {
                //200 - OK
                if(response.isSuccessful()){
                    Auth mAuthObject = response.body();
                }

                //400 - 404 - 500
                else{
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.errorBody().string());
                        String message = jsonObject.getString("message");
                        Toast.makeText(MainActivity.this, message + " ahhi", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<Auth> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to fetch Api", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
