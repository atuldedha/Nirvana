package com.example.nirvana.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.nirvana.service.MusicService;

import static com.example.nirvana.ApplicationClass.ACTION_NEXT;
import static com.example.nirvana.ApplicationClass.ACTION_PLAY;
import static com.example.nirvana.ApplicationClass.ACTION_PREVIOUS;

public class NotificationReciever extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

        String actionName = intent.getAction();
        Intent serviceIntent = new Intent(context, MusicService.class);
        if(actionName != null){

            switch(actionName){

                case ACTION_PLAY :
                    serviceIntent.putExtra("actionName", "playPause");
                    context.startService(serviceIntent);
                    break;

                case ACTION_NEXT :
                    serviceIntent.putExtra("actionName", "next");
                    context.startService(serviceIntent);
                    break;

                case ACTION_PREVIOUS :
                    serviceIntent.putExtra("actionName", "previous");
                    context.startService(serviceIntent);
                    break;

            }

        }

    }
}
