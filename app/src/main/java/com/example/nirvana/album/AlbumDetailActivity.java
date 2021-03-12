package com.example.nirvana.album;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nirvana.MainActivity;
import com.example.nirvana.R;
import com.example.nirvana.adapter.AlbumDetailsAdapter;
import com.example.nirvana.model.MusicFiles;

import java.util.ArrayList;


public class AlbumDetailActivity extends AppCompatActivity {

    RecyclerView albumDetailsRecyclerView;
    ImageView albumDetailsImage;
    String albumName;
    ArrayList<MusicFiles> albumSongs = new ArrayList<>();
    AlbumDetailsAdapter albumDetailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);

        initWidgets();

        byte[] albumArt = getAlbumArt(albumSongs.get(0).getPath());
        Glide.with(this).load(albumArt).apply(new RequestOptions().placeholder(R.drawable.ic_baseline_photo_24)).into(albumDetailsImage);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(albumSongs.size() > 0 ){

            albumDetailsAdapter = new AlbumDetailsAdapter(this, albumSongs);
            albumDetailsRecyclerView.setAdapter(albumDetailsAdapter);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            albumDetailsRecyclerView.setLayoutManager(linearLayoutManager);

        }
    }

    private void initWidgets(){

        albumDetailsRecyclerView = findViewById(R.id.albumDetailsRecyclerView);
        albumDetailsImage = findViewById(R.id.albumDetailsImage);

        albumName = getIntent().getStringExtra("albumName");

        int x = 0;

        for(int i = 0; i< MainActivity.musicFiles.size(); i++){

            if(albumName.equals(MainActivity.musicFiles.get(i).getAlbum())){

                albumSongs.add(x, MainActivity.musicFiles.get(i));

                x++;

            }

        }


    }

    private byte[] getAlbumArt(String uri){

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] albumArt = retriever.getEmbeddedPicture();
        retriever.release();

        return albumArt;

    }
}