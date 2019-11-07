package com.example.tours;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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

public class RegisterActivity extends AppCompatActivity {
    EditText edtEmail, edtPassword, edtFullName, edtPhone, edtAddress, edtDob;
    RadioButton rbtnMale, rbtnFemale;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
        String strEmail = "",strPass = "", strFullName = "", strPhone = "", strAddress = "", strDob = "";
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
                Number numGender = 0;
                if(rbtnMale.isChecked()){
                    numGender = 1;
                }
                else if(rbtnFemale.isChecked()){
                    numGender = 0;
                }
            }
        });

        // khoi tao api
        APITour apiTour =  new APIRetrofitCreator().getAPIService();

        // goi api va gui rq
        apiTour.Register(strPass, strFullName, strEmail, strPhone, strAddress, strDob, numGender).enqueue(new Callback<Auth>() {
            @Override
            public void onResponse(Call<Auth> call, Response<Auth> response) {
                // 200 - OK
                if(response.isSuccessful()){
                    Auth mAuthObject = response.body();
                }

                //400 - 404
                else{
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.errorBody().string());
                        String message = jsonObject.getString("message");
                        Toast.makeText(RegisterActivity.this, message + " ahuhu", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<Auth> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Failed to fetch Api", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void init(){

        edtEmail = (EditText) findViewById(R.id.register_input_email);
        edtPassword = (EditText) findViewById(R.id.register_input_password);
        edtFullName = (EditText) findViewById(R.id.register_input_fullname);
        edtPhone = (EditText) findViewById(R.id.register_input_phone);
        edtAddress = (EditText) findViewById(R.id.register_input_address);
        edtDob = (EditText) findViewById(R.id.register_input_dob);
        rbtnFemale = (RadioButton) findViewById(R.id.rbtn_register_female);
        rbtnMale = (RadioButton) findViewById(R.id.rbtn_register_male);
        btnRegister = (Button) findViewById(R.id.btn_register);
    }
}


