package com.example.nirvana.model;

import java.util.ArrayList;

public class PlaylistModel {

    String name;
    MusicFiles playlistSong;

    public PlaylistModel(String name, MusicFiles playlistSong) {
        this.name = name;
        this.playlistSong = playlistSong;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MusicFiles getPlaylistSong() {
        return playlistSong;
    }

    public void setPlaylistSong(MusicFiles playlistSong) {
        this.playlistSong = playlistSong;
    }
}
