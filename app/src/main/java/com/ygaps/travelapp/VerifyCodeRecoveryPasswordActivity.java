package com.ygaps.travelapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ygaps.travelapp.ApiService.APIRetrofitCreator;
import com.ygaps.travelapp.ApiService.APITour;
import com.ygaps.travelapp.AppHelper.DialogProgressBar;
import com.ygaps.travelapp.Model.MessageResponse;
import com.ygaps.travelapp.Model.RequestOTPPassWord;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyCodeRecoveryPasswordActivity extends AppCompatActivity {

    private APITour apiTour;
    private EditText edtOTPCode;
    private EditText edtNewPassword;
    private EditText edtNewPasswordComfirmation;
    private Button btnSend;
    private RequestOTPPassWord requestOTPPassWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code_recovery_password);
        getSupportActionBar().hide();

        Intent requestOtp=getIntent();
        if(requestOtp.hasExtra("requestOTP")){
            requestOTPPassWord = (RequestOTPPassWord) requestOtp.getSerializableExtra("requestOTP");
            init();
        }


    }
    private void init(){
        apiTour =  new APIRetrofitCreator().getAPIService();
        edtOTPCode = findViewById(R.id.input_otp);
        edtNewPassword = findViewById(R.id.input_new_password);
        edtNewPasswordComfirmation = findViewById(R.id.input_new_password_confirmation);
        btnSend = findViewById(R.id.btn_submit);

        btnSend.setOnClickListener(v -> {
            String otp = edtOTPCode.getText().toString();
            String newPassword = edtNewPassword.getText().toString();
            String newPasswordConfirmation = edtNewPasswordComfirmation.getText().toString();
            if(otp.isEmpty()){
                Toast.makeText(VerifyCodeRecoveryPasswordActivity.this, R.string.empty_otp, Toast.LENGTH_SHORT).show();
                return;
            }
            if(newPassword.isEmpty()){
                Toast.makeText(VerifyCodeRecoveryPasswordActivity.this, R.string.empty_new_password, Toast.LENGTH_SHORT).show();
                return;
            }
            if(newPasswordConfirmation.isEmpty()){
                Toast.makeText(VerifyCodeRecoveryPasswordActivity.this, R.string.empty_new_password_confirm, Toast.LENGTH_SHORT).show();
                return;
            }
            if(!newPassword.equals(newPasswordConfirmation)){
                Toast.makeText(VerifyCodeRecoveryPasswordActivity.this, R.string.password_not_same, Toast.LENGTH_SHORT).show();
                return;
            }

            DialogProgressBar.showProgress(VerifyCodeRecoveryPasswordActivity.this);
            apiTour.verifyPasswordRecovery(requestOTPPassWord.getUserId(),newPassword,otp).enqueue(new Callback<MessageResponse>() {
                @Override
                public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                    if(response.isSuccessful()){
                        Toast.makeText(VerifyCodeRecoveryPasswordActivity.this, R.string.recovery_password_successfully, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(VerifyCodeRecoveryPasswordActivity.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else if(response.code()==403){
                        Toast.makeText(VerifyCodeRecoveryPasswordActivity.this, R.string.wrong_or_expired_otp, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(VerifyCodeRecoveryPasswordActivity.this, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                    }
                    DialogProgressBar.closeProgress();
                }

                @Override
                public void onFailure(Call<MessageResponse> call, Throwable t) {
                    Toast.makeText(VerifyCodeRecoveryPasswordActivity.this, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                    DialogProgressBar.closeProgress();
                }
            });
        });
    }
}
