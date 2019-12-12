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
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ygaps.travelapp.ApiService.APIRetrofitCreator;
import com.ygaps.travelapp.ApiService.APITour;
import com.ygaps.travelapp.AppHelper.TokenStorage;
import com.ygaps.travelapp.HomeActivity;
import com.ygaps.travelapp.MainActivity;
import com.ygaps.travelapp.Model.MessageResponse;
import com.ygaps.travelapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    final static String TAG="FirebaseMessaging";

    @Override
    public void onCreate() {
//        Log.d(TAG, "onCreate: created service");
        super.onCreate();
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
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

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
                                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)))
                            .addAction(new NotificationCompat.Action(
                                    android.R.drawable.ic_input_add,
                                    "Chấp nhận",
                                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)));

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
            //SEND LOCATION
//            {"memPos":"[{\"id\":585,\"lat\":\"10.7793304\",\"long\":\"106.6118907\"}]","type":"9"}
            Log.d(TAG, "sendNotification: SEND LOCATION");
        }
    }

    @Override
    public void onNewToken(@NonNull String firebaseToken) {
        super.onNewToken(firebaseToken);
        sendRegistrationToServer(firebaseToken);

    }

    private void sendRegistrationToServer(String firebaseToken) {
//        APITour apiTour = new APIRetrofitCreator().getAPIService();
//        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//        apiTour.registerFirebaseToken(TokenStorage.getInstance().getAccessToken(),firebaseToken,android_id,1,"1.0").enqueue(new Callback<MessageResponse>() {
//            @Override
//            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
//                if(response.isSuccessful()){
//                    Log.d("Register Firebase", "onResponse: successfully");
//                }
//                else{
//                    Log.d("Register Firebase", "onResponse: failed");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<MessageResponse> call, Throwable t) {
//                Log.d("Register Firebase", "onResponse: failed");
//            }
//        });
    }
}
