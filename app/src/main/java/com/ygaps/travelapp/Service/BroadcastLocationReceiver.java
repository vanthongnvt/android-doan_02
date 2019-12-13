package com.ygaps.travelapp.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.ygaps.travelapp.HomeActivity;
import com.ygaps.travelapp.Model.FirebaseNotifyLocation;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.ui.map.MapFragment;

public class BroadcastLocationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(context.getString(R.string.receiver_action_send_coordinate))){
            FirebaseNotifyLocation firebaseNotifyLocation = (FirebaseNotifyLocation) intent.getSerializableExtra("memberCoordinate");


            Log.d("MAP_DIRECTION", "onReceive: " + firebaseNotifyLocation.getMemPos().size());

            Fragment fragment = ((HomeActivity)context).getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            if (fragment instanceof MapFragment) {

                ((MapFragment)fragment).updateMemberLocation(firebaseNotifyLocation);
            }
        }
//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
