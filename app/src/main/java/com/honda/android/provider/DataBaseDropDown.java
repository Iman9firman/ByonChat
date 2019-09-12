package com.honda.android.provider;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

/**
 * Created by byonc on 10/27/2016.
 */

public class DataBaseDropDown extends SQLiteOpenHelper {
    private String DATABASE_PATH = null;
    private static final int DATABASE_VERSION = 1;
    private static final String PACKAGE_NAME = "com.honda.android";
    private SQLiteDatabase db;

    public DataBaseDropDown(Context context, String DATABASE_NAME) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        DATABASE_PATH = getDatabaseFolder()+DATABASE_NAME+".sqlite";
        db = getWritableDatabase();
    }

    public static String getDatabaseFolder() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/"+PACKAGE_NAME+"/databases/";
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        try {
            if (db != null) {
                if (db.isOpen()) {
                    return db;
                }
            }
            return SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public synchronized void close() {
        if (db != null) {
            db.close();
            db = null;
        }
        super.close();
    }

    public Cursor selectAll() {
        return db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' limit 1", null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) { }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }


}
