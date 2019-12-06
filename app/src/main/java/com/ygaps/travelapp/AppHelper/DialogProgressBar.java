package com.ygaps.travelapp.AppHelper;

import android.app.ProgressDialog;
import android.content.Context;

public class DialogProgressBar {
    private static ProgressDialog progressDialog=null;
    public static void showProgress(Context context){
        progressDialog = new ProgressDialog(context);
        progressDialog.setMax(100);
        progressDialog.setMessage("Đang xử lý ....");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    public static void closeProgress(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }
}
