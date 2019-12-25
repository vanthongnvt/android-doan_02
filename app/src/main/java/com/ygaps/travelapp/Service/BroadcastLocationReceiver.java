package com.ygaps.travelapp.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ygaps.travelapp.FollowTourActivity;
import com.ygaps.travelapp.Model.FirebaseNotifyLocation;
import com.ygaps.travelapp.Model.NotificationOnRoadList;
import com.ygaps.travelapp.Model.FirebaseNotificationOnRoad;
import com.ygaps.travelapp.Model.TourNotificationText;
import com.ygaps.travelapp.R;

public class BroadcastLocationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        if (action.equals(context.getString(R.string.receiver_action_send_coordinate))) {
            FirebaseNotifyLocation firebaseNotifyLocation = (FirebaseNotifyLocation) intent.getSerializableExtra("memberCoordinate");


//            Log.d("MAP_DIRECTION", "onReceive: " + firebaseNotifyLocation.getMemPos().size());

            if (firebaseNotifyLocation != null) {
                ((FollowTourActivity) context).updateMemberLocation(firebaseNotifyLocation);
            }
        } else if (action.equals(context.getString(R.string.receiver_action_send_notification_on_road))) {
            NotificationOnRoadList notificationOnRoadList = (NotificationOnRoadList) intent.getSerializableExtra("notificationOnRoad");

            ((FollowTourActivity) context).updateNotificationOnRoad(notificationOnRoadList);
        } else if (action.equals(context.getString(R.string.receiver_action_noti_text))) {
            TourNotificationText notificationText = (TourNotificationText) intent.getSerializableExtra("notificationText");

            if (notificationText != null) {
                ((FollowTourActivity) context).updateNotificationText(notificationText);
            }
        } else if (action.equals(context.getString(R.string.receiver_action_firebase_noti_on_road))) {
            FirebaseNotificationOnRoad notification = (FirebaseNotificationOnRoad) intent.getSerializableExtra("firebaseNotificationOnRoad");

            if (notification != null) {
                ((FollowTourActivity) context).addMarkerNotiOnRoad(notification.getType(), notification.getLat(), notification.getLong(), notification.getNote(), notification.getSpeed());
            }
        }
//        Log.d("MAP_DIRECTION", "onReceive "+ intent.getAction());
//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
