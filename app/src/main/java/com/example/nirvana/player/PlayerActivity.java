package com.example.nirvana.player;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.palette.graphics.Palette;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nirvana.ActionPlaying;
import com.example.nirvana.ImageAnimation;
import com.example.nirvana.MainActivity;
import com.example.nirvana.R;
import com.example.nirvana.adapter.AlbumDetailsAdapter;
import com.example.nirvana.adapter.MusicAdapter;
import com.example.nirvana.adapter.PlaylistDetailsAdapter;
import com.example.nirvana.model.MusicFiles;
import com.example.nirvana.reciever.NotificationReciever;
import com.example.nirvana.service.MusicService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

import static com.example.nirvana.ApplicationClass.ACTION_NEXT;
import static com.example.nirvana.ApplicationClass.ACTION_PLAY;
import static com.example.nirvana.ApplicationClass.ACTION_PREVIOUS;
import static com.example.nirvana.ApplicationClass.CHANNEL_ID_2;

public class PlayerActivity extends AppCompatActivity implements
         ActionPlaying, ServiceConnection {

    TextView songDurationPlayedTextView, songTotalDurationTextView, playingSongTitle, playingSongArtistName;

    ImageView backImageView, menuImageView, playingSongImage, shuffleImageView, repeatImageView, prevImageView, nextImageView;

    FloatingActionButton playPauseButton;

    SeekBar durationPlayedSeekBar;

    int position = -1;

    public static ArrayList<MusicFiles> playedSong = new ArrayList<>();

    public static Uri uri;

    //public static MediaPlayer musicService;

    private Handler handler = new Handler();

    private Thread playPauseThread, prevThread, nextThread;

    MusicService musicService;

    MediaSessionCompat mediaSessionCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_player);

        initWidgets();

        mediaSessionCompat = new MediaSessionCompat(getBaseContext(), "My Audio");

        getIntentMethod();

        durationPlayedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (musicService != null && fromUser) {

                    musicService.seekTo(progress * 1000);

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (musicService != null) {

                    int currentPosition = musicService.getCurrentPosition() / 1000;
                    durationPlayedSeekBar.setProgress(currentPosition);

                    songDurationPlayedTextView.setText(setCurrentTime(currentPosition));

                }
                handler.postDelayed(this, 1000);

            }
        });

        shuffleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MainActivity.isShuffle) {

                    MainActivity.isShuffle = false;
                    shuffleImageView.setImageResource(R.drawable.ic_shufle_off);

                } else {

                    MainActivity.isShuffle = true;
                    shuffleImageView.setImageResource(R.drawable.ic_shuffle_on);

                }

            }
        });

        repeatImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MainActivity.isRepeat) {

                    MainActivity.isRepeat = false;
                    repeatImageView.setImageResource(R.drawable.ic_repeat_off);

                } else {

                    MainActivity.isRepeat = true;
                    repeatImageView.setImageResource(R.drawable.ic_reapeat_on);

                }

            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent serviceIntent = new Intent(this, MusicService.class);
        bindService(serviceIntent, this, BIND_AUTO_CREATE);

        playPauseButtonThread();
        prevButtonThread();
        nextButtonThread();

    }

    @Override
    protected void onPause() {
        super.onPause();

        unbindService(this);

    }

    private String setCurrentTime(int mCurrentPosition) {

        String totalOut = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        totalOut = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" + seconds;

        if (seconds.length() == 1) {

            return totalNew;

        } else {

            return totalOut;

        }

    }

    private void getIntentMethod() {

        position = getIntent().getIntExtra("position", -1);
        String sender = getIntent().getStringExtra("sender");

        if (sender != null && sender.equals("albumDetails")) {

            playedSong = AlbumDetailsAdapter.albumfiles;

        }else if(sender !=null && sender.equals("playlistName")){

            ArrayList<MusicFiles> tempPlaylist = new ArrayList<>();

            playedSong = (ArrayList<MusicFiles>) PlaylistDetailsAdapter.playlist;

        }else {

            playedSong = MusicAdapter.musicFiles;

        }


        if (playedSong != null) {

            playPauseButton.setImageResource(R.drawable.ic_pause);
            uri = Uri.parse(playedSong.get(position).getPath());

        }
        shoWNotification(R.drawable.ic_pause);
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.putExtra("servicePosition", position);
        startService(serviceIntent);

    }

    private void initWidgets() {

        songDurationPlayedTextView = findViewById(R.id.songDurationPlayedTextView);
        songTotalDurationTextView = findViewById(R.id.songTotalDurationTextView);
        playingSongTitle = findViewById(R.id.playingSongTitle);
        playingSongArtistName = findViewById(R.id.playingSongArtistName);

        backImageView = findViewById(R.id.backImageView);
        menuImageView = findViewById(R.id.menuImageView);
        playingSongImage = findViewById(R.id.playingSongImage);
        shuffleImageView = findViewById(R.id.shuffleImageView);
        repeatImageView = findViewById(R.id.repeatImageView);
        prevImageView = findViewById(R.id.prevImageView);
        nextImageView = findViewById(R.id.nextImageView);

        playPauseButton = findViewById(R.id.playPauseButton);

        durationPlayedSeekBar = findViewById(R.id.durationPlayedSeekBar);


    }

    private void getMetaData(Uri uri) {

        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(uri.toString());

        int totalDuration = Integer.parseInt(playedSong.get(position).getDuration()) / 1000;
        songTotalDurationTextView.setText(setCurrentTime(totalDuration));

        byte[] albumArt = mediaMetadataRetriever.getEmbeddedPicture();

        Bitmap bitmap = null;

        if (albumArt != null) {

            bitmap = BitmapFactory.decodeByteArray(albumArt, 0, albumArt.length);

            ImageAnimation imageAnimation = new ImageAnimation();
            imageAnimation.loadImageAnimation(this,  playingSongImage, bitmap);

            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();

                    if (swatch != null) {

                        ImageView gradientImageView = findViewById(R.id.gradientImageView);
                        ConstraintLayout playerContainer = findViewById(R.id.playerContainerConstraintLayout);
                        gradientImageView.setBackgroundResource(R.drawable.player_gradient);
                        playerContainer.setBackgroundResource(R.drawable.main_background);

                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(), 0x00000000});
                        gradientImageView.setBackground(gradientDrawable);

                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(), swatch.getRgb()});
                        playerContainer.setBackground(gradientDrawableBg);

                        playingSongTitle.setTextColor(swatch.getTitleTextColor());
                        playingSongArtistName.setTextColor(swatch.getBodyTextColor());

                    } else {

                        ImageView gradientImageView = findViewById(R.id.gradientImageView);
                        ConstraintLayout playerContainer = findViewById(R.id.playerContainerConstraintLayout);
                        gradientImageView.setBackgroundResource(R.drawable.player_gradient);
                        playerContainer.setBackgroundResource(R.drawable.main_background);

                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000, 0x00000000});
                        gradientImageView.setBackground(gradientDrawable);

                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000, swatch.getRgb()});
                        playerContainer.setBackground(gradientDrawableBg);

                        playingSongTitle.setTextColor(Color.WHITE);
                        playingSongArtistName.setTextColor(Color.DKGRAY);

                    }
                }
            });

        } else {
            
            ImageAnimation imageAnimation = new ImageAnimation();
            imageAnimation.loadImageAnimation(this,  playingSongImage, bitmap );

            ImageView gradientImageView = findViewById(R.id.gradientImageView);
            ConstraintLayout playerContainer = findViewById(R.id.playerContainerConstraintLayout);
            gradientImageView.setBackgroundResource(R.drawable.player_gradient);
            playerContainer.setBackgroundResource(R.drawable.main_background);

            playingSongTitle.setTextColor(Color.WHITE);
            playingSongArtistName.setTextColor(Color.DKGRAY);
        }

    }

    private void playPauseButtonThread() {

        playPauseThread = new Thread() {
            @Override
            public void run() {
                super.run();
                playPauseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPauseButtonClicked();
                    }
                });
            }
        };

        playPauseThread.start();

    }

    public void playPauseButtonClicked() {

        if (musicService.isPlaying()) {

            playPauseButton.setImageResource(R.drawable.ic_play);
           shoWNotification(R.drawable.ic_play);
            musicService.pause();
            durationPlayedSeekBar.setMax(musicService.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (musicService != null) {

                        int currentPosition = musicService.getCurrentPosition() / 1000;
                        durationPlayedSeekBar.setProgress(currentPosition);

                    }
                    handler.postDelayed(this, 1000);

                }
            });

        } else {

            playPauseButton.setImageResource(R.drawable.ic_pause);
            shoWNotification(R.drawable.ic_pause);
            musicService.start();
            durationPlayedSeekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (musicService != null) {

                        int currentPosition = musicService.getCurrentPosition() / 1000;
                        durationPlayedSeekBar.setProgress(currentPosition);

                    }
                    handler.postDelayed(this, 1000);

                }
            });

        }

    }

    private void prevButtonThread() {

        prevThread = new Thread() {
            @Override
            public void run() {
                super.run();
                prevImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevButtonClicked();
                    }
                });
            }
        };

        prevThread.start();

    }

    public void prevButtonClicked() {

        if (musicService.isPlaying()) {
            musicService.stop();
            musicService.release();

            if ((MainActivity.isShuffle) && (!MainActivity.isRepeat)) {

                position = getRandomPosition(playedSong.size() - 1);

            } else if ((!MainActivity.isShuffle) && (!MainActivity.isRepeat)) {

                if (position - 1 < 0) {
                    position = playedSong.size() - 1;
                } else {
                    position = position - 1;
                }

            }

            uri = Uri.parse(playedSong.get(position).getPath());
            musicService.createMediaPlayer(position);
            getMetaData(uri);

            playingSongTitle.setText(playedSong.get(position).getTitle());
            playingSongArtistName.setText(playedSong.get(position).getArtist());

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (musicService != null) {

                        int currentPosition = musicService.getCurrentPosition() / 1000;
                        durationPlayedSeekBar.setProgress(currentPosition);

                    }
                    handler.postDelayed(this, 1000);

                }
            });

            musicService.onCompleted();
            shoWNotification(R.drawable.ic_pause);
            playPauseButton.setBackgroundResource(R.drawable.ic_pause);

            musicService.start();

        } else {

            musicService.stop();
            musicService.release();

            if ((MainActivity.isShuffle) && (!MainActivity.isRepeat)) {

                position = getRandomPosition(playedSong.size() - 1);

            } else if ((!MainActivity.isShuffle) && (!MainActivity.isRepeat)) {

                if (position - 1 < 0) {
                    position = playedSong.size() - 1;
                } else {
                    position = position - 1;
                }

            }

            uri = Uri.parse(playedSong.get(position).getPath());
            musicService.createMediaPlayer(position);
            getMetaData(uri);

            playingSongTitle.setText(playedSong.get(position).getTitle());
            playingSongArtistName.setText(playedSong.get(position).getArtist());

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (musicService != null) {

                        int currentPosition = musicService.getCurrentPosition() / 1000;
                        durationPlayedSeekBar.setProgress(currentPosition);

                    }
                    handler.postDelayed(this, 1000);

                }
            });

            musicService.onCompleted();
            shoWNotification(R.drawable.ic_play);
            playPauseButton.setBackgroundResource(R.drawable.ic_play);

        }

    }

    private void nextButtonThread() {

        nextThread = new Thread() {
            @Override
            public void run() {
                super.run();
                nextImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextButtonClicked();
                    }
                });
            }
        };

        nextThread.start();

    }

    public void nextButtonClicked() {

        if (musicService.isPlaying()) {
            musicService.stop();
            musicService.release();

            if ((MainActivity.isShuffle) && (!MainActivity.isRepeat)) {

                position = getRandomPosition(playedSong.size() - 1);

            } else if ((!MainActivity.isShuffle) && (!MainActivity.isRepeat)) {

                position = (position + 1) % playedSong.size();

            }

            uri = Uri.parse(playedSong.get(position).getPath());
            musicService.createMediaPlayer(position);
            getMetaData(uri);

            playingSongTitle.setText(playedSong.get(position).getTitle());
            playingSongArtistName.setText(playedSong.get(position).getArtist());

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (musicService != null) {

                        int currentPosition = musicService.getCurrentPosition() / 1000;
                        durationPlayedSeekBar.setProgress(currentPosition);

                    }
                    handler.postDelayed(this, 1000);

                }
            });

            musicService.onCompleted();

            shoWNotification(R.drawable.ic_pause);

            playPauseButton.setBackgroundResource(R.drawable.ic_pause);

            musicService.start();

        } else {

            musicService.stop();
            musicService.release();

            if ((MainActivity.isShuffle) && (!MainActivity.isRepeat)) {

                position = getRandomPosition(playedSong.size() - 1);

            } else if ((!MainActivity.isShuffle) && (!MainActivity.isRepeat)) {

                position = (position + 1) % playedSong.size();

            }

            uri = Uri.parse(playedSong.get(position).getPath());
            musicService.createMediaPlayer(position);
            getMetaData(uri);

            playingSongTitle.setText(playedSong.get(position).getTitle());
            playingSongArtistName.setText(playedSong.get(position).getArtist());

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (musicService != null) {

                        int currentPosition = musicService.getCurrentPosition() / 1000;
                        durationPlayedSeekBar.setProgress(currentPosition);

                    }
                    handler.postDelayed(this, 1000);

                }
            });

            musicService.onCompleted();
            shoWNotification(R.drawable.ic_play);
            playPauseButton.setBackgroundResource(R.drawable.ic_play);

        }

    }

    private int getRandomPosition(int size) {

        Random random = new Random();

        return random.nextInt(size + 1);

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

        MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
        musicService = myBinder.getService();
        musicService.setCallback(this);
        Toast.makeText(this, "service is connected", Toast.LENGTH_SHORT).show();

        playingSongTitle.setText(playedSong.get(position).getTitle());

        playingSongArtistName.setText(playedSong.get(position).getArtist());

        musicService.onCompleted();

        durationPlayedSeekBar.setMax(musicService.getDuration() / 1000);

        getMetaData(uri);

//        musicService.shoWNotification(R.drawable.ic_pause);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

        musicService = null;

        Toast.makeText(this, "service is disconnected", Toast.LENGTH_SHORT).show();

    }

    public void shoWNotification(int playPauseButton){

        Intent intent = new Intent(this, PlayerActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,intent,0);

        Intent prevIntent = new Intent(this, NotificationReciever.class)
                .setAction(ACTION_PREVIOUS);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(this,0,prevIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(this, NotificationReciever.class)
                .setAction(ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this,0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent playPauseIntent = new Intent(this, NotificationReciever.class)
                .setAction(ACTION_PLAY);
        PendingIntent playPausePendingIntent = PendingIntent.getBroadcast(this,0,playPauseIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        byte[] picture = null;

        picture = getAlbumArt(playedSong.get(position).getPath());

        Bitmap thumb = null;

        if(picture != null){

            thumb = BitmapFactory.decodeByteArray(picture, 0, picture.length);

        }else{

            thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_baseline_photo_24);

        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setSmallIcon(playPauseButton)
                .setLargeIcon(thumb)
                .setContentTitle(playedSong.get(position).getTitle())
                .setContentText(playedSong.get(position).getArtist())
                .addAction(R.drawable.ic_prev, "Previous", prevPendingIntent)
                .addAction(playPauseButton, "Pause", playPausePendingIntent)
                .addAction(R.drawable.ic_next, "Next", nextPendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);

    }


    private byte[] getAlbumArt(String uri) {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] albumArt = retriever.getEmbeddedPicture();
        retriever.release();

        return albumArt;

    }

}