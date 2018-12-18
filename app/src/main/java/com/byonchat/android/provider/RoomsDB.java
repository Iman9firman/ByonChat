package com.byonchat.android.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.byonchat.android.list.ItemListTrending;

import java.util.ArrayList;

/**
 * Created by Lukmanpryg on 6/29/2016.
 */

public class RoomsDB {

    public static final String ROOMS_ID = "id";
    public static final String ROOMS_NAME = "name";
    public static final String ROOMS_DESC = "description";
    public static final String ROOMS_REALNAME = "realname";
    public static final String ROOMS_LINKICON = "link";
    public static final String ROOMS_TYPE = "type";
    public static final String TRENDING_ID = "id";
    public static final String TRENDING_NAME = "trending_name";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "ROOMS.db";
    private static final int DATABASE_VERSION = 3;//6

    private static final String ROOMS_TABLE = "rooms";
    private static final String TRENDING_TABLE = "trendings";

    private static final String CREATE_TABLE_ROOMS = "create table "
            + ROOMS_TABLE + " (" + ROOMS_ID
            + " integer primary key , "
            + ROOMS_NAME + " text, "
            + ROOMS_DESC + " text, "
            + ROOMS_REALNAME + " text, "
            + ROOMS_LINKICON + " text, "
            + ROOMS_TYPE + " text)";


    private static final String CREATE_TABLE_TRENDING = "create table "
            + TRENDING_TABLE + " (" + TRENDING_ID
            + " integer primary key , "
            + TRENDING_NAME + " text, "
            + ROOMS_TYPE + " text)";

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_ROOMS);
            db.execSQL(CREATE_TABLE_TRENDING);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (newVersion > oldVersion) {
                db.execSQL(CREATE_TABLE_TRENDING);
//                db.execSQL("ALTER TABLE vouchers ADD COLUMN icon text");
            }

            db.execSQL("DROP TABLE IF EXISTS " + ROOMS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + TRENDING_TABLE);
            onCreate(db);
        }
    }

    public void Reset() {
        mDbHelper.onUpgrade(this.mDb, 1, 1);
    }

    public RoomsDB(Context ctx) {
        mCtx = ctx;
        mDbHelper = new DatabaseHelper(mCtx);
    }

    public RoomsDB open() throws SQLException {
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public void insertRooms(ContactBot contactBot) {
        ContentValues cv = new ContentValues();
        cv.put(ROOMS_NAME, contactBot.getName());
        cv.put(ROOMS_DESC, contactBot.getDesc());
        cv.put(ROOMS_REALNAME, contactBot.getRealname());
        cv.put(ROOMS_LINKICON, contactBot.getLink());
        cv.put(ROOMS_TYPE, contactBot.getType());
        mDb.insert(ROOMS_TABLE, null, cv);
    }


    public void insertTrending(ItemListTrending itemListTrending) {
        ContentValues cv = new ContentValues();
        cv.put(TRENDING_NAME, itemListTrending.getName());
        cv.put(ROOMS_TYPE, itemListTrending.getType());
        mDb.insert(TRENDING_TABLE, null, cv);
    }

    public boolean deleteTrending(String type) {
        return mDb.delete(TRENDING_TABLE, ROOMS_TYPE + "= '" + type + "'", null) > 0;
    }

    public boolean deleteRooms() {
        return mDb.delete(ROOMS_TABLE, null, null) > 0;
    }

    public boolean deletebyId(String id) {
        return mDb.delete(ROOMS_TABLE, ROOMS_ID + "= " + id + "", null) > 0;
    }

    public boolean deletebyName(String judul) {
        return mDb.delete(ROOMS_TABLE, ROOMS_NAME + "= '" + judul + "'", null) > 0;
    }

    public boolean deleteRoomsSelected() {
        return mDb.delete(ROOMS_TABLE, ROOMS_TYPE + "= '2'", null) > 0;
    }

    public boolean deleteRoomsTrending() {
        return mDb.delete(ROOMS_TABLE, ROOMS_TYPE + "= '1'", null) > 0;
    }

    public ArrayList<ItemListTrending> retrieveTrending(String type) throws SQLException {
        ArrayList<ItemListTrending> listMemberCards = new ArrayList<ItemListTrending>();
        Cursor cur = mDb.query(true, TRENDING_TABLE, new String[]{
                        TRENDING_ID, TRENDING_NAME, ROOMS_TYPE}
                , ROOMS_TYPE + "= '" + type + "'", null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                String id = cur.getString(cur.getColumnIndex(TRENDING_ID));
                String nam = cur.getString(cur.getColumnIndex(TRENDING_NAME));
                String typ = cur.getString(cur.getColumnIndex(ROOMS_TYPE));
                listMemberCards.add(new ItemListTrending(id, "#" + nam, typ));
            } while (cur.moveToNext());
        }
        return listMemberCards;
    }

    public ArrayList<ContactBot> retrieveRooms(String type) throws SQLException {
        ArrayList<ContactBot> listMemberCards = new ArrayList<ContactBot>();
        Cursor cur = mDb.query(true, ROOMS_TABLE, new String[]{
                        ROOMS_ID, ROOMS_NAME, ROOMS_DESC, ROOMS_REALNAME, ROOMS_LINKICON, ROOMS_TYPE}
                , ROOMS_TYPE + "= '" + type + "'", null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                String id = cur.getString(cur.getColumnIndex(ROOMS_ID));
                String nam = cur.getString(cur.getColumnIndex(ROOMS_NAME));
                String des = cur.getString(cur.getColumnIndex(ROOMS_DESC));
                String real = cur.getString(cur.getColumnIndex(ROOMS_REALNAME));
                String ic = cur.getString(cur.getColumnIndex(ROOMS_LINKICON));
                String typ = cur.getString(cur.getColumnIndex(ROOMS_TYPE));
                listMemberCards.add(new ContactBot(id, nam, des, real, ic, typ));
            } while (cur.moveToNext());
        }
        return listMemberCards;
    }

    public ArrayList<ContactBot> retrieveRoomsByName(String name, String tipe) throws SQLException {
        ArrayList<ContactBot> listMemberCards = new ArrayList<ContactBot>();
        Cursor cur = mDb.query(true, ROOMS_TABLE, new String[]{
                        ROOMS_ID, ROOMS_NAME, ROOMS_DESC, ROOMS_REALNAME, ROOMS_LINKICON, ROOMS_TYPE}
                , ROOMS_NAME + "= '" + name + "' AND " + ROOMS_TYPE + "= '" + tipe + "'", null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                String id = cur.getString(cur.getColumnIndex(ROOMS_ID));
                String nam = cur.getString(cur.getColumnIndex(ROOMS_NAME));
                String des = cur.getString(cur.getColumnIndex(ROOMS_DESC));
                String real = cur.getString(cur.getColumnIndex(ROOMS_REALNAME));
                String ic = cur.getString(cur.getColumnIndex(ROOMS_LINKICON));
                String typ = cur.getString(cur.getColumnIndex(ROOMS_TYPE));
                listMemberCards.add(new ContactBot(id, nam, des, real, ic, typ));

            } while (cur.moveToNext());
        }
        return listMemberCards;
    }

    public ArrayList<ContactBot> retrieveRoomsByRealName(String name, String tipe) throws SQLException {
        ArrayList<ContactBot> listMemberCards = new ArrayList<ContactBot>();
        Cursor cur = mDb.query(true, ROOMS_TABLE, new String[]{
                        ROOMS_ID, ROOMS_NAME, ROOMS_DESC, ROOMS_REALNAME, ROOMS_LINKICON, ROOMS_TYPE}
                , ROOMS_REALNAME + "= '" + name + "' AND " + ROOMS_TYPE + "= '" + tipe + "'", null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                String id = cur.getString(cur.getColumnIndex(ROOMS_ID));
                String nam = cur.getString(cur.getColumnIndex(ROOMS_NAME));
                String des = cur.getString(cur.getColumnIndex(ROOMS_DESC));
                String real = cur.getString(cur.getColumnIndex(ROOMS_REALNAME));
                String ic = cur.getString(cur.getColumnIndex(ROOMS_LINKICON));
                String typ = cur.getString(cur.getColumnIndex(ROOMS_TYPE));
                listMemberCards.add(new ContactBot(id, nam, des, real, ic, typ));

            } while (cur.moveToNext());
        }
        return listMemberCards;
    }

}