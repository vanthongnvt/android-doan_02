package com.ygaps.travelapp.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BroadcastLocationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        intent.getSerializableExtra("memberCoordinate");
        Log.d("MAP_DIRECTION", "onReceive: nothing");
        int res = intent.getExtras().getInt("memberCoordinate");

        Log.d("MAP_DIRECTION", "onReceive: " + res);
//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
