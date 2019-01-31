package com.byonchat.android.location.SpyLocation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SpyLocDB  extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Loc.db";
    public static final String TABLE_LOC = "location";

    public static final String LOC_ID = "id_loc";
    public static final String LOC_LAT = "lat_loc";
    public static final String LOC_LNG = "lng_loc";
    public static final String LOC_DATE = "date_loc";

    private String CREATE_EVENT_TABLE = "CREATE TABLE "+ TABLE_LOC + " (" +
            LOC_ID + " INTEGER PRIMARY KEY, " +
            LOC_LAT + " TEXT, " +
            LOC_LNG + " TEXT, " +
            LOC_DATE + " TEXT" +
            ")";

    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_LOC;

    public SpyLocDB(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_EVENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_USER_TABLE);
        onCreate(db);
    }
}
