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

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<MusicFiles> albumfiles;

    public AlbumAdapter(Context mContext, ArrayList<MusicFiles> albumfiles) {
        this.mContext = mContext;
        this.albumfiles = albumfiles;
    }

    @NonNull
    @Override
    public AlbumAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.album_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AlbumAdapter.ViewHolder holder, final int position) {

        byte[] albumArt = getAlbumArt(albumfiles.get(position).getPath());
        String title = albumfiles.get(position).getAlbum();

        holder.setData(albumArt, title);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent albumDetails = new Intent(holder.itemView.getContext(), AlbumDetailActivity.class);
                albumDetails.putExtra("albumName", albumfiles.get(position).getAlbum());
                holder.itemView.getContext().startActivity(albumDetails);
            }
        });

    }

    @Override
    public int getItemCount() {
        return albumfiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView albumImage;
        private TextView albumName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            albumImage = itemView.findViewById(R.id.albumImage);

            albumName = itemView.findViewById(R.id.albumName);

        }

        private void setData(byte[] albumArt, String title){

            Glide.with(mContext).load(albumArt).apply(new RequestOptions().placeholder(R.drawable.ic_baseline_photo_24)).into(albumImage);
            albumName.setText(title);

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
