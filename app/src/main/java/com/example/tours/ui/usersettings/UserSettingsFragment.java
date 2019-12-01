package com.example.tours.ui.usersettings;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.tours.ApiService.APIRetrofitCreator;
import com.example.tours.ApiService.APITour;
import com.example.tours.AppHelper.TokenStorage;
import com.example.tours.CreateTourActivity;
import com.example.tours.HomeActivity;
import com.example.tours.MainActivity;
import com.example.tours.Model.MessageResponse;
import com.example.tours.Model.UserInfo;
import com.example.tours.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSettingsFragment extends Fragment {
    private UserSettingsViewModel userSettingsViewModel;
    private ImageView  btnSelectAvatar;
    private CircleImageView userAvatar;
    private TextView tvUserFullName;
    private TextView tvUserPhone;
    private TextView tvUserEmaill;
    private TextView tvUserAddress;
    private TextView tvDateOfBirth;
    private Button btnLogout;
    private Button btnEditInfo;
    private Button btnChangePassword;
    private APITour apiTour;
    private UserInfo userInfo;

    private Dialog dialogSelectAvatar;
    private static final int IMG_RQ = 1001;

    private Button btnSelectAnotherImage;
    private Button btnSaveAvatar;
    private ImageView tempImage;
    private String imgBase64Format;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        userSettingsViewModel =
//                ViewModelProviders.of(this).get(UserSettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_user_settings, container, false);
//        final TextView textView = root.findViewById(R.id.text_map);
//
//        userSettingsViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        init(root);

        btnSelectAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMG_RQ);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TokenStorage.getInstance().removeToken();
                Intent intent = new Intent(root.getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        return root;
    }

    private void init(View root) {
        btnSelectAvatar = root.findViewById(R.id.btn_select_avatar);
        userAvatar = root.findViewById(R.id.user_avatar);
        tvUserFullName = root.findViewById(R.id.user_full_name);
        tvUserPhone = root.findViewById(R.id.user_phone);
        tvUserEmaill = root.findViewById(R.id.user_email);
        tvUserAddress = root.findViewById(R.id.user_address);
        tvDateOfBirth = root.findViewById(R.id.user_birth_day);
        btnLogout = root.findViewById(R.id.user_logout);
        btnEditInfo=root.findViewById(R.id.btn_change_user_info);
        btnChangePassword = root.findViewById(R.id.btn_change_user_password);
        apiTour = new APIRetrofitCreator().getAPIService();

        getUserInfo(root);

        initDialogSelectAvatar(root);
    }

    private void initDialogSelectAvatar(View root){
        dialogSelectAvatar = new Dialog(root.getContext(),R.style.PlacesAutocompleteThemeFullscreen);
        dialogSelectAvatar.setContentView(R.layout.dialog_select_avatar);

        btnSelectAnotherImage= dialogSelectAvatar.findViewById(R.id.select_another_avatar);
        btnSaveAvatar = dialogSelectAvatar.findViewById(R.id.save_avatar);
        tempImage = dialogSelectAvatar.findViewById(R.id.temporary_image);

        btnSaveAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiTour.updateAvatar(TokenStorage.getInstance().getAccessToken(),imgBase64Format).enqueue(new Callback<MessageResponse>() {
                    @Override
                    public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(root.getContext(), R.string.change_avatar_successfully, Toast.LENGTH_SHORT).show();
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            if (Build.VERSION.SDK_INT >= 26) {
                                ft.setReorderingAllowed(false);
                            }
                            ft.detach(UserSettingsFragment.this).attach(UserSettingsFragment.this).commit();
                        }
                        else{
                            if(response.code()==413){
                                Toast.makeText(root.getContext(), R.string.image_too_large, Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(root.getContext(), R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageResponse> call, Throwable t) {
                        Toast.makeText(root.getContext(), R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnSelectAnotherImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMG_RQ);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMG_RQ && resultCode == HomeActivity.RESULT_OK && data != null){
            Uri selectedImg = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImg);
                tempImage.setImageBitmap(bitmap);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] imgBytes = byteArrayOutputStream.toByteArray();

                imgBase64Format = Base64.encodeToString(imgBytes, Base64.DEFAULT);

                dialogSelectAvatar.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getUserInfo(View root){
        apiTour.getUserInfo(TokenStorage.getInstance().getAccessToken()).enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if(response.isSuccessful()){
                    userInfo = response.body();

                    if(userInfo.getAvatar()!=null){
                        Picasso.get().load(userInfo.getAvatar()).into(userAvatar);
                    }
                    tvUserEmaill.setText(userInfo.getFullName());
                    tvUserPhone.setText(userInfo.getPhone());
                    tvUserEmaill.setText(userInfo.getEmail());
                    tvUserAddress.setText(userInfo.getAddress());

                    if(userInfo.getDob()!=null) {
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                        Date date = null;
                        try {
                            date = inputFormat.parse(userInfo.getDob());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String formattedDate = outputFormat.format(date);
                        tvDateOfBirth.setText(formattedDate);
                    }
                }
                else{
                    Toast.makeText(root.getContext(), R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                Toast.makeText(root.getContext(), R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
