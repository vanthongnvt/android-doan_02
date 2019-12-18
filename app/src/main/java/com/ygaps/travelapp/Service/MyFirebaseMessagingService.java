package com.ygaps.travelapp.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.ygaps.travelapp.ApiService.APIRetrofitCreator;
import com.ygaps.travelapp.ApiService.APITour;
import com.ygaps.travelapp.AppHelper.TokenStorage;
import com.ygaps.travelapp.HomeActivity;
import com.ygaps.travelapp.MainActivity;
import com.ygaps.travelapp.Model.FirebaseNotifyLocation;
import com.ygaps.travelapp.Model.MemberLocation;
import com.ygaps.travelapp.Model.MessageResponse;
import com.ygaps.travelapp.Model.TourNotificationLimitSpeed;
import com.ygaps.travelapp.Model.TourNotificationText;
import com.ygaps.travelapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    final static String TAG="FirebaseMessaging";
    private Intent intentForNotifciationType9, intentForNotifciationType3,intentForNotifciationType4;
    private Integer userId;

    @Override
    public void onCreate() {
//        Log.d(TAG, "onCreate: created service");
        super.onCreate();
        intentForNotifciationType9 = new Intent(getString(R.string.receiver_action_send_coordinate));
        intentForNotifciationType4 = new Intent(getString(R.string.receiver_action_noti_text));
        intentForNotifciationType3 = new Intent(getString(R.string.receiver_action_noti_limit_speed));
        userId = TokenStorage.getInstance().getUserId();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
//        Log.d(TAG, "onMessageReceived: start");
//        Log.d(TAG, "onMessageReceived: "+remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            try
            {
                Map<String, String> params = remoteMessage.getData();
                JSONObject object = new JSONObject(params);
                Log.d(TAG, object.toString());
                sendNotification(object);

            }catch (Exception e){
                Log.e(TAG, "onMessageReceived: " + e.getMessage() );
            }
        }
    }

    private void sendNotification(JSONObject messageBody) {
        String hostName="",tourName="",type="";
        try {
            type = messageBody.getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(type.equals("6")) {
            // INVITE
            try {
                hostName = messageBody.getString("hostName");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                tourName = messageBody.getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent intentNoti = new Intent(this, HomeActivity.class);
            intentNoti.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bundle = new Bundle();
            bundle.getBoolean("isInviteNoti",true);
            intentNoti.putExtras(bundle);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentNoti, PendingIntent.FLAG_ONE_SHOT);

            String channelId = getString(R.string.project_id);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                            .setContentTitle(getString(R.string.invite_title))
                            .setContentText(hostName + " đã mời bạn tham gia " + tourName)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setPriority(NotificationManager.IMPORTANCE_HIGH)
                            .addAction(new NotificationCompat.Action(
                                    android.R.drawable.ic_delete,
                                    "Từ chối",
                                    PendingIntent.getActivity(this, 0, intentNoti, PendingIntent.FLAG_CANCEL_CURRENT)))
                            .addAction(new NotificationCompat.Action(
                                    android.R.drawable.ic_input_add,
                                    "Chấp nhận",
                                    PendingIntent.getActivity(this, 0, intentNoti, PendingIntent.FLAG_CANCEL_CURRENT)));

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);

                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(0, notificationBuilder.build());
        }

        else if(type.equals("9")){
//            {"memPos":"[{\"id\":585,\"lat\":\"10.7793304\",\"long\":\"106.6118907\"}]","type":"9"}
//            Log.d(TAG, "sendNotification: SEND LOCATION");
            Bundle bundle = new Bundle();
            Gson gson = new Gson();

            FirebaseNotifyLocation firebaseNotifyLocation= new FirebaseNotifyLocation();
            try {
                List<MemberLocation> locationList = gson.fromJson(messageBody.get("memPos").toString(),new TypeToken<List<MemberLocation>>(){}.getType());
                firebaseNotifyLocation.setMemPos(locationList);
            } catch (JSONException e) {
                Log.d(TAG, "sendNotification: ERR "+e.getMessage());
                e.printStackTrace();
            }
            bundle.putSerializable("memberCoordinate",firebaseNotifyLocation);
            intentForNotifciationType9.putExtras(bundle);
            sendBroadcast(intentForNotifciationType9);

        }
        else if(type.equals("4")){
            Bundle bundle = new Bundle();
            Gson gson = new Gson();

            TourNotificationText notificationText = gson.fromJson(messageBody.toString(),TourNotificationText.class);

            bundle.putSerializable("notificationText",notificationText);
            intentForNotifciationType4.putExtras(bundle);
            sendBroadcast(intentForNotifciationType4);
            if(!userId.equals(notificationText.getUserId())) {
                pushNotificationOnTour(notificationText, null);
            }

        }
        else if(type.equals("3")){
            Bundle bundle = new Bundle();
            Gson gson = new Gson();

            TourNotificationLimitSpeed notificationLimitSpeed= gson.fromJson(messageBody.toString(),TourNotificationLimitSpeed.class);

            bundle.putSerializable("notificationLimitSpeed",notificationLimitSpeed);
            intentForNotifciationType3.putExtras(bundle);
            sendBroadcast(intentForNotifciationType3);
            if(!userId.equals(Integer.parseInt(notificationLimitSpeed.getUserId()))) {
                pushNotificationOnTour(null, notificationLimitSpeed);
            }
        }
    }

    @Override
    public void onNewToken(@NonNull String firebaseToken) {
        super.onNewToken(firebaseToken);
        sendRegistrationToServer(firebaseToken);

    }

    private void sendRegistrationToServer(String firebaseToken) {
        APITour apiTour = new APIRetrofitCreator().getAPIService();
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        apiTour.registerFirebaseToken(TokenStorage.getInstance().getAccessToken(),firebaseToken,android_id,1,"1.0").enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if(response.isSuccessful()){
                    Log.d("Register Firebase", "onResponse: successfully");
                }
                else{
                    Log.d("Register Firebase", "onResponse: failed");
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Log.d("Register Firebase", "onResponse: failed");
            }
        });
    }

    private void pushNotificationOnTour(TourNotificationText notiText, TourNotificationLimitSpeed notiSpeed){
        Intent intentNoti = new Intent(this, HomeActivity.class);
        Bundle bundle = new Bundle();
        if(notiText!=null){
            bundle.putBoolean("isNotiText",true);
            intentNoti.putExtras(bundle);
        }
        else{
            bundle.putSerializable("speedNoti",notiSpeed);
            intentNoti.putExtras(bundle);
        }
        intentNoti.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentNoti, PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.project_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setContentTitle("Thông báo mới")
                        .setContentText("Có một thông báo mới trong chuyến đi mà bạn theo dõi")
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(NotificationManager.IMPORTANCE_HIGH);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }
}
