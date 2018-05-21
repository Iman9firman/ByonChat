package com.byonchat.android.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class HistoryRewardDB {
    public static final String HISTORY_ID = "id";
    public static final String HISTORY_DATE = "date";
    public static final String HISTORY_DESC = "desc";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "HistoryReward.db";
    private static final int DATABASE_VERSION = 1;

    private static final String HISTORY_TABLE = "HistoryReward";

    private static final String CREATE_HISTORY_TABLE = "create table "
            + HISTORY_TABLE + " (" + HISTORY_ID
            + " integer primary key autoincrement, "
            + HISTORY_DATE+ " text not null, "
            + HISTORY_DESC+ " text not null );";

    private final Context mCtx;



    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_HISTORY_TABLE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + HISTORY_TABLE);
            onCreate(db);
        }
    }

    public void Reset() {
        mDbHelper.onUpgrade(this.mDb, 1, 1);
    }

    public HistoryRewardDB(Context ctx) {
        mCtx = ctx;
        mDbHelper = new DatabaseHelper(mCtx);
    }

    public HistoryRewardDB open() throws SQLException {
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public void insertHistoryDetails(History history) {
        ContentValues cv = new ContentValues();
        cv.put(HISTORY_DATE,history.getDate());
        cv.put(HISTORY_DESC,history.getDesc());
        mDb.insert(HISTORY_TABLE, null, cv);
    }

    public boolean delete()
    {
        return mDb.delete(HISTORY_TABLE,null, null) > 0;
    }

    public ArrayList<History> getHistoryList() throws SQLException {
        ArrayList<History> list = new ArrayList<History>();
        Cursor cur = mDb.query(true, HISTORY_TABLE, new String[] {HISTORY_DATE,HISTORY_DESC },null , null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                String date = cur.getString(cur.getColumnIndex(HISTORY_DATE));
                String desc = cur.getString(cur.getColumnIndex(HISTORY_DESC));
                list.add(new History(date,desc));
            } while (cur.moveToNext());
        }
        return list;
    }
}
