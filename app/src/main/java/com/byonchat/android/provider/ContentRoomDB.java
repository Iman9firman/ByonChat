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

import java.util.ArrayList;

public class ContentRoomDB {
    private String time;
    private String status;
    private String content;
    private String title;
    private String metode;
    private String attach;
    public static final String CONTENTROOM_ID = "id";
    public static final String CONTENTROOM_TIME = "time";
    public static final String CONTENTROOM_STATUS = "status";
    public static final String CONTENTROOM_CONTENT = "content";
    public static final String CONTENTROOM_TITTLE = "tittle";
    public static final String CONTENTROOM_METODE = "metode";
    public static final String CONTENTROOM_ATTACH = "attach";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "Content.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CONTENTROOM_TABLE = "content";

    private static final String CREATE_TABLE = "create table "
            + CONTENTROOM_TABLE + " (" + CONTENTROOM_ID
            + " integer primary key , "
            +CONTENTROOM_TIME+ " text, "
            +CONTENTROOM_CONTENT+ " text, "
            +CONTENTROOM_TITTLE+ " text, "
            +CONTENTROOM_METODE+ " text, "
            +CONTENTROOM_ATTACH+ " text, "
            +CONTENTROOM_STATUS+ " text );";

    private final Context mCtx;



    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + CONTENTROOM_TABLE);
            onCreate(db);
        }
    }

    public void Reset() {
        mDbHelper.onUpgrade(this.mDb, 1, 1);
    }

    public ContentRoomDB(Context ctx) {
        mCtx = ctx;
        mDbHelper = new DatabaseHelper(mCtx);
    }

    public ContentRoomDB open() throws SQLException {
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public void insertContent(ContentRoom contentRoom) {
        ContentValues cv = new ContentValues();
        cv.put(CONTENTROOM_ID, contentRoom.getId());
        cv.put(CONTENTROOM_TIME, contentRoom.getTime());
        cv.put(CONTENTROOM_TITTLE, contentRoom.getTitle());
        cv.put(CONTENTROOM_ATTACH, contentRoom.getAttach());
        cv.put(CONTENTROOM_CONTENT, contentRoom.getContent());
        cv.put(CONTENTROOM_METODE, contentRoom.getMetode());
        cv.put(CONTENTROOM_STATUS, contentRoom.getStatus());
        mDb.insert(CONTENTROOM_TABLE, null, cv);
    }

    public void updateDetail(ContentRoom contentRoom) {
        ContentValues cv = new ContentValues();
        cv.put(CONTENTROOM_TIME, contentRoom.getTime());
        cv.put(CONTENTROOM_ATTACH, contentRoom.getAttach());
        cv.put(CONTENTROOM_CONTENT, contentRoom.getContent());
        cv.put(CONTENTROOM_METODE, contentRoom.getMetode());
        cv.put(CONTENTROOM_STATUS, contentRoom.getStatus());
        mDb.update(CONTENTROOM_TABLE, cv, CONTENTROOM_ID + "= " + String.valueOf(contentRoom.getId()), null);
    }

    public void updateStatus(ContentRoom contentRoom) {
        ContentValues cv = new ContentValues();
        cv.put(CONTENTROOM_STATUS, contentRoom.getStatus());
        mDb.update(CONTENTROOM_TABLE, cv, CONTENTROOM_ID + "= " + String.valueOf(contentRoom.getId()),null);
    }

        public boolean deletebyId(String id)
    {
        return mDb.delete(CONTENTROOM_TABLE, CONTENTROOM_ID + "= " + id+"", null) > 0;
    }

    public boolean deletebyIn(ArrayList<String> arrayList)
    {
        String delete = "" ;
        for (String aa : arrayList){
            delete+=aa+",";
        }
        return mDb.delete(CONTENTROOM_TABLE, CONTENTROOM_ID + " not in (" +delete.substring(0,delete.length()-1)+")", null) > 0;
    }

    public boolean delete()
    {
        return mDb.delete(CONTENTROOM_TABLE, null, null) > 0;
    }

    public Cursor getContentRoom(String id)
    {
        Cursor cursor = mDb.query(CONTENTROOM_TABLE, new String[]
                {CONTENTROOM_ID, CONTENTROOM_STATUS,CONTENTROOM_ATTACH,CONTENTROOM_METODE,CONTENTROOM_TIME,CONTENTROOM_TITTLE,CONTENTROOM_CONTENT

                }, CONTENTROOM_ID + "= " +id+"", null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }


    public ArrayList<ContentRoom> retriveallContent() throws SQLException {
        ArrayList<ContentRoom> contentRoomsList = new ArrayList<ContentRoom>();
        Cursor cur = mDb.query(true, CONTENTROOM_TABLE, new String[] {CONTENTROOM_ID, CONTENTROOM_STATUS,CONTENTROOM_ATTACH,CONTENTROOM_METODE,CONTENTROOM_TIME,CONTENTROOM_TITTLE,CONTENTROOM_CONTENT
        },null
                , null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {

                Long a = cur.getLong(cur.getColumnIndex(CONTENTROOM_ID));
                String b = cur.getString(cur.getColumnIndex(CONTENTROOM_STATUS));
                String c = cur.getString(cur.getColumnIndex(CONTENTROOM_ATTACH));
                String e = cur.getString(cur.getColumnIndex(CONTENTROOM_METODE));
                String f = cur.getString(cur.getColumnIndex(CONTENTROOM_TIME));
                String g = cur.getString(cur.getColumnIndex(CONTENTROOM_TITTLE));
                String h = cur.getString(cur.getColumnIndex(CONTENTROOM_CONTENT));
                contentRoomsList.add(new ContentRoom(a,f,b,h,g,e,c));

            } while (cur.moveToNext());
        }
        return contentRoomsList;
    }


}
