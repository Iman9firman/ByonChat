package com.byonchat.android.provider;

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

import java.util.ArrayList;
import java.util.Date;

public class TimeLineDB extends SQLiteOpenHelper {
    public static final String TIMELINE_ID = "id";
    public static final String TIMELINE_JID = "jid";
    public static final String TIMELINE_NAME = "name";
    public static final String TIMELINE_STATUS = "status";
    public static final String TIMELINE_DATE = "date";
    public static final String TIMELINE_FLAG = "flag";

    private static TimeLineDB instance;
    private SQLiteDatabase database;

    private static final String DATABASE_NAME = "timeline.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TIMELINE_TABLE = "timeline";

    private static final String CREATE_TABLE_DETAIL = "create table "
            + TIMELINE_TABLE + " (" + TIMELINE_ID
            + " integer primary key , "
            + TIMELINE_JID + " text, "
            + TIMELINE_NAME + " text, "
            + TIMELINE_STATUS + " text, "
            + TIMELINE_DATE + " INTEGER DEFAULT 0, "
            + TIMELINE_FLAG + " text);";

    private final Context mCtx;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_DETAIL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("ALTER TABLE " + TIMELINE_TABLE + " ADD COLUMN " + TIMELINE_FLAG + " TEXT");
        }
        db.execSQL("DROP TABLE IF EXISTS " + TIMELINE_TABLE);
        onCreate(db);
    }


    private TimeLineDB(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        mCtx = ctx;
    }

    private SQLiteDatabase getDatabase() {
        if (database == null) {
            database = getWritableDatabase();
        }
        return database;
    }

    public synchronized static TimeLineDB getInstance(
            Context context) {
        if (instance == null) {
            instance = new TimeLineDB(
                    context.getApplicationContext());
        }
        return instance;
    }


    public void insert(TimeLine timeLine) {
        ContentValues cv = new ContentValues();
        cv.put(TIMELINE_JID, timeLine.getJabberId());
        cv.put(TIMELINE_NAME, timeLine.getName());
        cv.put(TIMELINE_STATUS, timeLine.getStatus());
        long tmp = 0;
        if (timeLine.getSendDate() != null) {
            tmp = timeLine.getSendDate().getTime() / 1000;
        }
        cv.put(TIMELINE_DATE, tmp);
        cv.put(TIMELINE_FLAG, timeLine.getFlag());
        getDatabase().insert(TIMELINE_TABLE, null, cv);
    }

    public boolean delete() {
        return getDatabase().delete(TIMELINE_TABLE, null, null) > 0;
    }

    public ArrayList<TimeLine> retrive() throws SQLException {
        ArrayList<TimeLine> listMemberCards = new ArrayList<TimeLine>();
        Cursor cur = getDatabase().query(true, TIMELINE_TABLE, new String[]{TIMELINE_ID, TIMELINE_JID, TIMELINE_NAME, TIMELINE_STATUS, TIMELINE_DATE, TIMELINE_FLAG}
                , null, null, null, null, TIMELINE_DATE + " DESC", String.valueOf(20));
        if (cur.moveToFirst()) {
            do {
                long id = cur.getLong(cur.getColumnIndex(TIMELINE_ID));
                String jid = cur.getString(cur.getColumnIndex(TIMELINE_JID));
                String nam = cur.getString(cur.getColumnIndex(TIMELINE_NAME));
                String status = cur.getString(cur.getColumnIndex(TIMELINE_STATUS));
                Date dateUpdate = null;
                long tmp = cur.getLong(cur.getColumnIndex(TIMELINE_DATE));
                if (tmp != 0) {
                    dateUpdate = new Date(tmp * 1000);
                }
                String fla = cur.getString(cur.getColumnIndex(TIMELINE_FLAG));

                listMemberCards.add(new TimeLine(id, jid, status, dateUpdate, nam, fla));

            } while (cur.moveToNext());
        }
        return listMemberCards;
    }

    public void updateUserFlag(String userid, String flag) {
        ContentValues cv = new ContentValues();
        cv.put(TIMELINE_FLAG, flag);
        getDatabase().update(TIMELINE_TABLE, cv, TIMELINE_JID + "= '" + userid + "'", null);
    }

    public void updateAllUserFlag() {
        ContentValues cv = new ContentValues();
        cv.put(TIMELINE_FLAG, 0);
        getDatabase().update(TIMELINE_TABLE, cv, TIMELINE_FLAG + "= 1", null);
    }

    public Cursor getDataByFlag() {
        Cursor cursor = getDatabase().query(TIMELINE_TABLE, new String[]
                {
                        TIMELINE_JID
                }, TIMELINE_FLAG + "= '1'", null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

    public Cursor getAllFlag() {
        Cursor cursor = getDatabase().query(TIMELINE_TABLE, new String[]
                {
                        TIMELINE_JID,TIMELINE_DATE, TIMELINE_FLAG
                },null, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

    public Cursor getDateAndFlagPerUser(String userid) {
        Cursor cursor = getDatabase().query(TIMELINE_TABLE, new String[]
                {
                        TIMELINE_DATE, TIMELINE_FLAG
                }, TIMELINE_JID + "= '" + userid + "'", null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

/*
    long tmp = 0;
    if (getSendDate() != null) {
        tmp = getSendDate().getTime();
    }*/
}
