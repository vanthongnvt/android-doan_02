package com.example.tours;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import com.example.tours.Model.Auth;
import com.example.tours.Model.CreateTour;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTourActivity extends AppCompatActivity {

    private EditText edtTourName, edtStartDate, edtEndDate, edtSrcLat, edtSrcLong, edtDestLat, edtDestLong, edtAdult, edtChild, edtMinCost, edtMaxCost;
    private RadioButton rbtnPrivate, rbtnPublic;
    private TextView tvUpImg;
    private Button btnCreate;
    private APITour apiTour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tour);

        // khoi tao:
        init();
        apiTour = new APIRetrofitCreator().getAPIService();

        // get intent lay token dang nhap:
        Intent intent = getIntent();
        final Auth auth= (Auth) intent.getSerializableExtra("Auth");

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
                new DatePickerDialog(CreateTourActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
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
                new DatePickerDialog(CreateTourActivity.this, date1, myCalendar1.get(Calendar.YEAR), myCalendar1.get(Calendar.MONTH),
                        myCalendar1.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get input
                String strTourName = edtTourName.getText().toString().trim();
                String strAvatar = "";
                String strStartDate = edtStartDate.getText().toString().trim();
                String strEndDate = edtEndDate.getText().toString().trim();
                String strSrcLat = edtSrcLat.getText().toString().trim();
                String strSrcLong = edtSrcLong.getText().toString().trim();
                String strDestLat = edtDestLat.getText().toString().trim();
                String strDestLong = edtDestLong.getText().toString().trim();
                String strAdult = edtAdult.getText().toString().trim();
                String strChild = edtChild.getText().toString().trim();
                String strMinCost = edtMinCost.getText().toString().trim();
                String strMaxCost = edtMaxCost.getText().toString().trim();
                Number isPrivate = -1;
                if(rbtnPrivate.isChecked()){
                    isPrivate = 1;
                }
                else if(rbtnPublic.isChecked()){
                    isPrivate = 0;
                }

                // check input
                if(strTourName.isEmpty()){
                    Toast.makeText(CreateTourActivity.this, "Bạn chưa nhập tên tour", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(strStartDate.isEmpty()){
                    Toast.makeText(CreateTourActivity.this, "Bạn chưa chọn ngày bắt đầu tour", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(isValidDate(strStartDate) == false){
                    Toast.makeText(CreateTourActivity.this, "Ngày bắt đầu không đúng định dạng", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(strEndDate.isEmpty()){
                    Toast.makeText(CreateTourActivity.this, "Bạn chưa chọn ngày kết thúc", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(isValidDate(strEndDate) == false){
                    Toast.makeText(CreateTourActivity.this, "Ngày kết thúc không đúng định dạng", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(strSrcLat.isEmpty()){
                    Toast.makeText(CreateTourActivity.this, "Bạn chưa nhập kinh độ cho điểm đi", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(strSrcLong.isEmpty()){
                    Toast.makeText(CreateTourActivity.this, "Bạn chưa nhập vĩ độ cho điểm đi", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(strDestLat.isEmpty()){
                    Toast.makeText(CreateTourActivity.this, "Bạn chưa nhập kinh độ cho điểm đến", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(strDestLong.isEmpty()){
                    Toast.makeText(CreateTourActivity.this, "Bạn chưa nhập vĩ độ cho điểm đến", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(isPrivate.intValue() == -1){
                    Toast.makeText(CreateTourActivity.this, "Bạn chưa chọn quyền riêng tư", Toast.LENGTH_SHORT).show();
                    return;
                }


                // chuyen strStartDate va strEndDate sang number (from 1970):
                Number numStartDate = 0;
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                try {
                    Date startDate = df.parse(strStartDate);
                    numStartDate = startDate.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Number numEndDate = 0;
                try {
                    Date endtDate = df.parse(strEndDate);
                    numStartDate = endtDate.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Number numSrcLat = Integer.parseInt(strSrcLat);
                Number numSrcLong = Integer.parseInt(strSrcLong);
                Number numDesLat = Integer.parseInt(strDestLat);
                Number numDesLong = Integer.parseInt(strDestLong);

                if(strAdult.isEmpty()){
                    strAdult = "0";
                }
                if(strChild.isEmpty()){
                    strChild = "0";
                }
                if(strMinCost.isEmpty()){
                    strMinCost = "0";
                }
                if(strMaxCost.isEmpty()){
                    strMaxCost = "0";
                }

                Number numAdults = Integer.parseInt(strAdult);
                Number numChilds = Integer.parseInt(strChild);
                Number numMinCost = Integer.parseInt(strMinCost);
                Number numMaxCost = Integer.parseInt(strMaxCost);

                createTour(auth, strTourName, numStartDate, numEndDate, numSrcLat, numSrcLong, numDesLat, numDesLong, (Boolean) (isPrivate.intValue() == 1), numAdults, numChilds, numMinCost, numMaxCost, null);

            }
        });

    }

    public void createTour(Auth auth, String name, Number startDate, Number endDate, Number srcLat, Number srcLong, Number desLat, Number desLong, Boolean isPrivate, Number adult, Number child, Number minCost, Number maxCost, String avatar){
        apiTour.createTour(auth.getToken(), name, startDate, endDate, srcLat, srcLong, desLat, desLong, isPrivate, adult, child, minCost, maxCost, avatar).enqueue(new Callback<CreateTour>() {
            @Override
            public void onResponse(Call<CreateTour> call, Response<CreateTour> response) {
                if(response.isSuccessful()){
                    Toast.makeText(CreateTourActivity.this, "Tạo tour thành công", Toast.LENGTH_SHORT).show();
                }
                else if(response.code() == 400){
                    Toast.makeText(CreateTourActivity.this, "Chưa nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                }
                else if(response.code() == 500){
                    Toast.makeText(CreateTourActivity.this, "Lỗi server, không tạo được tour", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreateTour> call, Throwable t) {
                Toast.makeText(CreateTourActivity.this, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void init(){
        edtTourName = (EditText)findViewById(R.id.createtour_tour_name);
        edtStartDate = (EditText) findViewById(R.id.createtour_start_date);
        edtEndDate = (EditText) findViewById(R.id.createtour_end_date);
        edtSrcLat = (EditText) findViewById(R.id.createtour_source_lat);
        edtSrcLong = (EditText) findViewById(R.id.createtour_source_long);
        edtDestLat = (EditText) findViewById(R.id.createtour_dest_lat);
        edtDestLong = (EditText) findViewById(R.id.createtour_dest_long);
        rbtnPrivate = (RadioButton) findViewById(R.id.rbtn_private);
        rbtnPublic = (RadioButton) findViewById(R.id.rbtn_public);
        edtAdult = (EditText) findViewById(R.id.createtour_adult);
        edtChild = (EditText) findViewById(R.id.createtour_child);
        edtMinCost = (EditText) findViewById(R.id.createtour_min_cost);
        edtMaxCost = (EditText) findViewById(R.id.createtour_max_cost);
        tvUpImg = (TextView) findViewById(R.id.upload_image);
        btnCreate = (Button) findViewById(R.id.btn_create);
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

}


