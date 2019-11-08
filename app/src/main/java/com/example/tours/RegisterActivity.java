package com.example.tours;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tours.ApiService.APIRetrofitCreator;
import com.example.tours.ApiService.APITour;
import com.example.tours.Model.Auth;
import com.example.tours.Model.AuthRegister;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterActivity extends AppCompatActivity {
    private APITour apiTour;
    private EditText edtEmail, edtPassword, edtFullName, edtPhone, edtAddress, edtDob;
    private RadioButton rbtnMale, rbtnFemale;
    private Button btnRegister;
    private TextView tvLinkLogin;

    public static String EMAIL = "EMAIL";
    public static String PASS = "PASS";
    public static String BUNDLE = "BUNDLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Đăng ký");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorCustomPrimary)));

        init();

        // khoi tao api
        apiTour = new APIRetrofitCreator().getAPIService();

        String strEmail = "", strPass = "", strFullName = "", strPhone = "", strAddress = "", strDob = "";
        Number numGender = 0;
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEmail = edtEmail.getText().toString().trim();
                String strPass = edtPassword.getText().toString().trim();
                String strFullName = edtFullName.getText().toString().trim();
                String strPhone = edtPhone.getText().toString().trim();
                String strAddress = edtAddress.getText().toString().trim();
                String strDob = edtDob.getText().toString().trim();
                Number numGender = -1;
                if (rbtnMale.isChecked()) {
                    numGender = 1;
                } else if (rbtnFemale.isChecked()) {
                    numGender = 0;
                }

                // kiem tra dau vao:
                if (strEmail.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, R.string.register_empty_email, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (strPass.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, R.string.register_empty_pass, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (strFullName.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, R.string.register_empty_fullname, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (strPhone.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, R.string.register_empty_phone, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (strAddress.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, R.string.register_empty_address, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (strDob.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, R.string.register_empty_dob, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (numGender.intValue() == -1) {
                    Toast.makeText(RegisterActivity.this, R.string.register_empty_gender, Toast.LENGTH_SHORT).show();
                    return;
                }

                Register(strPass, strFullName, strEmail, strPhone, strAddress, strDob, numGender);

            }
        });

        tvLinkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }

    public void init() {

        edtEmail = (EditText) findViewById(R.id.register_input_email);
        edtPassword = (EditText) findViewById(R.id.register_input_password);
        edtFullName = (EditText) findViewById(R.id.register_input_fullname);
        edtPhone = (EditText) findViewById(R.id.register_input_phone);
        edtAddress = (EditText) findViewById(R.id.register_input_address);
        edtDob = (EditText) findViewById(R.id.register_input_dob);
        rbtnFemale = (RadioButton) findViewById(R.id.rbtn_register_female);
        rbtnMale = (RadioButton) findViewById(R.id.rbtn_register_male);
        btnRegister = (Button) findViewById(R.id.btn_register);
        tvLinkLogin = (TextView) findViewById(R.id.link_login);
    }

    public void Register(final String strPass, String strFullName, final String strEmail, String strPhone, String strAddress, String strDob, Number numGender) {
        // goi api va gui rq
        apiTour.Register(strPass, strFullName, strEmail, strPhone, strAddress, strDob, numGender).enqueue(new Callback<AuthRegister>() {
            @Override
            public void onResponse(Call<AuthRegister> call, Response<AuthRegister> response) {
                // 200 - OK
                if (response.isSuccessful()) {
                    AuthRegister mAuthRegisterObject = response.body();
                    Toast.makeText(RegisterActivity.this, R.string.successful_register, Toast.LENGTH_SHORT).show();

                    //chuyen den man hinh login
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(EMAIL, strEmail);
                    bundle.putString(PASS, strPass);
                    intent.putExtra(BUNDLE, bundle);
                    startActivity(intent);
                }

                //400 - 503
                else {
                    if(response.code() == 503){
                        Toast.makeText(RegisterActivity.this, R.string.register_server_error, Toast.LENGTH_SHORT).show();
                    }
                    else if(response.code() == 400){
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.errorBody().string());
                            if(jsonObject.getString("message").contains("Email already registered")){
                                Toast.makeText(RegisterActivity.this, R.string.register_email_existed, Toast.LENGTH_SHORT).show();
                            }
                            else if(jsonObject.getString("message").contains("Phone already registered")){
                                Toast.makeText(RegisterActivity.this, R.string.phone_email_existed, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthRegister> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
            }
        });
    }
}


