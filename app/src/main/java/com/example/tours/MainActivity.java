package com.example.tours;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

    private APITour apiTour;
    private Button btnLogin;
    private Button btnFbLogin;
    private Button btnGGLogin;
    private TextView linkForgotPassword;
    private TextView linkSignUp;
    private EditText txtEmailPhone;
    private EditText txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.login_title_bar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorCustomPrimary)));

        //khoi tao doi tuong de goi api
        apiTour =  new APIRetrofitCreator().getAPIService();

        btnLogin=findViewById(R.id.btn_login);
        btnFbLogin=findViewById(R.id.btn_login_fb);
        btnGGLogin=findViewById(R.id.btn_login_gg);
        linkForgotPassword=findViewById(R.id.link_forgot_password);
        linkSignUp=findViewById(R.id.link_sign_up);
        txtEmailPhone=findViewById(R.id.login_input_email_phone);
        txtPassword=findViewById(R.id.login_input_password);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailorPhone = txtEmailPhone.getText().toString();
                if(emailorPhone.isEmpty()){
                    Toast.makeText(MainActivity.this, R.string.login_empty_emailphone, Toast.LENGTH_SHORT).show();
                    return;
                }
                String password = txtPassword.getText().toString();
                if(password.isEmpty()){
                    Toast.makeText(MainActivity.this, R.string.login_empty_password, Toast.LENGTH_SHORT).show();
                    return;
                }

                normaLogin(emailorPhone,password);
            }
        });

        btnFbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, R.string.unavailable_func, Toast.LENGTH_SHORT).show();
            }
        });

        btnGGLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, R.string.unavailable_func, Toast.LENGTH_SHORT).show();
            }
        });

        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(itent);
            }
        });




    }

    private void normaLogin(String emailorPhone, String password){
        //goi api, su dung queue de thuc hien tac vu chay nen
        apiTour.normalLogin(emailorPhone,password).enqueue(new Callback<Auth>() {
            @Override
            public void onResponse(Call<Auth> call, Response<Auth> response) {
                //200 - OK
                if(response.isSuccessful()){
                    Auth mAuthObject = response.body();

                    //chuyen sang man hinh danh sach tour
                    Intent itenthome= new Intent(MainActivity.this,HomeActivity.class);
                    itenthome.putExtra("Auth",mAuthObject);
                    startActivity(itenthome);
                }

                //400 - 404 - 500
                else{
                        Toast.makeText(MainActivity.this, R.string.login_not_existed, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Auth> call, Throwable t) {
                Toast.makeText(MainActivity.this, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
