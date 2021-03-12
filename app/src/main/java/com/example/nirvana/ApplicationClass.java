package com.example.nirvana;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class ApplicationClass extends Application {


    public static final String CHANNEL_ID_1 = "CHANNEL_1";
    public static final String CHANNEL_ID_2 = "CHANNEL_2";
    public static final String ACTION_NEXT = "NEXT";
    public static final String ACTION_PREVIOUS = "PREVIOUS";
    public static final String ACTION_PLAY = "PLAY";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

    }

    private void createNotificationChannel(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel notificationChannel1 = new NotificationChannel(CHANNEL_ID_1,
                    "channel{1}", NotificationManager.IMPORTANCE_HIGH);

            notificationChannel1.setDescription("channel 1 desciption");

            NotificationChannel notificationChannel2 = new NotificationChannel(CHANNEL_ID_2,
                    "channel{2}", NotificationManager.IMPORTANCE_HIGH);

            notificationChannel2.setDescription("channel 2 desciption");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel1);
            notificationManager.createNotificationChannel(notificationChannel2);

        }

    }

}