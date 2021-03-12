package com.example.nirvana;

import android.app.Activity;
import android.content.Context;

import com.example.nirvana.data.MyDbHandler;
import com.example.nirvana.model.MusicFiles;

import java.util.ArrayList;
import java.util.List;

public class LoadDataBase {

    MyDbHandler myDbHandler;

    Context mContext;

    private List<String> playlistNames = new ArrayList<>();
    private List<List<MusicFiles>> playlistSongs = new ArrayList<>();

    public LoadDataBase(Context mContext) {
        this.mContext = mContext;

        myDbHandler = new MyDbHandler(mContext);

    }

    public List<String> loadPlaylistNames(){

        playlistNames = myDbHandler.getTebles();

        return playlistNames;

    }

    public List<List<MusicFiles>> loadSongsOfPlaylist(){

        if(playlistNames.size() != 0){

            for(int i=0 ; i<playlistNames.size(); i++){

                playlistSongs.add(i, myDbHandler.showPlaylist(playlistNames.get(i)));

            }

        }

        return playlistSongs;

    }


}
