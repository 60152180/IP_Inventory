package com.example.ip_inventory;

import android.app.Notification;
import android.app.NotificationChannel;

import android.app.NotificationManager;

import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;

import android.net.Uri;

import android.os.Build;

import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;

import android.support.v4.app.NotificationCompat;

import android.util.Log;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;

import com.google.firebase.messaging.RemoteMessage;



public class FireBaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onNewToken(String mToken) {
        super.onNewToken(mToken);
        Log.e("TOKEN",mToken);
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {  //data payload로 보내면 실행

        //Log.d(TAG, "From: " + remoteMessage.getFrom());

        //여기서 메세지의 두가지 타입(1. data payload 2. notification payload)에 따라 다른 처리를 한다.
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            //꺼진 화면을 깨운다. 그러나 이방법은 Deprecated 되었다. 더이상 사용되지 않는다는 것이다. 현재로 작동은 하지만 나중에 어떻게 될지 모른다.
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "ip_inventory2:TAG");
            wakeLock.acquire(3000);

            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            //Log.d(TAG, title + " - " + body);
            //String click_action = remoteMessage.getData().get("clickAction");
            sendNotification(title, body);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

//            String title = remoteMessage.getNotification().getTitle();
//            String body = remoteMessage.getNotification().getBody();
//            String click_action = remoteMessage.getNotification().getClickAction();
//            sendNotification(title, body, click_action);
        }
    }

    private void sendNotification(String title, String body) {
        String channelId = "channel";
        String channelName = "Channel Name";

        NotificationManager notifManager

                = (NotificationManager) getSystemService  (Context.NOTIFICATION_SERVICE);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);

            notifManager.createNotificationChannel(mChannel);

        }

        if (title == null){
            //제목이 없는 payload이면 php에서 보낼때 이미 한번 점검했음.
            title = "공지사항"; //기본제목을 적어 주자.
        }
        //전달된 액티비티에 따라 분기하여 해당 액티비티를 오픈하도록 한다.
        Intent intent;
        intent = new Intent(this, MainActivity.class);
        //번들에 수신한 메세지를 담아서 메인액티비티로 넘겨 보자.
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("body", body);
        intent.putExtras(bundle);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), channelId);

        Intent notificationIntent = new Intent(getApplicationContext()
                , MainActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

        int requestID = (int) System.currentTimeMillis();

        PendingIntent pendingIntent
                = PendingIntent.getActivity(getApplicationContext()
                , requestID
                , notificationIntent
                , PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{1000, 1000})
                .setLights(Color.BLUE, 1,1)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notifManager.notify(0, builder.build());
    }

}
