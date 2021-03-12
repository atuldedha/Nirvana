package com.example.nirvana.song_fragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nirvana.MainActivity;
import com.example.nirvana.R;
import com.example.nirvana.adapter.MusicAdapter;

import java.util.ArrayList;

public class SongFragment extends Fragment {

    private RecyclerView recyclerView;
    public static MusicAdapter musicAdapter;

    public SongFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_song, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        if(MainActivity.musicFiles.size() != 0){

            musicAdapter = new MusicAdapter(getContext(), MainActivity.musicFiles);
            recyclerView.setAdapter(musicAdapter);

        }

        return view;
    }



}