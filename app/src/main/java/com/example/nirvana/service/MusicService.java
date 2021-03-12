package com.example.nirvana.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.nirvana.ActionPlaying;
import com.example.nirvana.R;
import com.example.nirvana.model.MusicFiles;
import com.example.nirvana.player.PlayerActivity;
import com.example.nirvana.reciever.NotificationReciever;

import java.util.ArrayList;

import static com.example.nirvana.ApplicationClass.ACTION_NEXT;
import static com.example.nirvana.ApplicationClass.ACTION_PLAY;
import static com.example.nirvana.ApplicationClass.ACTION_PREVIOUS;
import static com.example.nirvana.ApplicationClass.CHANNEL_ID_2;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {

    IBinder mBinder = new MyBinder();
    MediaPlayer mediaPlayer;

    private Uri uri;

    ArrayList<MusicFiles> musicFiles = new ArrayList<>();

    int position = -1;

    ActionPlaying actionPlaying;

    @Override
    public void onCreate() {
        super.onCreate();

        musicFiles = PlayerActivity.playedSong;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        Log.d("Bind", "Method");

        return mBinder;
    }

    public class MyBinder extends Binder {

        public MusicService getService() {

            return MusicService.this;

        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int songPosition = intent.getIntExtra("servicePosition", -1);
        String actionName = intent.getStringExtra("actionName");
        if (songPosition != -1) {

            playMedia(songPosition);
        }

        if(actionName != null){

            switch(actionName){

                case "playPause" :
                    Toast.makeText(this, "Play_ Pause", Toast.LENGTH_SHORT).show();

                    if(actionPlaying != null){

                        actionPlaying.playPauseButtonClicked();

                    }

                    break;

                case "next" :
                    Toast.makeText(this, "Next", Toast.LENGTH_SHORT).show();

                    if(actionPlaying != null){

                        actionPlaying.nextButtonClicked();

                    }

                    break;

                case "previous" :
                    Toast.makeText(this, "previous", Toast.LENGTH_SHORT).show();

                    if(actionPlaying != null){

                        actionPlaying.prevButtonClicked();

                    }

                    break;

            }

        }

        return START_STICKY;
    }

    private void playMedia(int startPosition) {

        musicFiles = PlayerActivity.playedSong;
        position = startPosition;
        if (mediaPlayer != null) {

            mediaPlayer.stop();
            mediaPlayer.release();

            if (musicFiles != null) {

                createMediaPlayer(position);
                mediaPlayer.start();

            }

        } else {

            createMediaPlayer(position);
            mediaPlayer.start();

        }

    }

    public void start() {

        mediaPlayer.start();

    }

    public boolean isPlaying() {

        return mediaPlayer.isPlaying();

    }

    public void pause() {

        mediaPlayer.pause();

    }

    public void stop() {

        mediaPlayer.stop();

    }

    public void release() {
        mediaPlayer.release();
    }

    public int getDuration() {

        return mediaPlayer.getDuration();

    }

    public void seekTo(int position) {

        mediaPlayer.seekTo(position);

    }

    public int getCurrentPosition() {

        return mediaPlayer.getCurrentPosition();

    }

    public void createMediaPlayer(int positionInner) {

        position = positionInner;

        uri = Uri.parse(musicFiles.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getBaseContext(), uri);

    }

    public void onCompleted() {

        mediaPlayer.setOnCompletionListener(this);

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        if(actionPlaying != null) {
            actionPlaying.nextButtonClicked();

            if(mediaPlayer != null){

                createMediaPlayer(position);
                mediaPlayer.start();
                onCompleted();

            }

        }


    }

    public void setCallback(ActionPlaying actionPlaying) {

        this.actionPlaying = actionPlaying;

    }

}
