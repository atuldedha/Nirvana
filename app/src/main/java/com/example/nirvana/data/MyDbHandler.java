package com.example.nirvana.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.nirvana.model.MusicFiles;
import com.example.nirvana.params.Parameter;

import java.util.ArrayList;
import java.util.List;

public class MyDbHandler extends SQLiteOpenHelper {

    public MyDbHandler(Context context){
        super(context, Parameter.DB_NAME,null,Parameter.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createAllTables = "CREATE TABLE TABLECONTAINER" + " ( " + "ID" + " INTEGER PRIMARY KEY, "
                + " TABLENAMES " + " TEXT " + ")";

        Log.d("myDb","Query being run = " + createAllTables);
        db.execSQL(createAllTables);

//        String tableName = Parameter.TABLE_NAME[Parameter.TABLE_NAME.length - 1];
//
//        String create = "CREATE TABLE " + tableName + " ( " + Parameter.KEY_ID + " INTEGER PRIMARY KEY, "
//                + Parameter.KEY_SONG_ID + " TEXT, " + Parameter.KEY_SONG_PATH + " TEXT, "
//                + Parameter.KEY_SONG_TITLE + " TEXT, " + Parameter.KEY_SONG_ARTIST + " TEXT, "
//                + Parameter.KEY_SONG_ALBUM + " TEXT, " + Parameter.KEY_SONG_DURATION + " TEXT " + " )";
//        Log.d("myDb","Query being run = " + create);
//        db.execSQL(create);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addTables(String tableName){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("TABLENAMES", tableName);

        sqLiteDatabase.insert("TABLECONTAINER", null, contentValues);

        String create = "CREATE TABLE " + tableName + " ( " + Parameter.KEY_ID + " INTEGER PRIMARY KEY, "
                + Parameter.KEY_SONG_ID + " TEXT, " + Parameter.KEY_SONG_PATH + " TEXT, "
                + Parameter.KEY_SONG_TITLE + " TEXT, " + Parameter.KEY_SONG_ARTIST + " TEXT, "
                + Parameter.KEY_SONG_ALBUM + " TEXT, " + Parameter.KEY_SONG_DURATION + " TEXT " + " )";

        sqLiteDatabase.execSQL(create);
        Log.d("myDb","Query being run = " + create);

    }

    public List<String> getTebles(){

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        List<String> tableNames = new ArrayList<>();

        String select = "SELECT * FROM TABLECONTAINER";
        Cursor cursor = sqLiteDatabase.rawQuery(select,null);

        if(cursor.moveToFirst()) {
            do {

                String name = "";
                name = (cursor.getString(1));

                tableNames.add(name);
            } while (cursor.moveToNext());

        }

        return tableNames;
    }

    public void addSong(MusicFiles song, String tableName){
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();

        contentValues.put(Parameter.KEY_SONG_ID, song.getID());
        contentValues.put(Parameter.KEY_SONG_PATH, song.getPath());
        contentValues.put(Parameter.KEY_SONG_TITLE, song.getTitle());
        contentValues.put(Parameter.KEY_SONG_ARTIST, song.getArtist());
        contentValues.put(Parameter.KEY_SONG_ALBUM, song.getAlbum());
        contentValues.put(Parameter.KEY_SONG_DURATION, song.getDuration());

        sqLiteDatabase.insert(tableName,null,contentValues);
        Log.d("myDb","Successfully Inserted");
        sqLiteDatabase.close();

    }

    public List<MusicFiles> showPlaylist(String tableName){
        List<MusicFiles> playlist = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        String select = "SELECT * FROM " + tableName;
        Cursor cursor = sqLiteDatabase.rawQuery(select,null);

        if(cursor.moveToFirst()){
            do{
                MusicFiles song = new MusicFiles();
                song.setID(cursor.getString(1));
                song.setPath(cursor.getString(2));
                song.setTitle((cursor.getString(3)));
                song.setArtist(cursor.getString(4));
                song.setAlbum(cursor.getString(5));
                song.setDuration(cursor.getString(6));

                playlist.add(song);
            }while(cursor.moveToNext());
        }
       return playlist;
    }

//    public int updateStudent(StudentAttendence student){
//        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
//        ContentValues contentValues=new ContentValues();
//
//        contentValues.put(Parameter.KEY_NAME,student.getStudentName());
//        contentValues.put(Parameter.KEY_ROLLNO,student.getRollNo());
//
//        return sqLiteDatabase.update(Parameter.TABLE_NAME,contentValues,Parameter.KEY_ID + "=?",
//                new String[]{String.valueOf(student.getId())});
//
//    }

    public void deleteSong(String id, int tablePosition){
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        sqLiteDatabase.delete(Parameter.TABLE_NAME[tablePosition],Parameter.KEY_SONG_ID + "=?",
                new String[]{String.valueOf(id)});
        sqLiteDatabase.close();
    }

    public void deleteAllSongs(int tablePosition){
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        sqLiteDatabase.delete(Parameter.TABLE_NAME[tablePosition],null,null);
        sqLiteDatabase.close();
    }

   /* public int getCount(){
        String select = "SELECT * FROM " + Parameter.TABLE_NAME;
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase()
    }*/
}
