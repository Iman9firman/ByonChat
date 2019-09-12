package com.honda.android.provider;

/**
 * Created by Iman Firmansyah on 1/9/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.honda.android.utils.Utility;

public class FilesDatabaseHelper {
    public static final String F_MESSAGE_ID = "message_id";
    public static final String F_PROGRESS = "progress";
    public static final String F_STATUS = "status";
    public static final String F_IMAGE = "image";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "FileDB.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE = "Files";

    private static final String CREATE_STICKER_TABLE = "create table "
            + TABLE + " (" + F_MESSAGE_ID
            + " integer , " + F_PROGRESS + " text ,"
            + F_STATUS+ " text , " + F_IMAGE+ " blob );";

    private final Context mCtx;



    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_STICKER_TABLE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +  TABLE);
            onCreate(db);
        }
    }

    public void Reset() {
        mDbHelper.onUpgrade(this.mDb, 1, 1);
    }

    public FilesDatabaseHelper(Context ctx) {
        mCtx = ctx;
        mDbHelper = new DatabaseHelper(mCtx);
    }

    public FilesDatabaseHelper open() throws SQLException {
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public void insertFiles(Files files) {
        ContentValues cv = new ContentValues();
        cv.put(F_MESSAGE_ID, files.getMessage_id());
        cv.put(F_PROGRESS,files.getProgress());
        cv.put(F_STATUS, files.getStatus());
        cv.put(F_IMAGE, Utility.getBytes(files.getImage()));
        mDb.insert(TABLE, null, cv);

    }
    public void insertFilesUpload(Files files) {
        ContentValues cv = new ContentValues();
        cv.put(F_MESSAGE_ID, files.getMessage_id());
        cv.put(F_PROGRESS,files.getProgress());
        cv.put(F_STATUS, files.getStatus());
        mDb.insert(TABLE, null, cv);

    }

    public void updateFiles(Files files) {
        ContentValues cv = new ContentValues();
        cv.put(F_PROGRESS,files.getProgress());
        cv.put(F_STATUS, files.getStatus());
        mDb.update(TABLE, cv, F_MESSAGE_ID + "= " + String.valueOf(files.getMessage_id()),null);

    }

    public boolean deleteFile(String id)
    {
        return mDb.delete(TABLE, F_MESSAGE_ID + "=" + id, null) > 0;
    }

    public Files retriveFiles(long id) throws SQLException {
        Cursor cur = mDb.query(true, TABLE, new String[] { F_MESSAGE_ID,F_PROGRESS,F_STATUS,F_IMAGE
                 },  F_MESSAGE_ID + "= " +id, null, null, null, null, null);
        if (cur.moveToFirst()) {

            String progress = cur.getString(cur.getColumnIndex(F_PROGRESS));
            String status = cur.getString(cur.getColumnIndex(F_STATUS));
            byte[] blob = cur.getBlob(cur.getColumnIndex(F_IMAGE));

            cur.close();
            return new Files(progress,status,blob!=null?Utility.getPhoto(blob):null);
        }
        cur.close();
        return null;
    }


}
