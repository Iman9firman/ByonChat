package com.honda.android.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Lukmanpryg on 10/3/2016.
 */
public class FilesURLDatabaseHelper {
    public static final String F_MESSAGE_ID = "message_id";
    public static final String F_PROGRESS = "progress";
    public static final String F_STATUS = "status";
    public static final String F_IMAGEURL = "imageurl";
    public static final String F_CACHEURL = "cacheurl";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "FileURLDB.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE = "Files";

    private static final String CREATE_STICKER_TABLE = "create table "
            + TABLE + " (" + F_MESSAGE_ID
            + " integer , " + F_PROGRESS + " text ,"
            + F_STATUS+ " text , " + F_IMAGEURL+ " text ,"
            + F_CACHEURL+ " text );";

    private final Context mCtx;


    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_STICKER_TABLE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (newVersion > oldVersion){
                db.execSQL("ALTER TABLE "+TABLE+" ADD COLUMN "+F_CACHEURL+" TEXT");
            }
            db.execSQL("DROP TABLE IF EXISTS " +  TABLE);
            onCreate(db);
        }
    }

    public void Reset() {
        mDbHelper.onUpgrade(this.mDb, 1, 2);
    }

    public FilesURLDatabaseHelper(Context ctx) {
        mCtx = ctx;
        mDbHelper = new DatabaseHelper(mCtx);
    }

    public FilesURLDatabaseHelper open() throws SQLException {
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public void insertFiles(FilesURL files) {
        ContentValues cv = new ContentValues();
        cv.put(F_MESSAGE_ID, files.getMessage_id());
        cv.put(F_PROGRESS,files.getProgress());
        cv.put(F_STATUS, files.getStatus());
        cv.put(F_IMAGEURL, files.getImage());
        cv.put(F_CACHEURL, files.getCache());
        mDb.insert(TABLE, null, cv);

    }
    public void insertFilesUpload(FilesURL files) {
        ContentValues cv = new ContentValues();
        cv.put(F_MESSAGE_ID, files.getMessage_id());
        cv.put(F_PROGRESS,files.getProgress());
        cv.put(F_STATUS, files.getStatus());
        mDb.insert(TABLE, null, cv);

    }

    public void updateFiles(FilesURL files) {
        ContentValues cv = new ContentValues();
        cv.put(F_PROGRESS,files.getProgress());
        cv.put(F_STATUS, files.getStatus());
        mDb.update(TABLE, cv, F_MESSAGE_ID + "= " + String.valueOf(files.getMessage_id()),null);

    }

    public boolean deleteFile(String id)
    {
        return mDb.delete(TABLE, F_MESSAGE_ID + "=" + id, null) > 0;
    }

    public FilesURL retriveFiles(long id) throws SQLException {
        Cursor cur = mDb.query(true, TABLE, new String[] { F_MESSAGE_ID,F_PROGRESS,F_STATUS,F_IMAGEURL,F_CACHEURL
        },  F_MESSAGE_ID + "= " +id, null, null, null, null, null);
        if (cur.moveToFirst()) {

            String progress = cur.getString(cur.getColumnIndex(F_PROGRESS));
            String status = cur.getString(cur.getColumnIndex(F_STATUS));
            String blob = cur.getString(cur.getColumnIndex(F_IMAGEURL));
            String cache = cur.getString(cur.getColumnIndex(F_CACHEURL));

            cur.close();
            return new FilesURL(progress,status,blob!=null?blob:null,cache);
        }
        cur.close();
        return null;
    }
}
