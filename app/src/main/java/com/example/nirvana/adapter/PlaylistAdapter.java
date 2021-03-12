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
import com.example.nirvana.LoadDataBase;
import com.example.nirvana.R;
import com.example.nirvana.model.MusicFiles;
import com.example.nirvana.playlist.PlaylistDetailsActivity;
import com.example.nirvana.playlist.PlaylistFragment;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    private Context mContext;
    private List<String> playlistFiles;

    private List<List<MusicFiles>> playlistSongs;

    public PlaylistAdapter(Context mContext, List<String> playlistFiles, List<List<MusicFiles>> playlistSongs) {
        this.mContext = mContext;
        this.playlistFiles = playlistFiles;

        this.playlistSongs = playlistSongs;
    }

    @NonNull
    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.playlist_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.ViewHolder holder, final int position) {

        byte[] albumArt = null;
        String title = "";

        if (playlistFiles.size() != 0) {

            title = playlistFiles.get(position);

            holder.playlistName.setText(title);
        }


        List<MusicFiles> images = new ArrayList<>();

        for (int i = 0; i < playlistFiles.size(); i++) {

            if (title.equals(playlistFiles.get(i))) {

                images.addAll(playlistSongs.get(i));

            }

        }

        if (images != null) {

            for (int i = 0; i < images.size(); i++) {

                albumArt = getAlbumArt(images.get(i).getPath());

                for (int j = i; j < 4; j++) {

                    ImageView imageView = (ImageView) holder.gridLayout.getChildAt(j);

                    Glide.with(mContext).load(albumArt).into(imageView);
                }
            }

        } else {

            for (int j = 0; j < 4; j++) {

                ImageView imageView = (ImageView) holder.gridLayout.getChildAt(j);

                Glide.with(mContext).load(R.drawable.ic_baseline_photo_24).into(imageView);
            }

        }

        holder.playlistMenuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.getMenuInflater().inflate(R.menu.delet_menu, popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch(item.getItemId()){

                            case R.id.deletePlaylist :

                                //deletePlaylist(position);

                                break;

                            default:

                                break;

                        }

                        return true;

                    }
                });

            }

        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent playlistDetailsIntent = new Intent(mContext, PlaylistDetailsActivity.class);

                playlistDetailsIntent.putExtra("playlistName", playlistFiles.get(position));

                playlistDetailsIntent.putExtra("playlistPosition", position);

                mContext.startActivity(playlistDetailsIntent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return playlistFiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private GridLayout gridLayout;

        private TextView playlistName;

        private ImageView playlistMenuImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            gridLayout = itemView.findViewById(R.id.playlistImagesContainer);

            playlistName = itemView.findViewById(R.id.playlistName);

            playlistMenuImageView = itemView.findViewById(R.id.playlistMenuImageView);

        }

        private void setData(byte[] albumArt, String title) {

            //Glide.with(mContext).load(albumArt).apply(new RequestOptions().placeholder(R.drawable.ic_baseline_photo_24)).into(playlistImage);

            playlistName.setText(title);

        }

    }

    private byte[] getAlbumArt(String uri) {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] albumArt = retriever.getEmbeddedPicture();
        retriever.release();

        return albumArt;

    }

//    private void deletePlaylist(int playlistPosition){
//
//        String title = PlaylistFragment.pla.get(playlistPosition);
//
//        for (int i=0; i<PlaylistDetailsActivity.playlistSongs.size(); i++){
//
//            if(title.equals(PlaylistDetailsActivity.playlistSongs.get(i).getName())){
//
//                PlaylistDetailsActivity.playlistSongs.remove(i);
//
//            }
//
//        }
//
//       MusicAdapter.files.remove(playlistPosition);
//
//
//        PlaylistFragment.names.remove(playlistPosition);
//
//        notifyDataSetChanged();
//
//    }

}
