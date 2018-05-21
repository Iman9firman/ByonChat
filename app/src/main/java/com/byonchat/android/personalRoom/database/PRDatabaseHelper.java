package com.byonchat.android.personalRoom.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lukma on 4/7/2016.
 */
public class PRDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "personal_room";
    private static final int DATABASE_VERSION = 1;
    public static final String TABEL_PROFILE = "profile";
    public static final String KOLOM_ID = "id";
    public static final String KOLOM_USERID = "userid";
    public static final String KOLOM_NAME = "name";
    public static final String KOLOM_STATUS = "status";
    public static final String KOLOM_HASHTAG = "hashtag";
    public static final String CREATE_TABLE_PROFILE = "CREATE TABLE "
            + TABEL_PROFILE + "(" + KOLOM_ID + " INTEGER PRIMARY KEY, "
            + KOLOM_USERID + " TEXT, " + KOLOM_NAME + " TEXT, "
            + KOLOM_STATUS + " TEXT, "
            + KOLOM_HASHTAG + " TEXT" + ")";

    private static PRDatabaseHelper instance;

    public static synchronized PRDatabaseHelper getHelper(Context context){
        if (instance == null){
            instance = new PRDatabaseHelper(context);
        }
        return instance;
    }

    private PRDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onOpen(SQLiteDatabase db){
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TABLE_PROFILE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
