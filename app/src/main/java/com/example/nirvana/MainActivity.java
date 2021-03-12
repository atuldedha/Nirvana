package com.example.nirvana;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.nirvana.adapter.MusicAdapter;
import com.example.nirvana.album.AlbumFragment;
import com.example.nirvana.model.MusicFiles;
import com.example.nirvana.playlist.PlaylistFragment;
import com.example.nirvana.song_fragment.SongFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private int REQUEST_CODE = 1;
    public static ArrayList<MusicFiles> musicFiles;

    public static boolean isShuffle = false, isRepeat = false;

    public static ArrayList<MusicFiles> albums = new ArrayList<>();

    private String MY_SORT_PREF = "sortOrder";

    private String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.FOREGROUND_SERVICE
    };

    ////sliding sheet

    private ConstraintLayout playerSheet;
    public static BottomSheetBehavior bottomSheetBehavior;

    private ArrayList<MusicFiles> playedSong = new ArrayList<>();

    private ImageView slidingImageView;

    /////sliding sheet

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();

    }

    private void checkPermissions() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);

        } else {

            musicFiles = getAllSongs(this);

            initWidgets();

        }

    }

    private void initWidgets() {

        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        slidingImageView = findViewById(R.id.slidingImageView);

        ViewPAgerAdapter viewPAgerAdapter = new ViewPAgerAdapter(getSupportFragmentManager());

        viewPAgerAdapter.addFragment(new SongFragment(), "Songs");
        viewPAgerAdapter.addFragment(new AlbumFragment(), "Albums");
        viewPAgerAdapter.addFragment(new PlaylistFragment(), "Playlist");

        viewPager.setAdapter(viewPAgerAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    public static class ViewPAgerAdapter extends FragmentPagerAdapter {

        List<Fragment> fragments;
        List<String> fragmentTitle;

        public ViewPAgerAdapter(@NonNull FragmentManager fm) {
            super(fm);

            fragments = new ArrayList<>();
            fragmentTitle = new ArrayList<>();

        }

        private void addFragment(Fragment fragment, String title) {

            fragments.add(fragment);
            fragmentTitle.add(title);

        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                musicFiles = getAllSongs(this);

                initWidgets();

            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);

            }
        }

    }

    public ArrayList<MusicFiles> getAllSongs(Context mContext) {

        SharedPreferences preferences = getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE);
        String sortOrder = preferences.getString("sorting", "sortByName");

        ArrayList<String> dulplicate = new ArrayList<>();

        ArrayList<MusicFiles> tempMusicList = new ArrayList<>();
        albums.clear();

        String order = null;

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        switch (sortOrder) {

            case "sortByName":

                order = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
                break;

            case "sortBySize":

                order = MediaStore.MediaColumns.SIZE + " DESC";
                break;

            case "sortByDate":

                order = MediaStore.MediaColumns.DATE_ADDED + " ASC";
                break;

        }

        String[] projection = {

                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media._ID

        };

        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, order);

        if (cursor != null) {

            while (cursor.moveToNext()) {

                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String artist = cursor.getString(2);
                String path = cursor.getString(3);
                String duration = cursor.getString(4);
                String ID = cursor.getString(5);

                MusicFiles musicFiles = new MusicFiles(ID, path, title, artist, album, duration);

                Log.d("Suxxess", "Path " + path + "Album" + album);

                tempMusicList.add(musicFiles);

                if (!dulplicate.contains(album)) {
                    albums.add(musicFiles);
                    dulplicate.add(album);
                }

            }

            cursor.close();

        }

        return tempMusicList;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.searchBar);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String userInput = newText.toLowerCase();

        ArrayList<MusicFiles> searchedFiles = new ArrayList<>();

        for (MusicFiles song : MainActivity.musicFiles) {

            if (song.getTitle().toLowerCase().contains(userInput)) {
                searchedFiles.add(song);
            }

        }
        SongFragment.musicAdapter.updateList(searchedFiles);

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        SharedPreferences.Editor editor = getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE).edit();
        switch (item.getItemId()) {

            case R.id.sortByName:

                editor.putString("sorting", "sortByName");
                editor.apply();
                this.recreate();

                break;

            case R.id.sortBySize:

                editor.putString("sorting", "sortBySize");
                editor.apply();
                this.recreate();

                break;

            case R.id.sortByDate:

                editor.putString("sorting", "sortByDate");
                editor.apply();
                this.recreate();

                break;

            default:
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    private byte[] getAlbumArt(String uri) {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] albumArt = retriever.getEmbeddedPicture();
        retriever.release();

        return albumArt;

    }

}