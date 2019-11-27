package com.example.tours;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tours.ApiService.APIRetrofitCreator;
import com.example.tours.ApiService.APITour;
import com.example.tours.AppHelper.TokenStorage;
import com.example.tours.Model.Auth;
import com.example.tours.Model.AuthRegister;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterActivity extends AppCompatActivity {
    private APITour apiTour;
    private EditText edtEmail, edtPassword, edtConfirmPass, edtFullName, edtPhone, edtAddress, edtDob;
    private RadioButton rbtnMale, rbtnFemale;
    private Button btnRegister;
    private TextView tvLinkLogin;

    public static String EMAIL = "EMAIL";
    public static String PASS = "PASS";
    public static String BUNDLE = "BUNDLE";
    public static String AUTH_REGISTER = "AUTH_REGISTER";

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

        // hien thi date picker cho muc nhap dob:
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yyyy"; //
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                edtDob.setText(sdf.format(myCalendar.getTime()));
            }

        };
        edtDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(RegisterActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // nut dang ki
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEmail = edtEmail.getText().toString().trim();
                String strPass = edtPassword.getText().toString().trim();
                String strConfirmPass = edtConfirmPass.getText().toString().trim();
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
                if (strPhone.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, R.string.register_empty_phone, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!strPass.equals(strConfirmPass)) {
                    Toast.makeText(RegisterActivity.this, R.string.not_same_pass, Toast.LENGTH_SHORT).show();
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
        edtConfirmPass = (EditText) findViewById(R.id.register_input_confirm_password);
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

                    loginInRegister(strEmail, strPass);
                }

                //400 - 503
                else {
                    if (response.code() == 503) {
                        Toast.makeText(RegisterActivity.this, R.string.register_server_error, Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 400) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.errorBody().string());
                            if (jsonObject.getString("message").contains("Email already registered")) {
                                Toast.makeText(RegisterActivity.this, R.string.register_email_existed, Toast.LENGTH_SHORT).show();
                            } else if (jsonObject.getString("message").contains("Phone already registered")) {
                                Toast.makeText(RegisterActivity.this, R.string.phone_email_existed, Toast.LENGTH_SHORT).show();
                            } else if (jsonObject.getString("message").contains("Invalid email")) {
                                Toast.makeText(RegisterActivity.this, R.string.register_invalid_email, Toast.LENGTH_SHORT).show();
                            } else if (jsonObject.getString("message").contains("Invalid phone")) {
                                Toast.makeText(RegisterActivity.this, R.string.register_invalid_phone, Toast.LENGTH_SHORT).show();
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

    private void loginInRegister(String emailorPhone, String password) {
        //goi api, su dung queue de thuc hien tac vu chay nen
        apiTour.normalLogin(emailorPhone, password).enqueue(new Callback<Auth>() {
            @Override
            public void onResponse(Call<Auth> call, Response<Auth> response) {
                //200 - OK
                Auth mAuthObject = response.body();
                //chuyen sang man hinh home
                TokenStorage.getInstance().setToken(mAuthObject.getToken(),mAuthObject.getUserId());
                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<Auth> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
            }
        });
    }
}


