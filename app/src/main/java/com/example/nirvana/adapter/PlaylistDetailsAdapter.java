package com.example.nirvana.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.MemoryFile;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nirvana.R;
import com.example.nirvana.model.MusicFiles;
import com.example.nirvana.player.PlayerActivity;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDetailsAdapter extends RecyclerView.Adapter<PlaylistDetailsAdapter.ViewHolder> {

    private Context mContext;
    public static List<MusicFiles> playlist;

    public PlaylistDetailsAdapter(Context mContext, List<MusicFiles> playlist) {
        this.mContext = mContext;
        this.playlist = playlist;
    }

    @NonNull
    @Override
    public PlaylistDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.music_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistDetailsAdapter.ViewHolder holder, final int position) {

        byte[] albumArt = getAlbumArt(playlist.get(position).getPath());

        String title = playlist.get(position).getTitle();

        holder.setData(albumArt, title);

        holder.menuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.getMenuInflater().inflate(R.menu.remove_menu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        removeSongFromPlaylist(position);

                        return true;
                    }
                });

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent playerIntent = new Intent(mContext, PlayerActivity.class);

                playerIntent.putExtra("position", position);
                playerIntent.putExtra("sender", "playlistName");

                mContext.startActivity(playerIntent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return playlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        GridLayout playlistImagesContainer;

        ImageView playlistSongImage;
        TextView playlistSongTitle;
        ImageView menuImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            playlistSongImage = itemView.findViewById(R.id.songImage);
            menuImageView = itemView.findViewById(R.id.menuImageView);

            playlistSongTitle = itemView.findViewById(R.id.songTitle);

        }

        private void setData(byte[] albumArt, String title){

            Glide.with(mContext).load(albumArt).apply(new RequestOptions().placeholder(R.drawable.ic_baseline_photo_24)).into(playlistSongImage);

            playlistSongTitle.setText(title);

        }

    }

    private byte[] getAlbumArt(String uri){

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] albumArt = retriever.getEmbeddedPicture();
        retriever.release();

        return albumArt;

    }

    private void removeSongFromPlaylist(int songPosition){

        playlist.remove(songPosition);

    }

}
