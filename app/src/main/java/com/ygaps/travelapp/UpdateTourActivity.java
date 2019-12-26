package com.ygaps.travelapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ygaps.travelapp.ApiService.APIRetrofitCreator;
import com.ygaps.travelapp.ApiService.APITour;
import com.ygaps.travelapp.AppHelper.DialogProgressBar;
import com.ygaps.travelapp.AppHelper.TokenStorage;
import com.ygaps.travelapp.Model.UpdateUserTour;
import com.ygaps.travelapp.Model.UserTour;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateTourActivity extends AppCompatActivity {
    private UserTour tour;
    private EditText edtTourName, edtStartDate, edtEndDate, edtAdult, edtChild, edtMinCost, edtMaxCost;
    RadioButton rbtnPrivate, rbtnPublic, rbtnCanceled, rbtnOpen, rbtnStarted, rbtnClosed;
    private TextView tvUpImg;
    private Button btnUpdate;
    private APITour apiTour;
    private Bitmap bitmap;
    private String imgBase64Format; // luu chuoi string base64 cua avatar

    private ImageView avatarInDialog;
    private Button btnchooseInDialog;
    private Button btnUpImgInDialog;

    private static final int UPDATE_IMG_RQ = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_tour);
        setTitle("Sửa chuyến đi");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorCustomPrimary)));
        init();
        handleStatusRadioButton();

        // hien thi date picker cho muc nhap startDate:
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
                edtStartDate.setText(sdf.format(myCalendar.getTime()));
            }

        };
        edtStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(UpdateTourActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // hien thi date picker cho muc nhap endDate:
        final Calendar myCalendar1 = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar1.set(Calendar.YEAR, year);
                myCalendar1.set(Calendar.MONTH, monthOfYear);
                myCalendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yyyy"; //
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                edtEndDate.setText(sdf.format(myCalendar1.getTime()));
            }

        };
        edtEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(UpdateTourActivity.this, date1, myCalendar1.get(Calendar.YEAR), myCalendar1.get(Calendar.MONTH),
                        myCalendar1.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        tvUpImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImgUploadDialog();
            }

            private void showImgUploadDialog() {
                Dialog dialog = new Dialog(UpdateTourActivity.this);
                dialog.setTitle("Chọn ảnh");
                dialog.setContentView(R.layout.dialog_upload_img);
                avatarInDialog = (ImageView) dialog.findViewById(R.id.img_avatar_selected);
                btnchooseInDialog = (Button) dialog.findViewById(R.id.btn_choose_img);
                btnUpImgInDialog = (Button) dialog.findViewById(R.id.btn_upload_img);

                if (imgBase64Format == null) {
                    imgBase64Format = "";
                }
                if (!imgBase64Format.isEmpty()) {
                    avatarInDialog.setImageBitmap(bitmap);
                    avatarInDialog.setVisibility(View.VISIBLE);
                }

                chooseImgBtnClick(); // btnchooseInDialog.setOnClickListeners
                uploadImgBtnClick(dialog); // btnUpLoadInDialog.setOnClickListeners
                dialog.show();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    String id = tour.getId().toString();
                    String name = edtTourName.getText().toString().trim();
                    Number startDate = DateToMiliSeconds(edtStartDate.getText().toString().trim());
                    Number endDate = DateToMiliSeconds(edtEndDate.getText().toString().trim());
                    Number adults = Long.parseLong(edtAdult.getText().toString().trim());
                    Number childs = Long.parseLong(edtChild.getText().toString().trim());
                    Number minCost = Long.parseLong(edtMinCost.getText().toString().trim());
                    Number maxCost = Long.parseLong(edtMaxCost.getText().toString().trim());
                    boolean isPrivate = true;
                    if (rbtnPublic.isChecked()) {
                        isPrivate = false;
                    }
                    Number status = 0;
                    if (rbtnCanceled.isChecked()) {
                        status = -1;
                    } else if (rbtnOpen.isChecked()) {
                        status = 0;
                    } else if (rbtnStarted.isChecked()) {
                        status = 1;
                    } else if (rbtnClosed.isChecked()) {
                        status = 2;
                    }
                    String strAvatar;
                    if (imgBase64Format == null) {
                        strAvatar = null;
                    } else {
                        if (imgBase64Format.isEmpty()) {
                            strAvatar = null;
                        } else {
                            strAvatar = imgBase64Format;
                        }
                    }

                    strAvatar = null; // gui null, vi gui dinh dang base64 server ko response
                    DialogProgressBar.showProgress(UpdateTourActivity.this);
                    apiTour.updateUserTour(TokenStorage.getInstance().getAccessToken(), id, name, startDate, endDate, isPrivate, adults, childs, minCost, maxCost, status, strAvatar).enqueue(new Callback<UpdateUserTour>() {
                        @Override
                        public void onResponse(Call<UpdateUserTour> call, Response<UpdateUserTour> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(UpdateTourActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UpdateTourActivity.this, HomeActivity.class);
                                startActivity(intent);
                            } else if (response.code() == 400) {
                                Toast.makeText(UpdateTourActivity.this, "Thông tin nhập chưa đầy đủ", Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 404) {
                                Toast.makeText(UpdateTourActivity.this, "Tour không tồn tại", Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 403) {
                                Toast.makeText(UpdateTourActivity.this, "Không được phép cập nhật", Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 500) {
                                Toast.makeText(UpdateTourActivity.this, "Lỗi server, không thể cập nhật", Toast.LENGTH_SHORT).show();
                            }
                            DialogProgressBar.closeProgress();
                        }

                        @Override
                        public void onFailure(Call<UpdateUserTour> call, Throwable t) {
                            Toast.makeText(UpdateTourActivity.this, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                            DialogProgressBar.closeProgress();
                        }
                    });
                }
            }
        });

    }

    void init() {
        apiTour = new APIRetrofitCreator().getAPIService();

        Intent intent = getIntent();
        tour = (UserTour) intent.getSerializableExtra("UserTour");

        edtTourName = (EditText) findViewById(R.id.updatetour_tour_name);
        edtStartDate = (EditText) findViewById(R.id.updatetour_start_date);
        edtEndDate = (EditText) findViewById(R.id.updatetour_end_date);
        edtAdult = (EditText) findViewById(R.id.updatetour_adult);
        edtChild = (EditText) findViewById(R.id.updatetour_child);
        edtMinCost = (EditText) findViewById(R.id.updatetour_min_cost);
        edtMaxCost = (EditText) findViewById(R.id.updatetour_max_cost);
        tvUpImg = (TextView) findViewById(R.id.updatetour_upload_image);
        btnUpdate = (Button) findViewById(R.id.btn_update_tour);
        rbtnPrivate = (RadioButton) findViewById(R.id.updatetour_rbtn_private);
        rbtnPublic = (RadioButton) findViewById(R.id.updatetour_rbtn_public);
        rbtnCanceled = (RadioButton) findViewById(R.id.updatetour_rbtn_canceled);
        rbtnOpen = (RadioButton) findViewById(R.id.updatetour_rbtn_open);
        rbtnStarted = (RadioButton) findViewById(R.id.updatetour_rbtn_started);
        rbtnClosed = (RadioButton) findViewById(R.id.updatetour_rbtn_closed);


        if (tour.getName() == null) {
            edtTourName.setText("");
        } else {
            edtTourName.setText(tour.getName());
        }
        Calendar cal = Calendar.getInstance();
        long time = 0;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        if (tour.getStartDate() == null) {
            edtStartDate.setText("00/00/0000");
        } else {
            time = Long.parseLong(tour.getStartDate());
            cal.setTimeInMillis(time);
            Date timeStartDate = cal.getTime();
            edtStartDate.setText(dateFormat.format(timeStartDate));
        }
        if (tour.getEndDate() == null) {
            edtEndDate.setText("00/00/0000");
        } else {
            time = Long.parseLong(tour.getEndDate());
            cal.setTimeInMillis(time);
            Date timeStartDate = cal.getTime();
            edtEndDate.setText(dateFormat.format(timeStartDate));
        }
        if (tour.getAdults() == null) {
            edtAdult.setText("0");
        } else {
            edtAdult.setText(tour.getAdults().toString());
        }
        if (tour.getChilds() == null) {
            edtChild.setText("0");
        } else {
            edtChild.setText(tour.getChilds().toString());
        }
        if (tour.getMinCost() == null) {
            edtMinCost.setText("0");
        } else {
            edtMinCost.setText(tour.getMinCost());
        }
        if (tour.getMaxCost() == null) {
            edtMaxCost.setText("0");
        } else {
            edtMaxCost.setText(tour.getMaxCost());
        }
        // api ko gui isPrivate ve! -> de mac dinh la private
        rbtnPrivate.setChecked(true);
        int status = tour.getStatus().intValue();
        if (status == -1)
            rbtnCanceled.setChecked(true);
        else if(status == 0)
            rbtnOpen.setChecked(true);
        else if(status == 1)
            rbtnStarted.setChecked(true);
        else if(status == 2)
            rbtnClosed.setChecked(true);
    }

    private void handleStatusRadioButton() {
        rbtnCanceled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbtnOpen.setChecked(false);
                rbtnStarted.setChecked(false);
                rbtnClosed.setChecked(false);
            }
        });

        rbtnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbtnCanceled.setChecked(false);
                rbtnStarted.setChecked(false);
                rbtnClosed.setChecked(false);
            }
        });

        rbtnStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbtnCanceled.setChecked(false);
                rbtnOpen.setChecked(false);
                rbtnClosed.setChecked(false);
            }
        });

        rbtnClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbtnCanceled.setChecked(false);
                rbtnOpen.setChecked(false);
                rbtnStarted.setChecked(false);
            }
        });

    }

    private long DateToMiliSeconds(String date) {
        //String date_ = date;
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date mDate = df.parse(date);
            long timeInMilliseconds = mDate.getTime();
            return timeInMilliseconds;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return 0;
    }

    private void chooseImgBtnClick() {
        btnchooseInDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImg();
            }

            private void selectImg() {
//                        Intent intent = new Intent();
//                        intent.setType("image/*");
//                        intent.setAction(Intent.ACTION_GET_CONTENT);
//                        startActivityForResult(intent, IMG_RQ);
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, UPDATE_IMG_RQ);
            }


        });
    }

    private void uploadImgBtnClick(Dialog dialog) {
        btnUpImgInDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] imgBytes = byteArrayOutputStream.toByteArray();

                imgBase64Format = Base64.encodeToString(imgBytes, Base64.DEFAULT);
                Toast.makeText(UpdateTourActivity.this, "Upload ảnh thành công", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    public static boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    // nhan phai hoi chon anh:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_IMG_RQ && resultCode == RESULT_OK && data != null) {
            Uri selectedImg = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImg);
                avatarInDialog.setImageBitmap(bitmap);
                avatarInDialog.setVisibility(View.VISIBLE);

                btnchooseInDialog.setEnabled(false);
                btnUpImgInDialog.setEnabled(true);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == UPDATE_IMG_RQ && resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Đã hủy chọn ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    boolean checkInput() {
        if (edtTourName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Tên tour không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (isValidDate(edtStartDate.getText().toString()) == false) {
            Toast.makeText(this, "Ngày bắt đầu không đúng định dạng dd/MM/yyyy", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (isValidDate(edtEndDate.getText().toString()) == false) {
            Toast.makeText(this, "Ngày kết thúc không đúng định dạng dd/MM/yyyy", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtAdult.getText().toString().isEmpty()) {
            edtAdult.setText("0");
        }
        if (edtChild.getText().toString().isEmpty()) {
            edtChild.setText("0");
        }
        if (edtMinCost.getText().toString().isEmpty()) {
            edtMinCost.setText("0");
        }
        if (edtMaxCost.getText().toString().isEmpty()) {
            edtMaxCost.setText("0");
        }
        return true;
    }

}
