package com.example.tours;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tours.ApiService.APIRetrofitCreator;
import com.example.tours.ApiService.APITour;
import com.example.tours.Model.Auth;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
    private CallbackManager mCallbackManager;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.login_title_bar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorCustomPrimary)));

        //khoi tao doi tuong de goi api
        apiTour = new APIRetrofitCreator().getAPIService();

        btnLogin = findViewById(R.id.btn_login);
        btnFbLogin = findViewById(R.id.btn_login_fb);
        btnGGLogin = findViewById(R.id.btn_login_gg);
        linkForgotPassword = findViewById(R.id.link_forgot_password);
        linkSignUp = findViewById(R.id.link_sign_up);
        txtEmailPhone = findViewById(R.id.login_input_email_phone);
        txtPassword = findViewById(R.id.login_input_password);

        //fb
        mCallbackManager = CallbackManager.Factory.create();

        //gg
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestIdToken(getString(R.string.gg_server_client_id)).requestServerAuthCode(getString(R.string.gg_server_client_id))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailorPhone = txtEmailPhone.getText().toString();
                if (emailorPhone.isEmpty()) {
                    Toast.makeText(MainActivity.this, R.string.login_empty_emailphone, Toast.LENGTH_SHORT).show();
                    return;
                }
                String password = txtPassword.getText().toString();
                if (password.isEmpty()) {
                    Toast.makeText(MainActivity.this, R.string.login_empty_password, Toast.LENGTH_SHORT).show();
                    return;
                }

                normaLogin(emailorPhone, password);
            }
        });

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String accessToken = loginResult.getAccessToken().getToken();

                facebookLogin(accessToken);
            }

            @Override
            public void onCancel() {
//                mTvInfo.setText("Login canceled.");
            }

            @Override
            public void onError(FacebookException e) {
//                mTvInfo.setText("Login failed.");
            }
        });

        btnFbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile"));
            }
        });

        btnGGLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 96);
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

    private void startNewActivity(Auth mAuth) {
        Intent itenthome = new Intent(MainActivity.this, HomeActivity.class);
        itenthome.putExtra("Auth", mAuth);
        startActivity(itenthome);
    }

    private void normaLogin(String emailorPhone, String password) {
        //goi api, su dung queue de thuc hien tac vu chay nen
        apiTour.normalLogin(emailorPhone, password).enqueue(new Callback<Auth>() {
            @Override
            public void onResponse(Call<Auth> call, Response<Auth> response) {
                //200 - OK
                if (response.isSuccessful()) {
                    Auth mAuthObject = response.body();

                    //chuyen sang man hinh danh sach tour
                    startNewActivity(mAuthObject);

                }

                //400 - 404 - 500
                else {
                    Toast.makeText(MainActivity.this, R.string.login_not_existed, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Auth> call, Throwable t) {
                Toast.makeText(MainActivity.this, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 96) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            String ggToken = account.getIdToken();
            String authCode = account.getServerAuthCode();

            getAccessTokenGGandLogin(authCode);

        } catch (ApiException e) {

            Log.d("GG_SIGN_IN", "Failed");
            Toast.makeText(MainActivity.this, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // ...
            }
        });
    }

    private void facebookLogin(String accessToken) {
        apiTour.facebookLogin(accessToken).enqueue(new Callback<Auth>() {
            @Override
            public void onResponse(Call<Auth> call, Response<Auth> response) {
                if (response.isSuccessful()) {
                    startNewActivity(response.body());
                } else {
                    Toast.makeText(MainActivity.this, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Auth> call, Throwable t) {
                Toast.makeText(MainActivity.this, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAccessTokenGGandLogin(String authCode) {

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("client_id", getString(R.string.gg_server_client_id))
                .add("client_secret", getString(R.string.gg_app_secret))
                .add("redirect_uri", "")
                .add("code", authCode)
                .build();
        final Request request = new Request.Builder()
                .url("https://www.googleapis.com/oauth2/v4/token")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
//                Log.e(LOG_TAG, e.toString());
                Toast.makeText(MainActivity.this, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String accessTokenGG = jsonObject.get("access_token").toString();
                    Log.i("GG_TOKEN", accessTokenGG);
                    googleLogin(accessTokenGG);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void googleLogin(String accessToken) {
        apiTour.googleLogin(accessToken).enqueue(new Callback<Auth>() {
            @Override
            public void onResponse(Call<Auth> call, Response<Auth> response) {
                if (response.isSuccessful()) {
                    startNewActivity(response.body());
                } else {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.errorBody().string());
                        String message = jsonObject.getString("message");

                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Auth> call, Throwable t) {
                Toast.makeText(MainActivity.this, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
