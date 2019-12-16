package com.ygaps.travelapp.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.ygaps.travelapp.HomeActivity;
import com.ygaps.travelapp.Model.FirebaseNotifyLocation;
import com.ygaps.travelapp.Model.NotificationOnRoadList;
import com.ygaps.travelapp.Model.TourNotificationLimitSpeed;
import com.ygaps.travelapp.Model.TourNotificationText;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.ui.map.MapFragment;

public class BroadcastLocationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action==null){
            return;
        }
        if(action.equals(context.getString(R.string.receiver_action_send_coordinate))){
            FirebaseNotifyLocation firebaseNotifyLocation = (FirebaseNotifyLocation) intent.getSerializableExtra("memberCoordinate");


//            Log.d("MAP_DIRECTION", "onReceive: " + firebaseNotifyLocation.getMemPos().size());

            Fragment fragment = ((HomeActivity)context).getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            if (fragment instanceof MapFragment) {

                if (firebaseNotifyLocation != null) {
                    ((MapFragment)fragment).updateMemberLocation(firebaseNotifyLocation);
                }
            }
        }
        else if(action.equals(context.getString(R.string.receiver_action_send_notification_on_road))){
            NotificationOnRoadList notificationOnRoadList = (NotificationOnRoadList) intent.getSerializableExtra("notificationOnRoad");
            Fragment fragment = ((HomeActivity)context).getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            if (fragment instanceof MapFragment) {
                ((MapFragment)fragment).updateNotificationOnRoad(notificationOnRoadList);
            }
        }
        else if(action.equals(context.getString(R.string.receiver_action_noti_text))){
            TourNotificationText notificationText = (TourNotificationText) intent.getSerializableExtra("notificationText");
            Fragment fragment = ((HomeActivity)context).getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            if (fragment instanceof MapFragment) {
                if (notificationText != null) {
                    ((MapFragment)fragment).updateNotificationText(notificationText);
                }
            }
        }
        else if(action.equals(context.getString(R.string.receiver_action_noti_limit_speed))){
            TourNotificationLimitSpeed tourNotificationLimitSpeed = (TourNotificationLimitSpeed) intent.getSerializableExtra("notificationLimitSpeed");
            Fragment fragment = ((HomeActivity)context).getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            if (fragment instanceof MapFragment) {
                if (tourNotificationLimitSpeed != null) {
                    ((MapFragment)fragment).updateNotificationLimitSpeed(tourNotificationLimitSpeed);
                }
            }
        }
//        Log.d("MAP_DIRECTION", "onReceive "+ intent.getAction());
//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
