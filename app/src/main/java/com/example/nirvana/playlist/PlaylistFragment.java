package com.example.nirvana.playlist;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nirvana.LoadDataBase;
import com.example.nirvana.R;
import com.example.nirvana.adapter.PlaylistAdapter;
import com.example.nirvana.data.MyDbHandler;
import com.example.nirvana.model.MusicFiles;

import java.util.ArrayList;
import java.util.List;

public class PlaylistFragment extends Fragment {

    private LoadDataBase loadDataBase;

    private RecyclerView playlistRecyclerView;

    private List<String> playlistNames;
    private List<List<MusicFiles>> playlistSongs;

    public PlaylistFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        playlistRecyclerView = view.findViewById(R.id.playlistRecyclerView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);

        loadDataBase = new LoadDataBase(getContext());

        playlistNames = loadDataBase.loadPlaylistNames();
        playlistSongs = loadDataBase.loadSongsOfPlaylist();

        if(playlistNames.size() != 0){

            PlaylistAdapter playlistAdapter = new PlaylistAdapter(getContext(), playlistNames, playlistSongs);

            playlistRecyclerView.setLayoutManager(gridLayoutManager);

            playlistRecyclerView.setAdapter(playlistAdapter);

        }


        return view;
    }
}