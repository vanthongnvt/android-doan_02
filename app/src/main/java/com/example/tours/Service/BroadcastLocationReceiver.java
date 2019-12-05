package com.example.tours.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BroadcastLocationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        intent.getSerializableExtra("memberCoordinate");


//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
