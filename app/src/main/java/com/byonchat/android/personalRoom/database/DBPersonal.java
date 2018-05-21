package com.byonchat.android.personalRoom.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Lukmanpryg on 12/1/2016.
 */

public class DBPersonal {

    public static final String PERSONAL_ID = "id";
    public static final String PERSONAL_USERID = "userid";
    public static final String PERSONAL_NAME = "name";
    public static final String PERSONAL_STATUS = "status";
    public static final String PERSONAL_COVER = "cover";
    public static final String PERSONAL_HASHTAG= "hashtag";
    public static final String PERSONAL_HASHTAG1 = "hashtag1";
    public static final String PERSONAL_HASHTAG2 = "hashtag2";
    public static final String PERSONAL_HASHTAG3 = "hashtag3";
    public static final String PERSONAL_HASHTAG4 = "hashtag4";
    public static final String PERSONAL_HASHTAG5 = "hashtag5";
    public static final String PERSONAL_HASHTAG6 = "hashtag6";
    public static final String PERSONAL_HASHTAG7 = "hashtag7";
    public static final String PERSONAL_HASHTAG8 = "hashtag8";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "PERSONALS.db";
    private static final int DATABASE_VERSION = 1;
    private static final String PROFILE_TABLE = "profile";
    private static final String PROFILE_HASHTAG = "profile_hashtag";

    private static final String CREATE_TABLE_PROFILE = " create table "
            + PROFILE_TABLE + " (" + PERSONAL_ID
            + " integer primary key autoincrement not null, "
            + PERSONAL_USERID + " text, "
            + PERSONAL_NAME + " text, "
            + PERSONAL_STATUS + " text, "
            + PERSONAL_COVER + " text, "
            + PERSONAL_HASHTAG + " text)";

    private static final String CREATE_TABLE_HASHTAG  = " create table "
            + PROFILE_HASHTAG + " (" + PERSONAL_ID
            + " integer primary key autoincrement not null, "
            + PERSONAL_USERID + " text, "
            + PERSONAL_HASHTAG1 + " text, "
            + PERSONAL_HASHTAG2 + " text, "
            + PERSONAL_HASHTAG3 + " text, "
            + PERSONAL_HASHTAG4 + " text, "
            + PERSONAL_HASHTAG5 + " text, "
            + PERSONAL_HASHTAG6 + " text, "
            + PERSONAL_HASHTAG7 + " text, "
            + PERSONAL_HASHTAG8 + " text)";

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db){
            db.execSQL(CREATE_TABLE_PROFILE);
            db.execSQL(CREATE_TABLE_HASHTAG);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL("DROP TABLE IF EXISTS " + PROFILE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + PROFILE_HASHTAG);

            onCreate(db);
        }
    }

    public void Reset() {
        mDbHelper.onUpgrade(this.mDb, 1, 1);
    }

    public DBPersonal(Context ctx){
        mCtx = ctx;
        mDbHelper = new DatabaseHelper(mCtx);
    }

    public DBPersonal open() throws SQLException {
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public void insertProfile(Profile profile){
        ContentValues cv = new ContentValues();
        cv.put(PERSONAL_USERID, profile.getUserid());
        cv.put(PERSONAL_NAME, profile.getName());
        cv.put(PERSONAL_STATUS, profile.getStatus());
        cv.put(PERSONAL_COVER, profile.getCover());
        cv.put(PERSONAL_HASHTAG, profile.getHashtag());
        mDb.insert(PROFILE_TABLE, null, cv);
    }

    public void insertHashtag(Profile profile){
        ContentValues cv = new ContentValues();
        cv.put(PERSONAL_USERID, profile.getUserid());
        cv.put(PERSONAL_HASHTAG1, profile.getHashtag1());
        cv.put(PERSONAL_HASHTAG2, profile.getHashtag2());
        cv.put(PERSONAL_HASHTAG3, profile.getHashtag3());
        cv.put(PERSONAL_HASHTAG4, profile.getHashtag4());
        cv.put(PERSONAL_HASHTAG5, profile.getHashtag5());
        cv.put(PERSONAL_HASHTAG6, profile.getHashtag6());
        cv.put(PERSONAL_HASHTAG7, profile.getHashtag7());
        cv.put(PERSONAL_HASHTAG8, profile.getHashtag8());
        mDb.insert(PROFILE_HASHTAG, null, cv);
    }

    public void updateStatus(String userid, String status){
        ContentValues cv = new ContentValues();
        cv.put(PERSONAL_STATUS, status);
        mDb.update(PROFILE_TABLE, cv, PERSONAL_USERID + "= '"+userid+"'", null);
    }

    public void updateCover(String userid, String cover){
        ContentValues cv = new ContentValues();
        cv.put(PERSONAL_COVER, cover);
        mDb.update(PROFILE_TABLE, cv, PERSONAL_USERID + "= '"+userid+"'", null);
    }

    public void updateHashtag1(String userid, String hashtag1){
        Log.w("personal", "hashtag1 masuk");
        ContentValues cv = new ContentValues();
        cv.put(PERSONAL_HASHTAG1, hashtag1);
        mDb.update(PROFILE_HASHTAG, cv, PERSONAL_USERID + "= '"+userid+"'", null);
    }

    public void updateHashtag2(String userid, String hashtag2){
        ContentValues cv = new ContentValues();
        cv.put(PERSONAL_HASHTAG2, hashtag2);
        mDb.update(PROFILE_HASHTAG, cv, PERSONAL_USERID + "= '"+userid+"'", null);
    }

    public void updateHashtag3(String userid, String hashtag3){
        ContentValues cv = new ContentValues();
        cv.put(PERSONAL_HASHTAG3, hashtag3);
        mDb.update(PROFILE_HASHTAG, cv, PERSONAL_USERID + "= '"+userid+"'", null);
    }

    public void updateHashtag4(String userid, String hashtag4){
        ContentValues cv = new ContentValues();
        cv.put(PERSONAL_HASHTAG4, hashtag4);
        mDb.update(PROFILE_HASHTAG, cv, PERSONAL_USERID + "= '"+userid+"'", null);
    }

    public void updateHashtag5(String userid, String hashtag5){
        ContentValues cv = new ContentValues();
        cv.put(PERSONAL_HASHTAG5, hashtag5);
        mDb.update(PROFILE_HASHTAG, cv, PERSONAL_USERID + "= '"+userid+"'", null);
    }

    public void updateHashtag6(String userid, String hashtag6){
        ContentValues cv = new ContentValues();
        cv.put(PERSONAL_HASHTAG6, hashtag6);
        mDb.update(PROFILE_HASHTAG, cv, PERSONAL_USERID + "= '"+userid+"'", null);
    }

    public void updateHashtag7(String userid, String hashtag7){
        ContentValues cv = new ContentValues();
        cv.put(PERSONAL_HASHTAG7, hashtag7);
        mDb.update(PROFILE_HASHTAG, cv, PERSONAL_USERID + "= '"+userid+"'", null);
    }

    public void updateHashtag8(String userid, String hashtag8){
        ContentValues cv = new ContentValues();
        cv.put(PERSONAL_HASHTAG8, hashtag8);
        mDb.update(PROFILE_HASHTAG, cv, PERSONAL_USERID + "= '"+userid+"'", null);
    }

    public ArrayList<Profile> retrieveProfile(String userid) throws SQLException {
        ArrayList<Profile> listProfile = new ArrayList<Profile>();
        Cursor cur = mDb.query(true, PROFILE_TABLE, new String[]{
                        PERSONAL_ID, PERSONAL_USERID, PERSONAL_NAME, PERSONAL_STATUS, PERSONAL_COVER, PERSONAL_HASHTAG}
                , PERSONAL_USERID + "= '"+userid+"'", null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                String uid = cur.getString(cur.getColumnIndex(PERSONAL_USERID));
                String nam = cur.getString(cur.getColumnIndex(PERSONAL_NAME));
                String stat = cur.getString(cur.getColumnIndex(PERSONAL_STATUS));
                String cov = cur.getString(cur.getColumnIndex(PERSONAL_COVER));
                String hash = cur.getString(cur.getColumnIndex(PERSONAL_HASHTAG));
                listProfile.add(new Profile(uid, nam, stat, cov, hash));

            } while (cur.moveToNext());
        }
        return listProfile;
    }

    public ArrayList<Profile> retrieveHashtag(String userid) throws SQLException {
        ArrayList<Profile> listProfile = new ArrayList<Profile>();
        Cursor cur = mDb.query(true, PROFILE_HASHTAG, new String[]{
                        PERSONAL_ID, PERSONAL_USERID, PERSONAL_HASHTAG1, PERSONAL_HASHTAG2, PERSONAL_HASHTAG3, PERSONAL_HASHTAG4, PERSONAL_HASHTAG5, PERSONAL_HASHTAG6, PERSONAL_HASHTAG7, PERSONAL_HASHTAG8}
                , PERSONAL_USERID + "= '"+userid+"'", null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                String uid = cur.getString(cur.getColumnIndex(PERSONAL_USERID));
                String hash1 = cur.getString(cur.getColumnIndex(PERSONAL_HASHTAG1));
                String hash2 = cur.getString(cur.getColumnIndex(PERSONAL_HASHTAG2));
                String hash3 = cur.getString(cur.getColumnIndex(PERSONAL_HASHTAG3));
                String hash4 = cur.getString(cur.getColumnIndex(PERSONAL_HASHTAG4));
                String hash5 = cur.getString(cur.getColumnIndex(PERSONAL_HASHTAG5));
                String hash6 = cur.getString(cur.getColumnIndex(PERSONAL_HASHTAG6));
                String hash7 = cur.getString(cur.getColumnIndex(PERSONAL_HASHTAG7));
                String hash8 = cur.getString(cur.getColumnIndex(PERSONAL_HASHTAG8));
                listProfile.add(new Profile(uid, hash1, hash2, hash3, hash4, hash5, hash6, hash7, hash8));

            } while (cur.moveToNext());
        }
        return listProfile;
    }

    public boolean deletePersonal(String userid)
    {
        return mDb.delete(PROFILE_TABLE, PERSONAL_USERID + "= '" +userid+"'", null) > 0;
    }

    public Cursor getCoverPersonal(String userid)
    {
        Cursor cursor = mDb.query(PROFILE_TABLE, new String[]
                {
                        PERSONAL_COVER
                }, PERSONAL_USERID + "= '" +userid+"'", null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

    public Cursor getStatusPersonal(String userid)
    {
        Cursor cursor = mDb.query(PROFILE_TABLE, new String[]
                {
                        PERSONAL_STATUS
                }, PERSONAL_USERID + "= '" +userid+"'", null, null, null,null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

    public Cursor getHashtagPersonal(String userid)
    {
        Cursor cursor = mDb.query(PROFILE_HASHTAG, new String[]
                {
                        PERSONAL_USERID, PERSONAL_HASHTAG1, PERSONAL_HASHTAG2, PERSONAL_HASHTAG3, PERSONAL_HASHTAG4,
                        PERSONAL_HASHTAG5, PERSONAL_HASHTAG6, PERSONAL_HASHTAG7, PERSONAL_HASHTAG8
                }, PERSONAL_USERID + "= '" +userid+"'", null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }
}
