package com.example.nirvana.adapter;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nirvana.LoadDataBase;
import com.example.nirvana.MainActivity;
import com.example.nirvana.R;
import com.example.nirvana.data.MyDbHandler;
import com.example.nirvana.model.MusicFiles;
import com.example.nirvana.model.PlaylistModel;
import com.example.nirvana.params.Parameter;
import com.example.nirvana.player.PlayerActivity;
import com.example.nirvana.playlist.PlaylistDetailsActivity;
import com.example.nirvana.playlist.PlaylistFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    private Context mContext;
    public static ArrayList<MusicFiles> musicFiles;

    public static List<List<MusicFiles>> files = new ArrayList<>();

    private List<String> playlistNames;
    private List<List<MusicFiles>> playlistSongs;

    private LoadDataBase loadDataBase;
    private MyDbHandler myDbHandler;

    public MusicAdapter(Context mContext, ArrayList<MusicFiles> musicFiles) {
        this.mContext = mContext;
        this.musicFiles = musicFiles;

        loadDataBase = new LoadDataBase(mContext);
        myDbHandler = new MyDbHandler(mContext);

        playlistNames = loadDataBase.loadPlaylistNames();
        playlistSongs = loadDataBase.loadSongsOfPlaylist();

    }

    @NonNull
    @Override
    public MusicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.music_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.ViewHolder holder, final int position) {

        final byte[] albumArt = getAlbumArt(musicFiles.get(position).getPath());
        String title = musicFiles.get(position).getTitle();

        holder.setData(albumArt, title);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent playerIntent = new Intent(mContext, PlayerActivity.class);
                playerIntent.putExtra("position", position);
                mContext.startActivity(playerIntent);

            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {



                return false;
            }
        });

        holder.menuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.delete:

                                deleteSong(position, v);

                                break;

                            case R.id.addToPlaylist:

                                final Dialog playlistDialog = new Dialog(mContext);
                                playlistDialog.setContentView(R.layout.playlist_dialog);
                                playlistDialog.setCancelable(false);

                                playlistDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                playlistDialog.show();

                                final ListView names = playlistDialog.findViewById(R.id.names);

                                Button createNewPlaylistButton = playlistDialog.findViewById(R.id.createNewPlaylistButton);
                                Button cancelButton = playlistDialog.findViewById(R.id.cancelButton);
                                final Button confirmButton = playlistDialog.findViewById(R.id.confiemButton);

                                final EditText playlistName = playlistDialog.findViewById(R.id.playlistName);

                                final ArrayAdapter arrayAdapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, playlistNames);

                                names.setAdapter(arrayAdapter);

                                createNewPlaylistButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        playlistName.setVisibility(View.VISIBLE);

                                        confirmButton.setVisibility(View.VISIBLE);

                                        confirmButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                if (!TextUtils.isEmpty(playlistName.getText())) {

                                                    String name = playlistName.getText().toString();

                                                    if(!(checkAlreadyExists(name))){

                                                        myDbHandler.addTables(name);

                                                        playlistNames.add(name);
                                                        arrayAdapter.notifyDataSetChanged();


                                                        playlistName.setVisibility(View.INVISIBLE);
                                                        confirmButton.setVisibility(View.GONE);

                                                    }else{

                                                        Toast.makeText(mContext, "Playlist Already Exists!", Toast.LENGTH_SHORT).show();

                                                    }

                                                } else {

                                                    Toast.makeText(mContext, "Please Enter a name for the Playlist", Toast.LENGTH_SHORT).show();

                                                }

                                            }
                                        });

                                    }
                                });

                                cancelButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        playlistDialog.dismiss();

                                    }
                                });


                                names.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int playlistPosition, long id) {

                                        addToPlaylist(playlistPosition, position);

                                    }
                                });

                                break;


                            default:
                                break;

                        }

                        return true;

                    }
                });

            }
        });

    }

    private boolean checkAlreadyExists(String name) {

        for(int i=0; i<playlistNames.size(); i++){

            if(name.equals(playlistNames.get(i))){

                return true;

            }

        }
        return false;
    }

    @Override
    public int getItemCount() {
        return musicFiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView songImage, menuImageView;
        private TextView songTitle;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            songImage = itemView.findViewById(R.id.songImage);
            menuImageView = itemView.findViewById(R.id.menuImageView);
            songTitle = itemView.findViewById(R.id.songTitle);

        }

        private void setData(byte[] albumArt, String title) {

            Glide.with(mContext).load(albumArt).apply(new RequestOptions().placeholder(R.drawable.ic_baseline_photo_24)).into(songImage);
            songTitle.setText(title);

        }

    }

    private byte[] getAlbumArt(String uri) {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] albumArt = retriever.getEmbeddedPicture();
        retriever.release();

        return albumArt;

    }

    ///to be done
    private void deleteSong(int songPosition, View view) {

        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Long.parseLong(musicFiles.get(songPosition).getID()));

        File file = new File(musicFiles.get(songPosition).getPath());

        boolean deleted = file.delete();

        if (deleted) {

            mContext.getContentResolver().delete(contentUri, null, null);

            musicFiles.remove(songPosition);
            notifyItemRemoved(songPosition);
            notifyItemRangeChanged(songPosition, musicFiles.size());

            Snackbar.make(view, "File Deleted ", Snackbar.LENGTH_LONG).show();

        } else {

            Snackbar.make(view, "File can't be Deleted ", Snackbar.LENGTH_LONG).show();

        }
    }

    public void updateList(ArrayList<MusicFiles> mSongs) {

        musicFiles = new ArrayList<>();
        musicFiles.addAll(mSongs);
        notifyDataSetChanged();

    }


    public void addToPlaylist(int listPosition, int songPosition) {

//        PlaylistDetailsActivity.playlistSongs.add(new PlaylistModel(PlaylistFragment.names.get(listPosition), musicFiles.get(songPosition)));
//        List<MusicFiles> adding;
//        for (int i = files.size(); i < PlaylistFragment.names.size(); i++) {
//
//            adding = new ArrayList<>();
//            files.add(adding);
//
//        }
//
//        if (files.get(listPosition).contains(musicFiles.get(songPosition))) {
//
//            Toast.makeText(mContext, "Song is already in the Playlist", Toast.LENGTH_SHORT).show();
//
//        } else {
//
//            files.get(listPosition).add(musicFiles.get(songPosition));
//
//            Toast.makeText(mContext, "Song added to the playlist", Toast.LENGTH_SHORT).show();
//
//        }


        if (playlistSongs.get(listPosition).contains(musicFiles.get(songPosition))) {

            Toast.makeText(mContext, "Song is already in the Playlist", Toast.LENGTH_SHORT).show();

        } else {

            myDbHandler.addSong(musicFiles.get(songPosition), playlistNames.get(listPosition));

            Toast.makeText(mContext, "Song added to the playlist", Toast.LENGTH_SHORT).show();

        }

    }


}
