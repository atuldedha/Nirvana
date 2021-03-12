package com.example.nirvana.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nirvana.R;
import com.example.nirvana.album.AlbumDetailActivity;
import com.example.nirvana.model.MusicFiles;
import com.example.nirvana.player.PlayerActivity;

import java.util.ArrayList;

public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.ViewHolder> {

    private Context mContext;
    public static ArrayList<MusicFiles> albumfiles;

    public AlbumDetailsAdapter(Context mContext, ArrayList<MusicFiles> albumfiles) {
        this.mContext = mContext;
        this.albumfiles = albumfiles;
    }

    @NonNull
    @Override
    public AlbumDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_item_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumDetailsAdapter.ViewHolder holder, final int position) {

        byte[] albumArt = getAlbumArt(albumfiles.get(position).getPath());
        String title = albumfiles.get(position).getTitle();

        holder.setData(albumArt, title);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playerIntent = new Intent(mContext, PlayerActivity.class);
                playerIntent.putExtra("position", position);
                playerIntent.putExtra("sender","albumDetails");

                mContext.startActivity(playerIntent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return albumfiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView songImage;
        private TextView songName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            songImage = itemView.findViewById(R.id.songImage);

            songName = itemView.findViewById(R.id.songTitle);
        }

        private void setData(byte[] albumArt, String title){

            Glide.with(mContext).load(albumArt).apply(new RequestOptions().placeholder(R.drawable.ic_baseline_photo_24)).into(songImage);
            songName.setText(title);

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
