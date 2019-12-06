package com.ygaps.travelapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ygaps.travelapp.ApiService.APIRetrofitCreator;
import com.ygaps.travelapp.ApiService.APITour;
import com.ygaps.travelapp.AppHelper.DialogProgressBar;
import com.ygaps.travelapp.Model.RequestOTPPassWord;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordRecoveryActivity extends AppCompatActivity {

    private EditText edtEmailOrPhone;
    private Button btnSendOTP;
    private TextView tvToLogin;
    private APITour apiTour;
    private RequestOTPPassWord requestOTPPassWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);
        getSupportActionBar().hide();
        init();
    }

    private void init(){
        edtEmailOrPhone = findViewById(R.id.input_email_phone);
        btnSendOTP = findViewById(R.id.btn_send_otp);
        tvToLogin = findViewById(R.id.link_login);
        apiTour = new APIRetrofitCreator().getAPIService();

        btnSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailOrPhone = edtEmailOrPhone.getText().toString();
                if(emailOrPhone.isEmpty()){
                    Toast.makeText(PasswordRecoveryActivity.this, R.string.login_empty_emailphone, Toast.LENGTH_SHORT).show();
                    return;
                }
                String type= "phone";
                if(emailOrPhone.contains("@")){
                    type="email";
                }
                DialogProgressBar.showProgress(PasswordRecoveryActivity.this);
                apiTour.requestOTPPassword(type,emailOrPhone).enqueue(new Callback<RequestOTPPassWord>() {
                    @Override
                    public void onResponse(Call<RequestOTPPassWord> call, Response<RequestOTPPassWord> response) {
                        if(response.isSuccessful()) {
                            requestOTPPassWord = response.body();
                            Toast.makeText(PasswordRecoveryActivity.this, R.string.sent_opt, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PasswordRecoveryActivity.this,VerifyCodeRecoveryPasswordActivity.class);
                            intent.putExtra("requestOTP",requestOTPPassWord);
                            startActivity(intent);
                        }
                        else if(response.code()==400){
                            Toast.makeText(PasswordRecoveryActivity.this, R.string.register_invalid_email, Toast.LENGTH_SHORT).show();
                        }
                        else if(response.code()==404){
                            Toast.makeText(PasswordRecoveryActivity.this, R.string.email_phone_not_exist, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(PasswordRecoveryActivity.this, R.string.wrong_parameter_type, Toast.LENGTH_SHORT).show();
                        }
                        DialogProgressBar.closeProgress();
                    }

                    @Override
                    public void onFailure(Call<RequestOTPPassWord> call, Throwable t) {
                        Toast.makeText(PasswordRecoveryActivity.this, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                        DialogProgressBar.closeProgress();
                    }
                });
            }
        });

        tvToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(PasswordRecoveryActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
