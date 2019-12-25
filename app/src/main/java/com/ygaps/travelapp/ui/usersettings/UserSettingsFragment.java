package com.ygaps.travelapp.ui.usersettings;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.iid.FirebaseInstanceId;
import com.ygaps.travelapp.ApiService.APIRetrofitCreator;
import com.ygaps.travelapp.ApiService.APITour;
import com.ygaps.travelapp.AppHelper.DialogProgressBar;
import com.ygaps.travelapp.AppHelper.TokenStorage;
import com.ygaps.travelapp.HomeActivity;
import com.ygaps.travelapp.MainActivity;
import com.ygaps.travelapp.Model.MessageResponse;
import com.ygaps.travelapp.Model.UpdateUserInfo;
import com.ygaps.travelapp.Model.UserInfo;
import com.ygaps.travelapp.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
    private TextView tvGender;
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
    private UserInfo userInfoForUpdateDialg;
    private View root;
    private Uri URI;
    private boolean isChangeName =false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        userSettingsViewModel =
//                ViewModelProviders.of(this).get(UserSettingsViewModel.class);
        root = inflater.inflate(R.layout.fragment_user_settings, container, false);
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
                intent.setType("image/*");
                startActivityForResult(intent, IMG_RQ);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogProgressBar.showProgress(getContext());
                String fmcToken= FirebaseInstanceId.getInstance().getToken();
                String android_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                apiTour.removeFirebaseToken(TokenStorage.getInstance().getAccessToken(),fmcToken,android_id).enqueue(new Callback<MessageResponse>() {
                    @Override
                    public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                        if(!response.isSuccessful()){
                            Toast.makeText(root.getContext(), R.string.server_err, Toast.LENGTH_SHORT).show();
                        }
                        TokenStorage.getInstance().removeToken();
                        SharedPreferences sharedPreferences = getContext().getSharedPreferences("FOLLOW_TOUR", Context.MODE_PRIVATE);
                        sharedPreferences.edit().remove("tourId").apply();
                        DialogProgressBar.closeProgress();
                        Intent intent = new Intent(root.getContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<MessageResponse> call, Throwable t) {
                        DialogProgressBar.closeProgress();
                        Toast.makeText(root.getContext(), R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUpdateUserInfoDialog();
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChangePassDialog();
            }
        });

        return root;
    }

    private void openChangePassDialog() {
        Dialog dialog = new Dialog(getContext(), R.style.DialogSlideAnimation);
        dialog.setContentView(R.layout.dialog_update_password);
        EditText edtOldPass, edtNewPass, edtConfirmPass;
        Button btnUpdate;
        edtOldPass = (EditText) dialog.findViewById(R.id.updatepass_oldpass);
        edtNewPass =(EditText) dialog.findViewById(R.id.updatepass_newpass);
        edtConfirmPass =(EditText) dialog.findViewById(R.id.updatepass_confirmpass);
        btnUpdate = (Button) dialog.findViewById(R.id.btn_update_pass);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPass = edtOldPass.getText().toString().trim();
                String newPass = edtNewPass.getText().toString().trim();
                String confirmPass = edtConfirmPass.getText().toString().trim();

                if(oldPass.isEmpty()){
                    Toast.makeText(getActivity(), "Bạn chưa nhập mật khẩu cũ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(newPass.isEmpty()){
                    Toast.makeText(getActivity(), "Bạn chưa nhập mật khẩu mới", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(confirmPass.isEmpty()){
                    Toast.makeText(getActivity(), "Bạn chưa xác nhận mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!newPass.equals(confirmPass)){
                    Toast.makeText(getActivity(), "Mật khẩu mới không trùng khớp", Toast.LENGTH_SHORT).show();
                    return;
                }
                DialogProgressBar.showProgress(getContext());
                apiTour.updatePassword(TokenStorage.getInstance().getAccessToken(), userInfo.getId(), oldPass, newPass).enqueue(new Callback<UpdateUserInfo>() {
                    @Override
                    public void onResponse(Call<UpdateUserInfo> call, Response<UpdateUserInfo> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(getActivity(), "Cập nhật mật khẩu thành công", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                        else if(response.code() == 400){
                            Toast.makeText(getActivity(), "Mật khẩu hiện tại không chính xác", Toast.LENGTH_SHORT).show();
                        }
                        else if(response.code() ==404){
                            Toast.makeText(getActivity(), "Tài khoản email / phone không tồn tại", Toast.LENGTH_SHORT).show();
                        }
                        else if(response.code() == 500){
                            Toast.makeText(getActivity(), "Lỗi Server, không thể cập nhật mật khẩu", Toast.LENGTH_SHORT).show();
                        }
                        DialogProgressBar.closeProgress();
                    }

                    @Override
                    public void onFailure(Call<UpdateUserInfo> call, Throwable t) {
                        Toast.makeText(getActivity(), R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                        DialogProgressBar.closeProgress();
                    }
                });

            }
        });

        dialog.show();
    }

    private void openUpdateUserInfoDialog() {
        Dialog dialog = new Dialog(getContext(), R.style.DialogSlideAnimation);
        dialog.setContentView(R.layout.dialog_update_info_user);
        EditText edtName, edtEmail, edtPhone, edtDob;
        RadioButton rbtnMale, rbtnFemale;
        edtName = (EditText) dialog.findViewById(R.id.updateuserinfo_name);
//        edtEmail = (EditText) dialog.findViewById(R.id.updateuserinfo_email);
//        edtPhone = (EditText) dialog.findViewById(R.id.updateuserinfo_phone);
        edtDob = (EditText) dialog.findViewById(R.id.updateuserinfo_dob);
        rbtnMale = (RadioButton) dialog.findViewById(R.id.updateuserinfo_rbtn_male);
        rbtnFemale = (RadioButton) dialog.findViewById(R.id.updateuserinfo_rbtn_female);
        Button btnUpdate = (Button) dialog.findViewById(R.id.btn_update_user_info);

        edtName.setText(userInfo.getFullName());
//        edtEmail.setText(userInfo.getEmail());
//        edtPhone.setText(userInfo.getPhone());
        String strGetDob =userInfo.getDob();
        if(strGetDob!=null) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date dateDob = null;
            try {
                dateDob = df.parse(strGetDob);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            DateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
            String dob = df2.format(dateDob);
            edtDob.setText(dob);
        }
        if(userInfo.getGender() != null){
            if(userInfo.getGender() == 0)
                rbtnFemale.setChecked(true);
            else if(userInfo.getGender() == 1)
                rbtnMale.setChecked(true);
        }

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
                new DatePickerDialog(getActivity(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString().trim();
                if(name.isEmpty())
                    name = null;
//                String email = edtEmail.getText().toString().trim();
//                String phone = edtPhone.getText().toString().trim();
                Number gender = null;
                if(rbtnMale.isChecked())
                    gender = 1;
                else if(rbtnFemale.isChecked())
                    gender = 0;
                String strDob = edtDob.getText().toString().trim();
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                Date dob = null;
                try {
                    dob = df.parse(strDob);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Number finalGender = gender;
                Date finalDob = dob;
                String finalName = name;
//                if(userInfo.getTypeLogin()==0) {
//                    if (email.isEmpty()) {
//                        Toast.makeText(getActivity(), "Email không được để trống", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    if (phone.isEmpty()) {
//                        Toast.makeText(getActivity(), "Số điện thoại không được để trống", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                }
                if(!finalName.equals(userInfo.getFullName())) {
                    userInfo.setFullName(finalName);
                    isChangeName= true;
                }
                DialogProgressBar.showProgress(getContext());
                apiTour.updateUserInfo(TokenStorage.getInstance().getAccessToken(), finalName, null, null, finalGender, finalDob).enqueue(new Callback<UpdateUserInfo>() {
                    @Override
                    public void onResponse(Call<UpdateUserInfo> call, Response<UpdateUserInfo> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(getActivity(), "Cập nhật thông tin tài khoản thành công", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            if(isChangeName) {
                                TokenStorage.getInstance().setUserInfo(userInfo.getFullName(),null);
                                isChangeName=false;
                            }
                            getUserInfo(root);
                        }
                        else if(response.code() == 400){
                            Toast.makeText(getActivity(), "Thông tin nhập không hợp lệ", Toast.LENGTH_SHORT).show();
                        }
                        DialogProgressBar.closeProgress();
                    }

                    @Override
                    public void onFailure(Call<UpdateUserInfo> call, Throwable t) {
                        Toast.makeText(getActivity(), R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                        DialogProgressBar.closeProgress();
                    }
                });
            }
        });


        dialog.show();

    }



    private void init(View root) {
        btnSelectAvatar = root.findViewById(R.id.btn_select_avatar);
        userAvatar = root.findViewById(R.id.user_avatar);
        tvUserFullName = root.findViewById(R.id.user_full_name);
        tvUserPhone = root.findViewById(R.id.user_phone);
        tvUserEmaill = root.findViewById(R.id.user_email);
        tvUserAddress = root.findViewById(R.id.user_address);
        tvDateOfBirth = root.findViewById(R.id.user_birth_day);
        tvGender = root.findViewById(R.id.user_gender);
        btnLogout = root.findViewById(R.id.user_logout);
        btnEditInfo=root.findViewById(R.id.btn_change_user_info);
        btnChangePassword = root.findViewById(R.id.btn_change_user_password);

        apiTour = new APIRetrofitCreator().getAPIService();

        getUserInfo(root);


        initDialogSelectAvatar(root);
    }

    private void initDialogSelectAvatar(View root){
        dialogSelectAvatar = new Dialog(root.getContext(),R.style.DialogSlideAnimation);
        dialogSelectAvatar.setContentView(R.layout.dialog_select_avatar);

        btnSelectAnotherImage= dialogSelectAvatar.findViewById(R.id.select_another_avatar);
        btnSaveAvatar = dialogSelectAvatar.findViewById(R.id.save_avatar);
        tempImage = dialogSelectAvatar.findViewById(R.id.temporary_image);

        btnSaveAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogProgressBar.showProgress(getContext());
//                Log.d("OkHttp", "onClick: "+URI);
                File file = new File(getRealPathFromURI(getContext(),URI));
//                Log.d("OkHttp", "onClick: "+file.getAbsolutePath());
                RequestBody fileReqBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

                MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);

                apiTour.updateAvatar2(TokenStorage.getInstance().getAccessToken(),part).enqueue(new Callback<MessageResponse>() {
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
                                Toast.makeText(root.getContext(), R.string.server_err, Toast.LENGTH_SHORT).show();
                            }
                        }
                        DialogProgressBar.closeProgress();
                    }

                    @Override
                    public void onFailure(Call<MessageResponse> call, Throwable t) {
                        Toast.makeText(root.getContext(), R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                        DialogProgressBar.closeProgress();
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
            URI = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), URI);
                tempImage.setImageBitmap(bitmap);

//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                byte[] imgBytes = byteArrayOutputStream.toByteArray();
//
//                imgBase64Format = Base64.encodeToString(imgBytes, Base64.DEFAULT);

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
                    tvUserFullName.setText(userInfo.getFullName());
                    tvUserPhone.setText(userInfo.getPhone());
                    tvUserEmaill.setText(userInfo.getEmail());
                    tvUserAddress.setText(userInfo.getAddress());
                    Integer gender = userInfo.getGender();
                    if(userInfo.getTypeLogin()!=0){
                        btnChangePassword.setVisibility(View.GONE);
                    }
                    if(gender == null)
                        tvGender.setText("(Trống)");
                    else{
                        if(gender == 0)
                            tvGender.setText("Nữ");
                        else if (gender == 1)
                            tvGender.setText("Nam");
                    }

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

    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            Log.e("AVATAR", "getRealPathFromURI Exception : " + e.toString());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
