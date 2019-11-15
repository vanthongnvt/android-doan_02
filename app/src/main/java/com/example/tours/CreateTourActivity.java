package com.example.tours;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tours.ApiService.APIRetrofitCreator;
import com.example.tours.ApiService.APITour;
import com.example.tours.AppHelper.TokenStorage;
import com.example.tours.Model.Auth;
import com.example.tours.Model.CreateTour;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import android.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTourActivity extends AppCompatActivity {

    private EditText edtTourName, edtStartDate, edtEndDate, edtAdult, edtChild, edtMinCost, edtMaxCost;
    private RadioButton rbtnPrivate, rbtnPublic;
    private TextView tvUpImg;
    private ImageView imgAvatar;
    private Button btnCreate;
    private APITour apiTour;
    private Bitmap bitmap;
    private String imgBase64Format; // luu chuoi string base64 cua avatar

    private ImageView avatarInDialog;
    private Button btnchooseInDialog;
    private Button btnUpImgInDialog;

    private static final int IMG_RQ = 1001;
    public static final String INTENT_TOUR_ID = "INTENT TOUR ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tour);
        setTitle("Tạo tour");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorCustomPrimary)));

        // khoi tao:
        init();
        apiTour = new APIRetrofitCreator().getAPIService();

        // get intent lay token dang nhap:
        //Intent intent = getIntent();
        //final Auth auth= (Auth) intent.getSerializableExtra("Auth");

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

        tvUpImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImgUploadDialog();
            }

            private void showImgUploadDialog() {
                Dialog dialog = new Dialog(CreateTourActivity.this);
                dialog.setTitle("Chọn ảnh");
                dialog.setContentView(R.layout.dialog_upload_img);
                avatarInDialog = (ImageView) dialog.findViewById(R.id.img_avatar_selected);
                btnchooseInDialog = (Button) dialog.findViewById(R.id.btn_choose_img);
                btnUpImgInDialog = (Button) dialog.findViewById(R.id.btn_upload_img);

                if(!imgBase64Format.isEmpty()){
                    avatarInDialog.setImageBitmap(bitmap);
                    avatarInDialog.setVisibility(View.VISIBLE);
                }

                chooseImgBtnClick(); // btnchooseInDialog.setOnClickListeners
                uploadImgBtnClick(dialog); // btnUpLoadInDialog.setOnClickListeners
                dialog.show();
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
                if(isPrivate.intValue() == -1){
                    Toast.makeText(CreateTourActivity.this, "Bạn chưa chọn quyền riêng tư", Toast.LENGTH_SHORT).show();
                    return;
                }


                // chuyen strStartDate va strEndDate sang number (from 1970):
                Number numStartDate = 0;
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    Date startDate = df.parse(strStartDate);
                    numStartDate = startDate.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Number numEndDate = 0;
                try {
                    Date endDate = df.parse(strEndDate);
                    numEndDate = endDate.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

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
                // strAvatar = "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InSZ51QpjVaMBEDk26tihlMpZ+exjNhoncler0lqz0x8XBf/AMP/AP/aAAwDAQACAAMAAAAQAAAAAAANX1+YCz9CaaQ0999999999999999/88uGF1tjV8sgOe0o/t2799999999999999sQAMcxXM40x1908q3JVG4a9999999999999sVdEMN3Vn5Et4s81CAko3HeAAAAAAAAAAAA96+4tUs8oHHNotOI8K8IRvXCCCCCCCCCCCCqPZLZXHXs7dJkgLk7Vd5CjbkCCCCCCCCCCCv6WbJUi4H69hbiNSeA4AzhyK8CCCCCCCCCCs+Y2u6PuuHLS4izW47kw3fmuoCCCCCCCCCChTahHLCK9BrkL4Wax4N46K5FcCCCCCCCCCCEJHQ/vetg5rEMbxmo8n38AbFZOOOOOCCCCC4mJmMFDdd2EH/7vmZQ+Pd+nUw999998AAAA984T6TmQOFdgDS5w0NlbWVbg8w99999NNNN9j+FLZTXXETNPyGOraogLqElU89999999999d1nGWFyqSifvJAQ2VKhD2PKoewNf1V99999+Bmc1saXjRWkTY8bn6/D5kYC2iOCd999999siqsfROTo2wchFsoobeEIo4CCW999999999tMM42CSAQJligtQuG+4s6u2UAO99999999999994CCICR/yRnwocwKya0gUB99999999999999pCCCmDS+ELOGOmu/WWCWB99999999999999p9CCCiKAQOWppIEYbyAIJ5x999999999999p99OCCCOTK8cZwc8Bp7a024999999999999p9999O9JSmS4gN0Qgk9rRXU/59999999999p999999+zir1Q4MwY4Z50LA9oKaMJ999999p999999BRhgwtxTv8A88x1+TPPAEOP/fffffafffffffffffQf/PPTTTTXfVMtBJ1PPffffaffffffffffffffQQQQQQQQffffffff/8QAIhEBAQEAAgIDAQEBAQEAAAAAAQARECExQSAwUUBhUHGB/9oACAEDAQE/EPnt22NkEHADh45f4cg4ZGLC6tt+RzkH+DtZHO8Z1yeLLPgFnGTXP3FkWPwJO4x0s3g5xgvVll5sZ8Sd/aW0bTxIL4nrgEnhl16Yks2LLI42yzhIW/YSdyAP4tCL3kYkgKzrSW/4kpX3MMdxlp6mC8LHYLJLIfduJKPGWLpg7TxiwNy927Cvc69X7MA4hvCZnCm8KXqGKfYcSv3IJ/V0kdtvCh6rt5qvUjPaQmlkSMSM8oR8WGA/eEOD3t5PsC/cYd0u4MGYQTA3XUDe7aZL+3+wiCTtg/ST5GZ+hZ6SL/l0MusfsLygTYQ2IgN47swPqcbOd+JNDxHscOoZB1M77tt7eDLC3jbbbbfls46I/NqC6KWt9tojAMIujZ6zyrq7os/eAnIHmWwow68BlxnyH6L3ZBDFbz/7YzzHbNkbn5aXVmDYj1GjZh5a2aJ8NgO70Er7h+8toCH+xJtiRkaWPrbGLdn6W9zxiz0XhXglj5i7OyUOHl6kzW8j7TzPsWD/AOoAIO7EL1nuQMNhr1KeGFadZJ7n9JD3Lsep8MZMJdvj7yTs3rLsw5fqPMQvczSx2juR8bE9Jt+WSnu182/bx2142y6SYFg8Lp2zl5utyVXv4b9Iw2pbbvjOdt4HjrLLLyBIxy19xP3bb8zk+QE79Qczl8c792/E+nfgnX8x895P71tt4GOX+PfhsEEnzH94yyz+PeAgtJafMvx8B/IWt3zlllnI5alPBwcZ/Hln1kx/xuv+N//EACERAQEBAAICAwEBAQEAAAAAAAEAESExEEEgMFFAYVBx/9oACAECAQE/EPoDYX54B+4oYBZ0j/Lf4gRG4ofbB92Sws8b5yyYiLX8PbfAww/E+CfAkso+7tPfjfBb+zHBfp5LS2z5Hyt2SfauZYzpyHXPgyfi0cdSs0mmN/rw39uXFlzandvhp46b90+422CO25u2YRFyF4eoFyXGWDSeHLH8sYYzD4fg8n2ngGOGFmN2EPBHstcPcZAZEwMDNtCxOV7+R1PH2vvaGQvcGQD4k2JRA4JScd5LCBSWxsdzzc+4POWb3PBhP29tn/i47lOi0bYi5IGQzuEg6deTq3wJpOW5O3r7hrP8uq9kyZLtNsAR+7ZxCcd20M7zxttsdRj49W83r7uqWASPLDcx4LH/AIW2ha7PzLTuIf7M0bYuLbGDxsN6lD7u/hXJlsW+POJ468aBB7sWkmbsB5WGZHvEKNdXDrCIEY459vaQ3JysMhxF6XR2w9Rot8FwXMp4ugduWrHBgbl6EIGzKh1fp+3tCblhsc+bQ0vYeAMl44sggvyFA3slLDOIJc0yXtZOG5nr4n0DlpzEG9DDh/tn3A3Tq/C/zC/LY8PvC85P5Q1zHdyHuSw21tt+zra8G7W1u7CIhia+AvUwS7CI596srsz+IN+GR8ByZQAw8q7h8Mj/AFArlgZ8MD9x6Hyd1/UdfBCV4JVdfGiMb44CznP59tux8MttttpZ49Q+y/xsXT+gN6uDmzUuu2tr41tZjiAm7I1bb/KOYBYPcCvoXcbfVlz68Db/ACf6Wtz7sfmq0mBLY+HX8+WWWWWWQR5SfB/Jn2Fvqcf+N//EACsQAQACAgEDAwMFAQEBAQAAAAEAESExQVFhcRCBkaGxwSBA0eHwMPFQYP/aAAgBAQABPxD9rx69ePLPkgBYBZ7kXxHeVa/WrWD1vBX3gMn1zQ/ExNGs5VmUH5DAKAHFEs2s60l1VrgCzSLzWcDGXSr6faUbuRf+8R1gr/5Vemt48wThgnS/GZtF7pyE8oRTS+Zv7QTSzxaZKrwVBmU8sKYJXLc1S8CKDLR1UJgvPD+ZhFq6S8ovQrfyzYV4D+YTR8q/xB2vdP3nV32EWr5xP5gtzlOuZoH2m+WUWdD73BQ9GdTiV/8AAzK9OJ7xDQ3iEseZKieB5ScsL6WzkB8H3gGXeWvtAOLd1WFVACKAdKCcDHUH5n2J2gDS8hlDId6IBdUcJ1uhfuxbHsAlsIvdIuKso7piDmrTmABWogw1vHpoagxDDmNVDJHKJL03++IfZqvmHP78G7zqtRX3C5QwnhMt0kOKMxTL8iFNYvf7wWCwhhwGuQH1iNK97vvKLje5mI9ji/WEKdXNEX80lqGL3O36sVWLHYfSZFju36XLvLl6qwA1DfpBYUhHzKPSo5QIlzMw0XKtjhKWLR1cVGSZNlj4zFUVBGIyRvlYKH4iVJeML+Z3req/eumt1Og9KCHjgGy48wJbhpa/Jj38Fv4hadOxdfLL+j4BH13dEtHytWVF9WKGob2BFgchKU8u7cwUq3SUS/vMiBU2d4WQ1DfpDTBvjUJl6K9KWBmIU1BBeJpqvwAfzLO8awfyaIpa5LYfEGoSaoxE5GvGyFWfkxo5zGBfrELEOWX2dTnExsXj/wBY7wkJTCZs7Tj91/JLG5ATHmnJ2U/iLETzBOkHiAg8wNZkZ+0qlR1Gq6SjWB14h4I8waxlYshqVCEVzhlZhr9NFQCVuoM77bioQTndgcv0I88wK/I8SlXn2jmLU6RipiK4Gnl/EywhwiqlCDQbMO/f2guRl2bqZwa7TCbHCMfCrm/f4eOZSCrXdGxP3X4mRHkJR2aP1IUQTrFCKVKEmJ2iHFWPWUrU6S0Wk4IEVtet4Iq49asfvFWLLWKOafEF3UNOH3OKgNQ8QaYTn9OOsUBbiZrMNVdUMXVRQYesfmADyAAKrx8TGxWYAyQHBRrH4iARaar/AGYqwNd3EArajoX/ALtLa2+X+/xLFv7QdbZxTFXypVtA79+kGIuqO1yJ+5bLqC27hbXzu7I/xCgy+IopN9LlxbXNynEdQDY4hBrEw7IWgkeesJr2ZQJCsJd1HTXmUX5jZXaVwBcT6a4YP6uCMOe8IQSoo3SIbM3cnOHzA7BUK4dHEroW9TOFDxcsVTVxaUYhQMURC6W9YQK6TZMjpHS19MypfZzNBedxybeYB6+9xTRiKbJcRJh4mYqSjg4PxGVKlSpUqVKlSpUqVKlSpUqVKlSpUZ3kg/VlvN5DjNfmbSAxUFDhyhxGSKqV3cst14GoZvKqZyLqCJBN3BjSZgZVllc9VUU6G+KM9Y23AXR9wf4sltEsjbseh4zDAucAw2AqXy+IkBORD5/uWyvzHL+iHuuxiLMMYocl95YWr4uDMlnrLtr8y5fMsnaOYjXLNaCK77S7i7wwxiVXFibxCzLJebl18MJkIaYwiawbguD6RRCiXTYmRPch8yK9nn6j+5YHqX7SmGmzfjP4gac2NS3sskOsShYXl1CbTApmQKA1Cel0QEYuLLuMlVeBhuGoBSLddVsOxzK9U4hlp96ilrUIe8nchuABUKYuiYPZXhhrfyABZpu4jQybSHwfxFjt0pXc7zIVxqphOq8kU4R0CQvASPal6lgozK6AiBMYDof3UoNV0E4goF21EvG5Zd3EOJbc94DHGBgl4lTUHiA3iJdxN2zQdrv7QLld5VfteZ2w2feWV0jrh/mZXEtdY2Q6h4h1quOj3lNxnLz2m+WpLLI7GFdpuMzLbnERSo8EGLErqOhcho94AfOwVig+YysZaZBsNGfmqi9ikU17qC+OavtOsFOAC78/i4s1JFsGc5d3dxLBxxBz5ISmSrdySnKUMoLYfF7xRTzLa9hE3xjibLjA7q/gjAdbpJYTn8QAWxoVGxCHkRWCRntAqG2DUsg4iJBg30i7Ut+yfxDNy/23MBF1ULTq1E69s8qjr0Td9GBwMTRvf8S54lL2fMaQYQ9rjihsBpRBs76s0sz4gVrUCz7LlvJUukMVL4b1kZOn9TIMXnBlYd8e0v8AaxWTf+JZZkFHeJW6IGM4Hm7PmOM8AZFb3EpoKBdHV6RJlUpV4Ma27pfDCe9bgH9xunjmFF4eIdO8Sk0PzE25CupmKmhSNvGPeUqvnBCQDc53EHBxLWRm8TMNypXoMyQyuR6QjmOHjVwEmj9ukipRHDACqz0XQ/KLGN1ggkNRQi8LZDnpECG3hHEKfG682RxSvbiXpapaIDAtolst6Mupqu7d6/8AY6oKDKo5/wBxLGULFINpR5tcblaXjFgF0+Ljd0KvUrKDwQgUTrh8iL92/iWO4mgxYKuXeCvZlWalNqvFY11hUSjHkdfOSDpW47nbcq58s7l1kQ0dIg1mUDAsJ7KfmcK0+pj2iNAxu7lB0lnMcpZcS0qXbBK7+vvBB6x1G+DvMWcFQUQKxV3TdwObMRdk2/b7Kj6KxiOsa06hV/QlFhrVkLqYMMqlxXPEfdzpK/1XEKDcpcb7OZ3KR2k6AMneIuBEOeH8fEGa4Kdp3YEjkwBhKc9w63iVXYdyGmzICuKeuYHZWkUktSwGRLxhgGr8xciAbB48woQSApAote9VK/pdT3e8e3ozTAtUh0zDE28+gK31h8PZ99fUgp17RQK77ago7nJGuZTLG5fEFJd+0CZBoXL+T6wU+ecD7pgBrAI9fQgJeN6rL9i32IycSmFDGXu+Wansd0Gz5L9/3EazCr8Yz5f4iKgfY5a+8pXsVmKmh0CY7ULwwoE3k6QS1OjOL/RWJhZfdlRFp5lySLVnExuCUNJpv2YxUyiA3/iA6wyttGFE1e4p5bIUDbLG80Q44rATDTKLdWwYUQra3Rw2XqVwrQoW9jtxqDjUipC6HJmt8XNZYBFLoeafEUF2rtjQzCjmNDrKXUaHO9S5d5RkbLBjozP6dllM4DiG59PofLQCkHfi40c304+P5gdsOzId/QxzGbRwA+07VzA/EWtFS7ibvqwcHP0Ue8FCoBDg0faIQ2HutG692O/Q/akKk5R/H5jAIgU8QKPY5GItXMMKt4Wl9o0vWsL1LnsmqWyFT6LTUOMa5YdIR7xPh8U0hkxfR4ekYlqKsXXiJAXqw37SrgbRgD5jQYGcL5r/ADKZhhQiN0t4rnGcRk2fZAXQPD74gTGKFbWIafLcunuqbMFc+0BN0NEYVhXiyA1QRazL1EpcvP8AcJCTVAm85gGuuSexxAOe8JfHKWs69B7X8TH/AHMusFNL4uALRDjtDBbfS0l2YqXUxvu8svgJlXe3D5dQfieMTXcCPDBYHG8MuXBYbte/c+kzPFNa4B/iVgwPcRc/7iO/Q/bWC2BrtCsTQ/MvBw5B1hX1ixgL6ViyscRMqQ4JlqrbF2mFhdx6UOpLtNvGo/KzY3x4hHB/9bMRbMXxHc1Estat2wXhzXqXgNnfZM6toMj5JoSVgY4t+mJfSLgtlWqM2mc4zOVJtc37wgHQ7qXUrALh/lRErEOyUgGszE0UzNyUHS1+9R1g7jR2VLrCBcDk9qrzLbxCFkGLwylHtG9gh81K18IOv9qFA7GIcyOkLBDYJXsS+dt27jJ61xAAJYZapQs7XKqkwBs8bb32hD1cbPIFxXPSZrq7V7BuEILkFve7IOb6d4ZxQ4dsv5jpwwTih+WveIuigAAaDiPMP29PUj/fWBSC6Lh1N7/MsiEerkiC0Mv5lUC8LhggHS0MXahjGYOsjqFXAxYdC1MrCnQfaIYUskDQytO4+pM5K7TOcR3hURKnVvmDSqebrPETKjair7Q3J9j6R7BfbglIbQCBU0bFeJZrmdG5sgMXB7Vl1rb7s+7DAbYv/do3wI00OQ82L3weizVSyurHlGvtFX8S0OdUGnMuKd6qZWMKLF9m+GUVIBdKFo/7UC0FtLB7XCoKILEWsPS4YoAwGAO0BgVqyn4z2hE4LA4SDoBVwMvvXaKhaNXXSK5/c1J1R+ZQa5fmAryOpgkwDXJFvX0JjYCq6kputyt5Ikqp4Nyt3cy7rIcR3ZrmWAr0czJgAOmoCGjobuMlO+RzGy2Wco+8KlZirM37y8uidBZlALtSAQwO7INjS4aoBL7XmfZiWMhFl24FKFu8Jin+tyzCoLg9w09ZTWiMFC/mochFTTcMRGalEdA0/P3ngYqV288SxIyHAxqWg2AsrF1EC3eja6EsVqYbptj2olLXEeo9kZEcE5KqAB0oR+FsmGYaSw5o3/7Cmb9EbuhwZrMwwAgAoDjHQqJT0/heEXZ6H7bmbhukAbHTj8QFYprPmoEN7uy5mcnXETyMzFdETNFTtpwWOdjMGM+ZSLrCPvN19oqwB8uZtfhs+IAtFLpkdk6zyIKu7HlgGkidz+IWJxCql+mj1jLUBsTiHQCBVydusBYXTJUabdoiesayfEWlsr3vmWRoMug/r7RwyhxDse0MkuKluxUt4TETFYrHXsLdEuq+qxVOg4/MumqlWm6v5jLO3Jf8vvKDxKJVsDg5fvKDhrZyRsAO4AywqNLixyzAaq9RZGIxkcGPLfxMV/twGa/bEqLoz7yzHzHf4yxLI1UtN2sfcKPQQamLdfiCaq4l2l77Bm8UQRqhgHHeUGjLzLcLzqtviXsDIro6X1gBQA0YBC5ueWJghAo2e8ouOwaEpYXbMRyVUBQhfEWgU5bPEel2wirgy43EGZQAFFFJw8TXk11O5Bc8wxsnWuYuuXrDmBimUVxCVlpl7mvEA6qJYxhzOCw9VxvW5YexzXmmj6UwLxuLXSoVkyB7i6Ua7whqIHbvOb9H9uNN8kze7EHAyCSyCkrU6xs1iDvMG4pkpXMClXzTEWLadKOVY0XlLObWng8vtNbo5k6vV6QAbwN5OYwgj4xANQGy4NBQWaM7h2QKFvgX3avHaUYjAoGCxgl2Nn1RYAC0LA+r6R6FLnCU8ymbCYQWvYsqdEXxHThOsWFtoFvXVmpt/wAg1+ZjFfQfV6PmAF8cS0w5gKbl5coAWIYCnpn5YxtHlh9tQ7cGy9HCna77QK45iiIBNrVebgkth0bmn/x3mUlPuDaozQMUkcTr+wuXLly5cv1J1k/8TwY+7BNko2qBYkDgehu0DzFN0+0NJdXBSyCgeWL+PiUPBY6OIjbPvS3Eo3kdIGoKvWWKzXYQg67r5rEdQFFU3SR1WWbHn/CYFQDceNg3eoNW1gBbVX5VcUldvFt0g65rrCAwQUWttfMVMRwro8nMUqFZZK7VFtuoQv2lpfwxFWbPzQLrl9IAoIAGjX4iwCOxz9Jnrh3DTR/cdU8vJDxzT3mKFyoB9biz5AHwzIw+Mzjz2gq0tFAjy9K5Py79iCqUYLOcDuu/xAABWACWfdtFPw8EL/6Yy+g8Lw8xsnD9ZWPWv2o5mB9S+ZjOQWfaBVRYluWmjfmZi17wsqdQDC07ExfeKRmkdVzHEIUbow1fmphkhO1Qqhy0M0RnDKGrHTYnmWb3P7I4gDihiAEct3ymxF/1W5j2iCqBGBjvAB0t6xVKtRA4KpOIiNfUpZ8rdwoMNOGDzMgolobF7EGQ2co13IfdlFvDfHSI2wNwwoX1lcoQ+McO7j+ZcINxhgUgwHD5NdyAcC4OjI/uKACrBraR4q3qQQqkoMjrsMdLIyFcOBt4ckGX3WJsuWgvwjc2t274hU64rEZOlbVutrupLvK2rlf4qMfeLBfpDF47eOJcbIbHM+CmX+h/aG5Z0kWduNeP6j9pdUs65lUaviB0lXQ/mW5OuClipanCixTYDAgXNV2JrOktMwM6+AB0q9GkmURgxtJQdqG+C9wl3uzCGevL1uASaDVnU1x2llhVOHBvu3cu/e5Afkxq5VuNhUcWG48VwVhTBaIzbT7EAFGB0KiDGkB4PG4rmUbQ9f8AcHo1FnDm4h4OtJVlUZD2VuDOSOaq4peIIwyP22sWM9Y0yqAaqcjwkagFYGFNvvUs0MA6Dge9fWEk0lZu5N1erh3pQZjLuj0o5lSbeVwTQ2c9HmAXIZvU++4t9FYXDWe6vabHGTjshVMYApxxKdXI0w2LgAOFrp9XiCCXEsdhW9yURtL1hqz8MEAmMiunH6X9mR48DTK0x94iLeS68QBNDaoXmUMZBTdze5Zkap1NmTZUVmgOkITVU+LgWhMTOKr8MG8VEAiSLFM2cF6DftLk/STZAONrF0vg0Lo7ohDxTgngQI02KImQdVbupW9LA7tYgDR7sCdzcM95mosvQuIjZeOF+0GPkcon2TjtHU8aFbOMErQCIFcx9B2gEG87uLZZWVketD8xwFcgEQoqqNXnmZCVYerLDYZQA8geN+0JdNXIFsb6/eZJ4BvpYoBXfpAcC8gB2/aZ42weHj6iHmZOEotDWdwgfYgdQ/TtKZydYpVvOmopuQp60p+Jv9CfqqVK9KlSpUqVK/RRcw9U6oDfGfxGMogXejn4JsIaBjuuq94hVgVcBbbfhhW5kHnkf4jEbCXRM1+Yy6ijnJ/UyxGIFGyMcLs0+GCKay1oq+nFXGwYQCuQfYI8gwSgmnWwT5h1+ILaXgqTeT9WLFLzTA8X/vaMjSsAunSPv5cSi0s9DcBkWs2z8SiFawIf73hSHGKJfdti6DYwyFtgVNWX3VqVPjkop0T04azKRs281L70Q5svrmGIrxgZTg9BiDg5VCdQCXT2MHx7sK5JsA1rFQEGQFk+9vECPloQV4avvCKbzqLDfRc9opsCVXZo93MDoBjUv9oMvtMZbfIv4uYF5/KZ5RS7Cmrwy/g94YaAAAFdse0oRTKHf97zAUgvZc/lg4l7x6VD0q5VTjXoC4CdqW8Sv+dy6iM5x+2MQjgHo5iWE3azvMTzshS3Zx5l0OwpkBgoOZhQKA/ylA+0kbpSUiDpbPdfxE1V2Le51IcpiigXoaeH3jLWYU9q5d7V8zYs1dl85LmZTw1beZaSjuzR+Ft7cx4NOsl4DMr0vMzk98x6pjjNb5csZvzQ/eapd6CZrropr4lUEvSFTwsEA4KAnjs9/mCoAKS0TxzBYFXJX207Oz8yiTLTkLoiY94VuvjEYgq8vkfhgmLmHPKtRynYahkwpAUeHcp/IuV8iZ7N05ioRCjFN21jBd5QlnlAUoyfB7wKcK3VFF+lzoLe9hYqsFQFuVGO7GaXdW/k+00Fj8i8tHFECo+UyDCFwZs6/hKF6HBM9fSCajFkx3lzYe8q37VxAy17Ep0LtdTo/JuY4Cj1g9id4+WWef8AoYZYuw+YmHnNVNRKnwsFc0LWqLqF/XtbS7b8sXnLqAS7dJYlbKbVlLkdVfzO9fOpnMrOjARXYUH0gQ9Ts+qgdgdk/S5rn6BUcsk7h7w1vaUtBfLVzyBr/dBKpuc/vAa8EAS+cypk+6E2QO+sqyX8RTCpSIUncgo7lq1nk68a8RWpazAnIHT9+rBBUzgI7nDzCV+6LKnc4fp3mAxSdi9J9/aWvoiRD63pasY2Q2OkTJLVtisn1QHBhA8pjvEtYcOUxR3i6A9mEvMuK2ofDZ+SEHanQrXwsZtYXfMxGmUrx8ytGcJWPVcda7SqQh2nMv2OhA9B4Ex7+Jd2fMQjbFOIuJv0olEZn/s+oAfrLvqxMr+hpnvK/FewfeeAlqL6ENQF733WHcZ0AH2i1lHUm2PSl/eBajoEMYMHbEFsj3SKNL5RZnLsLLNNeB+YmsuNW/xEmD5tn2YQmz3o7D+7AzbnzPaoFmoy6iXRCwQrDB3OHmUqzWFD7ndL1oaD8n4ZhOn1qHVDICfWCTR4YvulJyLIBulC+lRFdj/FaRS7f97xBHK4J7XmMRNhQG26Z0huJXdC3nOvqGKKODDPV9rhm0yYVs8I/aXHgih8TlvZHpVQZb6B5llGUUw93J56QkLUsJkNu/vAVxMJz39Llz3/AEXL/Y255aiEW0CGxO0QHDjLGxQ4bgdUvjMTsDwEAU51aRZQu6mcMeP5Tlp4Am3Tustdt11mJ7XPabO8NRaJwSW5p5xFDKPLNlZ0IWL+MKHL8QA5O8OGA8AQt/UvSnA1nk0+5N1h9JiPqvpDAQ2tE+T7RwUSapPkhlWuGrF+47gin7/qhEGlDClY8R2zAChFops4V1QhazUUCDzu+n3gru+NRaPnWfMphMArOH+JiOFVtPKOfePOtC17LaQI4urHnMoqlBxXaNdr33r+z6zR+6sEF8ily1Nr5VlF95fEu/MrEBTAvtB9L4nVp5QmBkO13A9n2JQ5Z4gnm+Ep+6Vg9M3QnDdeCpfze8FxeGD6y56EOpM8ue0KRdoGqiahBYqE+5K5ewd4mtJgk7fNbMh+ZWMulzS5GpXrHYvxceeXKou8ZDuwE1cBgL2B/mNHtX8md/7vA/QRT54+u5bvTQPtMhCyn8CLlUuVzfdhBFRBZ2OG/eps3+4PQK9ADm5fAuCHB85lwqg8EOsnhlqZT5ZjzLA1XtLg/oN+g4gwekHZBxB1Dh6gViHIfYIP8EVPdpGDf3jAMHoNLBz0iGEU+0aYMKu3bmUSAqg1AZ3bb7w7UAq1iH0A6P8A2UC9q3n0qGoAOKSnwzId3r9r/Tz+0uXWv0XLgy/0H6bg+g0Q6wYMIUmM4O0DlcBH5pwOD0CADOjcOwcS9stly8REnAycQtrTkE+ZdHuaXwmYh6Drt4znMvlFvKZJWPQjrz1HXcpL8WfiP6Ll/uj0PUZZ+gYLcGD6EEIOYPeKFUC16BzL7h6Ll1/SBBsFj0dZXqiDx1g9kDYF4dSkPQpF+w4givUBSnkiJ1dWULcpmtxYeSFQ5BelQN3+/PQ/QfpGCwl5gwYPpuFRKOmb+h9Yi2opl3DUuXFwuXBV2viHzAwu3xuI+AUQg77kFUTE2S37hh4dJlvMrGJUqG9TIl5fZP5IOP356j+gYZ9T0FOYMGDcupd3Mz7DOl/qX29M1UqO0fsPmCjmYaDwbf8AZgRASsT7OD2i3fK7YOP5l34grFBrdkW534BjvgF3ZDoYj2xC7lntHe4wnVH+5ky/vz1P0XDn1PUZcGpkwdOIxL0DXQlbmYeLY8GbEg+f4zOTAYUeBv5gAAAKACvgisuXBY5g1hUiYeIg27l0en+6w4FKusBRxNGLVeA4ZRDZxOjZ+/P0mv0E4/RqX0mYYlaQLTwf+E1nbFeWFB6PyJBOM5+kpz0KOXqvNyvWUc3K+i8S/EplTq5BGjX9y24XIH+9xdSxPMKTs+kuDZNBy06d+0QFgSxOen74/TeIP6LYer6Wy8RDKko9Dn6H1lg9Z1gVXLxCr7OhTcXFzF/cP8MP9MQa+8MNwNVd+8y5jjs+Zet58xmtVNwtUf7Edj4epLi6yiRLby2JxLRyIFu+GezuHriDf7k/UfpGXLm/Wmp30Cs5/wDJVG4b3ACLtAOrxBMpMu7zFnMU7tGvNwr/AOyv+YHzAVuAJkZYKyow569JuS2mydwiQFF4a3UNx5z1lKh9kj1Bv7DPKHczLz6Ka/cH6rl+g+vHpcO8yRKjBYZ8r+C4xEFIAPga91Y9Evcgmw9ocmiXocUTLvcYbykr1lesAYuZcw74Y7mTcExcqf7hdJdpryiFp+bPPMutcRqj54ilqThPv06Q8nXcFuD6DDP7g/7cRCnAZe0y40PyPYmcq2rdrlestq5aG3gLEFi938QKVsWEF/8AI908pfpvn0vrL6TMuGG4I5gTeYQwrqjFNvMo5uC+GANB7SrKTcQUeTp1fiOhKAeYQv8AcH/K5f6KCu2NAbvPggaiaRfpCxT3YPEo6FyIXtzDnDpXFLRrqvxKq4ykoeLlVi7mvW69HWIHf1PTMFuC6wOIEC48w8vYEIxd8tZf0iL0loX0DrEwINHRHP1Im7l9lhn0uX6Y/aH/AG6k14iF3HDc8USkfeL6yhQANAVUwcz5IueYlxFdILkljj0ilg1BPpqUkqVD0xCp0tL4GdnfZEmP7s8i3CwIAFIbVy5WJdT7noNehDPe+ICBtLo1Tu9ZVuC7cBzBy5G28HpX7A/7oVUVxFDj3nWIGEXglekT0mfFTriCdR+k96OMw5+ka8xJEVMDcV1gpbqSxD/GHh8Qv2jXiJcGMwQ9SgGtDpc/1KiuWyJUILaOPPEbV678y3pRZawHvAsksxjrLlwf+p+wqyZMsM5jb39NuktBzSPuidUKjAoVeFFGFQ1YcdRfSWi/SaQtBdIZajHErZ6TFuFOYehMGGAsKA0Hn+YO9FqUnQTxHQusiaDrOUFa56u8rKufMSEP+p+4qV6cT3+k2f1EOy/aJeD4iXieCPSI5cfEx4v29KhHjDtlGtJ2YUIP0DmNY94xR5i1DasDCcY7RS2A4PcfFShuDQM31ZTaiN5EpIjAgf8AU/dVK/Rv0CVcAJRKlSu0qUdPSv0BcCMupxZ3hr8jiYBwfmBHBCrS6vEqUqiBaOfmZQAAAPMZexot9lwymosSvXvACWVHUdZcuX/zP3fEqVKlSpUqVKlSpUqVKlSpXogupFKvETJfVFB87i2ciuV5YEAIhe6auBV9esdVU9OYTRDxeH2/7n/4U/8At//Z";
                if(imgBase64Format.isEmpty()){
                    strAvatar = null;
                }
                else{
                    strAvatar = imgBase64Format;
                }

                //createTour(auth, strTourName, numStartDate, numEndDate, numSrcLat, numSrcLong, numDesLat, numDesLong, (Boolean) (isPrivate.intValue() == 1), numAdults, numChilds, numMinCost, numMaxCost, strAvatar);
                createTour(TokenStorage.getInstance().getAccessToken(), strTourName, numStartDate, numEndDate, (Boolean) (isPrivate.intValue() == 1), numAdults, numChilds, numMinCost, numMaxCost, null);

            }
        });

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
                startActivityForResult(intent, IMG_RQ);
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
                Toast.makeText(CreateTourActivity.this, "Upload ảnh thành công", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    public void createTour(String token, String name, Number startDate, Number endDate, Boolean isPrivate, Number adult, Number child, Number minCost, Number maxCost, String avatar){
        apiTour.createTour(token, name, startDate, endDate, isPrivate, adult, child, minCost, maxCost, avatar).enqueue(new Callback<CreateTour>() {
            @Override
            public void onResponse(Call<CreateTour> call, Response<CreateTour> response) {
                if(response.isSuccessful()){
                    int tourID = response.body().getId();
                    Toast.makeText(CreateTourActivity.this, "Tạo tour thành công", Toast.LENGTH_SHORT).show();
                    // gui tour id sang man hinh create stop point:
                    Intent intent = new Intent(CreateTourActivity.this, CreateStopPointActivity.class);
                    intent.putExtra(INTENT_TOUR_ID, tourID + "");
                    startActivity(intent);
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
        rbtnPrivate = (RadioButton) findViewById(R.id.rbtn_private);
        rbtnPublic = (RadioButton) findViewById(R.id.rbtn_public);
        edtAdult = (EditText) findViewById(R.id.createtour_adult);
        edtChild = (EditText) findViewById(R.id.createtour_child);
        edtMinCost = (EditText) findViewById(R.id.createtour_min_cost);
        edtMaxCost = (EditText) findViewById(R.id.createtour_max_cost);
        tvUpImg = (TextView) findViewById(R.id.upload_image);
        imgAvatar = (ImageView) findViewById(R.id.createtour_avatar);
        btnCreate = (Button) findViewById(R.id.btn_create);
        imgBase64Format = "";

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
        if(requestCode == IMG_RQ && resultCode == RESULT_OK && data != null){
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
        }
        else if(requestCode == IMG_RQ && resultCode == RESULT_CANCELED){
            Toast.makeText(this, "Đã hủy chọn ảnh", Toast.LENGTH_SHORT).show();
        }
    }
}


