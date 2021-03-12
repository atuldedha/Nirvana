package com.example.nirvana.playlist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.MemoryFile;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nirvana.LoadDataBase;
import com.example.nirvana.R;
import com.example.nirvana.adapter.MusicAdapter;
import com.example.nirvana.adapter.PlaylistDetailsAdapter;
import com.example.nirvana.data.MyDbHandler;
import com.example.nirvana.model.MusicFiles;
import com.example.nirvana.model.PlaylistModel;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDetailsActivity extends AppCompatActivity {

    private RecyclerView playlistDetailsRecyclerView;
    private GridLayout gridLayout;

    private PlaylistDetailsAdapter playlistDetailsAdapter;

    private boolean alreadyInPlaylist = false;

    public static ArrayList<PlaylistModel> playlistSongs = new ArrayList<>();
    ArrayList<MusicFiles> arrayList = new ArrayList<>();

    private LoadDataBase loadDataBase;

    private List<String> playlistNames;
    private List<List<MusicFiles>> songsInPlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_details);

        loadDataBase = new LoadDataBase(this);

        playlistNames = loadDataBase.loadPlaylistNames();
        songsInPlaylist = loadDataBase.loadSongsOfPlaylist();

        playlistDetailsRecyclerView = findViewById(R.id.playlistDetailRecyclerView);
        gridLayout = findViewById(R.id.playlistImagesContainer);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        playlistDetailsRecyclerView.setLayoutManager(linearLayoutManager);
        playlistDetailsRecyclerView.setHasFixedSize(false);

        gridLayout.setVisibility(View.VISIBLE);

        ViewCompat.setNestedScrollingEnabled(playlistDetailsRecyclerView, false);

        String openedPlaylist = getIntent().getStringExtra("playlistName");

        byte[] albumArt;

        ArrayList<MusicFiles> images = new ArrayList<>();

        for (int i = 0; i < playlistNames.size(); i++) {

            if (openedPlaylist.equals(playlistNames.get(i))) {

                images.addAll(songsInPlaylist.get(i));

            }

        }

        if (images != null) {

            for (int i = 0; i < images.size(); i++) {

                albumArt = getAlbumArt(images.get(i).getPath());

                for (int j = i; j < 4; j++) {

                    ImageView imageView = (ImageView) gridLayout.getChildAt(j);

                    Glide.with(this).load(albumArt).centerCrop().into(imageView);
                }
            }

        } else {

            for (int j = 0; j < 4; j++) {

                ImageView imageView = (ImageView) gridLayout.getChildAt(j);

                Glide.with(this).load(R.drawable.ic_baseline_photo_24).into(imageView);
            }

        }

        int playlistPosition = getIntent().getIntExtra("playlistPosition", -1);

        playlistDetailsAdapter = new PlaylistDetailsAdapter(this,songsInPlaylist.get(playlistPosition));

        playlistDetailsRecyclerView.setAdapter(playlistDetailsAdapter);



    }

    private byte[] getAlbumArt(String uri) {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] albumArt = retriever.getEmbeddedPicture();
        retriever.release();

        return albumArt;

    }
}